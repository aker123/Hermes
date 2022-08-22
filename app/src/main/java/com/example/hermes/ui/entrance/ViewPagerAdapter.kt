package com.example.hermes.ui.entrance

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hermes.ui.authorization.AuthorizationFragment
import com.example.hermes.ui.registration.RegistrationFragment


class ViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AuthorizationFragment()
            1 -> RegistrationFragment()
            else -> AuthorizationFragment()
        }
    }

}