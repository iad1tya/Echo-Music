import 'dart:async';

import 'package:get_it/get_it.dart';
import 'package:Echo/services/library.dart';
import 'package:Echo/services/spotify.dart';
import 'package:Echo/ytmusic/ytmusic.dart';

class ImportState {
  final int total;
  final int current;
  final String message;
  final bool isDone;
  final bool isError;

  ImportState({
    required this.total,
    required this.current,
    required this.message,
    this.isDone = false,
    this.isError = false,
  });
}

class ImportService {
  final SpotifyService _spotify = SpotifyService();
  final YTMusic _ytMusic = GetIt.I<YTMusic>();
  final LibraryService _library = GetIt.I<LibraryService>();

  Stream<ImportState> import(String url) async* {
    if (url.contains('spotify.com')) {
      yield* _importSpotify(url);
    } else if (url.contains('youtu.be') || url.contains('youtube.com')) {
      yield* _importYouTube(url);
    } else {
      yield ImportState(
        total: 0,
        current: 0,
        message: "Invalid URL",
        isError: true,
      );
    }
  }

  Stream<ImportState> _importYouTube(String url) async* {
    yield ImportState(
      total: 0,
       current: 0,
       message: "Importing YouTube Playlist...",
    );
    try {
      final result = await _library.importPlaylist(url);
      if (result.contains('Error') || result == 'Invalid Url') {
         yield ImportState(
          total: 0,
          current: 0,
          message: result,
          isError: true,
        );
      } else {
        yield ImportState(
          total: 1,
          current: 1,
          message: result,
          isDone: true,
        );
      }
    } catch (e) {
      yield ImportState(
        total: 0,
        current: 0,
        message: "Failed to import: $e",
        isError: true,
      );
    }
  }

  Stream<ImportState> _importSpotify(String url) async* {
    try {
      yield ImportState(total: 0, current: 0, message: "Connecting to Spotify...");
      final token = await _spotify.getAccessTokenCC();
      
      String? playlistId;
      if (url.contains('/playlist/')) {
        playlistId = url.split('/playlist/')[1].split('?')[0];
      } else if (url.contains('/album/')) {
         // TODO: Handle albums if needed, but request said playlist.
         // Bloomee handles albums too.
         playlistId = url.split('/album/')[1].split('?')[0]; 
      }

      if (playlistId == null) {
         yield ImportState(total: 0, current: 0, message: "Invalid Spotify URL", isError: true);
         return;
      }
      
      yield ImportState(total: 0, current: 0, message: "Fetching Playlist Tracks...");
      Map<String, Object> playlistData;
      
      if (url.contains('/album/')) {
           playlistData = await _spotify.getAllAlbumTracks(token, playlistId);
      } else {
           playlistData = await _spotify.getAllTracksOfPlaylist(token, playlistId);
      }

      final tracks = playlistData['tracks'] as List;
      final playlistName = playlistData['playlistName'] as String? ?? (url.contains('/album/') ? (playlistData['albumName'] as String? ?? "Imported Album") : "Imported Playlist");

      if (tracks.isEmpty) {
        yield ImportState(total: 0, current: 0, message: "No tracks found", isError: true);
        return;
      }
      
      // Create playlist in Echo
      yield ImportState(total: tracks.length, current: 0, message: "Created Playlist: $playlistName");
      String playlistKey = await _library.createPlaylistKey(playlistName);
      
      int count = 0;
      for (var track in tracks) {
         if (track == null) continue;
         String title = "";
         if (track['name'] != null) title += track['name'];
         if (track['artists'] != null && (track['artists'] as List).isNotEmpty) {
             // Handle different structures if needed, but usually it is list of maps or just strings?
             // SpotifyService returns 'artists': element['artists'] (List) or 'artists': "Name" (String)?
             // In SpotifyService: 'artists': element['artists'], which is a List of Maps usually.
             // We should check structure.
             var artists = track['artists'];
             if (artists is List) {
                 title += " " + artists.map((a) => a['name']).join(" ");
             } else if (artists is String) {
                 title += " " + artists;
             }
         }
         
         yield ImportState(
             total: tracks.length, 
             current: count + 1, 
             message: "Searching: $title"
         );
         
         // Search on YT
         final match = await _findSongOnYouTube(title);
         if (match != null) {
             await _library.addToPlaylist(item: match, key: playlistKey);
             yield ImportState(
                 total: tracks.length, 
                 current: count + 1, 
                 message: "Imported: ${match['title']}"
             );
         } else {
             yield ImportState(
                 total: tracks.length, 
                 current: count + 1, 
                 message: "Not found: $title" // Don't fail the whole import
             );
         }
         count++;
      }
      
      yield ImportState(
          total: tracks.length,
          current: count,
          message: "Import Complete",
          isDone: true,
      );
      
    } catch (e) {
      yield ImportState(total: 0, current: 0, message: "Error: $e", isError: true);
    }
  }

  // Helper to find song on YT
  Future<Map?> _findSongOnYouTube(String query) async {
    try {
      final result = await _ytMusic.search(query, filter: 'songs');
      if (result['sections'] != null && (result['sections'] as List).isNotEmpty) {
        final section = result['sections'][0];
        if (section['contents'] != null && (section['contents'] as List).isNotEmpty) {
           return section['contents'][0];
        }
      }
    } catch (e) {
      // ignore
    }
    return null;
  }
}
