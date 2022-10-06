package com.example.hermes.ui.orderManager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.OrderManagerFragmentActivityBinding
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.google.android.material.snackbar.Snackbar

class OrderManagerFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_ORDER = "ARGUMENT_ORDER"
        const val ARGUMENT_BACK = "ARGUMENT_BACK"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: OrderManagerFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderManagerViewModel by viewModels()

    private var adapter: OrderManagerAdapter? = null

    private var products: List<Product>? = null

    var onBackListener: OnBackListener? = null


    private lateinit var order: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OrderManagerFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()
        initProperty()

        viewModel.getOrderProducts(order).observe(this) { _products ->
            products = _products
            update()
        }

        binding.onSend.setOnClickListener {
            val status = binding.order.status.text.toString()
            order.status = Order.Status.values().first { it.key == status }
            viewModel.setEvent(OrderManagerContract.Event.OnClickSendOrder(order))
        }

        binding.topAppBar.setNavigationOnClickListener {
            exit()
        }

        binding.order.status.setOnClickListener {
            showMenu(it, R.menu.statuses_menu)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
          binding.order.status.text = menuItem.title
            return@setOnMenuItemClickListener true
        }
        popup.show()
    }

    @SuppressLint("SetTextI18n")
    private fun initProperty() {
        binding.order.amount.text = binding.order.amount.resources.getString(R.string.price,order.amount.toString())
        binding.order.quantity.text = order.quantity.toString()
        binding.order.numberOrder.text = binding.order.numberOrder.resources.getString(R.string.number,order.number)
        binding.order.comment.text = order.comment
        binding.order.status.text = order.status.key

        val clientName = order.client.name
        val clientSurname = order.client.surname
        binding.client.client.text = "$clientName $clientSurname"
        binding.client.clientPhone.text = order.client.phoneNumber
        binding.client.clientMail.text = order.client.mail

        if (order.address != null) {
            binding.delivery.street.text = order.address?.street
            binding.delivery.entrance.text = order.address?.entrance
            binding.delivery.intercom.text = order.address?.intercom
            binding.delivery.numberApartment.text = order.address?.numberApartment
            binding.delivery.floor.text = order.address?.floor?.toString() ?: ""
        } else {
            binding.delivery.street.text = order.shop.physicalAddress
            binding.delivery.deliveryText.text = getString(R.string.order_manager_address_shop)
            binding.delivery.entrance.isVisible = false
            binding.delivery.entranceText.isVisible = false
            binding.delivery.intercom.isVisible = false
            binding.delivery.intercomText.isVisible = false
            binding.delivery.numberApartment.isVisible = false
            binding.delivery.numberApartmentText.isVisible = false
            binding.delivery.floor.isVisible = false
            binding.delivery.floorText.isVisible = false
        }
    }

    private fun init() {
        order = arguments[ARGUMENT_ORDER] as Order
        onBackListener = arguments[ARGUMENT_BACK] as OnBackListener
        adapter = OrderManagerAdapter(viewModel.getPicasso())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    OrderManagerContract.State.Default -> {}
                    OrderManagerContract.State.Setting -> toStateSetting()
                    OrderManagerContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is OrderManagerContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is OrderManagerContract.Effect.OnOrdersActivity -> {
                        onBackListener?.onBack(order)
                        finish()
                    }
                }
            }
        }
    }

    private fun update() {
        adapter?.items = products ?: listOf()
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            binding.message.text = getString(R.string.order_manager_not_products)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
        }
    }

    private fun exit() {
        finish()
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
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

    fun interface OnBackListener{
       fun onBack(order: Order)
    }
}