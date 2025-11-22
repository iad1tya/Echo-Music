package iad1tya.echo.music.dlna

data class DLNADevice(
    val id: String,
    val name: String,
    val location: String,
    val controlUrl: String,
    val eventSubUrl: String,
    val manufacturer: String = "Unknown",
    val modelName: String = "Unknown",
    val isConnected: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DLNADevice) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
