package com.hfad.someapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment : Fragment() {

    companion object {
        const val PERMISSION_STRING = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val PERMISSION_REQUEST_CODE = 2128
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val snapRecycler = view.findViewById<RecyclerView>(R.id.snap_recycler)
        val layoutManager = GridLayoutManager(activity, 2)
        val snapList = Snap.getSnapList(requireContext())

        val adapter = CaptionedSnapAdapter(snapList.toList())

        snapRecycler.apply {
            setAdapter(adapter)
            setLayoutManager(layoutManager)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(requireContext(), PERMISSION_STRING)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(PERMISSION_STRING),
                PERMISSION_REQUEST_CODE)
        }
    }
}