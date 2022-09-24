package com.shortymonk.someapp

import android.net.Uri
import androidx.core.net.toUri
import java.io.File

class Snap(path: String, cacheDir: File) {
    var name = path.substringAfterLast("/")
    val fullPath = path
    private val pathHashCode = path.hashCode().toString()
    val thumbnail by lazy {
        File(cacheDir, "$pathHashCode.jpeg")
    }
    val thumbnailUri: Uri by lazy {
        thumbnail.toUri()
    }

}