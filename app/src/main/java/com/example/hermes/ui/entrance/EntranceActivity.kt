package com.example.hermes.ui.entrance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.databinding.EntranceActivityBinding
import com.example.hermes.domain.models.User
import com.example.hermes.domain.usecase.GetOperatorDBUseCase
import com.example.hermes.domain.usecase.GetUserDBUseCase
import com.example.hermes.domain.usecase.SaveUserUseCase
import com.example.hermes.ui.authorization.AuthorizationFragment
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.orders.OrdersActivity
import com.example.hermes.ui.registration.RegistrationFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class EntranceActivity : AppCompatActivity() {

    private var _binding: EntranceActivityBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var getUserDBUseCase: GetUserDBUseCase

    @Inject
    lateinit var getOperatorDBUseCase: GetOperatorDBUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as Hermes).appComponent?.inject(this)

        super.onCreate(savedInstanceState)
        _binding = EntranceActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        init()
        setEvents()

        val operator = getOperatorDBUseCase.execute()
        val user = getUserDBUseCase.execute()

        if (operator != null) onOrdersActivity()
        else if (user != null) onGeneralActivity()
    }

    private fun init() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
    }

    private fun setEvents() {
        TabLayoutMediator(binding.tabs, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.entrance_tab_authorization)
                }
                1 -> {
                    tab.text = getString(R.string.entrance_tab_registration)
                }
            }
        }.attach()
    }

    private fun onOrdersActivity() {
        val i = Intent()
        i.setClass(this, OrdersActivity::class.java)
        startActivity(i)
    }

    private fun onGeneralActivity() {
        val i = Intent()
        i.setClass(this, GeneralActivity::class.java)
        startActivity(i)
    }

}