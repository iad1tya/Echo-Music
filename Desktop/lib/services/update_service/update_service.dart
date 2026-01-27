import 'dart:convert';
import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:Echo/services/update_service/models/update_info.dart';
import 'package:Echo/services/update_service/widgets/update_checking.dart';
import 'package:Echo/services/update_service/widgets/update_dialog.dart';
import 'package:http/http.dart' as http;
import 'package:package_info_plus/package_info_plus.dart';
import 'package:pub_semver/pub_semver.dart';

class UpdateService {
  static const String owner = 'iad1tya';
  static const String repo = 'Echo_music';

  /* ─────────────────────────────────────────────
   * 1️⃣ PURE UPDATE CHECK (NO UI)
   * ───────────────────────────────────────────── */
  static Future<UpdateInfo?> checkForUpdate() async {
    try {
      final package = await PackageInfo.fromPlatform();
      final currentVersion = Version.parse(package.version);

      // Fetch the update JSON
      final Uri uri = Uri.parse(
        'https://raw.githubusercontent.com/iad1tya/Echo-Music/main/desktop_update.json',
      );

      final response = await http.get(uri);

      if (response.statusCode != 200) return null;

      final Map<String, dynamic> data = jsonDecode(response.body);
      final String remoteVersionString = data['version']?.toString() ?? '1.0.0';
      
      // Handle "1.0" by appending ".0.0" if needed for semantic versioning
      Version remoteVersion;
      try {
        remoteVersion = Version.parse(remoteVersionString);
      } catch (e) {
        // Fallback or attempt to normalize simple versions like "1.0" -> "1.0.0"
        final parts = remoteVersionString.split('.');
        if (parts.length == 2) {
          remoteVersion = Version.parse('$remoteVersionString.0');
        } else if (parts.length == 1) {
           remoteVersion = Version.parse('$remoteVersionString.0.0');
        } else {
           // If parsing completely fails, we can't compare
           return null;
        }
      }

      // User requested: "if the version dont match with app one then should the update is avaialbe"
      if (remoteVersion != currentVersion) {
        return UpdateInfo(
          version: remoteVersion,
          name: 'New Update Available',
          body: 'A new version of Echo Music is available. Please update to continue.',
          publishedAt: '',
          downloadUrl: 'https://echomusic.fun',
        );
      }
      
      return null;
    } catch (e) {
      debugPrint('Error checking for update: $e');
      return null;
    }
  }

  /* ─────────────────────────────────────────────
   * 2️⃣ AUTO CHECK (NO LOADER)
   * ───────────────────────────────────────────── */
  static Future<void> autoCheck(BuildContext context) async {
    final update = await checkForUpdate();
    if (update == null || !context.mounted) return;

    await showUpdateDialog(context, update);
  }

  /* ─────────────────────────────────────────────
   * 3️⃣ MANUAL CHECK (SHOW LOADER)
   * ───────────────────────────────────────────── */
  static Future<void> manualCheck(BuildContext context) async {
    showDialog(
      context: context,
      barrierDismissible: false,
      useRootNavigator: false,
      builder: (_) => const UpdateCheckingDialog(),
    );

    final update = await checkForUpdate();

    if (!context.mounted) return;
    Navigator.pop(context); // close loader

    if (update != null) {
      await showUpdateDialog(context, update);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('You are already on the latest version')),
      );
    }
  }

  /* ─────────────────────────────────────────────
   * 4️⃣ DIALOG
   * ───────────────────────────────────────────── */
  static Future<void> showUpdateDialog(
    BuildContext context,
    UpdateInfo info,
  ) {
    return showDialog(
      context: context,
      useRootNavigator: false,
      builder: (_) => UpdateDialog(info),
    );
  }
}
