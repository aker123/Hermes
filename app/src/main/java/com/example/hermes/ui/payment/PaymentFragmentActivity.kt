package com.example.hermes.ui.payment

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.cloudipsp.android.*
import com.example.hermes.R
import com.example.hermes.databinding.PaymentFragmentActivityBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class PaymentFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_ORDER = "ARGUMENT_ORDER"

        val arguments: MutableMap<String, Any> = mutableMapOf()
    }


    private var _binding: PaymentFragmentActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentViewModel by viewModels()
    private var order: com.example.hermes.domain.models.Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PaymentFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()


        order = arguments[ARGUMENT_ORDER] as com.example.hermes.domain.models.Order

        binding.spinnerCcy.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Currency.values()
        )

        binding.btnPayCard.setOnClickListener {
            val cloudipsp = Cloudipsp(1396424, binding.webView)
            val orderPay = createOrderPay() ?: return@setOnClickListener
            val card = getCard() ?: return@setOnClickListener
            viewModel.setEvent(PaymentContract.Event.OnClickPay(orderPay, card, cloudipsp))
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    PaymentContract.State.Default -> {}
                    PaymentContract.State.Setting -> toStateSetting()
                    PaymentContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is PaymentContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
                    }
                }
            }
        }
    }


    private fun getCard(): Card? {
        return binding.cardLayout.confirm(object : CardInputLayout.ConfirmationErrorHandler {
            override fun onCardInputErrorClear(view: CardInputLayout?, editText: EditText?) {}
            override fun onCardInputErrorCatched(
                view: CardInputLayout?,
                editText: EditText?,
                error: String?
            ) {
            }
        })
    }

    private fun createOrderPay(): Order? {
        binding.editAmount.error = null
        binding.editEmail.error = null
        binding.editDescription.error = null
        val amount: Int
        try {
            amount = Integer.valueOf(binding.editAmount.text.toString())
        } catch (e: Exception) {
            binding.editAmount.error = getString(R.string.e_invalid_amount)
            return null
        }
        val email: String = binding.editEmail.text.toString()
        val description: String = binding.editDescription.text.toString()
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editEmail.error = getString(R.string.e_invalid_email)
            return null
        } else if (TextUtils.isEmpty(description)) {
            binding.editDescription.error = getString(R.string.e_invalid_description)
            return null
        }


        val currency = binding.spinnerCcy.selectedItem as Currency
        val order = Order(amount, currency, "vb_" + System.currentTimeMillis(), description, email)
        order.setLang(Order.Lang.ru)
        return order
    }

    private fun toStateSetting() {
    }

    private fun toStateLoading() {
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
