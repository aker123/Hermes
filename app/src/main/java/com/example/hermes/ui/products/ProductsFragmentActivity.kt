package com.example.hermes.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.ProductsFragmentActivityBinding
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.basket.BasketFragmentActivity
import com.example.hermes.ui.shops.ShopsContract
import com.google.android.material.snackbar.Snackbar

class ProductsFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_SHOP = "ARGUMENT_SHOP"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: ProductsFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProductsViewModel by viewModels()

    private var adapter: ProductsAdapter? = null

    var products: List<Product>? = null
    private var productsSearch: List<Product>? = null
    var search: Boolean = false

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
                productsSearch = _products
                update()
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

        adapter?.onCheckedStateChangeListener =
            ProductsAdapter.OnCheckedStateChangeListener { product, size ->
                viewModel.setEvent(ProductsContract.Event.OnCheckedChange(product, size))
            }

        binding.onBasket.setOnClickListener {
            val isFilled =
                products?.filter { it.quantity > 0 }?.all { it.sizes.any { size -> size.selected } }
                    ?: false
            if (!isFilled) {
                showMessage(R.string.products_not_correct_size)
                return@setOnClickListener
            }
            viewModel.setEvent(ProductsContract.Event.OnClickOnBasket(shop, products))
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val searchView = binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    productsSearch?.let {
                        viewModel.setEvent(
                            ProductsContract.Event.OnSearch(
                                productsSearch!!,
                                newText
                            )
                        )
                    }
                }
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener { _, focus ->
            search = focus
        }
    }

    private fun init() {
        shop = arguments[ARGUMENT_SHOP] as Shop
        adapter = ProductsAdapter(viewModel.getPicasso())
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
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
                    is ProductsContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is ProductsContract.Effect.Update -> {
                        binding.onBasket.isVisible =
                            products?.any { product -> product.quantity > 0 && product.sizes.any { size -> size.selected } }
                                ?: false
                        effect.products?.let { products = it }
                        update()
                    }
                    is ProductsContract.Effect.OnBasketFragmentActivity -> onBasketFragmentActivity()
                }
            }
        }

    }

    private fun update() {
        adapter?.items = products ?: listOf()
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            if (search) binding.message.text = getString(R.string.products_not_found_products)
            else binding.message.text = getString(R.string.products_not_products)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
        }
    }

    private fun onBasketFragmentActivity() {
        val i = Intent()
        i.setClass(this, BasketFragmentActivity::class.java)
        startActivity(i)
    }

    private fun toStateSetting() {
        binding.recyclerView.isVisible = true
        binding.load.isVisible = false
    }

    private fun toStateLoading() {
        binding.recyclerView.isVisible = false
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