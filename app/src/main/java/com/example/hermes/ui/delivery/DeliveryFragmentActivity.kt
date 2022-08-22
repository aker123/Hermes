package com.example.hermes.ui.delivery

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.DeliveryFragmentActivityBinding
import com.example.hermes.domain.models.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import java.util.*

class DeliveryFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_ORDER_PRODUCTS = "ARGUMENT_ORDER_PRODUCTS"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }


    private var _binding: DeliveryFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryViewModel by viewModels()

    private var orderProducts: OrderProducts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DeliveryFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        initProperty()
        orderProducts = arguments[ARGUMENT_ORDER_PRODUCTS] as OrderProducts

        binding.onSendOrder.setOnClickListener {
            if (orderProducts == null) return@setOnClickListener
            if (orderProducts!!.products == null) return@setOnClickListener
            val delivery = Delivery(
                UUID.randomUUID().toString(),
                binding.street.name.text.toString(),
                binding.entrance.name.text.toString(),
                binding.floor.name.text.toString().toLong(),
                binding.numberApartment.name.text.toString(),
                binding.intercom.name.text.toString(),
            )

            val amount = orderProducts?.products?.sumOf { it.amount } ?: return@setOnClickListener
            val quantity =
                orderProducts?.products?.sumOf { it.quantity } ?: return@setOnClickListener

            val order = Order(
                UUID.randomUUID().toString(),
                amount,
                quantity,
                orderProducts!!.shop,
                orderProducts!!.user,
                delivery,
                orderProducts!!.products!!,
                binding.comment.name.text.toString()
            )

            viewModel.setEvent(DeliveryContract.Event.OnClickSendOrder(order))
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    DeliveryContract.State.Default -> {}
                    DeliveryContract.State.Setting -> toStateSetting()
                    DeliveryContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is DeliveryContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                }
            }
        }
    }

    private fun initProperty() {
        binding.street.title.text = getString(R.string.delivery_street_name_title)
        binding.street.light.isVisible = false

        binding.entrance.title.text = getString(R.string.delivery_entrance_name_title)
        binding.entrance.light.isVisible = false

        binding.numberApartment.title.text =
            getString(R.string.delivery_number_apartment_name_title)
        binding.numberApartment.light.isVisible = false

        binding.intercom.title.text = getString(R.string.delivery_intercom_apartment_name_title)
        binding.intercom.light.isVisible = false

        binding.floor.title.text = getString(R.string.delivery_floor_apartment_name_title)
        binding.floor.light.isVisible = false

        binding.comment.title.text = getString(R.string.delivery_comment_apartment_name_title)
        binding.comment.light.isVisible = false
    }

    private fun toStateSetting() {
        binding.street.root.isVisible = true
        binding.entrance.root.isVisible = true
        binding.intercom.root.isVisible = true
        binding.numberApartment.root.isVisible = true
        binding.floor.root.isVisible = true
        binding.comment.root.isVisible = true
        binding.load.isVisible = true
    }

    private fun toStateLoading() {
        binding.street.root.isVisible = false
        binding.entrance.root.isVisible = false
        binding.intercom.root.isVisible = false
        binding.numberApartment.root.isVisible = false
        binding.floor.root.isVisible = false
        binding.comment.root.isVisible = false
        binding.load.isVisible = true
    }

    private fun showMessage(messageId: Int) {
        Snackbar
            .make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}