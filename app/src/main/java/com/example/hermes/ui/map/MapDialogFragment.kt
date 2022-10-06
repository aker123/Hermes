package com.example.hermes.ui.map

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.MapDialogFragmentBinding
import com.example.hermes.domain.Map
import com.example.hermes.domain.models.Address
import com.example.hermes.ui.basket.BasketAdapter
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.MapKitFactory

class MapDialogFragment : DialogFragment() {

    private var _binding: MapDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    lateinit var map: Map
    var onBackListener: OnBackListener? = null
    var address: Address? = null
    var addresses: List<Address> = listOf()
    var isVisibleAddress = true
    private var adapter: AddressAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)

        _binding = MapDialogFragmentBinding
            .inflate(inflater, container, false)

        initObservers()
        init()

        map = Map(
            requireActivity(), binding.mapView
        ) {
            binding.street.editText?.setText(it)
        }

        map.create()

        viewModel.getAddress().let {
            addresses = it
            update()
        }

        val searchView = binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText != "") map.setSearch(newText)
                }
                return true
            }
        })

        binding.topAppBar.setNavigationOnClickListener {
            dismiss()
        }

        binding.onPosition.setOnClickListener {
            map.movePositon()
        }

        adapter?.onItemClickListener  = AddressAdapter.OnItemClickListener {
            address = it
            onBackClick()
        }

        binding.onContinue.setOnClickListener {
            onBackClick()
        }

        return binding.root
    }

    private fun update() {
        adapter?.items = addresses
        binding.recyclerView.isVisible = adapter?.itemCount != 0 && isVisibleAddress
    }

    private fun init() {
        adapter = AddressAdapter()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect { state ->
                when (state) {
                    MapContract.State.Default -> {}
                    MapContract.State.Setting -> toStateSetting()
                    MapContract.State.Loading -> toStateLoading()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is MapContract.Effect.ShowMessage<*> -> {
                        when (effect.message) {
                            is Int -> showMessage(effect.message)
                            is String -> showMessage(effect.message)
                        }
                    }
                }
            }
        }
    }

    private fun onBackClick() {
        exit()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                dismiss()
                super.onBackPressed()
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun exit() {
        onBackListener?.onBack(address = address?.let {
            address
        } ?: Address(
            "",
            binding.street.editText?.text.toString(),
            "",
            0L,
            "",
            "",
            false
        ))

        dismiss()
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

    fun interface OnBackListener {
        fun onBack(address: Address)
    }
}