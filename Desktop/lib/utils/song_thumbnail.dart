
import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:Echo/utils/enhanced_image.dart';

class SongThumbnail extends StatefulWidget {
  final Map song;
  final double? dp;
  final double? width;
  final double? height;
  final FilterQuality filterQuality;
  final BoxFit? fit;
  final Widget Function(BuildContext, String, Object)? errorWidget;
  final void Function(ImageProvider)? onImageReady;

  const SongThumbnail({
    super.key,
    required this.song,
    this.dp,
    this.height,
    this.width,
    this.filterQuality = FilterQuality.high,
    this.fit,
    this.errorWidget,
    this.onImageReady,
  });

  @override
  State<SongThumbnail> createState() => _SongThumbnailState();
}

class _SongThumbnailState extends State<SongThumbnail> {
  MemoryImage? _localImageProvider;
  bool _isCheckingLocal = true;
  ImageProvider? _lastNotifiedProvider;

  @override
  void initState() {
    super.initState();
    _checkLocalThumbnail();
  }

  @override
  void didUpdateWidget(covariant SongThumbnail oldWidget) {
    super.didUpdateWidget(oldWidget);

    final oldId = oldWidget.song['videoId'] ?? '';
    final newId = widget.song['videoId'] ?? '';
    final oldPath = oldWidget.song['path'] ?? '';
    final newPath = widget.song['path'] ?? '';

    if (oldId != newId || oldPath != newPath) {
      _lastNotifiedProvider = null;
      _checkLocalThumbnail();
    }
  }

  Future<void> _checkLocalThumbnail() async {
    if (!_isCheckingLocal) setState(() => _isCheckingLocal = true);
    MemoryImage? foundImage;
    if (widget.song['status'] == "DOWNLOADED" && widget.song['path'] != null) {
      // AudioTags support removed for desktop build temporarily
    }
    if (!mounted) return;
    setState(() {
      _localImageProvider = foundImage;
      _isCheckingLocal = false;
    });
  }

  Widget _buildDisplayImage(ImageProvider provider) {
    if (widget.onImageReady != null && provider != _lastNotifiedProvider) {
      _lastNotifiedProvider = provider;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (mounted) widget.onImageReady!(provider);
      });
    }
    return RepaintBoundary(
      child: Image(
        image: provider,
        height: widget.height,
        width: widget.width,
        fit: widget.fit,
        filterQuality: widget.filterQuality,
        gaplessPlayback: true,
      ),
    );
  }

  Widget _buildCachedNetworkImage(List<String> urls, int index) {
    return RepaintBoundary(
      child: CachedNetworkImage(
        imageUrl: urls[index],
        height: widget.height,
        width: widget.width,
        fit: widget.fit,
        filterQuality: widget.filterQuality,
        imageBuilder: (context, provider) => _buildDisplayImage(provider),
        placeholder: (context, url) => SizedBox(
          height: widget.height,
          width: widget.width,
        ),
        errorWidget: (index + 1 < urls.length)
            ? (context, url, error) => _buildCachedNetworkImage(urls, index + 1)
            : widget.errorWidget,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isCheckingLocal) {
      return SizedBox(
        height: widget.height,
        width: widget.width,
      );
    }
    if (_localImageProvider != null) {
      return _buildDisplayImage(_localImageProvider!);
    }
    final String baseUrl = widget.song['thumbnails'].first['url'];
    final List<String> urls = [
      getEnhancedImage(baseUrl, dp: widget.dp, width: widget.width),
      getEnhancedImage(baseUrl, quality: 'medium'),
      getEnhancedImage(baseUrl, quality: 'low'),
    ];
    return _buildCachedNetworkImage(urls, 0);
  }
}
