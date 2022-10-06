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
import com.example.hermes.R
import com.example.hermes.databinding.BasketFragmentBinding
import com.example.hermes.domain.models.Client
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.PickupBottomDialog
import com.example.hermes.ui.delivery.DeliveryFragmentActivity
import com.example.hermes.ui.general.GeneralActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.*

class BasketFragment : Fragment() {

    private var _binding: BasketFragmentBinding? = null
    private val binding get() = _binding!!

    private var selectedProducts: MutableList<Product> = mutableListOf()

    private var adapter: BasketAdapter? = null

    private val viewModel: BasketViewModel by activityViewModels()
    var isVisibleBack = false

    var isDelivery: Boolean? = null
    private var isPickup: Boolean? = null

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
            selectedProducts = it.toMutableList()
            binding.onDelivery.isVisible =
                selectedProducts.any { product -> product.quantity > 0 && product.sizes.any { size -> size.selected } }
        }

        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        adapter?.onItemClickListener = BasketAdapter.OnItemClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickProduct(it))
        }


        adapter?.onAddClickListener = BasketAdapter.OnAddClickListener {
            viewModel.setEvent(BasketContract.Event.OnClickAdd(it))
        }

        adapter?.onRemoveClickListener = BasketAdapter.OnRemoveClickListener { product ->
            viewModel.setEvent(BasketContract.Event.OnClickRemove(product))
        }

        binding.onDelivery.setOnClickListener {
            val isFilled = selectedProducts.filter { it.quantity > 0 }
                .all { it.sizes.any { size -> size.selected } }
            if (!isFilled) {
                showMessage(R.string.basket_not_correct_size)
                return@setOnClickListener
            }
            if (isDelivery == null && isPickup == null) showMessage(R.string.basket_need_change_method)
            when (true) {
                isDelivery -> viewModel.setEvent(BasketContract.Event.OnClickDelivery(selectedProducts))
                isPickup -> viewModel.setEvent(BasketContract.Event.OnClickPickup(selectedProducts))
                else -> return@setOnClickListener
            }
        }

        adapter?.onCheckedStateChangeListener =
            BasketAdapter.OnCheckedStateChangeListener { product, size ->
                viewModel.setEvent(BasketContract.Event.OnCheckedChange(product, size))
            }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.clear_basket -> {
                    context?.let {
                        MaterialAlertDialogBuilder(it)
                            .setTitle(R.string.basket_clear_basket_question)
                            .setNegativeButton(R.string.basket_to_leave) { dialog, which ->
                                dialog.cancel()
                            }
                            .setPositiveButton(R.string.basket_clear) { dialog, which ->
                                selectedProducts = mutableListOf()
                                viewModel.setEvent(BasketContract.Event.OnClearBasket)
                            }
                            .show()
                    }

                    true
                }
                else -> false
            }
        }

        binding.chipDelivery.setOnCheckedChangeListener { chip, isChecked ->
            isDelivery = isChecked
        }

        binding.chipPickup.setOnCheckedChangeListener { chip, isChecked ->
            isPickup = isChecked
        }
    }

    private fun init() {
       if (!isVisibleBack) binding.topAppBar.navigationIcon = null
        adapter = BasketAdapter(viewModel.getPicasso())
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    BasketContract.State.Default -> {}
                    is BasketContract.State.Setting -> toStateSetting()
                    BasketContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is BasketContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is BasketContract.Effect.Update -> {
                        binding.onDelivery.isVisible =
                            selectedProducts.any { product -> product.quantity > 0 && product.sizes.any { size -> size.selected } }
                        effect.products?.let { selectedProducts = it.toMutableList() }

                        update()
                    }
                    is BasketContract.Effect.ShowDialogMessage -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle(R.string.basket_delete_product)
                                .setNegativeButton(R.string.basket_cancel) { dialog, which ->
                                    dialog.cancel()
                                }
                                .setPositiveButton(R.string.basket_yes) { dialog, which ->
                                    selectedProducts.remove(effect.product)
                                    binding.onDelivery.isVisible =
                                        selectedProducts.any { product -> product.quantity > 0 && product.sizes.any { size -> size.selected } }
                                    update()
                                }
                                .show()
                        }
                    }
                    is BasketContract.Effect.OnDeliveryFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, DeliveryFragmentActivity::class.java) }
                        DeliveryFragmentActivity.arguments[DeliveryFragmentActivity.ARGUMENT_ORDER_PRODUCTS] =
                            effect.orderProducts
                        startActivity(i)
                    }
                    is BasketContract.Effect.OnPickupBottomDialog -> {
                        val pickupBottomDialog = PickupBottomDialog(effect.orderProducts.shop , object : PickupBottomDialog.OnClickSendOrderListener{
                            override fun onClickSendOrder(comment: String) {
                                val amount = effect.orderProducts.products?.sumOf { it.amount } ?: return
                                val quantity = effect.orderProducts.products.sumOf { it.quantity }

                                val client = Client(
                                    effect.orderProducts.user.uid,
                                    effect.orderProducts.user.surname,
                                    effect.orderProducts.user.name,
                                    effect.orderProducts.user.phoneNumber,
                                    effect.orderProducts.user.mail
                                )

                                val number = Random().nextInt(1000000 - 99999) + 99999

                                val order = Order(
                                    UUID.randomUUID().toString(),
                                    number.toString(),
                                    amount,
                                    quantity,
                                    effect.orderProducts.shop,
                                    client,
                                    null,
                                    effect.orderProducts.products,
                                    comment,
                                    Order.Status.NEW,
                                    Order.Method.PICKUP
                                )
                                viewModel.setEvent(BasketContract.Event.OnClickSendOrder(order))
                            }
                        })
                        activity?.supportFragmentManager?.let { pickupBottomDialog.show(it, PickupBottomDialog.TAG) }
                    }
                    is BasketContract.Effect.OnGeneralActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, GeneralActivity::class.java) }
                        startActivity(i)
                    }
                }
            }
        }

    }

    private fun update() {
        adapter?.items = selectedProducts
        if (adapter?.itemCount == 0) {
            binding.chipDelivery.isVisible = false
            binding.chipPickup.isVisible = false
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            binding.message.text = getString(R.string.basket_not_products)
        } else {
            binding.chipDelivery.isVisible = true
            binding.chipPickup.isVisible = true
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
        }
    }

    private fun toStateSetting() {
        binding.chipDelivery.isVisible = true
        binding.chipPickup.isVisible = true
        binding.recyclerView.isVisible = true
        binding.onDelivery.isVisible = true
        binding.load.isVisible = false
    }

    private fun toStateLoading() {
        binding.chipDelivery.isVisible = false
        binding.chipPickup.isVisible = false
        binding.recyclerView.isVisible = false
        binding.onDelivery.isVisible = false
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