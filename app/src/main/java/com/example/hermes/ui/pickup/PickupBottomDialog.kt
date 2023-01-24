package com.example.hermes.ui.pickup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hermes.databinding.PickupBottomDialogBinding
import com.example.hermes.domain.models.Shop
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PickupBottomDialog(
    val shop: Shop,
    private val onClickSendOrderListener: OnClickSendOrderListener
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "PickupBottomDialog"
    }

    private var _binding: PickupBottomDialogBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  PickupBottomDialogBinding.inflate(inflater, container, false)
        binding.addressShop.editText?.setText(shop.physicalAddress)
        binding.sendOrder.setOnClickListener {
            onClickSendOrderListener.onClickSendOrder(binding.comment.editText?.text.toString())
            dismiss()
        }

        return binding.root
    }

    fun interface OnClickSendOrderListener {
        fun onClickSendOrder(comment: String)
    }

}