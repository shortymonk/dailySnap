package com.hfad.someapp

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore

class Snap {

    companion object {
        fun getSnapList(context: Context): List<String> {
            val videList: HashSet<String> = HashSet()
            val projection = arrayOf(
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME
            )
            val cursor: Cursor = context.contentResolver
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)!!
            try {
                cursor.moveToFirst()
                do {
                    videList.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                } while (cursor.moveToNext())
                cursor.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return ArrayList(videList)
        }
    }
}