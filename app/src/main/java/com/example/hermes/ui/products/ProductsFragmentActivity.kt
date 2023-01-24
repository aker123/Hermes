package com.example.hermes.ui.products

import android.annotation.SuppressLint
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
import com.example.hermes.databinding.ChipChoiceBinding
import com.example.hermes.databinding.ChipGroupsBinding
import com.example.hermes.databinding.ProductsFragmentActivityBinding
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.basket.BasketFragmentActivity
import com.example.hermes.ui.productItem.ProductBottomDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

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
    private var selectedProducts: MutableList<Product> = mutableListOf()
    var search: Boolean = false

    private var shop: Shop? = null

    var textGender: String? = null
    var textCategory: String? = null

    lateinit var picasso: Picasso

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
                initGroups()
            }
        }

        adapter?.onItemClickListener = ProductsAdapter.OnItemClickListener { product ->
            val productBottomDialog = ProductBottomDialog(product, picasso, selectedProducts, {
                viewModel.setEvent(ProductsContract.Event.ClearBasket)
                selectedProducts = mutableListOf()
                addBasket(product)
            }
            ) { addBasket(product) }
            productBottomDialog.show(supportFragmentManager, ProductBottomDialog.TAG)
        }

        adapter?.onAddBasketClickListener = ProductsAdapter.OnAddBasketClickListener {
            checkSelectedProductAnotherShop(it)
        }

        adapter?.onCheckedStateChangeListener =
            ProductsAdapter.OnCheckedStateChangeListener { product, size ->
                viewModel.setEvent(ProductsContract.Event.OnCheckedChange(product, size))
            }

        binding.onBasket.setOnClickListener {
            viewModel.setEvent(ProductsContract.Event.OnClickOnBasket)
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
                                newText,
                                textGender,
                                textCategory
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

        binding.categories.setOnCheckedStateChangeListener { group, checkedIds ->
            var chip: Chip? = null
            if (checkedIds.isNotEmpty()) chip = group.findViewById(checkedIds[0])
            val text: String? = chip?.text?.toString()

            val chipGender =
                binding.genders.findViewById<Chip>(binding.genders.checkedChipId)
            var textGender: String? = chipGender?.text?.toString()
            if (textGender == getString(R.string.products_men_clothing)) textGender =
                getString(R.string.products_man)
            else if (textGender == getString(R.string.products_women_clothing)) textGender =
                getString(R.string.products_woman)

            onFiltersGroup(textGender, text)
        }

        binding.genders.setOnCheckedStateChangeListener { group, checkedIds ->
            var chip: Chip? = null
            if (checkedIds.isNotEmpty()) chip = group.findViewById(checkedIds[0])
            var text: String? = chip?.text?.toString()
            if (text == getString(R.string.products_men_clothing)) text =
                getString(R.string.products_man)
            else if (text == getString(R.string.products_women_clothing)) text =
                getString(R.string.products_woman)

            val chipCategory =
                binding.categories.findViewById<Chip>(binding.categories.checkedChipId)
            val textCategory: String? = chipCategory?.text?.toString()

            onFiltersGroup(text, textCategory)
        }

    }

    private fun onFiltersGroup(textGender: String?, textCategory: String?) {
        var productsFilters = productsSearch
        textGender?.let {
            productsFilters = productsFilters?.filter { it.gender == textGender }
        }
        textCategory?.let {
            productsFilters = productsFilters?.filter { it.category.key == textCategory }
        }

        this.textGender = textGender
        this.textCategory = textCategory
        products = productsFilters

        update()
    }

    private fun checkSelectedProductAnotherShop(product: Product) {
        if (product.sizes.any { size -> size.selected }) {
            if (selectedProducts.isNotEmpty() && selectedProducts.firstOrNull { selectedProduct -> selectedProduct.shopUid == product.shopUid } == null) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.products_clear_basket)
                    .setNegativeButton(R.string.products_to_leave) { dialog, which ->
                        dialog.cancel()
                    }
                    .setPositiveButton(R.string.products_clear) { dialog, which ->
                        viewModel.setEvent(ProductsContract.Event.ClearBasket)
                        selectedProducts = mutableListOf()
                        addBasket(product)
                    }
                    .show()
            } else addBasket(product)
        } else showMessage(R.string.products_not_correct_size)
    }

    private fun addBasket(product: Product) {
        viewModel.setEvent(ProductsContract.Event.OnCLickAddBasket(product, selectedProducts))
    }

    private fun initGroups() {
        if (binding.genders.childCount == 0) {
            val groups: MutableList<String> = mutableListOf()
            products?.groupBy { product ->
                product.gender
            }
                ?.map { map ->
                    groups.add(map.key)
                }

            groups.sortedBy { it }.forEach { group ->
                val chipGender = ChipGroupsBinding
                    .inflate(
                        LayoutInflater.from(binding.genders.context),
                        binding.genders,
                        false
                    )

                chipGender.root.text = if (group == getString(R.string.products_man))
                    getString(R.string.products_men_clothing)
                else getString(R.string.products_women_clothing)

                binding.genders.addView(chipGender.root)
            }
        }

        if (binding.categories.childCount == 0) {
            val groups: MutableList<Product.Category> = mutableListOf()
            products?.groupBy { product ->
                product.category
            }
                ?.map { map ->
                    groups.add(map.key)
                }

            groups.sortedBy { group -> group.key }.forEach { group ->
                val chipCategory = ChipGroupsBinding
                    .inflate(
                        LayoutInflater.from(binding.categories.context),
                        binding.categories,
                        false
                    )
                chipCategory.root.text = group.key
                binding.categories.addView(chipCategory.root)
            }
        }
    }

    private fun init() {
        picasso = viewModel.getPicasso()
        shop = arguments[ARGUMENT_SHOP] as Shop
        adapter = ProductsAdapter(picasso)
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
                        effect.products?.let { products = it }
                        update()
                    }
                    is ProductsContract.Effect.UpdateAmount -> {
                        updateAmount()
                    }
                    is ProductsContract.Effect.OnBasketFragmentActivity -> onBasketFragmentActivity()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        selectedProducts = mutableListOf()
        viewModel.getSelectedProducts(selectedProducts)
    }

    private fun updateAmount() {
        binding.onBasket.isVisible = selectedProducts.isNotEmpty()
        val sum = selectedProducts.sumOf { it.price }
        binding.onBasket.text = binding.onBasket.resources.getString(R.string.price, sum.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun update() {
        adapter?.items = products ?: listOf()
        updateAmount()

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