import 'package:pub_semver/pub_semver.dart';

class UpdateInfo {
  final Version version;
  final String name;
  final String body;
  final String publishedAt;
  final String downloadUrl;

  UpdateInfo({
    required this.version,
    required this.name,
    required this.body,
    required this.publishedAt,
    required this.downloadUrl,
  });
}
