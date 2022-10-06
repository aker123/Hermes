package com.example.hermes.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hermes.databinding.ProfileFragmentBinding
import com.example.hermes.domain.models.User
import com.example.hermes.ui.address.AddressFragmentActivity
import com.example.hermes.ui.orderHistory.OrderHistoryFragmentActivity
import com.example.hermes.ui.profileManager.ProfileManagerFragmentActivity
import com.google.android.material.snackbar.Snackbar

class ProfileFragment: Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding
            .inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        user = viewModel.getUser()
        update()

        binding.historyOrders.setOnClickListener {
            viewModel.setEvent(ProfileContract.Event.OnClickOrderHistory)
        }

        binding.address.setOnClickListener {
            viewModel.setEvent(ProfileContract.Event.OnClickAddress)
        }

        binding.data.setOnClickListener {
            viewModel.setEvent(ProfileContract.Event.OnClickData)
        }

        binding.exit.setOnClickListener {
            viewModel.setEvent(ProfileContract.Event.OnClickExit)
        }
    }

    @SuppressLint("SetTextI18n")
    fun update(){
        val name = user?.name ?: ""
        val surname = user?.surname ?: ""
        binding.name.text =  "$name $surname"
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    ProfileContract.State.Default -> {}
                    ProfileContract.State.Setting -> {}
                    ProfileContract.State.Loading -> {}
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is ProfileContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is ProfileContract.Effect.OnExit -> {
                        activity?.finish()
                    }
                    is ProfileContract.Effect.OnOrderHistoryFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, OrderHistoryFragmentActivity::class.java) }
                        activity?.startActivity(i)
                    }
                    is ProfileContract.Effect.OnAddressFragmentActivity -> {
                        val i = Intent()
                        activity?.let { i.setClass(it, AddressFragmentActivity::class.java) }
                        activity?.startActivity(i)
                    }
                    is ProfileContract.Effect.OnDataFragmentActivity -> {
                        if (user == null) return@collect
                        val i = Intent()
                        activity?.let { i.setClass(it, ProfileManagerFragmentActivity::class.java) }
                        user?.let { ProfileManagerFragmentActivity.arguments[ProfileManagerFragmentActivity.ARGUMENT_USER] = it }
                        ProfileManagerFragmentActivity.arguments[ProfileManagerFragmentActivity.ARGUMENT_CALL_BACK] =
                            object : ProfileManagerFragmentActivity.OnCallBackListener {
                                override fun onBack(user: User) {
                                    this@ProfileFragment.user = user
                                    update()
                                }

                            }

                        startActivity(i)
                    }
                }
            }
        }
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