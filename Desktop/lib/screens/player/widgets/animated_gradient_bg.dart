import 'dart:math' as math;
import 'package:flutter/material.dart';

/// Apple Music-style animated gradient background
/// Uses multiple colors extracted from album art and animates them smoothly
class AnimatedGradientBackground extends StatefulWidget {
  final List<Color> colors;
  final Duration animationDuration;

  const AnimatedGradientBackground({
    super.key,
    required this.colors,
    this.animationDuration = const Duration(seconds: 8),
  });

  @override
  State<AnimatedGradientBackground> createState() =>
      _AnimatedGradientBackgroundState();
}

class _AnimatedGradientBackgroundState extends State<AnimatedGradientBackground>
    with TickerProviderStateMixin {
  late AnimationController _controller;
  late AnimationController _colorController;

  List<Color> _currentColors = [];
  List<Color> _targetColors = [];

  @override
  void initState() {
    super.initState();
    _currentColors = _getDefaultColors();
    _targetColors = _currentColors;

    // Main rotation animation
    _controller = AnimationController(
      duration: widget.animationDuration,
      vsync: this,
    )..repeat();

    // Color transition animation
    _colorController = AnimationController(
      duration: const Duration(milliseconds: 800),
      vsync: this,
    );
  }

  List<Color> _getDefaultColors() {
    return [
      Colors.deepPurple.shade900,
      Colors.purple.shade800,
      Colors.indigo.shade900,
      Colors.black,
    ];
  }

  @override
  void didUpdateWidget(AnimatedGradientBackground oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.colors != oldWidget.colors && widget.colors.isNotEmpty) {
      _currentColors = _targetColors;
      _targetColors = _expandColors(widget.colors);
      _colorController.forward(from: 0);
    }
  }

  // Expand the color palette to have enough colors for the gradient
  List<Color> _expandColors(List<Color> colors) {
    if (colors.isEmpty) return _getDefaultColors();

    List<Color> expanded = [];

    // Take up to 4 colors and create variations
    for (int i = 0; i < math.min(colors.length, 4); i++) {
      expanded.add(colors[i]);
    }

    // If we have less than 4 colors, create darker/lighter variations
    while (expanded.length < 4) {
      final baseColor = colors[expanded.length % colors.length];
      final hsl = HSLColor.fromColor(baseColor);
      expanded.add(
          hsl.withLightness((hsl.lightness * 0.6).clamp(0.0, 1.0)).toColor());
    }

    return expanded;
  }

  @override
  void dispose() {
    _controller.dispose();
    _colorController.dispose();
    super.dispose();
  }

  Color _lerpColor(Color a, Color b, double t) {
    return Color.lerp(a, b, t) ?? a;
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: Listenable.merge([_controller, _colorController]),
      builder: (context, child) {
        // Interpolate colors during transition
        final t = _colorController.value;
        final colors = List.generate(
          4,
          (i) => _lerpColor(
            i < _currentColors.length ? _currentColors[i] : Colors.black,
            i < _targetColors.length ? _targetColors[i] : Colors.black,
            t,
          ),
        );

        return CustomPaint(
          painter: _GradientPainter(
            colors: colors,
            animation: _controller.value,
          ),
          size: Size.infinite,
        );
      },
    );
  }
}

class _GradientPainter extends CustomPainter {
  final List<Color> colors;
  final double animation;

  _GradientPainter({
    required this.colors,
    required this.animation,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final rect = Offset.zero & size;

    // Create multiple animated gradient blobs
    _drawGradientBlob(
      canvas,
      size,
      colors[0].withOpacity(0.8),
      Offset(
        size.width * (0.2 + 0.3 * math.sin(animation * 2 * math.pi)),
        size.height * (0.2 + 0.2 * math.cos(animation * 2 * math.pi)),
      ),
      size.width * 0.8,
    );

    _drawGradientBlob(
      canvas,
      size,
      colors[1].withOpacity(0.7),
      Offset(
        size.width * (0.8 - 0.3 * math.cos(animation * 2 * math.pi + 1)),
        size.height * (0.3 + 0.25 * math.sin(animation * 2 * math.pi + 1)),
      ),
      size.width * 0.7,
    );

    _drawGradientBlob(
      canvas,
      size,
      colors[2].withOpacity(0.6),
      Offset(
        size.width * (0.5 + 0.35 * math.sin(animation * 2 * math.pi + 2)),
        size.height * (0.7 + 0.2 * math.cos(animation * 2 * math.pi + 2)),
      ),
      size.width * 0.9,
    );

    _drawGradientBlob(
      canvas,
      size,
      colors[3].withOpacity(0.5),
      Offset(
        size.width * (0.3 - 0.2 * math.cos(animation * 2 * math.pi + 3)),
        size.height * (0.8 + 0.15 * math.sin(animation * 2 * math.pi + 3)),
      ),
      size.width * 0.6,
    );

    // Add a subtle dark overlay at the bottom for readability
    final overlayPaint = Paint()
      ..shader = LinearGradient(
        begin: Alignment.topCenter,
        end: Alignment.bottomCenter,
        colors: [
          Colors.transparent,
          Colors.black.withOpacity(0.3),
          Colors.black.withOpacity(0.6),
        ],
        stops: const [0.0, 0.6, 1.0],
      ).createShader(rect);

    canvas.drawRect(rect, overlayPaint);
  }

  void _drawGradientBlob(
    Canvas canvas,
    Size size,
    Color color,
    Offset center,
    double radius,
  ) {
    final paint = Paint()
      ..shader = RadialGradient(
        colors: [
          color,
          color.withOpacity(0.0),
        ],
        stops: const [0.0, 1.0],
      ).createShader(
        Rect.fromCircle(center: center, radius: radius),
      )
      ..blendMode = BlendMode.plus;

    canvas.drawCircle(center, radius, paint);
  }

  @override
  bool shouldRepaint(_GradientPainter oldDelegate) {
    return oldDelegate.animation != animation || oldDelegate.colors != colors;
  }
}
