package com.hfad.someapp

import android.net.Uri
import androidx.core.net.toUri
import java.io.File

class Snap(path: String, cacheDir: File) {
    var name = ""
    val fullPath = path
    val pathHashCode = path.hashCode().toString()
    val thumbnail by lazy {
        File(cacheDir, path)
    }
    val thumbnailUri: Uri by lazy {
        File(cacheDir, "$pathHashCode.jpeg").toUri()
    }

}