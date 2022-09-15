package com.hfad.someapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    companion object {
        const val PERMISSION_READ = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val PERMISSION_REQUEST_CODE = 2128
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val snapRecycler = view.findViewById<RecyclerView>(R.id.snap_recycler)
        snapRecycler.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(30)
            isNestedScrollingEnabled = false
        }
        val layoutManager = GridLayoutManager(activity, 2)
        val snapList = homeViewModel.getSnapList(requireContext())
        val adapter = CaptionedSnapAdapter(snapList)

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