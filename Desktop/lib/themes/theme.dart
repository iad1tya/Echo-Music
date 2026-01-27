import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'typography.dart';

class AppTheme {
  static ThemeData light({Color? primary}) {
    final colorScheme = ColorScheme.fromSeed(
            seedColor: primary??Colors.red,
            brightness: Brightness.light,
          );

    return ThemeData.light(useMaterial3: true).copyWith(
      scaffoldBackgroundColor: Colors.transparent,
      textTheme: appTextTheme(ThemeData.light().textTheme),
      colorScheme: colorScheme,
      visualDensity: VisualDensity.adaptivePlatformDensity,
      appBarTheme: AppBarTheme(
        systemOverlayStyle: const SystemUiOverlayStyle(
          statusBarBrightness: Brightness.light,
          statusBarColor: Colors.transparent,
          statusBarIconBrightness: Brightness.dark,
          systemNavigationBarColor: Colors.transparent,
        ),
      ),
      navigationRailTheme: NavigationRailThemeData(
        backgroundColor: Colors.transparent,
        unselectedIconTheme: const IconThemeData(color: Colors.black),
        selectedIconTheme: const IconThemeData(color: Colors.white),
        indicatorColor: Colors.black,
        labelType: NavigationRailLabelType.all,
        selectedLabelTextStyle: const TextStyle(color: Colors.black, fontSize: 11),
        unselectedLabelTextStyle:
            TextStyle(color: Colors.black.withValues(alpha: 0.6), fontSize: 11),
      ),
      pageTransitionsTheme: PageTransitionsTheme(
        builders: Map<TargetPlatform, PageTransitionsBuilder>.fromIterable(
          TargetPlatform.values,
          value: (_) => const FadeForwardsPageTransitionsBuilder(),
        ),
      ),
    );
  }

  static ThemeData dark({Color? primary}) {
    final colorScheme = ColorScheme.dark(
      primary: Colors.white,
      onPrimary: Colors.black,
      secondary: Colors.white,
      onSecondary: Colors.black,
      surface: Colors.black,
      onSurface: Colors.white,
      error: Colors.red,
      onError: Colors.white,
    );
    return ThemeData.dark(useMaterial3: true).copyWith(
      scaffoldBackgroundColor: Colors.transparent,
      textTheme: appTextTheme(ThemeData.dark().textTheme),
      colorScheme: colorScheme,
      visualDensity: VisualDensity.adaptivePlatformDensity,
      appBarTheme: const AppBarTheme(
        backgroundColor: Colors.transparent,
        surfaceTintColor: Colors.transparent,
        systemOverlayStyle: SystemUiOverlayStyle(
          statusBarBrightness: Brightness.dark,
          statusBarColor: Colors.transparent,
          statusBarIconBrightness: Brightness.light,
          systemNavigationBarColor: Colors.transparent,
        ),
      ),
      navigationBarTheme: const NavigationBarThemeData(
        backgroundColor: Colors.transparent,
      ),
      navigationRailTheme: NavigationRailThemeData(
        backgroundColor: Colors.transparent,
        unselectedIconTheme: const IconThemeData(color: Colors.white),
        selectedIconTheme: const IconThemeData(color: Colors.black),
        indicatorColor: Colors.white,
        labelType: NavigationRailLabelType.all,
        selectedLabelTextStyle: const TextStyle(color: Colors.black, fontSize: 11, fontWeight: FontWeight.bold),
        unselectedLabelTextStyle:
            TextStyle(color: Colors.black.withValues(alpha: 0.6), fontSize: 11),
      ),
      pageTransitionsTheme: PageTransitionsTheme(
        builders: Map<TargetPlatform, PageTransitionsBuilder>.fromIterable(
          TargetPlatform.values,
          value: (_) => const FadeForwardsPageTransitionsBuilder(),
        ),
      ),
    );
  }
}
