package com.theayushyadav11.messease.messCommitteeFragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.theayushyadav11.messease.databinding.FragmentMcMenuBinding
import com.theayushyadav11.messease.utils.Mess
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class McMenu : Fragment() {

    private lateinit var binding: FragmentMcMenuBinding
    val REQUEST_CODE = 1232
    private lateinit var mess:Mess


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        askPermissions()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding=FragmentMcMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    fun initialise(){
mess=Mess(requireContext())
    }
    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }
fun listeners()
{
    binding.menu.setOnClickListener {
        Toast.makeText(requireContext(), "maneesh ki ma ki choot", Toast.LENGTH_SHORT).show()
    }
}


}