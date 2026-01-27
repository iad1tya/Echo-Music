import 'dart:io';
import 'dart:math';
import 'dart:ui';
import 'package:audio_video_progress_bar/audio_video_progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:Echo/screens/player/widgets/play_pause_buton.dart';
import 'package:Echo/utils/song_thumbnail.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:just_audio/just_audio.dart';
import 'package:just_audio_background/just_audio_background.dart';
import 'package:provider/provider.dart';
import 'package:sliding_up_panel/sliding_up_panel.dart';
import 'package:text_scroll/text_scroll.dart';
import 'package:palette_generator/palette_generator.dart';

import 'widgets/animated_gradient_bg.dart';

import '../../generated/l10n.dart';
import '../../services/download_manager.dart';
import '../../services/media_player.dart';
import '../../themes/colors.dart';
import '../../themes/dark.dart';
import '../../themes/text_styles.dart';
import '../../utils/adaptive_widgets/adaptive_widgets.dart';
import '../../utils/bottom_modals.dart';
import '../../ytmusic/ytmusic.dart';
import 'widgets/lyrics_box.dart';
import 'widgets/queue_list.dart';

class PlayerPage extends StatefulWidget {
  const PlayerPage({super.key, this.videoId});
  final String? videoId;

  @override
  State<PlayerPage> createState() => _PlayerPageState();
}

class _PlayerPageState extends State<PlayerPage> {
  late PanelController panelController;
  final GlobalKey<ScaffoldState> _key = GlobalKey();
  Color? color;
  List<Color> paletteColors = [];
  bool fetchedSong = false;
  late MediaItem? currentSong;
  bool _hasLyrics = false;
  bool _lyricsLoading = true;
  final GlobalKey<State> _lyricsBoxKey = GlobalKey();

  @override
  void initState() {
    super.initState();
    panelController = PanelController();
    if (widget.videoId != null) {
      GetIt.I<YTMusic>().getSongDetails(widget.videoId!).then((song) {
        if (song != null) {
          GetIt.I<MediaPlayer>().playSong(song);
          setState(() {
            fetchedSong = true;
          });
        }
      });
    }
    currentSong = GetIt.I<MediaPlayer>().currentSongNotifier.value;
    GetIt.I<MediaPlayer>().currentSongNotifier.addListener(songListener);
  }

  @override
  dispose() {
    GetIt.I<MediaPlayer>().currentSongNotifier.removeListener(songListener);
    super.dispose();
  }

  void songListener() {
    if (currentSong != GetIt.I<MediaPlayer>().currentSongNotifier.value) {
      if (mounted) {
        setState(() {
          currentSong = GetIt.I<MediaPlayer>().currentSongNotifier.value;
          _lyricsLoading = true;
          _hasLyrics = false;
        });
      }
    }
  }

  Future<void> updateBackgroundColor(ImageProvider image) async {
    final palette = await PaletteGenerator.fromImageProvider(
      image,
      maximumColorCount: 20,
    );
    if (mounted) {
      // Extract multiple colors for animated gradient
      List<Color> extractedColors = [];

      if (palette.dominantColor != null) {
        extractedColors.add(palette.dominantColor!.color);
      }
      if (palette.vibrantColor != null) {
        extractedColors.add(palette.vibrantColor!.color);
      }
      if (palette.mutedColor != null) {
        extractedColors.add(palette.mutedColor!.color);
      }
      if (palette.darkVibrantColor != null) {
        extractedColors.add(palette.darkVibrantColor!.color);
      }
      if (palette.darkMutedColor != null) {
        extractedColors.add(palette.darkMutedColor!.color);
      }
      if (palette.lightVibrantColor != null) {
        extractedColors.add(palette.lightVibrantColor!.color);
      }

      // Ensure we have at least one color
      if (extractedColors.isEmpty && palette.colors.isNotEmpty) {
        extractedColors = palette.colors.take(4).toList();
      }

      setState(() {
        color = palette.dominantColor?.color;
        paletteColors = extractedColors;
      });
    }
  }

  MaterialColor primaryWhite = const MaterialColor(
    0xFFFFFFFF,
    <int, Color>{
      50: Color(0xFFFFFFFF),
      100: Color(0xFFFFFFFF),
      200: Color(0xFFFFFFFF),
      300: Color(0xFFFFFFFF),
      400: Color(0xFFFFFFFF),
      500: Color(0xFFFFFFFF),
      600: Color(0xFFFFFFFF),
      700: Color(0xFFFFFFFF),
      800: Color(0xFFFFFFFF),
      900: Color(0xFFFFFFFF),
    },
  );

  @override
  Widget build(BuildContext context) {
    return Theme(
      data: darkTheme(
        colorScheme: ColorScheme.fromSeed(
          seedColor: primaryWhite,
          primary: primaryWhite,
          brightness: Brightness.dark,
        ),
      ),
      child: (widget.videoId != null && fetchedSong == false)
          ? const Center(
              child: AdaptiveProgressRing(),
            )
          // ignore: deprecated_member_use
          : WillPopScope(
              onWillPop: () async {
                return true;
              },
              child: Scaffold(
                key: _key,
                backgroundColor: Colors.black,
                body: Stack(
                  children: [
                    // ANIMATED BACKGROUND GRADIENT (Apple Music style)
                    Positioned.fill(
                      child: AnimatedGradientBackground(
                        colors: paletteColors.isNotEmpty
                            ? paletteColors
                            : [
                                Colors.deepPurple.shade900,
                                Colors.deepPurple.shade700,
                                Colors.purple.shade800,
                                Colors.indigo.shade900,
                              ],
                      ),
                    ),

                    // MAIN CONTENT
                    SafeArea(
                      child: Column(
                        children: [
                          // App Bar
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 16),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                AdaptiveIconButton(
                                  onPressed: () => context.pop(),
                                  icon: Icon(AdaptiveIcons.chevron_down,
                                      color: Colors.white),
                                ),
                                Row(
                                  children: [
                                    AdaptiveIconButton(
                                      onPressed: () {
                                        _key.currentState?.openEndDrawer();
                                      },
                                      icon: Icon(AdaptiveIcons.queue,
                                          color: Colors.white),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                          Expanded(
                            child: LayoutBuilder(
                              builder: (context, constraints) {
                                bool isWide = constraints.maxWidth > 800;
                                if (isWide) {
                                  return Stack(
                                    children: [
                                      Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          // LEFT: Art & Controls (always on left now)
                                          Expanded(
                                            flex: 5,
                                            child: Center(
                                              child: Container(
                                                constraints: const BoxConstraints(
                                                    maxWidth: double.infinity),
                                                padding:
                                                    const EdgeInsets.all(40.0),
                                                child: Column(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  crossAxisAlignment: CrossAxisAlignment.start,
                                                  children: [
                                                    Expanded(
                                                      child: Center(
                                                        child: AspectRatio(
                                                          aspectRatio: 1,
                                                          child: Container(
                                                            decoration:
                                                                BoxDecoration(
                                                              borderRadius:
                                                                  BorderRadius
                                                                      .circular(
                                                                          12),
                                                              boxShadow: [
                                                                BoxShadow(
                                                                  color: Colors
                                                                      .black
                                                                      .withValues(
                                                                          alpha:
                                                                              0.4),
                                                                  blurRadius:
                                                                      20,
                                                                  spreadRadius:
                                                                      5,
                                                                  offset:
                                                                      const Offset(
                                                                          0,
                                                                          10),
                                                                ),
                                                              ],
                                                            ),
                                                            child: ClipRRect(
                                                              borderRadius:
                                                                  BorderRadius
                                                                      .circular(
                                                                          12),
                                                              child:
                                                                  SongThumbnail(
                                                                song: currentSong!
                                                                    .extras!,
                                                                fit: BoxFit
                                                                    .cover,
                                                                onImageReady:
                                                                    updateBackgroundColor,
                                                              ),
                                                            ),
                                                          ),
                                                        ),
                                                      ),
                                                    ),
                                                    const SizedBox(height: 40),
                                                    _buildTitleAndControls(
                                                        context,
                                                        centered: false),
                                                  ],
                                                ),
                                              ),
                                            ),
                                          ),
                                          // RIGHT: Lyrics or "No Lyrics" message
                                          Expanded(
                                            flex: 6,
                                            child: Padding(
                                              padding:
                                                  const EdgeInsets.all(40.0),
                                              child: _hasLyrics
                                                  ? LyricsBox(
                                                      key: _lyricsBoxKey,
                                                      currentSong: currentSong!,
                                                      size: Size(
                                                          constraints.maxWidth / 2,
                                                          constraints.maxHeight),
                                                      onLyricsFound: (found) {
                                                        if (mounted) {
                                                          setState(() {
                                                            _hasLyrics = found;
                                                            _lyricsLoading = false;
                                                          });
                                                        }
                                                      },
                                                    )
                                                  : Center(
                                                      child: _lyricsLoading
                                                          ? const CircularProgressIndicator(
                                                              color: Colors.white,
                                                            )
                                                          : Text(
                                                              'No Lyrics',
                                                              style: TextStyle(
                                                                fontSize: 24,
                                                                fontWeight: FontWeight.w600,
                                                                color: Colors.white.withValues(alpha: 0.5),
                                                              ),
                                                            ),
                                                    ),
                                            ),
                                          ),
                                        ],
                                      ),
                                      if (!_hasLyrics)
                                        Offstage(
                                          child: LyricsBox(
                                            key: _lyricsBoxKey,
                                            currentSong: currentSong!,
                                            size: Size.zero,
                                            onLyricsFound: (found) {
                                              if (mounted) {
                                                setState(() {
                                                  _hasLyrics = found;
                                                  _lyricsLoading = false;
                                                });
                                              }
                                            },
                                          ),
                                        ),
                                    ],
                                  );
                                } else {
                                  // Mobile / Narrow Layout (Vertical)
                                  return Padding(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: 24.0),
                                    child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceEvenly,
                                      children: [
                                        Expanded(
                                          child: Padding(
                                            padding: const EdgeInsets.all(20.0),
                                            child: Container(
                                              decoration: BoxDecoration(
                                                borderRadius:
                                                    BorderRadius.circular(12),
                                                boxShadow: [
                                                  BoxShadow(
                                                    color: Colors.black
                                                        .withValues(alpha: 0.4),
                                                    blurRadius: 20,
                                                    spreadRadius: 5,
                                                    offset: const Offset(0, 10),
                                                  ),
                                                ],
                                              ),
                                              child: ClipRRect(
                                                borderRadius:
                                                    BorderRadius.circular(12),
                                                child: SongThumbnail(
                                                  song: currentSong!.extras!,
                                                  fit: BoxFit.cover,
                                                  onImageReady:
                                                      updateBackgroundColor,
                                                ),
                                              ),
                                            ),
                                          ),
                                        ),
                                        _buildTitleAndControls(context),
                                        const SizedBox(height: 20),
                                      ],
                                    ),
                                  );
                                }
                              },
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
                endDrawer: SizedBox(
                  width: min(400, MediaQuery.of(context).size.width) - 50,
                  child: const QueueList(),
                ),
              ),
            ),
    );
  }

  Widget _buildTitleAndControls(BuildContext context, {bool centered = false}) {
    if (currentSong == null) return const SizedBox();
    MediaPlayer mediaPlayer = context.watch<MediaPlayer>();
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment:
          centered ? CrossAxisAlignment.center : CrossAxisAlignment.start,
      children: [
        // Top Row: [Title/Artist] [Like] [Menu]
        Row(
          mainAxisAlignment:
              centered ? MainAxisAlignment.center : MainAxisAlignment.start,
          children: [
            // Title and Artist
            Expanded(
              child: Column(
                crossAxisAlignment: centered
                    ? CrossAxisAlignment.center
                    : CrossAxisAlignment.start,
                children: [
                  TextScroll(
                    currentSong?.title ?? 'Title',
                    style: const TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                        color: Colors.white),
                    mode: TextScrollMode.endless,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    currentSong?.artist ??
                        currentSong?.album ??
                        currentSong?.extras?['subtitle'] ??
                        '',
                    style: TextStyle(
                      fontSize: 18,
                      color: Colors.white.withValues(alpha: 0.7),
                      fontWeight: FontWeight.w500,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            const SizedBox(width: 16),
            // Favorite
            ValueListenableBuilder(
              valueListenable: Hive.box('FAVOURITES').listenable(),
              builder: (context, value, child) {
                Map? item = value.get(currentSong?.extras?['videoId']);
                return AdaptiveIconButton(
                  icon: Icon(
                    item == null
                        ? AdaptiveIcons.heart
                        : AdaptiveIcons.heart_fill,
                    size: 24,
                    color: item == null
                        ? Colors.white.withValues(alpha: 0.7)
                        : Colors.redAccent,
                  ),
                  onPressed: () async {
                    if (item == null) {
                      await Hive.box('FAVOURITES').put(
                        currentSong!.extras!['videoId'],
                        {
                          ...currentSong!.extras!,
                          'createdAt': DateTime.now().millisecondsSinceEpoch
                        },
                      );
                    } else {
                      await value.delete(currentSong!.extras!['videoId']);
                    }
                  },
                );
              },
            ),
            const SizedBox(width: 8),
            // More Options
            Builder(
              builder: (buttonContext) => AdaptiveIconButton(
                onPressed: () {
                  final RenderBox renderBox =
                      buttonContext.findRenderObject() as RenderBox;
                  final position = renderBox.localToGlobal(Offset.zero);
                  final size = renderBox.size;
                  Modals.showPlayerOptionsModal(
                    context,
                    mediaPlayer.currentSongNotifier.value!.extras!,
                    buttonPosition: position,
                    buttonSize: size,
                  );
                },
                icon: Icon(
                  Icons.more_horiz,
                  size: 24,
                  color: Colors.white.withValues(alpha: 0.7),
                ),
              ),
            ),
          ],
        ),

        const SizedBox(height: 30),

        // Progress Bar
        ValueListenableBuilder(
          valueListenable: mediaPlayer.progressBarState,
          builder: (context, ProgressBarState value, child) {
            return ProgressBar(
              progress: value.current,
              total: value.total,
              buffered: value.buffered,
              barHeight: 4,
              thumbRadius: 6,
              baseBarColor: Colors.white.withValues(alpha: 0.3),
              bufferedBarColor: Colors.white.withValues(alpha: 0.5),
              progressBarColor: Colors.white,
              thumbColor: Colors.white,
              timeLabelTextStyle: const TextStyle(color: Colors.white),
              onSeek: (value) => mediaPlayer.player.seek(value),
            );
          },
        ),
        const SizedBox(height: 20),

        // Controls
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            // Shuffle
            AdaptiveIconButton(
              onPressed: () {
                // Toggle shuffle not implemented in UI, just icon for now or placeholder
              },
              icon: Icon(
                Icons.shuffle,
                size: 24,
                color: Colors.white.withValues(alpha: 0.7),
              ),
            ),
            // Previous
            AdaptiveIconButton(
              onPressed: () {
                mediaPlayer.player.seekToPrevious();
              },
              icon: Icon(
                AdaptiveIcons.skip_previous,
                size: 32,
                color: Colors.white,
              ),
            ),
            // Play/Pause
            Container(
              height: 64,
              width: 64,
              decoration: BoxDecoration(
                  color: Colors.white, borderRadius: BorderRadius.circular(50)),
              child: ValueListenableBuilder(
                valueListenable: mediaPlayer.buttonState,
                builder: (context, ButtonState value, child) {
                  if (value == ButtonState.loading) {
                    return const Center(
                        child: CircularProgressIndicator(color: Colors.black));
                  }
                  return IconButton(
                    onPressed: () {
                      value == ButtonState.playing
                          ? mediaPlayer.player.pause()
                          : mediaPlayer.player.play();
                    },
                    icon: Icon(
                      value == ButtonState.playing
                          ? Icons.pause
                          : Icons.play_arrow,
                      size: 32,
                      color: Colors.black,
                    ),
                  );
                },
              ),
            ),
            // Next
            AdaptiveIconButton(
              onPressed: () {
                mediaPlayer.player.seekToNext();
              },
              icon: Icon(
                AdaptiveIcons.skip_next,
                size: 32,
                color: Colors.white,
              ),
            ),
            // Repeat
            ValueListenableBuilder(
                valueListenable: mediaPlayer.loopMode,
                builder: (context, value, child) {
                  return AdaptiveIconButton(
                    onPressed: () {
                      mediaPlayer.changeLoopMode();
                    },
                    icon: Icon(
                      value == LoopMode.off || value == LoopMode.all
                          ? AdaptiveIcons.repeat_all
                          : AdaptiveIcons.repeat_one,
                      size: 24,
                      color: value == LoopMode.off
                          ? Colors.white.withValues(alpha: 0.7)
                          : Colors.white,
                    ),
                  );
                }),
          ],
        ),
        const SizedBox(height: 20),
      ],
    );
  }
}
