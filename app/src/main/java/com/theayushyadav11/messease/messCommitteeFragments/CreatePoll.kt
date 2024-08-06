package com.theayushyadav11.messease.messCommitteeFragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentCreatePollBinding
import com.theayushyadav11.messease.databinding.SelTargetDialogBinding
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.messease.utils.Mess
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class CreatePoll : Fragment() {

    private lateinit var binding: FragmentCreatePollBinding
    var optionList: MutableList<EditText> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var mess: Mess
    private var scrollTo = 1000

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialise()
        listeners()


    }

    private fun initialise() {
        optionList.add(binding.opt0)
        optionList.add(binding.opt1)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePollBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    fun listeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        addElements(binding.opt1)
        binding.btnPost.setOnClickListener {
            var options: MutableList<String> = mutableListOf()
            for (i in optionList) {

                if (i.text.toString().isNotEmpty())
                    options.add(i.text.toString() + "\n")
            }
            if (binding.tvQuestion.text.isNotEmpty() && options.size > 1)
                openDialog()
            else {
                mess.toast("Cannot add Empty feilds!")
            }

        }


    }

    fun addElements(edit: EditText) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! < 2) {
                    scrollTo += 50
                    val adding = LayoutInflater.from(binding.adder.context)
                        .inflate(R.layout.poll_elements, binding.adder, false)
                    val et: EditText = adding.findViewById<EditText>(R.id.opt)
                    optionList.add(et)
                    addElements(et)
                    binding.adder.addView(adding)
                    binding.scroll.scrollTo(0, scrollTo)

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    fun addPoll(poll: Poll) {

        val key = poll.uid
        if (poll.question.isNotEmpty() && poll.options.size > 0) {
            mess.addPb("Adding poll..")
            database.child("polls").child(key).setValue(poll).addOnCompleteListener {
                if (it.isSuccessful) {
                    mess.toast("Poll added Successfully.")
                } else {
                    mess.toast("failed to add poll!")
                }

                mess.pbDismiss()
            }
        } else {
            mess.toast("Cannot add Empty feilds!")
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
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

    fun send(target: String) {
        var options: MutableSet<String> = mutableSetOf()



        for (i in optionList) {

            if (i.text.toString().isNotEmpty())
                options.add(i.text.toString() + "\n")
        }
        val poll = Poll(
            database.push().key.toString(),
            auth.currentUser?.uid.toString(),
            auth.currentUser?.displayName.toString(),
            binding.tvQuestion.text.toString(),
            Date(),
            mess.getCurrentDate(),
            mess.getCurrentTimeInAmPm(),
            0,
            binding.materialSwitch.isChecked,
            options.toMutableList(),
            target


        )
        addPoll(poll)
    }
}