import 'package:bloc/bloc.dart';
import 'package:Echo/ytmusic/ytmusic.dart';
import 'package:Echo/services/charts_service.dart';
import 'package:Echo/services/chart_model.dart';
import 'package:meta/meta.dart';

part 'home_state.dart';

class HomeCubit extends Cubit<HomeState> {
  final YTMusic _ytMusic;
  HomeCubit(this._ytMusic) : super(HomeLoading());

  Future<void> fetch() async {
    emit(const HomeLoading());
    try {
      final responses = await Future.wait([
        _ytMusic.browse(),
        ChartsService().getChartsWithPreviews(),
      ]);
      
      final feed = responses[0] as Map<String, dynamic>;
      final charts = responses[1] as List<ChartURL>;

      List sections = feed['sections'];
      
      // Add "Browse Charts" Section with dynamic previews
      sections.insert(0, _createChartsSection(charts));

      emit(HomeSuccess(
        chips: feed['chips'] ?? [],
        sections: sections,
        continuation: feed['continuation'],
        loadingMore: false,
      ));
    } catch (e, st) {
      emit(HomeError(e.toString(), st.toString()));
    }
  }

  Map<String, dynamic> _createChartsSection(List<ChartURL> charts) {
      return {
          'title': 'Browse Charts',
          'contents': charts.map((chart) => {
              'title': chart.title,
              'subtitle': 'Billboard Chart',
              'thumbnails': [{'url': chart.coverArt ?? 'https://www.billboard.com/wp-content/themes/vip/pmc-billboard-2021/assets/app/icons/icon-512x512.png', 'width': 500, 'height': 500}],
              'chartUrl': chart,
              'aspectRatio': 1.0, // Square cards
          }).toList(),
      };
  }
  
  // Removed _formatChartToSection as distinct chart sections are no longer needed

  Future<void> refresh() async {
    try {
      final responses = await Future.wait([
        _ytMusic.browse(),
         ChartsService().getChartsWithPreviews(),
      ]);

      final feed = responses[0] as Map<String, dynamic>;
      final charts = responses[1] as List<ChartURL>;

      List sections = feed['sections'];

      // Add "Browse Charts" Section
      sections.insert(0, _createChartsSection(charts));

      emit(HomeSuccess(
        chips: feed['chips'] ?? [],
        sections: sections,
        continuation: feed['continuation'],
        loadingMore: false,
      ));
    } catch (e, st) {
      emit(HomeError(e.toString(), st.toString()));
    }
  }

  Future<void> fetchNext() async {
    final current = state;
    if (current is! HomeSuccess) return;
    if (current.loadingMore || current.continuation == null) return;
    emit(current.copyWith(loadingMore: true));
    try {
      final feed = await _ytMusic.browseContinuation(
          additionalParams: current.continuation!);
      emit(
        HomeSuccess(
          chips: current.chips,
          sections: [...current.sections, ...feed['sections']],
          continuation: feed['continuation'],
          loadingMore: false,
        ),
      );
    } catch (e, st) {
      emit(HomeError(e.toString(), st.toString()));
    }
  }
}
