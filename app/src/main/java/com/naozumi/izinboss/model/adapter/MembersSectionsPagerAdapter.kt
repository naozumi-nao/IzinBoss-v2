package com.naozumi.izinboss.model.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.naozumi.izinboss.view.MembersListFragment

class MembersSectionsPagerAdapter internal constructor(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        val fragment = MembersListFragment()
        val bundle = Bundle()
        if (position == 0) {
            bundle.putString(MembersListFragment.ARG_TAB, MembersListFragment.TAB_EMPLOYEES)
        } else {
            bundle.putString(MembersListFragment.ARG_TAB, MembersListFragment.TAB_MANAGERS)
        }
        fragment.arguments = bundle
        return fragment
    }

    override fun getItemCount() = 2
}