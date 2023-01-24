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
import com.example.hermes.ui.pickup.PickupBottomDialog
import com.example.hermes.ui.delivery.DeliveryFragmentActivity
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.productItem.BasketBottomDialog
import com.example.hermes.ui.productItem.ProductBottomDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class BasketFragment : Fragment() {

    private var _binding: BasketFragmentBinding? = null
    private val binding get() = _binding!!

    var selectedProducts: MutableList<Product> = mutableListOf()

    private var adapter: BasketAdapter? = null

    private val viewModel: BasketViewModel by activityViewModels()
    var isVisibleBack = false

    var isDelivery: Boolean? = null
    private var isPickup: Boolean? = null

    lateinit var picasso: Picasso

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

        binding.topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter?.onItemClickListener = BasketAdapter.OnItemClickListener {
            val basketBottomDialog = BasketBottomDialog(
                it, picasso
            ) { product, isDelete ->
                val productCurrent = selectedProducts.first { productCurrent -> productCurrent.uid == product.uid }
                if (isDelete) {
                    viewModel.setEvent(BasketContract.Event.RemoveProductBase(productCurrent))
                    selectedProducts.remove(productCurrent)
                } else {
                    productCurrent.quantity = product.quantity
                    productCurrent.sizes = product.sizes
                    productCurrent.amount = product.amount
                }
                update()
            }
            activity?.supportFragmentManager?.let { it1 ->
                basketBottomDialog.show(
                    it1,
                    BasketBottomDialog.TAG
                )
            }
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
                isDelivery -> viewModel.setEvent(
                    BasketContract.Event.OnClickDelivery(
                        selectedProducts
                    )
                )
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

    override fun onResume() {
        super.onResume()
        viewModel.getSelectedProducts(selectedProducts)
    }

    private fun init() {
        picasso = viewModel.getPicasso()
        if (!isVisibleBack) binding.topAppBar.navigationIcon = null
        adapter = BasketAdapter(picasso)
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
                        effect.products?.let { selectedProducts = it.toMutableList() }
                        effect.isNeedUpdate?.let { if (it) update() else checkCountSelectedProducts() }
                            ?: update()
                    }
                    is BasketContract.Effect.ShowDialogMessage -> {
                        context?.let {
                            MaterialAlertDialogBuilder(it)
                                .setTitle(R.string.basket_delete_product)
                                .setNegativeButton(R.string.basket_cancel) { dialog, which ->
                                    dialog.cancel()
                                }
                                .setPositiveButton(R.string.basket_yes) { dialog, which ->
                                    viewModel.setEvent(BasketContract.Event.RemoveProductBase(effect.product))
                                    selectedProducts.remove(effect.product)
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
                        val pickupBottomDialog = PickupBottomDialog(
                            effect.orderProducts.shop,
                            object : PickupBottomDialog.OnClickSendOrderListener {
                                override fun onClickSendOrder(comment: String) {
                                    val amount =
                                        effect.orderProducts.products?.sumOf { it.amount } ?: return
                                    val quantity =
                                        effect.orderProducts.products.sumOf { it.quantity }

                                    val client = Client(
                                        effect.orderProducts.user.uid,
                                        effect.orderProducts.user.surname,
                                        effect.orderProducts.user.name,
                                        effect.orderProducts.user.phoneNumber,
                                        effect.orderProducts.user.mail
                                    )

                                    val number = Random().nextInt(1000000 - 99999) + 99999
                                    val dateFormat =
                                        SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault())
                                    val date = dateFormat.format(Date()).toString()

                                    val order = Order(
                                        UUID.randomUUID().toString(),
                                        number.toString(),
                                        date,
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
                        activity?.supportFragmentManager?.let {
                            pickupBottomDialog.show(
                                it,
                                PickupBottomDialog.TAG
                            )
                        }
                    }
                    is BasketContract.Effect.OnGeneralActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, GeneralActivity::class.java) }
                        startActivity(i)
                        activity?.finish()
                    }
                }
            }
        }

    }

    private fun update() {
        adapter?.items = selectedProducts
        checkCountSelectedProducts()
    }

    private fun checkCountSelectedProducts() {
        binding.onDelivery.isVisible =
            selectedProducts.any { product -> product.quantity > 0 && product.sizes.any { size -> size.selected } }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}