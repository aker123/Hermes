package com.example.hermes.ui.authorization

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.AuthorizationFragmentBinding
import com.example.hermes.domain.models.User
import com.example.hermes.ui.general.GeneralActivity
import com.example.hermes.ui.orders.OrdersActivity
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
        initProperty()

        binding.enter.setOnClickListener {
            if (binding.login.name.text.toString() == "") {
                showMessage(R.string.authorization_mes_not_filled_login)
                return@setOnClickListener
            }

            if (binding.password.name.text.toString() == "")
            {
                showMessage(R.string.authorization_mes_not_filled_password)
                return@setOnClickListener
            }

            viewModel.setEvent(AuthorizationContract.Event.OnClickAuthorize(binding.login.name.text.toString(),binding.password.name.text.toString()))
        }

    }

    private fun initProperty() {
        binding.login.title.text = getString(R.string.registration_login_title)
        binding.login.light.isVisible = false

        binding.password.title.text = getString(R.string.registration_password_title)
        binding.password.light.isVisible = false
        binding.password.name.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
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
                    is AuthorizationContract.Effect.ShowMessage -> showMessage(effect.messageId)
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


    private fun showMessage(messageId: Int) {
        binding.load.visibility = View.GONE
        Snackbar
            .make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}