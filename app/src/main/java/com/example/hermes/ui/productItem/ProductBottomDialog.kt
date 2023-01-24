package com.example.hermes.ui.productItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hermes.R
import com.example.hermes.databinding.ChipChoiceBinding
import com.example.hermes.databinding.ProductBottomDialogBinding
import com.example.hermes.domain.models.Product
import com.example.hermes.ui.products.ProductsContract
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class ProductBottomDialog(
    val product: Product,
    val picasso: Picasso,
    var selectedProducts: List<Product>,
    private val onClearBasketClickListener: OnClearBasketClickListener,
    private val onAddProductClickListener: OnAddProductClickListener
) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ProductBottomDialog"
    }

    private var _binding: ProductBottomDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductBottomDialogBinding.inflate(inflater, container, false)
        init()
        setEvents()

        return binding.root
    }

    private fun init() {
        picasso.load(product.imagePath).placeholder(R.drawable.hermes).into(binding.image)
        binding.name.text = product.name
        binding.description.text = product.description
        if (binding.sizes.childCount == 0) {
            product.sizes.sortedBy { it.value }.forEach { size ->
                val chip = ChipChoiceBinding
                    .inflate(LayoutInflater.from(binding.sizes.context), binding.sizes, false)
                chip.root.text = size.value
                chip.root.isChecked = size.selected
                binding.sizes.addView(chip.root)
            }
        }
    }

    private fun setEvents() {
        binding.sizes.setOnCheckedStateChangeListener { group, checkedIds ->
            var chip: Chip? = null
            if (checkedIds.isNotEmpty()) chip = group.findViewById(checkedIds[0])

            product.sizes.forEach {
                it.selected = it.value == (chip?.text?.toString() ?: "")
            }
        }

        binding.button.setOnClickListener {
            checkProducts()
        }
    }


    private fun checkProducts() {
        if (product.sizes.any { size -> size.selected }) {
            if (selectedProducts.isNotEmpty() && selectedProducts.firstOrNull { selectedProduct -> selectedProduct.shopUid == product.shopUid } == null) {
                activity?.let {
                    MaterialAlertDialogBuilder(it)
                        .setTitle(R.string.products_clear_basket)
                        .setNegativeButton(R.string.products_to_leave) { dialog, which ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.products_clear) { dialog, which ->
                            onClearBasketClickListener.onClearBasketClick()
                            dismiss()
                        }
                        .show()
                }
            } else {
                onAddProductClickListener.onAddProductClick()
                dismiss()
            }
        } else showMessage(R.string.products_not_correct_size)
    }

    private fun showMessage(message: Int) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun interface OnClearBasketClickListener {
        fun onClearBasketClick()
    }

    fun interface OnAddProductClickListener {
        fun onAddProductClick()
    }

}