package com.theayushyadav11.messease.fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentUploadMenuBinding
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.UploadMenuViewModel

class UploadMenuFragment : Fragment() {
    private lateinit var binding:FragmentUploadMenuBinding
    private lateinit var mess:Mess

    companion object {
        fun newInstance() = UploadMenuFragment()
    }

    private val viewModel: UploadMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentUploadMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    fun initialize()
    {
        mess=Mess(requireContext())
    }
}