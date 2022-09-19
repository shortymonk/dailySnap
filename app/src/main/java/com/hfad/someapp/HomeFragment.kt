package com.hfad.someapp

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

const val IMAGE_CACHE = "/thumbnails"

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var cacheDir: File

    companion object {
        const val PERMISSION_READ = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val PERMISSION_REQUEST_CODE = 2128
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        cacheDir = File(requireContext().cacheDir.toString() + IMAGE_CACHE)
        if (!cacheDir.exists()) cacheDir.mkdir()

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val snapRecycler = view.findViewById<RecyclerView>(R.id.snap_recycler)
        snapRecycler.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            isNestedScrollingEnabled = false
        }
        val layoutManager = GridLayoutManager(activity, 2)
        val snapList = homeViewModel.getSnapList(requireContext())
        val asyncLoader = viewLifecycleOwner.lifecycleScope
        val adapter = CaptionedSnapAdapter(snapList, cacheDir, asyncLoader)

        snapRecycler.apply {
            setAdapter(adapter)
            setLayoutManager(layoutManager)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val isReadGranted = ContextCompat.checkSelfPermission(requireContext(), PERMISSION_READ) !=
                PackageManager.PERMISSION_GRANTED
        if (isReadGranted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(PERMISSION_READ),
                PERMISSION_REQUEST_CODE
            )
        }
    }
}