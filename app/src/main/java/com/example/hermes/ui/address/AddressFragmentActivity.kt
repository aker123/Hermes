package com.example.hermes.ui.address

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.AddressFragmentActivityBinding
import com.example.hermes.domain.models.Address
import com.example.hermes.ui.addressManager.AddressManagerFragmentActivity
import com.example.hermes.ui.map.MapDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.*


class AddressFragmentActivity : FragmentActivity() {

    private var _binding: AddressFragmentActivityBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddressViewModel by viewModels()

    private var adapter: AddressAdapter? = null

    private var addresses: MutableList<Address> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AddressFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initObservers()
        init()

        viewModel.getAddresses().let {
            addresses = it.toMutableList()
            update()
        }

        binding.newAddress.setOnClickListener {
            val dialog = MapDialogFragment()
            dialog.onBackListener = MapDialogFragment.OnBackListener { address ->
                address.uid = UUID.randomUUID().toString()
                viewModel.setEvent(AddressContract.Event.OnCreateAddressClick(address))
            }
            dialog.isVisibleAddress = false
            dialog.show(supportFragmentManager, "MapFragmentActivity")
        }

        adapter?.onItemClickListener = AddressAdapter.OnItemClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.address_set_active)
                .setNegativeButton(R.string.address_no) { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton(R.string.address_yes) { dialog, which ->
                    viewModel.setEvent(
                        AddressContract.Event.OnAddressClick(
                            it,
                            addresses
                        )
                    )
                }
                .show()
        }

        adapter?.onEditingClickListener = AddressAdapter.OnEditingClickListener {
            viewModel.setEvent(AddressContract.Event.OnEditingClick(it))
        }

        adapter?.onDeleteClickListener = AddressAdapter.OnDeleteClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.address_delete)
                .setNegativeButton(R.string.address_no) { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton(R.string.address_yes) { dialog, which ->
                    viewModel.setEvent(AddressContract.Event.OnDeleteClick(it, addresses))
                }
                .show()
        }

        binding.topAppBar.setNavigationOnClickListener {
            exit()
        }
    }

    private fun init() {
        adapter = AddressAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    AddressContract.State.Default -> {}
                    AddressContract.State.Setting -> toStateSetting()
                    AddressContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is AddressContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                    is AddressContract.Effect.OnAddressManagerFragmentActivity -> {
                        val i = Intent()
                        i.setClass(
                            this@AddressFragmentActivity,
                            AddressManagerFragmentActivity::class.java
                        )
                        AddressManagerFragmentActivity.arguments[AddressManagerFragmentActivity.ARGUMENT_ADDRESS] =
                            effect.address
                        AddressManagerFragmentActivity.arguments[AddressManagerFragmentActivity.ARGUMENT_IS_DELETE] =
                            effect.isVisibleDelete

                        AddressManagerFragmentActivity.arguments[AddressManagerFragmentActivity.ARGUMENT_CALL_BACK] =
                            object : AddressManagerFragmentActivity.OnCallBackListener {
                                override fun onAdd(address: Address) {
                                    addresses.add(address)
                                    update()
                                }

                                override fun onDelete(address: Address) {
                                    addresses.remove(address)
                                    update()
                                }

                            }

                        startActivity(i)
                    }
                    is AddressContract.Effect.Update -> {
                        effect.addresses?.let { addresses = it }
                        update()
                    }
                }
            }
        }

    }

    private fun update() {
        adapter?.items = addresses
        if (adapter?.itemCount == 0) {
            binding.recyclerView.isVisible = false
            binding.message.visibility = View.VISIBLE
            binding.message.text = getString(R.string.address_not_addresses)
        } else {
            binding.recyclerView.isVisible = true
            binding.message.visibility = View.GONE
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
        binding.recyclerView.isVisible = true
        binding.load.isVisible = false
    }

    private fun toStateLoading() {
        binding.recyclerView.isVisible = false
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