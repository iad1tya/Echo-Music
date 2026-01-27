import 'dart:convert';
import 'dart:developer';

import 'package:hive_flutter/hive_flutter.dart';
import 'package:http/http.dart';

class SpotifyService {
  final List<String> _scopes = [
    'user-read-private',
    'user-read-email',
    'playlist-read-private',
    'playlist-read-collaborative',
  ];

  final String clientID = '4ede44382bf14ac3ba1d97ad753b233f';
  final String clientSecret = 'fb01ad204aff4c12bbcb3ca7ac617990';
  final String redirectUrl = '127.0.0.1';
  final String spotifyApiUrl = 'https://accounts.spotify.com/api';
  final String spotifyApiBaseUrl = 'https://api.spotify.com/v1';
  final String spotifyUserPlaylistEndpoint = '/me/playlists';
  final String spotifyPlaylistTrackEndpoint = '/playlists';
  final String spotifyRegionalChartsEndpoint = '/views/charts-regional';
  final String spotifyFeaturedPlaylistsEndpoint = '/browse/featured-playlists';
  final String spotifyBaseUrl = 'https://accounts.spotify.com';
  final String requestToken = 'https://accounts.spotify.com/api/token';

  Future<String> getAccessTokenCC() async {
    final box = await Hive.openBox('SPOTIFY_TOKEN');
    final token = box.get('access_token');
    final expiry = box.get('expiry');

    if (token != null && expiry != null) {
      if (DateTime.now().isBefore(DateTime.parse(expiry))) {
        return token;
      }
    }

    var response;
    final tokenUrl = Uri.parse('https://accounts.spotify.com/api/token');
    String basicAuth =
        'Basic ${base64Encode(utf8.encode('$clientID:$clientSecret'))}';
    try {
      response = await post(
        tokenUrl,
        headers: {
          'Authorization': basicAuth,
        },
        body: {
          'grant_type': 'client_credentials',
        },
      );
    } catch (e) {
      log('Error in getting spotify access token: $e', name: "spotifyAPI");
    }

    if (response != null && response.statusCode == 200) {
      final responseBody = json.decode(response.body);
      final accessToken = responseBody['access_token'];
      final expiresIn = responseBody['expires_in'];
      
      await box.put('access_token', accessToken);
      await box.put('expiry', DateTime.now().add(Duration(seconds: expiresIn)).toIso8601String());
      
      return accessToken;
    } else {
      throw Exception('Failed to get access token');
    }
  }

  Future<Map<String, Object>> getAllTracksOfPlaylist(
    String accessToken,
    String playlistId,
  ) async {
    final List tracks = [];
    int totalTracks = 100;
    String playlistName = "Liked";
    String? imgUrl;
    String url = 'https://open.spotify.com/playlist/$playlistId';
    String? description;

    final Map data = await getHundredTracksOfPlaylist(
      accessToken,
      playlistId,
      0,
    );

    try {
      final Uri path = Uri.parse(
        '$spotifyApiBaseUrl$spotifyPlaylistTrackEndpoint/$playlistId',
      );

      final response = await get(
        path,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Accept': 'application/json'
        },
      );

      if (response.statusCode == 200) {
        final result = jsonDecode(response.body);
        if (result["images"] != null && (result["images"] as List).isNotEmpty) {
           imgUrl = (result["images"] as List).first["url"];
        }
       
        if (result['external_urls'] != null) {
           url = result['external_urls']['spotify'] ?? url;
        }
        playlistName = result["name"] ?? "Liked";
        description = result["description"];
      } else {
        log(
          'Error in getHundredTracksOfPlaylist, called: $path, returned: ${response.statusCode}',
          error: response.body,
        );
      }
    } catch (e) {
      log('Error in getting spotify playlist tracks: $e');
    }

    if (data.containsKey('total')) {
      totalTracks = data['total'] as int;
    }
    if (data.containsKey('tracks')) {
      tracks.addAll(data['tracks'] as List);
    }

    if (totalTracks > 100) {
      for (int i = 1; i * 100 <= totalTracks; i++) {
        final Map data = await getHundredTracksOfPlaylist(
          accessToken,
          playlistId,
          i * 100,
        );
        if (data.containsKey('tracks')) {
            tracks.addAll(data['tracks'] as List);
        }
      }
    }
    return {
      'tracks': tracks,
      'playlistName': playlistName,
      'url': url,
      'imgUrl': imgUrl ?? "",
      'description': description ?? "",
    };
  }

  Future<Map> getHundredTracksOfPlaylist(
    String accessToken,
    String playlistId,
    int offset,
  ) async {
    try {
      final Uri path = Uri.parse(
        '$spotifyApiBaseUrl$spotifyPlaylistTrackEndpoint/$playlistId/tracks?limit=100&offset=$offset',
      );

      final response = await get(
        path,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Accept': 'application/json'
        },
      );

      if (response.statusCode == 200) {
        final result = jsonDecode(response.body);
        final List tracks = result['items'] as List;
        final int total = result['total'] as int;

        return {'tracks': tracks, 'total': total};
      } else {
        log(
          'Error in getHundredTracksOfPlaylist, called: $path, returned: ${response.statusCode}',
          error: response.body,
        );
      }
    } catch (e) {
      log('Error in getting spotify playlist tracks: $e', name: "spotifyAPI");
    }
    return {};
  }

  Future<Map> getTrackDetails(String accessToken, String trackId) async {
    final Uri path = Uri.parse(
      '$spotifyApiBaseUrl/tracks/$trackId',
    );
    final response = await get(
      path,
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Accept': 'application/json'
      },
    );

    if (response.statusCode == 200) {
      final result = jsonDecode(response.body) as Map;
      return result;
    } else {
      log(
        'Error in getTrackDetails, called: $path, returned: ${response.statusCode}',
        error: response.body,
      );
    }
    return {};
  }

  Future<List<Map>> get50AlbumTracks(String accessToken, String albumId,
      {int offset = 0, int limit = 50}) async {
    try {
      final Uri path = Uri.parse(
        '$spotifyApiBaseUrl/albums/$albumId/tracks?offset=$offset&limit=$limit',
      );
      final response = await get(
        path,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Accept': 'application/json'
        },
      );
      final List<Map> songsData = [];
      if (response.statusCode == 200) {
        final result = jsonDecode(response.body);
        for (var element in result['items']) {
          songsData.add({
            'name': element['name'],
            'id': element['id'],
            'externalUrl': element['external_urls']['spotify'],
            'artists': element['artists'],
          });
        }
      } else {
        log(
          'Error in get50AlbumTracks, called: ${path.toString()}, returned: ${response.statusCode}',
          error: response.body,
        );
      }
      return songsData;
    } catch (e) {
      log('Error in getting spotify album tracks: $e', name: "spotifyAPI");
      return List.empty();
    }
  }

  Future<Map<String, Object>> getAllAlbumTracks(
      String accessToken, String albumId) async {
    try {
      int totalItems = 0;
      String albumName = "";
      String? imgUrl;
      String url = 'https://open.spotify.com/album/$albumId';
      String? description;
      String? artists;
      final Uri path = Uri.parse(
        '$spotifyApiBaseUrl/albums/$albumId',
      );
      final response = await get(
        path,
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Accept': 'application/json'
        },
      );
      final List<Map> songsData = [];
      if (response.statusCode == 200) {
        totalItems = jsonDecode(response.body)['total_tracks'] as int;
        albumName = jsonDecode(response.body)['name'];
        if (jsonDecode(response.body)["images"] != null && (jsonDecode(response.body)["images"] as List).isNotEmpty) {
           imgUrl = (jsonDecode(response.body)["images"] as List).first["url"];
        }
        url = jsonDecode(response.body)['external_urls']['spotify'];
        description = jsonDecode(response.body)['album_type'];
        List<String> _artists = [];
        for (var e in (jsonDecode(response.body)['artists'] as List)) {
          _artists.add(e['name']);
        }
        artists = _artists.join(", ");
        final result = jsonDecode(response.body);
        for (var element in result['tracks']['items']) {
          songsData.add({
            'name': element['name'],
            'id': element['id'],
            'externalUrl': element['external_urls']['spotify'],
            'artists': element['artists'],
          });
        }
        if (totalItems > 50) {
          for (int i = 1; i * 50 <= totalItems; i++) {
            songsData.addAll(
                await get50AlbumTracks(accessToken, albumId, offset: i * 50));
          }
        }
      } else {
        log(
          'Error in getAllAlbumTracks, called: ${path.toString()}, returned: ${response.statusCode}',
          error: response.body,
        );
      }
      return {
        "tracks": songsData,
        "total": totalItems,
        "playlistName": albumName, // mapped to playlistName as used in import_service
        "albumName": albumName,
        "url": url,
        "imgUrl": imgUrl ?? "",
        "description": description ?? "",
        "artists": artists ?? "",
      };
    } catch (e) {
      log('Error in getting spotify album tracks: $e', name: "spotifyAPI");
      return {"tracks": List.empty(), "total": 0, "error": e, 'playlistName': ""};
    }
  }
}
