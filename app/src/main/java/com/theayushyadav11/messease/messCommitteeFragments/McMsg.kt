package com.theayushyadav11.messease.messCommitteeFragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentMsgBinding
import com.theayushyadav11.messease.databinding.SelTargetDialogBinding
import com.theayushyadav11.messease.models.Msg
import com.theayushyadav11.messease.utils.FireBase
import com.theayushyadav11.messease.utils.Mess
import java.util.Date

class McMsg : Fragment() {
    val PICK_IMAGE_REQUEST = 1
    private lateinit var binding: FragmentMsgBinding
    val auth = FirebaseAuth.getInstance()
    val dataBase = FirebaseDatabase.getInstance().reference
    val listOfImages: MutableList<Uri> = mutableListOf()
    var noi = 0
   private lateinit var mess: Mess
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMsgBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        listeners()

    }

    private fun listeners() {
        binding.addImage.setOnClickListener {
            openFileChooser()
        }
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnPost.setOnClickListener{
            openDialog()
        }
    }

    private fun initialise() {
 mess=Mess(requireContext())
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data

            if (noi < 3) {
                val view = LayoutInflater.from(context).inflate(R.layout.img, binding.adder, false)
                val img = view.findViewById<ImageView>(R.id.img)



                Glide.with(this)
                    .load(imageUri)
                    .into(img)

                binding.adder.addView(view)
                if (imageUri != null) {
                    listOfImages.add(imageUri)
                }
                view.setOnLongClickListener {
                    binding.adder.removeView(view)
                    listOfImages.remove(imageUri)
                    binding.addImage.visibility = View.VISIBLE
                    noi--
                    true
                }
                noi++;

            }
            if (noi == 3) {
                binding.addImage.visibility = View.GONE
            }


        }
    }

    fun send(target: String) {
        val uid = dataBase.push().key.toString()
        val creater = auth.currentUser?.displayName.toString()
        val time = mess.getCurrentTimeInAmPm()
        val date = mess.getCurrentDate()
        val title = binding.tvQuestion.text.toString()
        val body = binding.tvBody.text.toString()
        if (title.isNotEmpty() && body.isNotEmpty()) {
            mess.addPb("Posting message...")
            FireBase().uploadImages(uid,listOfImages, onSuccess = {
                val photos = it
                val msg = Msg(uid, creater, time, date, Date(), title, body, photos, target)
                FireBase().addMsg(msg, onSuccess = {
                    mess.toast("Message Sent")
                    try {
                        findNavController().navigateUp()
                        mess.pbDismiss()
                    } catch (e: Exception) {
                        mess.pbDismiss()
                    }
                },
                    onFailure = {
                        mess.toast(it.message.toString())
                        mess.pbDismiss()
                    })
            }, onFailure = {
                mess.toast(it.message.toString())
                mess.pbDismiss()
            })
        } else {
            mess.toast("Parameters cannot be empty!")
        }
    }

    fun openDialog() {
        val dialog = Dialog(requireContext())
        val bind = SelTargetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bind.root)
        dialog.setCancelable(false)
        val c = (Date().year + 1900)
        val batches = listOf(c, c + 1, c + 2, c + 3, c + 4, c + 5)
        val rbList: List<RadioButton> = listOf(
            bind.rb0,
            bind.rb1,
            bind.rb2,
            bind.rb3,
            bind.rb4,
            bind.rb5,
            bind.rbGirl,
            bind.rbBoy,
            bind.rbBtech,
            bind.rbMtech,
            bind.rbMba,
            bind.rbMsc
        )

        for (i in 0 until batches.size) {
            rbList[i].setText("Batch - ${batches[i]}")
        }
        bind.cb.setOnCheckedChangeListener { buttonView, isChecked ->
            for (i in rbList) {
                i.isChecked = isChecked
            }
        }
        bind.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        bind.btnAddPoll.setOnClickListener {
            var target = ""
            for (i in rbList) {
                if (i.isChecked) {
                    target += i.text
                }
            }
            var yearSelected = false
            var genderSelected = false
            var batchSelected = false
            for (i in 0 until rbList.size) {
                if (i < 6 && rbList[i].isChecked)
                    yearSelected = true
                if (i > 5 && i < 8 && rbList[i].isChecked)
                    genderSelected = true
                if (i > 7 && rbList[i].isChecked)
                    batchSelected = true
            }
            if (!yearSelected) {
                mess.toast("Please Select a year")
            } else if (!genderSelected) {
                mess.toast("Please Select gender")
            } else if (!batchSelected) {
                mess.toast("Please Select a batch")
            } else {
                send(target)
                dialog.dismiss()
                findNavController().navigateUp()
            }
        }

        dialog.show()


    }
}