package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.adapters.PollsAdapter
import com.theayushyadav11.messease.databinding.FragmentPollsBinding
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.McMenuViewModel

class PollsFragment : Fragment() {
    private lateinit var binding: FragmentPollsBinding
    private val viewModel: McMenuViewModel by viewModels()
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
        setupRecyclerView()
        getPolls()
    }

    private fun initialise() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(requireContext())
    }

    private fun setupRecyclerView() {
        pollsAdapter = PollsAdapter(pollsList, requireContext())
        binding.rv.apply {
            adapter = pollsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getPolls() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).child("polls")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pollIds = snapshot.children.mapNotNull { it.value?.toString() }
                    if (pollIds.size == 0) {
                        binding.msg.isVisible = true
                        return
                    }
                    mess.log(pollIds)
                    println(pollIds)
                    fetchPolls(pollIds)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun fetchPolls(ids: List<String>) {
        for (id in ids) {
            database.child("polls").child(id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val poll = snapshot.getValue(Poll::class.java)
                    if (poll != null) {
                        pollsList.add(poll)
                        pollsList.sortByDescending { it.comp }
                    } else {
                        println("lund lelo")
                    }
                    if (pollsList.size == ids.size) {
                        pollsAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.toString())
                }
            })
        }
    }
}
