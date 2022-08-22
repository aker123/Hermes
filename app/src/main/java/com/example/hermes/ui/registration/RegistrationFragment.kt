package com.example.hermes.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
        initProperty()

        binding.register.setOnClickListener {
            if (!isNeedPropertiesFilled()) {
                showMessage(R.string.registration_mes_not_filled)
                return@setOnClickListener
            }

            val user = User(
                UUID.randomUUID().toString(),
                binding.login.name.text.toString(),
                binding.password.name.text.toString(),
                binding.personSurname.name.text.toString(),
                binding.personName.name.text.toString(),
                binding.phone.name.text.toString(),
                binding.mail.name.text.toString()
            )

            viewModel.setEvent(RegistrationContract.Event.OnClickRegister(user))
        }

        binding.personName.name.addTextChangedListener {
            binding.personName.light.isVisible = binding.personName.name.text.toString() == ""
        }
        binding.personSurname.name.addTextChangedListener {
            binding.personSurname.light.isVisible = binding.personSurname.name.text.toString() == ""
        }
        binding.login.name.addTextChangedListener {
            binding.login.light.isVisible = binding.login.name.text.toString() == ""
        }
        binding.password.name.addTextChangedListener {
            binding.password.light.isVisible = binding.password.name.text.toString() == ""
        }
        binding.passwordConfirmation.name.addTextChangedListener {
            binding.passwordConfirmation.light.isVisible =
                binding.passwordConfirmation.name.text.toString() == ""
        }
        binding.phone.name.addTextChangedListener {
            binding.phone.light.isVisible = binding.phone.name.text.toString() == ""
        }

        binding.mail.name.addTextChangedListener {
            binding.mail.light.isVisible = binding.mail.name.text.toString() == ""
        }

    }

    private fun isNeedPropertiesFilled(): Boolean {
        return !(binding.personName.name.text.toString() == ""
                || binding.personSurname.name.text.toString() == ""
                || binding.login.name.text.toString() == ""
                || binding.password.name.text.toString() == ""
                || binding.passwordConfirmation.name.text.toString() == "")
    }

    private fun initProperty() {
        binding.personName.title.text = getString(R.string.registration_person_name_title)
        binding.personName.light.isVisible = binding.personName.name.text.toString() == ""

        binding.personSurname.title.text = getString(R.string.registration_person_surname_title)
        binding.personSurname.light.isVisible = binding.personSurname.name.text.toString() == ""

        binding.login.title.text = getString(R.string.registration_login_title)
        binding.login.light.isVisible = binding.login.name.text.toString() == ""

        binding.password.title.text = getString(R.string.registration_password_title)
        binding.password.light.isVisible = binding.password.name.text.toString() == ""

        binding.passwordConfirmation.title.text =
            getString(R.string.registration_password_confirmation_title)
        binding.passwordConfirmation.light.isVisible =
            binding.passwordConfirmation.name.text.toString() == ""

        binding.phone.title.text = getString(R.string.registration_phone_title)
        binding.phone.light.isVisible = false
        binding.phone.name.inputType = InputType.TYPE_CLASS_PHONE

        binding.mail.title.text = getString(R.string.registration_mail_title)
        binding.mail.light.isVisible = false
        binding.mail.name.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
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
                    is RegistrationContract.Effect.ShowMessage -> {
                        showMessage(effect.messageId)
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
        binding.personName.root.isVisible = true
        binding.personSurname.root.isVisible = true
        binding.login.root.isVisible = true
        binding.password.root.isVisible = true
        binding.passwordConfirmation.root.isVisible = true
        binding.phone.root.isVisible = true
        binding.mail.root.isVisible = true
        binding.load.isVisible = false
        binding.register.isVisible = true
    }

    private fun toStateLoading() {
        binding.personName.root.isVisible = false
        binding.personSurname.root.isVisible = false
        binding.login.root.isVisible = false
        binding.password.root.isVisible = false
        binding.passwordConfirmation.root.isVisible = false
        binding.phone.root.isVisible = false
        binding.mail.root.isVisible = false
        binding.load.isVisible = true
        binding.register.isVisible = false
    }

    private fun showMessage(messageId: Int) {
        Snackbar
            .make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}