package com.example.hermes.ui.entrance

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.application.Hermes
import com.example.hermes.databinding.EntranceActivityBinding
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.usecase.get.GetOperatorDBUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.ui.basket.BasketViewModel
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.orders.OrdersActivity
import com.example.hermes.ui.orders.OrdersContract
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class EntranceActivity : AppCompatActivity() {

    private var _binding: EntranceActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EntranceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = EntranceActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()
        setEvents()

        viewModel.setEvent(EntranceContract.Event.CheckProfile)
    }

    private fun init() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    EntranceContract.State.Default -> {}
                    EntranceContract.State.Setting -> toStateSetting()
                    EntranceContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is EntranceContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is EntranceContract.Effect.OnOrdersActivity -> onOrdersActivity()
                    is EntranceContract.Effect.OnGeneralActivity -> onGeneralActivity()
                }
            }
        }
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

    private fun toStateSetting() {
        binding.load.isVisible = false
    }

    private fun toStateLoading() {
        binding.load.isVisible = true
    }

    private fun showMessage(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showMessage(message: Int) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
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