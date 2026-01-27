import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:get_it/get_it.dart';
import 'package:Echo/models/lyrics_model.dart'; // Import the new model
import 'package:Echo/services/lyrics.dart'; // Import the new service
import 'package:Echo/services/media_player.dart';
import 'package:just_audio_background/just_audio_background.dart';
import 'package:loading_indicator_m3e/loading_indicator_m3e.dart';
import 'package:provider/provider.dart';
import 'package:scrollable_positioned_list/scrollable_positioned_list.dart';
import 'package:wakelock_plus/wakelock_plus.dart';

class LyricsBox extends StatefulWidget {
  const LyricsBox({
    required this.currentSong,
    required this.size,
    this.onLyricsFound,
    super.key,
  });
  final MediaItem currentSong;
  final Size size;
  final Function(bool)? onLyricsFound;

  @override
  State<LyricsBox> createState() => _LyricsBoxState();
}

class _LyricsBoxState extends State<LyricsBox> {
  Future<Lyrics>? _fetchLyricsFuture;
  bool _lyricsLoaded = false;

  @override
  void initState() {
    super.initState();
    _initFetchLyrics();
    _initWakelock();
  }

  void _initFetchLyrics() {
    _fetchLyrics();
  }

  void _initWakelock() {
    GetIt.I<MediaPlayer>().buttonState.addListener(_updateWakelock);
  }

  void _updateWakelock() {
    if (!mounted) return;
    final isPlaying =
        GetIt.I<MediaPlayer>().buttonState.value == ButtonState.playing;
    if (isPlaying && _lyricsLoaded) {
      WakelockPlus.enable();
    } else {
      WakelockPlus.disable();
    }
  }

  @override
  void didUpdateWidget(covariant LyricsBox oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.currentSong.id != oldWidget.currentSong.id) {
      _initFetchLyrics();
    }
  }

  void _fetchLyrics() {
    if (context.mounted) {
      setState(() {
        _fetchLyricsFuture = GetIt.I<LyricsService>().getLyrics(
          title: widget.currentSong.title,
          artist: widget.currentSong.artist,
          album: widget.currentSong.album,
          duration: widget.currentSong.duration?.inSeconds.toString(),
        );
        _lyricsLoaded = false;
        _fetchLyricsFuture!.then((lyrics) {
          _lyricsLoaded =
              (lyrics.parsedLyrics?.lyrics.isNotEmpty ?? false) ||
                  lyrics.lyricsPlain.isNotEmpty;
          widget.onLyricsFound?.call(_lyricsLoaded);
          _updateWakelock();
        }).catchError((_) {
          _lyricsLoaded = false;
          widget.onLyricsFound?.call(false);
          _updateWakelock();
        });
      });
    }
  }

  @override
  void dispose() {
    GetIt.I<MediaPlayer>().buttonState.removeListener(_updateWakelock);
    WakelockPlus.disable();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Center(
        child: _fetchLyricsFuture != null
            ? FutureBuilder<Lyrics>(
                future: _fetchLyricsFuture,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    if (snapshot.data == null) {
                      return const Text('No Lyrics Found (Null)');
                    }
                    return LoadedLyricsWidget(lyrics: snapshot.data!);
                  }
                  if (snapshot.hasError) {
                    // return Text('Error: ${snapshot.error}');
                    return const Text('No Lyrics Found');
                  }
                  return const ExpressiveLoadingIndicator();
                },
              )
            : const ExpressiveLoadingIndicator(),
      ),
    );
  }
}

class LoadedLyricsWidget extends StatelessWidget {
  final Lyrics lyrics;
  const LoadedLyricsWidget({
    super.key,
    required this.lyrics,
  });

  @override
  Widget build(BuildContext context) {
    if ((lyrics.parsedLyrics?.lyrics.isEmpty ?? true) &&
        lyrics.lyricsPlain.isNotEmpty) {
      return PlainLyricsWidget(lyrics: lyrics);
    } else if (lyrics.parsedLyrics?.lyrics.isNotEmpty ?? false) {
      return SyncedLyricsWidget(
        lyrics: lyrics,
      );
    }
    return const Center(
      child: Text("No Lyrics found!"),
    );
  }
}

class PlainLyricsWidget extends StatelessWidget {
  final Lyrics lyrics;
  const PlainLyricsWidget({
    super.key,
    required this.lyrics,
  });

  @override
  Widget build(BuildContext context) {
    return ShaderMask(
      shaderCallback: (Rect bounds) {
        return const LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            Colors.transparent,
            Colors.white,
            Colors.white,
            Colors.transparent
          ],
          stops: [0.0, 0.08, 0.92, 1.0],
        ).createShader(bounds);
      },
      blendMode: BlendMode.dstIn,
      child: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(vertical: 60, horizontal: 24),
        child: SelectableText(
          "\n${lyrics.lyricsPlain}\n",
          textAlign: TextAlign.center,
          style: const TextStyle(
            fontSize: 34,
            fontWeight: FontWeight.w800,
            color: Colors.white,
            height: 1.5,
            letterSpacing: 0.2,
          ),
        ),
      ),
    );
  }
}

class SyncedLyricsWidget extends StatefulWidget {
  final Lyrics lyrics;
  const SyncedLyricsWidget({
    required this.lyrics,
    super.key,
  });

  @override
  State<SyncedLyricsWidget> createState() => _SyncedLyricsWidgetState();
}

class _SyncedLyricsWidgetState extends State<SyncedLyricsWidget>
    with TickerProviderStateMixin {
  StreamSubscription? _streamSubscription;
  final ItemScrollController _itemScrollController = ItemScrollController();
  final ItemPositionsListener _itemPositionsListener =
      ItemPositionsListener.create();
  Duration duration = Duration.zero;
  int _currentLyricIndex = 0;
  
  // Scrolling Logic
  bool _isUserScrolling = false;
  Timer? _userScrollTimer;
  final Duration _userScrollCooldown = const Duration(milliseconds: 1500);

  // Animation
  late AnimationController _glowController;
  late Animation<double> _glowAnimation;

  @override
  void initState() {
    super.initState();

    // Glow animation for current lyric
    _glowController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 2000), // Slower breathing
    )..repeat(reverse: true);

    _glowAnimation = Tween<double>(begin: 0.2, end: 0.9).animate(
      CurvedAnimation(parent: _glowController, curve: Curves.easeInOut),
    );

    // Verify MediaPlayer availability
    try {
      _streamSubscription =
          GetIt.I<MediaPlayer>().player.positionStream.listen((event) {
        if (mounted) {
          duration = event;
          final newIndex = _findCurrentLyricIndex();
          // Only update if the lyric index actually changed
          if (newIndex != _currentLyricIndex) {
            setState(() {
              _currentLyricIndex = newIndex;
            });
            _scrollToCurrentLyric(newIndex);
          }
        }
      });
    } catch (e) {
      print("Error attaching position stream: $e");
    }
  }

  @override
  void dispose() {
    _userScrollTimer?.cancel();
    _glowController.dispose();
    _streamSubscription?.cancel();
    super.dispose();
  }

  void _scrollToCurrentLyric(int index) {
    if (_itemScrollController.isAttached && !_isUserScrolling) {
      _itemScrollController.scrollTo(
        index: index,
        duration: const Duration(milliseconds: 700), // Smoother scroll
        curve: Curves.easeOutCubic, // Apple Music-like curve
        alignment: 0.5, // Center alignment
      );
    }
  }

  int _findCurrentLyricIndex() {
    if (widget.lyrics.parsedLyrics == null) return 0;

    final lines = widget.lyrics.parsedLyrics!.lyrics;
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].start.inMilliseconds > duration.inMilliseconds) {
        return i > 0 ? i - 1 : 0;
      }
    }
    return lines.length - 1;
  }

  bool isCurrentLyric(int index) {
    return index == _currentLyricIndex;
  }

  // Seek to the tapped lyric's timestamp
  void _seekToLyric(int index) {
    if (widget.lyrics.parsedLyrics == null) return;
    final lyric = widget.lyrics.parsedLyrics!.lyrics[index];
    GetIt.I<MediaPlayer>().player.seek(lyric.start);
    // Explicitly scroll to tapped lyric
    _scrollToCurrentLyric(index);
  }

  @override
  Widget build(BuildContext context) {
    if (widget.lyrics.parsedLyrics == null) return const SizedBox();

    return ShaderMask(
      shaderCallback: (Rect bounds) {
        return const LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            Colors.transparent,
            Colors.white,
            Colors.white,
            Colors.transparent
          ],
          stops: [0.0, 0.15, 0.85, 1.0], // Increased fade area
        ).createShader(bounds);
      },
      blendMode: BlendMode.dstIn,
      child: NotificationListener<UserScrollNotification>(
        onNotification: (notification) {
           if (notification.direction != ScrollDirection.idle) {
            // User is scrolling
            setState(() {
              _isUserScrolling = true;
            });
            _userScrollTimer?.cancel();
          } else {
             // Scroll ended (idle) - start cooldown
             _userScrollTimer?.cancel();
             _userScrollTimer = Timer(_userScrollCooldown, () {
               if (mounted) {
                 setState(() {
                   _isUserScrolling = false;
                 });
                 // Resume follow mode
                 _scrollToCurrentLyric(_currentLyricIndex);
               }
             });
          }
          return false;
        },
        child: ScrollConfiguration(
          behavior: ScrollConfiguration.of(context).copyWith(scrollbars: false),
          child: ScrollablePositionedList.builder(
            itemScrollController: _itemScrollController,
            itemPositionsListener: _itemPositionsListener,
            itemCount: widget.lyrics.parsedLyrics!.lyrics.length,
            // Huge padding to allow first and last items to be centered
            padding: EdgeInsets.symmetric(
                vertical: MediaQuery.of(context).size.height / 2.5),
            itemBuilder: (context, index) {
              final isCurrent = isCurrentLyric(index);
              final lyricText = widget.lyrics.parsedLyrics!.lyrics[index].text;
              
              // Only apply blur if not current and not too close to current (optional optimization)
              // For now, simpler is better: Opacity handle focus
              
              return GestureDetector(
                onTap: () => _seekToLyric(index),
                behavior: HitTestBehavior.opaque,
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(vertical: 16.0, horizontal: 32),
                  child: AnimatedBuilder(
                    animation: _glowAnimation,
                    builder: (context, child) {
                      return AnimatedDefaultTextStyle(
                        duration: const Duration(milliseconds: 400),
                        curve: Curves.easeOutCubic,
                        style: TextStyle(
                          fontSize: isCurrent ? 38 : 28, // Large active, small inactive
                          fontWeight:
                              isCurrent ? FontWeight.w900 : FontWeight.w600,
                          color: isCurrent
                              ? Colors.white
                              : Colors.white.withValues(alpha: 0.35), // Transparent inactive
                          height: 1.4,
                          letterSpacing: isCurrent ? 0.5 : -0.3,
                          shadows: isCurrent
                              ? [
                                  // Spicy Lyrics Glow Effect
                                  Shadow(
                                    color: Colors.white.withValues(
                                        alpha: _glowAnimation.value * 0.8),
                                    blurRadius: 15,
                                  ),
                                  Shadow(
                                    color: Colors.white.withValues(
                                        alpha: _glowAnimation.value * 0.4),
                                    blurRadius: 30,
                                  ),
                                  Shadow(
                                    color: Colors.blueAccent.withValues(
                                        alpha: _glowAnimation.value * 0.3),
                                    blurRadius: 50,
                                  ),
                                ]
                              : [],
                        ),
                        child: Text(
                          lyricText,
                          textAlign: TextAlign.center,
                        ),
                      );
                    },
                  ),
                ),
              );
            },
          ),
        ),
      ),
    );
  }
}

// AnimatedBuilder is a simpler version of AnimatedWidget for inline use
class AnimatedBuilder extends AnimatedWidget {
  final Widget Function(BuildContext context, Widget? child) builder;
  final Widget? child;

  const AnimatedBuilder({
    super.key,
    required Animation<double> animation,
    required this.builder,
    this.child,
  }) : super(listenable: animation);

  @override
  Widget build(BuildContext context) {
    return builder(context, child);
  }
}
