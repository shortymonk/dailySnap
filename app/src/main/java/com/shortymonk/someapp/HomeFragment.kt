package com.shortymonk.someapp

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
    private lateinit var snapList: List<Snap>

    companion object {
        const val PERMISSION_READ = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

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
        /*val*/ snapList = homeViewModel.getSnapList(requireContext())
        val scope = viewLifecycleOwner.lifecycleScope
        val adapter = CaptionedSnapAdapter(snapList, scope)

        snapRecycler.apply {
            setAdapter(adapter)
            setLayoutManager(layoutManager)
        }

        return view
    }

}