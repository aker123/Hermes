package com.example.hermes.ui.basket

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.databinding.BasketFragmentBinding
import com.example.hermes.domain.models.OrderProducts
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.delivery.DeliveryFragmentActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class BasketFragment : Fragment() {

    private var _binding: BasketFragmentBinding? = null
    private val binding get() = _binding!!

    private var selectedProducts: List<Product>? = null

    private var adapter: BasketAdapter? = null

    private val viewModel: BasketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BasketFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        init()

        viewModel.getSelectedProducts().let {
            selectedProducts = it
            adapter?.items = selectedProducts ?: listOf()
        }

        adapter?.onItemClickListener = BasketAdapter.OnItemClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickProduct(it))
        }


        adapter?.onAddClickListener = BasketAdapter.OnAddClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickAdd(it))
        }

        adapter?.onRemoveClickListener = BasketAdapter.OnRemoveClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickRemove(it))
        }

        binding.onDelivery.setOnClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickDelivery(selectedProducts))
        }
    }

    private fun init() {
        adapter = BasketAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    BasketContract.State.Default -> {}
                    BasketContract.State.Setting -> toStateSetting()
                    BasketContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is BasketContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                    is BasketContract.Effect.Update -> {
                        adapter?.items = selectedProducts ?: listOf()
                    }
                    is BasketContract.Effect.OnDeliveryFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, DeliveryFragmentActivity::class.java) }
                        DeliveryFragmentActivity.arguments[DeliveryFragmentActivity.ARGUMENT_ORDER_PRODUCTS] = effect.orderProducts
                        startActivity(i)
                    }
                }
            }
        }

    }


    private fun toStateSetting() {
        adapter?.items = selectedProducts ?: listOf()
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