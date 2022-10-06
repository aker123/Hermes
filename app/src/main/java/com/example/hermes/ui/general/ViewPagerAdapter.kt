package com.example.hermes.ui.general

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hermes.ui.authorization.AuthorizationFragment
import com.example.hermes.ui.basket.BasketFragment
import com.example.hermes.ui.orderHistory.OrderHistoryFragment
import com.example.hermes.ui.profile.ProfileFragment
import com.example.hermes.ui.shops.ShopsFragment

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShopsFragment()
            1 -> BasketFragment()
            2 -> OrderHistoryFragment()
            3 -> ProfileFragment()
            else -> ShopsFragment()
        }
    }

}