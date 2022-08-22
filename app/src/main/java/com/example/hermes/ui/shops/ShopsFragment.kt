package com.example.hermes.ui.shops

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
import com.example.hermes.databinding.ShopsFragmentBinding
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.authorization.AuthorizationContract
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.products.ProductsFragmentActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class ShopsFragment : Fragment() {

    private var _binding: ShopsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShopsViewModel by activityViewModels()

    private var adapter: ShopAdapter? = null

    private var shops: List<Shop>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShopsFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        init()

        viewModel.getShops().observe(viewLifecycleOwner) {
            shops = it
            adapter?.items = shops ?: listOf()
            if (it != null) viewModel.setEvent(ShopsContract.Event.SaveShopsDB(it))
        }

        adapter?.onItemClickListener = ShopAdapter.OnItemClickListener {
            viewModel.setEvent(ShopsContract.Event.OnClickShop(it))
        }
    }

    private fun init() {
        adapter = ShopAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    ShopsContract.State.Default -> {}
                    ShopsContract.State.Setting -> toStateSetting()
                    ShopsContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is ShopsContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                    is ShopsContract.Effect.OnProductsFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, ProductsFragmentActivity::class.java) }
                        ProductsFragmentActivity.arguments[ProductsFragmentActivity.ARGUMENT_SHOP] =
                            effect.shop
                        activity?.startActivity(i)
                    }
                }
            }
        }

    }


    private fun toStateSetting() {
        adapter?.items = shops ?: listOf()
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