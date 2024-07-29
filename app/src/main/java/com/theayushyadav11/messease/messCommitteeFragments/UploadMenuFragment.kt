package com.theayushyadav11.messease.messCommitteeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.FragmentUploadMenuBinding
import com.theayushyadav11.messease.models.AprMenu
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.UploadMenuViewModel

class UploadMenuFragment : Fragment() {
    private lateinit var binding: FragmentUploadMenuBinding
    private lateinit var mess: Mess

    companion object {
        fun newInstance() = UploadMenuFragment()
    }

    private val viewModel: UploadMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        listener()
        check()

    }

    private fun initialize() {
        mess = Mess(requireContext())
    }

    fun listener() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun check() {
        viewModel.menuList.observe(viewLifecycleOwner, Observer { aprMenu ->

            set(aprMenu)

        })
    }

    fun set(menuList: List<AprMenu>) {
        binding.adder.removeAllViews()
        for (menuDetail in menuList) {
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.list_elemenr, binding.adder, false)
            view.findViewById<TextView>(R.id.Head).text = menuDetail.creater
            view.findViewById<LinearLayout>(R.id.delete).setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Alert!")
                builder.setCancelable(false)
                builder.setMessage("Are you sure you want to delete? \n This cannot be undone!.")
                builder.setPositiveButton("Ok") { dialog, which ->
                    viewModel.deleteApprove(menuDetail.id)
                    dialog.dismiss()
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()

            }
            view.findViewById<TextView>(R.id.test).text = menuDetail.date.date.toString()
            binding.adder.addView(view)

        }
    }

}
