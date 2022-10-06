package com.example.hermes.ui.authorization

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.AuthorizationFragmentBinding
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.User
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.orders.OrdersActivity
import com.example.hermes.ui.products.ProductsFragmentActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class AuthorizationFragment: Fragment()  {

    private var _binding: AuthorizationFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthorizationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AuthorizationFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        binding.login.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.login.isErrorEnabled) {
                binding.login.isErrorEnabled = false
                binding.login.error = null
            }
        }
        binding.password.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.password.isErrorEnabled) {
                binding.password.isErrorEnabled = false
                binding.password.error = null
            }
        }

        binding.enter.setOnClickListener {
            if (binding.login.editText?.text.toString() == "") {
                setErrorProperty()
                showMessage(R.string.authorization_mes_not_filled_login)
                return@setOnClickListener
            }

            if (binding.password.editText?.text.toString() == "") {
                setErrorProperty()
                showMessage(R.string.authorization_mes_not_filled_password)
                return@setOnClickListener
            }

            viewModel.setEvent(AuthorizationContract.Event.OnClickAuthorize(binding.login.editText?.text.toString(),binding.password.editText?.text.toString()))
        }

    }

    private fun setErrorProperty() {
        if (binding.login.editText?.text?.isEmpty() == true) {
            binding.login.error = getString(R.string.registration_error)
            binding.login.isErrorEnabled = true
        } else {
            binding.login.isErrorEnabled = false
            binding.login.error = null
        }
        if (binding.password.editText?.text?.isEmpty() == true) {
            binding.password.error = getString(R.string.registration_error)
            binding.password.isErrorEnabled = true
        } else {
            binding.password.isErrorEnabled = false
            binding.password.error = null
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    AuthorizationContract.State.Default -> {}
                    AuthorizationContract.State.Setting -> toStateSetting()
                    AuthorizationContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is AuthorizationContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is AuthorizationContract.Effect.OnGeneralActivity -> onGeneralActivity()
                    is AuthorizationContract.Effect.OnOrdersActivity -> onOrdersActivity()
                }
            }
        }

    }

    private fun onOrdersActivity() {
        val i = Intent()
        activity?.let { i.setClass(it, OrdersActivity::class.java) }
        activity?.startActivity(i)
    }

    private fun onGeneralActivity() {
        val i = Intent()
        activity?.let { i.setClass(it, GeneralActivity::class.java) }
        activity?.startActivity(i)
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