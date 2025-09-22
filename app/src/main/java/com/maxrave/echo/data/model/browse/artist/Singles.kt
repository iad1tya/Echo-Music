package iad1tya.echo.music.data.model.browse.artist

data class Singles(
    val browseId: String,
    val params: String,
    val results: List<ResultSingle>,
)