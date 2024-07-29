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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentMcMenuBinding
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.Menu2
import com.theayushyadav11.myapplication.models.Menu
import com.theayushyadav11.myapplication.models.Particulars
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class McMenu() : Fragment() {

    private lateinit var binding: FragmentMcMenuBinding
    val REQUEST_CODE = 1232
    private lateinit var mess:Mess
    private var texts: MutableList<MutableList<TextView>> = mutableListOf()
    private lateinit var menu: Menu2


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        assign()


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

    fun assign() {

        var l = listOf(
             view?.findViewById<TextView>(R.id.mobe),
             view?.findViewById<TextView>(R.id.tube),
             view?.findViewById<TextView>(R.id.webe),
             view?.findViewById<TextView>(R.id.thbe),
             view?.findViewById<TextView>(R.id.frbe),
             view?.findViewById<TextView>(R.id.sabe),
             view?.findViewById<TextView>(R.id.sube),
        )

        texts.add(l as MutableList<TextView>)
        l = listOf(
             view?.findViewById<TextView>(R.id.molu),
             view?.findViewById<TextView>(R.id.tulu),
             view?.findViewById<TextView>(R.id.welu),
             view?.findViewById<TextView>(R.id.thlu),
             view?.findViewById<TextView>(R.id.frlu),
             view?.findViewById<TextView>(R.id.salu),
             view?.findViewById<TextView>(R.id.sulu)
        )
        texts.add(l as MutableList<TextView>)

        l = listOf(
             view?.findViewById<TextView>(R.id.mosn),
             view?.findViewById<TextView>(R.id.tusn),
             view?.findViewById<TextView>(R.id.wesn),
             view?.findViewById<TextView>(R.id.thsn),
             view?.findViewById<TextView>(R.id.frsn),
             view?.findViewById<TextView>(R.id.sasn),
             view?.findViewById<TextView>(R.id.susn)
        )
        texts.add(l as MutableList<TextView>)

        l = listOf(
             view?.findViewById<TextView>(R.id.modi),
             view?.findViewById<TextView>(R.id.tudi),
             view?.findViewById<TextView>(R.id.wedi),
             view?.findViewById<TextView>(R.id.thdi),
             view?.findViewById<TextView>(R.id.frdi),
             view?.findViewById<TextView>(R.id.sadi),
             view?.findViewById<TextView>(R.id.sudi)
        )
        texts.add(l as MutableList<TextView>)
    }
    fun updateUi(menu: Menu2) {
        this.menu=menu
        val dd: List<List<Particulars>> = listOf(
            menu.menu.list[0],
            menu.menu.list[1],
            menu.menu.list[2],
            menu.menu.list[3],
            menu.menu.list[4],
            menu.menu.list[5],
            menu.menu.list[6],
        )
        for (i in 0 until texts.size) {
            for (j in 0 until texts[i].size) {
                try {
                    texts[i][j].text = dd[j][i].food
                } catch (e: Exception) {
                    println(e)
                }
            }
        }


    }
}