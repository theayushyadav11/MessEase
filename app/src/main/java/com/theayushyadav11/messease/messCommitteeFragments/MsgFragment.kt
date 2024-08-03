package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentMsgBinding
import com.theayushyadav11.messease.databinding.FragmentUploadMenuBinding


class MsgFragment : Fragment() {
    private lateinit var binding:FragmentMsgBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentMsgBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}