package com.example.hermes.ui.shops

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.ShopsFragmentBinding
import com.example.hermes.domain.models.Shop
import com.example.hermes.ui.products.ProductsFragmentActivity
import com.google.android.material.snackbar.Snackbar


class ShopsFragment : Fragment() {

    private var _binding: ShopsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShopsViewModel by activityViewModels()

    private var adapter: ShopAdapter? = null

    private var shops: List<Shop>? = null
    private var shopsSearch: List<Shop>? = null
    var search: Boolean = false

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

        toStateLoading()
        viewModel.getShops().observe(viewLifecycleOwner) {
            shops = it
            shopsSearch = it
            update()
            toStateSetting()
            if (it != null) viewModel.setEvent(ShopsContract.Event.SaveShopsDB(it))
        }

        adapter?.onItemClickListener = ShopAdapter.OnItemClickListener {
            viewModel.setEvent(ShopsContract.Event.OnClickShop(it))
        }

        val searchView = binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    shopsSearch?.let {
                        viewModel.setEvent(ShopsContract.Event.OnSearch(shopsSearch!!, newText))
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
        adapter = ShopAdapter(viewModel.getPicasso())
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
                    is ShopsContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is ShopsContract.Effect.OnProductsFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, ProductsFragmentActivity::class.java) }
                        ProductsFragmentActivity.arguments[ProductsFragmentActivity.ARGUMENT_SHOP] =
                            effect.shop
                        activity?.startActivity(i)
                    }
                    is ShopsContract.Effect.Update -> {
                        shops = effect.shops
                        update()
                    }
                }
            }
        }

    }

    private fun update() {
        adapter?.items = shops ?: listOf()
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            if (search) binding.message.text = getString(R.string.shops_not_found_shops)
            else binding.message.text = getString(R.string.shops_not_shops)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}