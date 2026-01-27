class AppConfig {
  final bool isBeta;
  final Uri stableReleasesUri;
  final Uri allReleasesUri;
  final String codeName; // e.g. 2.0.16 or 2.0.16-beta.3

  AppConfig({
    required this.isBeta,
    required this.stableReleasesUri,
    required this.allReleasesUri,
    required this.codeName,
  });
}
