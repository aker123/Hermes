package com.example.hermes.ui.orderHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.OrderHistoryFragmentBinding
import com.example.hermes.domain.models.Order
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class OrderHistoryFragment : Fragment() {

    private var _binding: OrderHistoryFragmentBinding? = null
    private val binding get() = _binding!!


    private var adapter: OrderHistoryAdapter? = null

    private val ordersProducts: MutableList<Any> = mutableListOf()

    private val viewModel: OrderHistoryViewModel by activityViewModels()
    var isVisibleBack = false
    var onlyActiveOrders = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OrderHistoryFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        init()

        val user = viewModel.getUserDB()

        user?.let { it ->
            viewModel.getOrderHistory(it, onlyActiveOrders).observe(viewLifecycleOwner) { orders ->
                orders?.forEach { order ->
                    ordersProducts.add(order)
                    ordersProducts.addAll(order.products)
                }
                update()
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

    }

    private fun update() {
        adapter?.items = ordersProducts
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            binding.message.text = getString(R.string.order_history_not_orders)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
        }
    }

    private fun init() {
        if (!isVisibleBack) binding.topAppBar.navigationIcon = null
        adapter = OrderHistoryAdapter(viewModel.getPicasso())
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    OrderHistoryContract.State.Default -> {}
                    OrderHistoryContract.State.Setting -> toStateSetting()
                    OrderHistoryContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is OrderHistoryContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                }
            }
        }
    }

    private fun toStateSetting() {
        binding.recyclerView.isVisible = true
        binding.load.isVisible = false
    }

    private fun toStateLoading() {
        binding.recyclerView.isVisible = false
        binding.load.isVisible = true
    }

    private fun showMessage(messageId: Int) {
        Snackbar
            .make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}