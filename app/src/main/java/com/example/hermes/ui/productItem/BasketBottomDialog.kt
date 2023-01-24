package com.example.hermes.ui.productItem

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.hermes.R
import com.example.hermes.databinding.BasketBottomDialogBinding
import com.example.hermes.databinding.ChipChoiceBinding
import com.example.hermes.domain.models.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso

class BasketBottomDialog(
    val product: Product,
    val picasso: Picasso,
    private val onButtonClickListener: OnButtonClickListener
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "BasketBottomDialog"
    }

    private var _binding: BasketBottomDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var productCurrent: Product


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BasketBottomDialogBinding.inflate(inflater, container, false)
        init()
        setEvents()

        return binding.root
    }


    private fun init() {
        productCurrent = Product(
            product.uid,
            product.shopUid,
            product.name,
            product.price,
            product.amount,
            product.quantity,
            product.description,
            product.gender,
            product.category,
            product.imagePath,
            product.sizes,
            product.productUid
        )

        picasso.load(productCurrent.imagePath).placeholder(R.drawable.hermes).into(binding.image)
        binding.name.text = productCurrent.name
        binding.description.text = productCurrent.description
        binding.quantity.text = productCurrent.quantity.toString()
        if (binding.sizes.childCount == 0) {
            productCurrent.sizes.sortedBy { it.value }.forEach { size ->
                val chip = ChipChoiceBinding
                    .inflate(LayoutInflater.from(binding.sizes.context), binding.sizes, false)
                chip.root.text = size.value
                chip.root.isChecked = size.selected
                binding.sizes.addView(chip.root)
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun setEvents() {

        binding.addBtn.setOnClickListener {
            productCurrent.quantity = productCurrent.quantity + 1
            productCurrent.amount = productCurrent.price * productCurrent.quantity
            binding.quantity.text = productCurrent.quantity.toString()
            if (productCurrent.quantity > 0L) {
                binding.button.isVisible = true
                binding.buttonDelete.isVisible = false
            }
        }

        binding.removeBtn.setOnClickListener {
            productCurrent.quantity =
                if (productCurrent.quantity > 0L) productCurrent.quantity - 1 else 0
            productCurrent.amount = productCurrent.price * productCurrent.quantity
            binding.quantity.text = productCurrent.quantity.toString()
            if (productCurrent.quantity == 0L) {
                binding.button.isVisible = false
                binding.buttonDelete.isVisible = true
            }
        }

        binding.sizes.setOnCheckedStateChangeListener { group, checkedIds ->
            var chip: Chip? = null
            if (checkedIds.isNotEmpty()) chip = group.findViewById(checkedIds[0])

            productCurrent.sizes.forEach {
                it.selected = it.value == (chip?.text?.toString() ?: "")
            }
        }

        binding.button.setOnClickListener {
            onButtonClickListener.onButtonClick(productCurrent, false)
            dismiss()
        }

        binding.buttonDelete.setOnClickListener {
            onButtonClickListener.onButtonClick(productCurrent, true)
            dismiss()
        }
    }


    fun interface OnButtonClickListener {
        fun onButtonClick(product: Product, isDelete: Boolean)
    }
}