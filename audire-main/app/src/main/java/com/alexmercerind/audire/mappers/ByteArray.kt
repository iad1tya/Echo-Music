package com.alexmercerind.audire.mappers

fun ByteArray.toShortArray(): ShortArray {
    val result = ShortArray(size / 2)
    for (i in 0..result.size step 2) {
        result[i / 2] = (this[i].toInt() and 0xFF or (this[i + 1].toInt() shl 8)).toShort()
    }
    return result
}
