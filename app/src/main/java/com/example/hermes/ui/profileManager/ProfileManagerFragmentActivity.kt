package com.example.hermes.ui.profileManager

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.hermes.R
import com.example.hermes.databinding.ProfileManagerFragmentActivityBinding
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.User
import com.example.hermes.ui.addressManager.AddressManagerFragmentActivity
import com.example.hermes.ui.registration.RegistrationContract
import com.google.android.material.snackbar.Snackbar
import java.util.*

class ProfileManagerFragmentActivity : FragmentActivity() {
    companion object {
        const val ARGUMENT_USER = "ARGUMENT_USER"
        const val ARGUMENT_CALL_BACK = "ARGUMENT_CALL_BACK"
        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: ProfileManagerFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileManagerViewModel by viewModels()
    lateinit var user: User

    var onCallBackListener: OnCallBackListener? = null

    interface OnCallBackListener {
        fun onBack(user: User)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ProfileManagerFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()

        binding.save.setOnClickListener {
            if (!isNeedPropertiesFilled()) {
                setErrorProperty()
                showMessage(R.string.profile_manager_mes_not_filled)
                return@setOnClickListener
            }
                user.name = binding.personName.editText?.text.toString()
                user.surname = binding.personSurname.editText?.text.toString()
                user.password = binding.password.editText?.text.toString()
                user.mail = binding.mail.editText?.text.toString()
                user.phoneNumber = binding.phone.editText?.text.toString()
                viewModel.setEvent(ProfileManagerContract.Event.OnClickSave(user))
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

        binding.password.editText?.doOnTextChanged { text, start, before, count ->
            if (text != null && binding.password.isErrorEnabled) {
                binding.password.isErrorEnabled = false
                binding.password.error = null
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            exit()
        }
    }

    private fun init() {
        user = arguments[ARGUMENT_USER] as User
        onCallBackListener = arguments[ARGUMENT_CALL_BACK] as OnCallBackListener
        binding.personName.editText?.setText(user.name)
        binding.personSurname.editText?.setText(user.surname)
        binding.password.editText?.setText(user.password)
        binding.mail.editText?.setText(user.mail)
        binding.phone.editText?.setText(user.phoneNumber)
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    ProfileManagerContract.State.Default -> {}
                    ProfileManagerContract.State.Setting -> toStateSetting()
                    ProfileManagerContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is ProfileManagerContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is ProfileManagerContract.Effect.OnExit -> {
                        onCallBackListener?.onBack(effect.user)
                        exit()
                    }
                }
            }
        }

    }

    private fun setErrorProperty() {
        if (binding.personName.editText?.text?.isEmpty() == true) {
            binding.personName.error = getString(R.string.profile_manager_error)
            binding.personName.isErrorEnabled = true
        } else {
            binding.personName.isErrorEnabled = false
            binding.personName.error = null
        }
        if (binding.personSurname.editText?.text?.isEmpty() == true) {
            binding.personSurname.error = getString(R.string.profile_manager_error)
            binding.personSurname.isErrorEnabled = true
        } else {
            binding.personSurname.isErrorEnabled = false
            binding.personSurname.error = null
        }
        if (binding.password.editText?.text?.isEmpty() == true) {
            binding.password.error = getString(R.string.profile_manager_error)
            binding.password.isErrorEnabled = true
        } else {
            binding.password.isErrorEnabled = false
            binding.password.error = null
        }
    }

    private fun isNeedPropertiesFilled(): Boolean {
        return !(binding.personName.editText?.text.toString() == ""
                || binding.personSurname.editText?.text.toString() == ""
                || binding.password.editText?.text.toString() == "")
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