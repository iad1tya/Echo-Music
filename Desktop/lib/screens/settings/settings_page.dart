import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:Echo/services/update_service/update_service.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../generated/l10n.dart';
import '../../themes/text_styles.dart';
import '../../utils/adaptive_widgets/adaptive_widgets.dart';
import '../../utils/bottom_modals.dart';
import 'widgets/color_icon.dart';
import 'widgets/setting_item.dart';
import 'cubit/settings_system_cubit.dart';

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => SettingsSystemCubit()..load(),
      child: AdaptiveScaffold(
        appBar: AdaptiveAppBar(
          title: Text(
            S.of(context).Settings,
            style: appBarTitleStyle(),
          ),
          automaticallyImplyLeading: false,
        ),
        body: BlocBuilder<SettingsSystemCubit, SettingsSystemState>(
          builder: (context, state) {
            final bool? batteryDisabled = state is SettingsSystemLoaded
                ? state.isBatteryOptimizationDisabled
                : null;

            return Center(
              child: Container(
                constraints: const BoxConstraints(maxWidth: 1000),
                child: ListView(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16,
                    vertical: 8,
                  ),
                  children: [
                    if (Platform.isAndroid && batteryDisabled != true)
                      _BatteryWarningTile(),
                    GroupTitle(title: "General"),

                    SettingTile(
                      title: "Player",
                      leading: const Icon(Icons.play_arrow_rounded),
                      isFirst: true,
                      isLast: true,
                      onTap: () => context.go('/settings/player'),
                    ),
                    GroupTitle(title: "Services"),
                    SettingTile(
                      title: "Youtube Music",
                      leading: const Icon(Icons.play_circle_fill),
                      isFirst: true,
                      isLast: true,
                      onTap: () => context.go('/settings/services/ytmusic'),
                    ),
                    GroupTitle(title: "Storage & Privacy"),
                    SettingTile(
                      title: "Backup and storage",
                      leading: const Icon(
                        Icons.cloud_upload_rounded,
                      ),
                      isFirst: true,
                      onTap: () => context.go(
                        '/settings/backup_storage',
                      ),
                    ),
                    SettingTile(
                      title: "Privacy",
                      leading: const Icon(Icons.privacy_tip),
                      isLast: true,
                      onTap: () => context.go('/settings/privacy'),
                    ),
                    GroupTitle(title: "Updates & About"),
                    SettingTile(
                      title: S.of(context).About,
                      leading: const Icon(Icons.info_rounded),
                      isFirst: true,
                      onTap: () => context.go('/settings/about'),
                    ),
                    SettingTile(
                      title: S.of(context).Check_For_Update,
                      leading: const Icon(Icons.update_rounded),
                      onTap: () async {
                        await UpdateService.manualCheck(context);
                      },
                    ),
                    SettingTile(
                      title: "Support",
                      leading: const Icon(Icons.favorite_rounded),
                      isLast: true,
                      onTap: () async {
                        final uri = Uri.parse('https://support.iad1tya.cyou/');
                        if (await canLaunchUrl(uri)) {
                          await launchUrl(uri, mode: LaunchMode.externalApplication);
                        }
                      },
                    ),
                    const SizedBox(height: 100), // Extra padding for miniplayer
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

class _BatteryWarningTile extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListTile(
      tileColor: Theme.of(context).colorScheme.errorContainer.withAlpha(200),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(25),
      ),
      leading: const ColorIcon(
        icon: Icons.battery_alert,
        color: Colors.red,
      ),
      title: Text(
        S.of(context).Battery_Optimisation_title,
        style: TextStyle(
          color: Theme.of(context).colorScheme.onErrorContainer,
        ),
      ),
      subtitle: Text(
        S.of(context).Battery_Optimisation_message,
        style: tinyTextStyle(context).copyWith(
          color: Theme.of(context)
              .colorScheme
              .onErrorContainer
              .withValues(alpha: 0.7),
        ),
      ),
      onTap: () {
        context.read<SettingsSystemCubit>().requestBatteryOptimizationIgnore();
      },
    );
  }
}

