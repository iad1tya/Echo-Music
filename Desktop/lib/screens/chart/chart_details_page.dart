import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:Echo/services/chart_model.dart';
import 'package:Echo/services/charts_service.dart';
import 'package:Echo/core/widgets/section_item.dart';
import 'package:Echo/ytmusic/ytmusic.dart';
import 'package:Echo/services/media_player.dart';
import '../../services/bottom_message.dart';
import 'package:loading_indicator_m3e/loading_indicator_m3e.dart';

class ChartDetailsPage extends StatefulWidget {
  final ChartURL chartUrl;

  const ChartDetailsPage({super.key, required this.chartUrl});

  @override
  State<ChartDetailsPage> createState() => _ChartDetailsPageState();
}

class _ChartDetailsPageState extends State<ChartDetailsPage> {
  late Future<ChartModel> futureChart;

  @override
  void initState() {
    super.initState();
    // Identify if it is Spotify or Billboard based on URL or Type potentially,
    // but for now we only strictly support those two logic paths in import/charts service.
    // ChartsService has specific methods.
    if (widget.chartUrl.url.contains('spotify.com')) {
       futureChart = ChartsService().getSpotifyTop50Chart(widget.chartUrl);
    } else {
       futureChart = ChartsService().getBillboardChart(widget.chartUrl);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.chartUrl.title.replaceAll('\n', ' ')),
      ),
      body: FutureBuilder<ChartModel>(
        future: futureChart,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child:  LoadingIndicatorM3E());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.chartItems == null || snapshot.data!.chartItems!.isEmpty) {
            return const Center(child: Text('No data found'));
          }

          final items = snapshot.data!.chartItems!;
          return ListView.builder(
            itemCount: items.length,
            itemBuilder: (context, index) {
              final item = items[index];
              final mapItem = {
                  'title': item.name,
                  'subtitle': item.subtitle,
                  'thumbnails': [{'url': item.imageUrl, 'width': 500, 'height': 500}],
                  'isChart': true,
              };
              
              return SongTile(song: mapItem);
            },
          );
        },
      ),
    );
  }
}
