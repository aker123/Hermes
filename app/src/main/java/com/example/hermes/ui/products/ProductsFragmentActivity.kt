package com.example.hermes.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hermes.databinding.ProductsFragmentActivityBinding
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.basket.BasketFragmentActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class ProductsFragmentActivity: FragmentActivity() {
    companion object {
        const val ARGUMENT_SHOP = "ARGUMENT_SHOP"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: ProductsFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductsViewModel by viewModels()

    private var adapter: ProductsAdapter? = null

    private var products: List<Product>? = null

    private var shop: Shop? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ProductsFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()

        shop?.let {
            viewModel.getProducts(it).observe(this) { _products ->
                products = _products
                adapter?.items = products ?: listOf()
            }
        }

        adapter?.onItemClickListener = ProductsAdapter.OnItemClickListener {
            viewModel.setEvent(ProductsContract.Event.OnClickProduct(it))
        }

        adapter?.onPriceClickListener = ProductsAdapter.OnPriceClickListener {
            viewModel.setEvent(ProductsContract.Event.OnCLickPrice(it))
        }

        adapter?.onAddClickListener = ProductsAdapter.OnAddClickListener {
            viewModel.setEvent(ProductsContract.Event.OnClickAdd(it))
        }

        adapter?.onRemoveClickListener = ProductsAdapter.OnRemoveClickListener {
            viewModel.setEvent(ProductsContract.Event.OnClickRemove(it))
        }

        binding.onBasket.setOnClickListener {
            viewModel.setEvent(ProductsContract.Event.OnClickOnBasket(shop,products))
        }
    }

    private fun init(){
        shop = arguments[ARGUMENT_SHOP] as Shop
        adapter = ProductsAdapter()
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
       lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    ProductsContract.State.Default -> {}
                    ProductsContract.State.Setting -> toStateSetting()
                    ProductsContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is ProductsContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                    is ProductsContract.Effect.Update -> {
                        adapter?.items = products ?: listOf()
                       val isSelected =  products?.any { it.quantity > 0 }
                        if (isSelected != null) {
                            binding.onBasket.isVisible = isSelected
                        }

                    }
                    is ProductsContract.Effect.OnBasketFragmentActivity -> onBasketFragmentActivity()
                }
            }
        }

    }

    private fun onBasketFragmentActivity(){
        val i = Intent()
        i.setClass(this, BasketFragmentActivity::class.java)
        startActivity(i)
    }

    private fun toStateSetting() {
        adapter?.items = products ?: listOf()
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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}