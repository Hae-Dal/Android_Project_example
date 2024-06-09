package com.example.android_project_example

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(StopWatchFragment(), PedometerFragment(), TimerFragment())
    private val titles = listOf("스탑워치", "만보기", "타이머")

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}