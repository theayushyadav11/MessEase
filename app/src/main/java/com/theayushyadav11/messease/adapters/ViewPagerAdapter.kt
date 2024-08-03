package com.theayushyadav11.messease.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.theayushyadav11.messease.messCommitteeFragments.McMsg
import com.theayushyadav11.messease.messCommitteeFragments.MsgFragment
import com.theayushyadav11.messease.messCommitteeFragments.PollsFragment


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PollsFragment()
            else -> MsgFragment()
        }

    }
}