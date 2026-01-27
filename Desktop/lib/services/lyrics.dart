import '../models/lyrics_model.dart';
import 'lrcnet_api.dart';

class LyricsService {
  Future<Lyrics> getLyrics({
    required String title,
    String? artist,
    String? album,
    String? duration,
  }) async {
    // Basic caching could be added here if needed, but for now delegating to API
    // which has some internal logic or just fetches.
    return await getLRCNetAPILyrics(
      title,
      artist: artist,
      album: album,
      duration: duration,
    );
  }
}
