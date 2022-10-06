package com.example.hermes.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.RegistrationFragmentBinding
import com.example.hermes.domain.models.User
import com.example.hermes.ui.general.GeneralActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import java.util.*


class RegistrationFragment : Fragment() {

    private var _binding: RegistrationFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegistrationFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.register.setOnClickListener {
            if (!isNeedPropertiesFilled()) {
                setErrorProperty()
                showMessage(R.string.registration_mes_not_filled)
                return@setOnClickListener
            }

            val user = User(
                UUID.randomUUID().toString(),
                binding.login.editText?.text.toString(),
                binding.password.editText?.text.toString(),
                binding.personSurname.editText?.text.toString(),
                binding.personName.editText?.text.toString(),
                binding.phone.editText?.text.toString(),
                binding.mail.editText?.text.toString()
            )

            viewModel.setEvent(RegistrationContract.Event.OnClickRegister(user))
        }

        binding.personName.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.personName.isErrorEnabled) {
                binding.personName.isErrorEnabled = false
                binding.personName.error = null
            }
        }
        binding.personSurname.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.personSurname.isErrorEnabled) {
                binding.personSurname.isErrorEnabled = false
                binding.personSurname.error = null
            }
        }
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
        binding.passwordConfirmation.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.passwordConfirmation.isErrorEnabled) {
                binding.passwordConfirmation.isErrorEnabled = false
                binding.passwordConfirmation.error = null
            }
        }
    }

    private fun setErrorProperty() {
        if (binding.personName.editText?.text?.isEmpty() == true) {
            binding.personName.error = getString(R.string.registration_error)
            binding.personName.isErrorEnabled = true
        } else {
            binding.personName.isErrorEnabled = false
            binding.personName.error = null
        }
        if (binding.personSurname.editText?.text?.isEmpty() == true) {
            binding.personSurname.error = getString(R.string.registration_error)
            binding.personSurname.isErrorEnabled = true
        } else {
            binding.personSurname.isErrorEnabled = false
            binding.personSurname.error = null
        }
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
            checkPasswordEquality()
        }
        if (binding.passwordConfirmation.editText?.text?.isEmpty() == true) {
            binding.passwordConfirmation.error = getString(R.string.registration_error)
            binding.passwordConfirmation.isErrorEnabled = true
        } else {
            binding.passwordConfirmation.isErrorEnabled = false
            binding.passwordConfirmation.error = null
            checkPasswordEquality()
        }
    }

    private fun checkPasswordEquality() {
        if (binding.password.editText?.text != binding.passwordConfirmation.editText?.text) {
            binding.password.error = getString(R.string.registration_error_password)
            binding.password.isErrorEnabled = true
            binding.passwordConfirmation.error = getString(R.string.registration_error_password)
            binding.passwordConfirmation.isErrorEnabled = true
        } else {
            binding.passwordConfirmation.isErrorEnabled = false
            binding.passwordConfirmation.error = null
            binding.password.isErrorEnabled = false
            binding.password.error = null
        }
    }

    private fun isNeedPropertiesFilled(): Boolean {
        return !(binding.personName.editText?.text.toString() == ""
                || binding.personSurname.editText?.text.toString() == ""
                || binding.login.editText?.text.toString() == ""
                || binding.password.editText?.text.toString() == ""
                || binding.passwordConfirmation.editText?.text.toString() == ""
                || binding.password.editText?.text.toString() != binding.passwordConfirmation.editText?.text.toString())
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    RegistrationContract.State.Default -> {}
                    RegistrationContract.State.Setting -> toStateSetting()
                    RegistrationContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is RegistrationContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is RegistrationContract.Effect.OnGeneralActivity -> {
                        onGeneralActivity()
                    }
                }
            }
        }
    }

    private fun onGeneralActivity() {
        val i = Intent()
        activity?.let { i.setClass(it, GeneralActivity::class.java) }
        activity?.startActivity(i)
    }

    private fun toStateSetting() {
        binding.personName.isVisible = true
        binding.personSurname.isVisible = true
        binding.login.isVisible = true
        binding.password.isVisible = true
        binding.passwordConfirmation.isVisible = true
        binding.phone.isVisible = true
        binding.mail.isVisible = true
        binding.load.isVisible = false
        binding.register.isVisible = true
    }

    private fun toStateLoading() {
        binding.personName.isVisible = false
        binding.personSurname.isVisible = false
        binding.login.isVisible = false
        binding.password.isVisible = false
        binding.passwordConfirmation.isVisible = false
        binding.phone.isVisible = false
        binding.mail.isVisible = false
        binding.load.isVisible = true
        binding.register.isVisible = false
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