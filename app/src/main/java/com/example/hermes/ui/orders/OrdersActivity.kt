package com.example.hermes.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.OrdersActivityBinding
import com.example.hermes.domain.filters.FilterGroup
import com.example.hermes.domain.filters.FilterProvider
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.filters.FiltersDialog
import com.example.hermes.ui.orderManager.OrderManagerFragmentActivity
import com.google.android.material.snackbar.Snackbar


class OrdersActivity : AppCompatActivity() {
    private var _binding: OrdersActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrdersViewModel by viewModels()

    private var adapter: OrdersAdapter? = null

    private var orders: List<Order>? = null
    private var ordersSearch: List<Order>? = null

    var search: Boolean = false

    private var filters: FilterGroup<Order>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OrdersActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()

        toStateLoading()
        viewModel.getOperator()?.let { operator ->
            operator.shop.let {
                viewModel.getOrders(it).observe(this) { _orders ->
                    orders = _orders
                    ordersSearch = _orders
                    filters = FilterProvider.getFiltersOrder(orders)
                    toStateSetting()
                    update()
                }
            }
        }

        adapter?.onItemClickListener = OrdersAdapter.OnItemClickListener {
            viewModel.setEvent(OrdersContract.Event.OnClickOrder(it))
        }

        val searchView = binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    ordersSearch?.let {
                        viewModel.setEvent(
                            OrdersContract.Event.OnSearch(
                                ordersSearch!!,
                                newText,
                                filters
                            )
                        )
                    }
                }
                return true
            }
        })

        binding.topAppBar.setNavigationOnClickListener {
            viewModel.setEvent(OrdersContract.Event.OnClickExit)
        }

        searchView.setOnQueryTextFocusChangeListener { _, focus ->
            search = focus
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    val dialog = FiltersDialog()
                    dialog.title = getString(R.string.cap_filters)
                    dialog.filterGroup = filters
                    dialog.onBackListener = FiltersDialog.OnBackListener { onFilters() }
                    dialog.show(supportFragmentManager, "FilterDialog")
                    true
                }
                else -> false
            }
        }
    }

    private fun onFilters() {
        val ordersFilter = ordersSearch?.filter { filters?.filter(it) == true }
        orders = ordersFilter
        update()
    }

    private fun init() {
        adapter = OrdersAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    OrdersContract.State.Default -> {}
                    OrdersContract.State.Setting -> toStateSetting()
                    OrdersContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is OrdersContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is OrdersContract.Effect.OnOrderManagerFragmentActivity -> onOrderManagerFragmentActivity(
                        effect.order
                    )
                    is OrdersContract.Effect.Update -> {
                        orders = effect.orders
                        update()
                    }
                    is OrdersContract.Effect.OnExit -> {
                        this@OrdersActivity.finish()
                    }
                }

            }
        }
    }

    private fun exit() {
        finishAffinity()
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
    }

    private fun update() {
        adapter?.items = orders ?: listOf()
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            if (search) binding.message.text = getString(R.string.orders_not_found_orders)
            else binding.message.text = getString(R.string.orders_not_orders)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
        }
    }

    private fun onOrderManagerFragmentActivity(order: Order) {
        val onBack = OrderManagerFragmentActivity.OnBackListener { orderReturn ->
            orders?.first { it.uid == orderReturn.uid }?.status = orderReturn.status
            adapter?.items = orders ?: listOf()
        }

        val i = Intent()
        i.setClass(this, OrderManagerFragmentActivity::class.java)
        OrderManagerFragmentActivity.arguments[OrderManagerFragmentActivity.ARGUMENT_ORDER] = order
        OrderManagerFragmentActivity.arguments[OrderManagerFragmentActivity.ARGUMENT_BACK] = onBack
        startActivity(i)
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