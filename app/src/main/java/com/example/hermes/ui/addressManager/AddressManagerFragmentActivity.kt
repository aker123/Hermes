package com.example.hermes.ui.addressManager

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.AddressManagerFragmentActivityBinding
import com.example.hermes.domain.models.Address
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class AddressManagerFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_ADDRESS = "ARGUMENT_ADDRESS"
        const val ARGUMENT_IS_DELETE = "ARGUMENT_IS_DELETE"
        const val ARGUMENT_CALL_BACK = "ARGUMENT_CALL_BACK"
        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: AddressManagerFragmentActivityBinding? = null
    private val binding get() = _binding!!

    var onCallBackListener : OnCallBackListener?  = null

    interface OnCallBackListener {
        fun onAdd(address: Address)
        fun onDelete(address: Address)
    }

    private val viewModel: AddressManagerViewModel by viewModels()

    var isVisibleDelete = true

    lateinit var address: Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AddressManagerFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_address -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.address_delete)
                        .setNegativeButton(R.string.address_no) { dialog, which ->
                            dialog.cancel()
                        }
                        .setPositiveButton(R.string.address_yes) { dialog, which ->
                            viewModel.setEvent(AddressManagerContract.Event.OnClickDelete(address))
                        }
                        .show()
                    true
                }
                else -> false
            }
        }

        binding.onSave.setOnClickListener {
            address.street = binding.street.editText?.text.toString()
            val floor = binding.floor.editText?.text.toString()
            address.floor = if (floor.isEmpty()) 0L else floor.toLong()
            address.entrance = binding.entrance.editText?.text.toString()
            address.intercom = binding.intercom.editText?.text.toString()
            address.numberApartment = binding.numberApartment.editText?.text.toString()
            viewModel.setEvent(AddressManagerContract.Event.OnClickSave(address))
        }

        binding.topAppBar.setNavigationOnClickListener {
            exit()
        }
    }

    private fun init() {
        address = arguments[ARGUMENT_ADDRESS] as Address
        isVisibleDelete = arguments[ARGUMENT_IS_DELETE] as Boolean
        onCallBackListener = arguments[ARGUMENT_CALL_BACK] as OnCallBackListener
        val deleteAddress = binding.topAppBar.menu.findItem(R.id.delete_address)
        deleteAddress.isVisible = isVisibleDelete
        binding.street.editText?.setText(address.street)
        binding.floor.editText?.setText(if (address.floor == 0L) "" else address.floor.toString())
        binding.entrance.editText?.setText(address.entrance)
        binding.intercom.editText?.setText(address.intercom)
        binding.numberApartment.editText?.setText(address.numberApartment)
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    AddressManagerContract.State.Default -> {}
                    AddressManagerContract.State.Setting -> toStateSetting()
                    AddressManagerContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is AddressManagerContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is AddressManagerContract.Effect.OnCallBackDelete -> {
                            onCallBackListener?.onDelete(address)
                            exit()
                    }
                    is AddressManagerContract.Effect.OnCallBackAdd -> {
                        onCallBackListener?.onAdd(address)
                        exit()
                    }
                }
            }
        }

    }

    private fun exit() {
        finish()
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
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