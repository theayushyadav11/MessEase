package com.theayushyadav11.messease.messCommitteeFragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentPollDetailsBinding
import com.theayushyadav11.messease.models.Poll
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.PollDetailsViewModel

class PollDetails : Fragment() {
    private lateinit var binding: FragmentPollDetailsBinding
    private lateinit var mess:Mess
    private lateinit var poll:Poll

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
        listeners()
    }

fun initialize()
{
    try {
        mess=Mess(requireContext())
        binding.tvQuestion.text=mess.getPollId()
        viewModel.getPoll(mess.getPollId(), onSuccess = {
            poll=it
            binding.tvQuestion.text=it.question
            binding.adder.removeAllViews()
            for(option in poll.options)
            {
                addOption(poll.uid,option)
            }





        }, onFailure = {

            mess.toast(it)
        })
    } catch (e: Exception) {

    }


}
    fun listeners()
    {
        try {
            binding.ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        } catch (e: Exception) {

        }
    }
    fun addOption(id:String,opt:String)
    {
        try {
            val option=LayoutInflater.from(requireContext()).inflate(R.layout.poll_detail_layout,binding.adder,false)
            val optTitle=option.findViewById<TextView>(R.id.tvOpt)
            val nov=option.findViewById<TextView>(R.id.nov)
            val adder=option.findViewById<LinearLayout>(R.id.adder)
            optTitle.text=opt
            viewModel.getoptionDetails(id,opt, onSuccess = { votes,names,os->
                nov.text="$votes Votes"
                adder.removeAllViews()
                for(i in 0 until names.size)
                {
                    try {
                        val usr=LayoutInflater.from(requireContext()).inflate(R.layout.name_time,adder,false)
                        val name = usr.findViewById<TextView>(R.id.name)
                        val time=usr.findViewById<TextView>(R.id.time)
                        name.text=os[i].name+"  "+os[i].email
                        time.text=os[i].time+" "+os[i].date
                        adder.addView(usr)
                    } catch (e: Exception) {

                    }

                }


            },onFailure={

            })



            binding.adder.addView(option)
        } catch (e: Exception) {

        }

    }


}