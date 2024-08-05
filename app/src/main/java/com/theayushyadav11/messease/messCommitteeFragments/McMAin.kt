package com.theayushyadav11.messease.messCommitteeFragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.activities.MainActivity
import com.theayushyadav11.messease.activities.MenuMaking
import com.theayushyadav11.messease.adapters.ViewPagerAdapter
import com.theayushyadav11.messease.databinding.FragmentMcMainBinding
import com.theayushyadav11.messease.utils.FireBase
import com.theayushyadav11.messease.utils.Mess


class McMAin : Fragment() {

    private lateinit var binding: FragmentMcMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var mess: Mess

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMcMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialise()
        listeners()


    }

    fun initialise() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        mess = Mess(requireContext())
        FireBase().getDetails(auth.currentUser?.uid.toString()) { name, designation ->
            binding.tvname.text = name
            binding.tvDesignation.text = designation
        }
        binding.tvname.text = auth.currentUser?.displayName
        binding.tvEmail.text = auth.currentUser?.email

        try {
            val v = ((auth.currentUser?.email.toString().substring(3, 7).toInt() + 4))
            binding.tvYear.text = " Batch - $v"
        } catch (e: Exception) {
            binding.tvYear.text = "Batch - 2027"
        }

        loadImage(auth.currentUser?.photoUrl)
        setTab()
    }

    fun listeners() {
        binding.ivBack.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        binding.btnPoll.setOnClickListener {
            findNavController().navigate(R.id.action_mcMain2_to_createPoll)
        }
        binding.editMenu.setOnClickListener {
            startActivity(Intent(requireContext(), MenuMaking::class.java))
        }
        binding.createMsg.setOnClickListener {
            findNavController().navigate(R.id.action_mcMain2_to_msgFragment)
        }

        binding.uploadMenu.setOnClickListener {
            mess.disign.observe(requireActivity(), Observer {
                if (it.equals("Coordinator")) {
                    findNavController().navigate(R.id.action_mcMain2_to_uploadMenuFragment)
                } else {
                    mess.toast("Only Coordinator can upload Menu")
                }
            })
        }

    }


    private fun loadImage(imageUri: Uri?) {
        imageUri?.let {
            Glide.with(this)
                .load(it) // Optional error drawable
                .into(binding.ivUser)
        }
    }



    fun setTab() {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Polls"
                }

                1 -> tab.text = "Messages"
            }
        }.attach()
    }


}
