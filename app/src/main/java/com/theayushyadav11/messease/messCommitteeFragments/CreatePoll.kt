package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentCreatePollBinding
import com.theayushyadav11.messease.models.Option
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

            var options: MutableSet<String> = mutableSetOf()



            for (i in optionList) {

                if (i.text.toString().isNotEmpty())
                    options.add(i.text.toString() + "\n")
            }
            val poll = Poll(
                database.push().key.toString(),
                auth.currentUser?.displayName.toString(),
                binding.tvQuestion.text.toString(),
                mess.getCurrentDate(),
                mess.getCurrentTimeInAmPm(),
                0,
                binding.materialSwitch.isChecked,
                options.toMutableList(),


                )
            addPoll(poll)
        }


    }

    fun addElements(edit: EditText) {
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! < 2) {
                    scrollTo += 50
                    val adding = LayoutInflater.from(requireContext())
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
                    addToUser(key)
                    findNavController().navigate(R.id.action_createPoll_to_mcMain2)
                } else {
                    mess.toast("failed to add poll!")
                }

                mess.pbDismiss()
            }
        } else {
            mess.toast("Cannot add Empty feilds!")
        }
    }

    fun addToUser(key: String) {
        database.child("Users").child(auth.currentUser?.uid.toString()).child("polls").push()
            .setValue(key)
            .addOnCompleteListener {

            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
    }
}