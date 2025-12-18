package com.alexmercerind.audire.repository

import com.alexmercerind.audire.models.Music

abstract class IdentifyRepository {
    abstract suspend fun identify(duration: Int, data: ByteArray): Music?
}
