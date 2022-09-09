package com.hfad.someapp

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getSnapList(appContext: Context): List<String> {
        val videList: HashSet<String> = HashSet()
        val projection = arrayOf(
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.Media.DISPLAY_NAME
        )

//        val size = Size(200, 200)
//        val cs = CancellationSignal()
//        val directory = appContext.getDir("thumbnails", Context.MODE_PRIVATE)

        val cursor: Cursor = appContext.contentResolver
            .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)!!
        try {
            cursor.moveToFirst()
            do {
                val fullPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
//                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
//                val thumbnail = File(directory, "$name.jpeg")
//
//                if (!thumbnail.exists()) {
//                    try {
//                        val imageOut = FileOutputStream(thumbnail)
//                        val contentResolver = appContext.contentResolver
//                        val bitmap = contentResolver.loadThumbnail(fullPath.toUri(), size, cs)
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageOut)
//                        imageOut.flush()
//                        imageOut.close()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }

                videList.add(fullPath)
            } while (cursor.moveToNext())
            cursor.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ArrayList(videList)
    }
}