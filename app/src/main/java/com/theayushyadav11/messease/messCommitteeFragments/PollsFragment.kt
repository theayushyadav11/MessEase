package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.adapters.PollsAdapter
import com.theayushyadav11.messease.databinding.FragmentPollsBinding
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.PollsFragmentViewModel

class PollsFragment : Fragment() {
    private lateinit var binding: FragmentPollsBinding
    private val viewModel: PollsFragmentViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var mess: Mess
    private val pollsList = mutableListOf<Poll>()
    private lateinit var pollsAdapter: PollsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPollsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        getPolls()


    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(requireContext())

    }

    fun getPolls() {
        database.child("polls").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pollsList.clear()
                for (data in snapshot.children) {
                    val poll = data.getValue(Poll::class.java)
                    if (poll != null) {
                        if (poll.creater == auth.currentUser?.uid) {
                            pollsList.add(poll)
                        }
                    }
                }
                if (pollsList.isEmpty()) {
                    binding.msg.isVisible = true
                } else {
                    binding.msg.isVisible = false
                }
                pollsList.sortByDescending { it.comp }
                binding.adder.removeAllViews()
                for (poll in pollsList) {
                    addPoll(poll)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                mess.toast(error.message)
            }
        })
    }


    fun addPoll(poll: Poll) {

        mess.log("added again")


        val pollLayout = LayoutInflater.from(requireActivity())
            .inflate(R.layout.poll_layout, binding.adder, false)
        val question = pollLayout.findViewById<TextView>(R.id.tvQuestion)
        val creator = pollLayout.findViewById<TextView>(R.id.tvname)
        val time = pollLayout.findViewById<TextView>(R.id.time)
        val delete = pollLayout.findViewById<ImageView>(R.id.delete)
        val viewVotes = pollLayout.findViewById<TextView>(R.id.vw)
        val optionAdder = pollLayout.findViewById<LinearLayout>(R.id.radioGroup)
        question.text = poll.question
        creator.text = poll.createrName
        time.text = poll.time
        delete.setOnClickListener {
            deletePoll(poll.uid, pollLayout)
        }
        viewVotes.setOnClickListener {
            mess.sendPollId(poll.uid)
            findNavController().navigate(R.id.action_mcMain2_to_pollDetails)
        }
        addOptins(optionAdder, poll.options, poll.uid)
        mess.log("added again")
        binding.adder.addView(pollLayout)

    }

    private fun addOptins(optionAdder: LinearLayout, options: MutableList<String>, uid: String) {
        for (option in options) {
            val optionLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.poll_layout_element, optionAdder, false)
            val rb = optionLayout.findViewById<TextView>(R.id.rb)
            val title = optionLayout.findViewById<TextView>(R.id.title)
            val progressBar = optionLayout.findViewById<ProgressBar>(R.id.ProgressBar)
            val nop = optionLayout.findViewById<TextView>(R.id.nop)
            rb.visibility = View.INVISIBLE
            title.text = option
            viewModel.getoptCount(uid, option, onSuccess = { count, totalVotes ->
                var progress = 0
                if (totalVotes > 0) {
                    progress = ((count) * 100) / totalVotes
                }
                progressBar.progress = progress
                nop.text = count.toString()
            })
            optionAdder.addView(optionLayout)

        }
    }

    private fun deletePoll(uid: String, layout: View) {

        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Alert")
        dialog.setMessage("Are you sure you want to delete this poll?")
        dialog.setPositiveButton("Yes") { dialog, _ ->
            mess.addPb("Deleting poll...")
            database.child("polls").child(uid).removeValue().addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    database.child("pollResult").child(uid).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            mess.pbDismiss()
                            mess.toast("Poll deleted")
                            binding.adder.removeView(layout)
                        } else {
                            mess.pbDismiss()
                            mess.toast(it.exception?.message.toString())
                        }
                    }
                } else {
                    mess.pbDismiss()
                    mess.toast(it.exception?.message.toString())
                }
            }
        }
        dialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()


    }
}
