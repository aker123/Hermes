package com.example.hermes.ui.delivery

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.hermes.databinding.DeliveryFragmentActivityBinding
import com.example.hermes.domain.Map
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.Client
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.map.MapDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class DeliveryFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_ORDER_PRODUCTS = "ARGUMENT_ORDER_PRODUCTS"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: DeliveryFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryViewModel by viewModels()
    lateinit var map: Map

    private var address: Address? = null
    private var orderProducts: OrderProducts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DeliveryFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        orderProducts = arguments[ARGUMENT_ORDER_PRODUCTS] as OrderProducts

        viewModel.getAddressActive()?.let {
            address = it
            setProperty(it)
        }

        binding.onMap.setOnClickListener {
            val dialog = MapDialogFragment()
            dialog.onBackListener = MapDialogFragment.OnBackListener { address ->
                setProperty(address)
            }
            dialog.show(supportFragmentManager, "MapFragmentActivity")
        }


        binding.onSendOrder.setOnClickListener {
            if (orderProducts == null) return@setOnClickListener
            if (orderProducts!!.products == null) return@setOnClickListener
            val address = Address(
                UUID.randomUUID().toString(),
                binding.street.editText?.text.toString(),
                binding.entrance.editText?.text.toString(),
                binding.floor.editText?.text.toString().toLong(),
                binding.numberApartment.editText?.text.toString(),
                binding.intercom.editText?.text.toString(),
                false
            )

            val amount = orderProducts?.products?.sumOf { it.amount } ?: return@setOnClickListener
            val quantity =
                orderProducts?.products?.sumOf { it.quantity } ?: return@setOnClickListener

            val client = Client(
                orderProducts!!.user.uid,
                orderProducts!!.user.surname,
                orderProducts!!.user.name,
                orderProducts!!.user.phoneNumber,
                orderProducts!!.user.mail
            )

            val number = Random().nextInt(1000000 - 99999) + 99999

            val dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault())
            val date = dateFormat.format(Date()).toString()

            val order = Order(
                UUID.randomUUID().toString(),
                number.toString(),
                date,
                amount,
                quantity,
                orderProducts!!.shop,
                client,
                address,
                orderProducts!!.products!!,
                binding.comment.editText?.text.toString(),
                Order.Status.NEW,
                Order.Method.DELIVERY
            )

            viewModel.setEvent(DeliveryContract.Event.OnClickSendOrder(order))
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setProperty(address: Address) {
        binding.street.editText?.setText(address.street)
        binding.entrance.editText?.setText(address.entrance)
        binding.numberApartment.editText?.setText(address.numberApartment)
        binding.intercom.editText?.setText(address.intercom)
        binding.floor.editText?.setText(if (address.floor != 0L) address.floor.toString() else "")
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
                    is DeliveryContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is DeliveryContract.Effect.OnGeneralActivity -> {
                        val i = Intent()
                        i.setClass(this@DeliveryFragmentActivity, GeneralActivity::class.java)
                        startActivity(i)
                    }
                }
            }
        }
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}