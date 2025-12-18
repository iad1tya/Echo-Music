package com.alexmercerind.audire.native

// This class provides JNI binding to Shazam's signature algorithm.
//
// ShazamSignature.create takes audio samples as ShortArray.
// Format: PCM 16 Bit LE
// Sample Rate: 16000 Hz
//
// References:
// https://github.com/marin-m/SongRec
// https://github.com/alexmercerind/shazam-signature-jni
class ShazamSignature {
    init {
        System.loadLibrary("shazam_signature_jni")
    }

    external fun create(input: ShortArray): String
}
