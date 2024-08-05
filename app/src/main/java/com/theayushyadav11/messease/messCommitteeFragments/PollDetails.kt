package com.theayushyadav11.messease.messCommitteeFragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theayushyadav11.messease.databinding.FragmentPollDetailsBinding
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.PollDetailsViewModel

class PollDetails : Fragment() {
    private lateinit var binding: FragmentPollDetailsBinding
    private lateinit var mess:Mess

    companion object {
        fun newInstance() = PollDetails()
    }


    private val viewModel: PollDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPollDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         initialize()
    }

fun initialize()
{
    mess=Mess(requireContext())
    binding.tvQuestion.text=mess.getPollId()
}
}