import 'dart:convert';
import 'dart:developer';

import 'package:http/http.dart' as http;
import 'package:html/parser.dart' show parse;
import 'package:html/dom.dart';
import 'package:Echo/services/chart_model.dart';

class ChartsService {
 
  // Billboard Links
  static const String HOT_100 = 'https://www.billboard.com/charts/hot-100/';
  static const String BILLBOARD_200 = 'https://www.billboard.com/charts/billboard-200/';
  static const String GLOBAL_200 = 'https://www.billboard.com/charts/billboard-global-200/';
  static const String ARTIST_100 = 'https://www.billboard.com/charts/artist-100/';
  static const String STREAMING_SONGS = 'https://www.billboard.com/charts/streaming-songs/';
  static const String RADIO_SONGS = 'https://www.billboard.com/charts/radio-songs/';
  static const String DIGITAL_SONG_SALES = 'https://www.billboard.com/charts/digital-song-sales/';
  static const String POP_SONGS = 'https://www.billboard.com/charts/pop-songs/';
  static const String ADULT_CONTEMPORARY = 'https://www.billboard.com/charts/adult-contemporary/';
  static const String COUNTRY_SONGS = 'https://www.billboard.com/charts/country-songs/';
  static const String HOT_ROCK_ALTERNATIVE = 'https://www.billboard.com/charts/rock-songs/';
  static const String R_AND_B_HIP_HOP = 'https://www.billboard.com/charts/r-b-hip-hop-songs/';
  static const String LATIN_SONGS = 'https://www.billboard.com/charts/latin-songs/';
  static const String DANCE_ELECTRONIC = 'https://www.billboard.com/charts/dance-electronic-songs/';
  static const String CHRISTIAN_SONGS = 'https://www.billboard.com/charts/christian-songs/';


  // Spotify Links
  static const String SPOTIFY_TOP_50_GLOBAL = 'https://charts-spotify-com-service.spotify.com/public/v0/charts';

  List<ChartURL> getAllBillboardCharts() {
      return [
          ChartURL(title: "Spotify Top 50 Global", url: SPOTIFY_TOP_50_GLOBAL), // Added Spotify here
          ChartURL(title: "Hot 100", url: HOT_100),
          ChartURL(title: "Billboard 200", url: BILLBOARD_200),
          ChartURL(title: "Global 200", url: GLOBAL_200),
          ChartURL(title: "Artist 100", url: ARTIST_100),
          ChartURL(title: "Streaming Songs", url: STREAMING_SONGS),
          ChartURL(title: "Radio Songs", url: RADIO_SONGS),
          ChartURL(title: "Digital Sales", url: DIGITAL_SONG_SALES),
          ChartURL(title: "Pop Songs", url: POP_SONGS),
          ChartURL(title: "Adult Contemporary", url: ADULT_CONTEMPORARY),
          ChartURL(title: "Country Songs", url: COUNTRY_SONGS),
          ChartURL(title: "Rock & Alternative", url: HOT_ROCK_ALTERNATIVE),
          ChartURL(title: "R&B / Hip-Hop", url: R_AND_B_HIP_HOP),
          ChartURL(title: "Latin Songs", url: LATIN_SONGS),
          ChartURL(title: "Dance/Electronic", url: DANCE_ELECTRONIC),
          ChartURL(title: "Christian Songs", url: CHRISTIAN_SONGS),
      ];
  }

  Future<List<ChartURL>> getChartsWithPreviews() async {
      List<ChartURL> charts = getAllBillboardCharts();
      
      // We will fetch the first song's image for each chart
      // To optimize, we can limit the number of parallel requests or just do them all.
      // Given it's around 15 charts, doing them in parallel is okay but might be heavy.
      // Let's do it in parallel but handle errors gracefully.
      
      await Future.wait(charts.map((chart) async {
         try {
             ChartModel model;
             if (chart.url.contains('spotify.com')) {
                 model = await getSpotifyTop50Chart(chart);
             } else {
                 model = await getBillboardChart(chart);
             }
             
             if (model.chartItems != null && model.chartItems!.isNotEmpty) {
                 chart.coverArt = model.chartItems!.first.imageUrl;
             }
         } catch (e) {
             log("Error fetching preview for ${chart.title}: $e");
         }
      }));
      
      return charts;
  }

  Future<ChartModel> getBillboardHot100() async {
      return getBillboardChart(ChartURL(title: "Billboard Hot 100", url: HOT_100));
  }
  
  Future<ChartModel> getBillboardGlobal200() async {
      return getBillboardChart(ChartURL(title: "Billboard Global 200", url: GLOBAL_200));
  }

  Future<ChartModel> getSpotifyTop50Global() async {
      return getSpotifyTop50Chart(ChartURL(title: "Spotify Top 50 Global", url: SPOTIFY_TOP_50_GLOBAL));
  }
 

  Future<ChartModel> getBillboardChart(ChartURL url) async {
    var client = http.Client();
    try {
      var response = await client.get(Uri.parse(url.url), headers: {
        'User-Agent':
            'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'
      });

      if (response.statusCode == 200) {
        var document = parse(response.body);
        var songs =
            document.querySelectorAll('.o-chart-results-list-row-container');
        List<ChartItemModel> chartItems = [];
        for (var item in songs) {
          var row = item.querySelector('ul.o-chart-results-list-row');
          var attributes = row!.querySelectorAll('li');
          var img = attributes[1].querySelector('img');
          var title = attributes[3].querySelector('h3.c-title');
          var label = attributes[3].querySelector('span.c-label');
          var ttl = title?.text.trim();
          var lbl = label?.text.trim();

          String imgURL =
              img?.attributes['data-lazy-src'] ?? img?.attributes['src'] ?? '';
          if (imgURL.isEmpty || imgURL.contains("lazyload-fallback.gif")) {
            imgURL =
                "https://www.billboard.com/wp-content/themes/vip/pmc-billboard-2021/assets/app/icons/icon-512x512.png";
          } else {
            imgURL = imgURL.replaceAll(RegExp(r'(\d+x\d+)\.jpg$'), '180x180.jpg');
          }
          chartItems
              .add(ChartItemModel(name: ttl, imageUrl: imgURL, subtitle: lbl));
        }
        final chart = ChartModel(
            chartName: url.title,
            chartItems: chartItems,
            url: url.url,
            lastUpdated: DateTime.now());
        log('Billboard Charts: ${chart.chartItems!.length} tracks',
            name: "Billboard");
        return chart;
      } else {
        throw Exception("Failed to load page");
      }
    } catch (e) {
      log('Error while getting data from:${url.url}', name: "Billboard");
      throw Exception("Error: $e");
    } finally {
        client.close();
    }
  }

  Future<ChartModel> getSpotifyTop50Chart(ChartURL url) async {
    try {
      final response = await http.get(Uri.parse(url.url));
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        List<ChartItemModel> chartItems = [];
        for (var item in data['chartEntryViewResponses'][0]['entries']) {
          chartItems.add(ChartItemModel(
            name: item['trackMetadata']['trackName'],
            subtitle: item['trackMetadata']['artists'][0]['name'],
            imageUrl: item['trackMetadata']['displayImageUri'],
          ));
        }
        final chart = ChartModel(
          chartName: url.title,
          chartItems: chartItems,
          url: url.url,
          lastUpdated: DateTime.now(),
        );
        log('Spotify Charts: ${chart.chartItems!.length} tracks',
            name: "Spotify");
        return chart;
      } else {
        throw Exception('Failed to load chart');
      }
    } catch (e) {
      throw Exception('Something went wrong while parsing the page');
    }
  }
}
