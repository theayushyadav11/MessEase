package com.theayushyadav11.messease.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityMenuMakingBinding
import com.theayushyadav11.messease.databinding.EditDialogBinding
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.DayMenu
import com.theayushyadav11.myapplication.models.Menu
import com.theayushyadav11.myapplication.models.Particulars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MenuMaking : AppCompatActivity() {
    val REQUEST_CODE = 1232
    private lateinit var binding: ActivityMenuMakingBinding
    private lateinit var mess: Mess
    private var texts: MutableList<MutableList<TextView>> = mutableListOf()
    private var isClicked = false
    private lateinit var currentMenu:Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuMakingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mess = Mess(this)
        askPermissions()
        assign()
        listeners()
        getData()
    }

    private fun askPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE
            )
        } else {
            setupNextButtonListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {


                setupNextButtonListener()
            }

        isClicked = false
    }

    private fun setupNextButtonListener() {

        binding.next.setOnClickListener {
            binding.next.isVisible = false
            convertXmlToPdf()
            binding.next.isVisible = true
            val database = MenuDatabase.getDatabase(this).menuDao()
            lifecycleScope.launch(Dispatchers.IO) {
                database.addMenu(getEditedMenu(currentMenu))
            }
            startActivity(Intent(this@MenuMaking, EditComplete::class.java))
        }
    }

    private fun convertXmlToPdf() {
        val horizontalScrollView = binding.root
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        horizontalScrollView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        horizontalScrollView.layout(
            0,
            0,
            horizontalScrollView.measuredWidth,
            horizontalScrollView.measuredHeight
        )

        val document = PdfDocument()
        val viewWidth = horizontalScrollView.measuredWidth
        val viewHeight = horizontalScrollView.measuredHeight
        val pageInfo = PdfDocument.PageInfo.Builder(viewWidth, viewHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        horizontalScrollView.draw(canvas)
        document.finishPage(page)

        val fileName = "Mess Menu.pdf"
        val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        Log.d("PDF", "File path: ${filePath.absolutePath}")

        try {
            if (filePath.exists()) {
                filePath.delete()
            }
            if (filePath.parentFile?.exists() == false) {
                filePath.parentFile?.mkdirs()
            }
            val fos = FileOutputStream(filePath)
            document.writeTo(fos)
            document.close()
            fos.close()
            Toast.makeText(this, "PDF Conversion Successful", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.d("PDF", "Error while writing: $e")
            e.printStackTrace()
        }
    }

    private fun openPDF() {
        val fileName = "Mess Menu.pdf"
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        if (file.exists()) {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "com.theayushyadav11.messease.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.d("PDF", e.toString())
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun assign() {
        var l = listOf(
            binding.mobe,
            binding.tube,
            binding.webe,
            binding.thbe,
            binding.frbe,
            binding.sabe,
            binding.sube
        )
        texts.add(l.toMutableList())

        l = listOf(
            binding.molu,
            binding.tulu,
            binding.welu,
            binding.thlu,
            binding.frlu,
            binding.salu,
            binding.sulu
        )
        texts.add(l.toMutableList())

        l = listOf(
            binding.mosn,
            binding.tusn,
            binding.wesn,
            binding.thsn,
            binding.frsn,
            binding.sasn,
            binding.susn
        )
        texts.add(l.toMutableList())

        l = listOf(
            binding.modi,
            binding.tudi,
            binding.wedi,
            binding.thdi,
            binding.frdi,
            binding.sadi,
            binding.sudi
        )
        texts.add(l.toMutableList())
    }

    private fun listeners() {
        for (i in 0 until texts.size) {
            for (j in 0 until texts[i].size) {
                texts[i][j].setOnLongClickListener {
                    val dialog = Dialog(this@MenuMaking)
                    val bind = EditDialogBinding.inflate(layoutInflater)

                    dialog.setContentView(bind.root)
                    dialog.setCancelable(false)
                    dialog.show()
                    bind.etUpdate.setText(texts[i][j].text.toString())
                    bind.cancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    bind.done.setOnClickListener {
                        if (bind.etUpdate.text.toString().trim().isNotEmpty()) {
                            texts[i][j].text = bind.etUpdate.text.toString().trim()
                            dialog.dismiss()
                        } else {
                            mess.toast("Cannot add empty item.")
                        }
                    }
                    true
                }
            }
        }
    }

    private fun getData() {
        mess.addPb("Loading menu...")
        val database = MenuDatabase.getDatabase(this)
        val dao = database.menuDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val menu = dao.getMenu()
            withContext(Dispatchers.Main) {
                currentMenu=menu
                updateUi(menu)
            }
        }
    }

    private fun updateUi(menu: Menu) {
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
                    mess.log("error")
                }
            }
        }
        mess.pbDismiss()


    }

    fun getEditedMenu(menu: Menu): Menu {
        val dd: List<List<Particulars>> = listOf(
            menu.menu.list[0],
            menu.menu.list[1],
            menu.menu.list[2],
            menu.menu.list[3],
            menu.menu.list[4],
            menu.menu.list[5],
            menu.menu.list[6],

        )
        var editedMenu: Menu
        var dayMenu: MutableList<MutableList<Particulars>> = mutableListOf()
        for (i in 0 until texts[0].size) {
            var list: MutableList<Particulars> = mutableListOf()
            for (j in 0 until texts.size) {
                try {
                    list.add(
                        Particulars(
                            dd[i][j].foodType,
                            texts[j][i].text.toString(),
                            dd[i][j].timing
                        )
                    )

                    mess.log(dd[i][j].foodType + "    " + texts[j][i].text.toString() + " " + dd[i][j].timing + "  \n")
                } catch (e: Exception) {
                    mess.log("error")
                }
            }
            dayMenu.add(list)
            mess.log("space")
        }
        editedMenu = Menu("edited", DayMenu(listOf( dayMenu[0], dayMenu[1], dayMenu[2], dayMenu[3], dayMenu[4], dayMenu[5], dayMenu[6])))


        return editedMenu
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert!")
        builder.setCancelable(false)
        builder.setMessage("Are you sure you want to exit? Your edited data will be lost.")
        builder.setPositiveButton("Ok") { dialog, which ->
            super.onBackPressed()
            finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}
