import 'dart:async';
import 'dart:io';
import 'package:blur/blur.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:Echo/services/update_service/update_service.dart';



import '../../generated/l10n.dart';
import 'widgets/bottom_player.dart';

class AppShell extends StatefulWidget {
  const AppShell({
    Key? key,
    required this.navigationShell,
  }) : super(key: key ?? const ValueKey('AppShell'));
  final StatefulNavigationShell navigationShell;

  @override
  State<AppShell> createState() => _AppShellState();
}

class _AppShellState extends State<AppShell> {
  @override
  void initState() {
    super.initState();
    UpdateService.autoCheck(context);
  }



  void _goBranch(int index) {
    widget.navigationShell.goBranch(
      index,
      initialLocation: index == widget.navigationShell.currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Stack(
        children: [
          Row(
            children: [
              if (screenWidth >= 450)
                NavigationRail(
                  extended: true,
                  backgroundColor: Colors.transparent,
                  groupAlignment: 0.0,
                  destinations: [
                    NavigationRailDestination(
                      selectedIcon:
                          const Icon(CupertinoIcons.music_house_fill, color: Colors.black),
                      icon: const Icon(CupertinoIcons.music_house),
                      label: Text(S.of(context).Home),
                    ),
                    NavigationRailDestination(
                      selectedIcon:
                          const Icon(CupertinoIcons.search, color: Colors.black),
                      icon: const Icon(CupertinoIcons.search),
                      label: Text(S.of(context).Search_Echo),
                    ),
                    NavigationRailDestination(
                        selectedIcon:
                            const Icon(Icons.library_music_outlined, color: Colors.black),
                        icon: const Icon(Icons.library_music_outlined),
                        label: Text(S.of(context).Saved)),
                    NavigationRailDestination(
                      selectedIcon:
                          const Icon(CupertinoIcons.gear_alt_fill, color: Colors.black),
                      icon: const Icon(CupertinoIcons.gear_alt),
                      label: Text(S.of(context).Settings),
                    )
                  ],
                  selectedIndex: widget.navigationShell.currentIndex,
                  onDestinationSelected: _goBranch,
                ),
              Expanded(
                child: widget.navigationShell,
              ),
            ],
          ),
          const Align(
            alignment: Alignment.bottomCenter,
            child: BottomPlayer(),
          ),
        ],
      ),
      bottomNavigationBar: screenWidth < 450
          ? NavigationBar(
              selectedIndex: widget.navigationShell.currentIndex,
              destinations: [
                NavigationDestination(
                  selectedIcon: const Icon(CupertinoIcons.music_house_fill, color: Colors.black),
                  icon: const Icon(CupertinoIcons.music_house),
                  label: S.of(context).Home,
                ),
                NavigationDestination(
                  selectedIcon: const Icon(CupertinoIcons.search, color: Colors.black),
                  icon: const Icon(CupertinoIcons.search),
                  label: S.of(context).Search_Echo,
                ),
                NavigationDestination(
                  selectedIcon: const Icon(Icons.library_music, color: Colors.black),
                  icon: const Icon(Icons.library_music_outlined),
                  label: S.of(context).Saved,
                ),
                NavigationDestination(
                  selectedIcon: const Icon(CupertinoIcons.settings_solid, color: Colors.black),
                  icon: const Icon(CupertinoIcons.settings),
                  label: S.of(context).Settings,
                ),
              ],
              backgroundColor:
                  Theme.of(context).colorScheme.surfaceContainerLow,
              // colo: Theme.of(context).colorScheme.onSurface,
              onDestinationSelected: _goBranch,
            )
          : null,
    );
  }
}
