package com.shortymonk.someapp

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

const val IMAGE_CACHE = "/thumbnails"

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var cacheDir: File

    companion object {
        const val PERMISSION_READ = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val orientation = requireContext().resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3

        cacheDir = File(requireContext().cacheDir.toString() + IMAGE_CACHE)
        if (!cacheDir.exists()) cacheDir.mkdir()

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val snapRecycler = view.findViewById<RecyclerView>(R.id.snap_recycler_view)
        snapRecycler.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            isNestedScrollingEnabled = false
        }
        val layoutManager = GridLayoutManager(activity, spanCount)
        val snapList = homeViewModel.getSnapList(requireContext())
        val scope = viewLifecycleOwner.lifecycleScope
        val snapContainer = SnapContainer(
            view.findViewById(R.id.snap_video_view),
            view.findViewById(R.id.snap_video_background)
        )
        val adapter = CaptionedSnapAdapter(snapList, scope, requireContext(), snapContainer)

        snapRecycler.apply {
            setAdapter(adapter)
            setLayoutManager(layoutManager)
        }

        return view
    }
}