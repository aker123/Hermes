package com.example.hermes.ui.filters

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hermes.R
import com.example.hermes.databinding.FilterDialogBinding
import com.example.hermes.domain.filters.FilterGroup


class FiltersDialog : DialogFragment() {

    var filterGroup: FilterGroup<*>? = null
    var onBackListener: OnBackListener? = null
    private var adapter: FiltersAdapter? = null
    private var binding: FilterDialogBinding? = null

    var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)

        val _binding = FilterDialogBinding.inflate(inflater, container, false)
        binding = _binding

        if (filterGroup == null || filterGroup?.filters == null) {
            exit()
        }

        init()

        setEvents()

        updateView()

        return _binding.root
    }

    private fun init() {
        adapter = FiltersAdapter()

        binding?.recyclerView?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.adapter = adapter
    }

    private fun setEvents() {
        binding?.topAppBar?.setNavigationOnClickListener {
            exit()
        }

        adapter?.onItemClickListener = FiltersAdapter.OnItemClickListener { filter ->
        }

        adapter?.onMenuClickListener = FiltersAdapter.OnMenuClickListener { filter ->
            showFiltersDialog(requireActivity().supportFragmentManager, filter.name, filter.filterGroup) {
                filter.enable = filter.filterGroup.getFiltersByEnable(true).isNotEmpty()
                updateView()
            }
        }

        adapter?.onCheckedChangeListener = FiltersAdapter.OnCheckedChangeListener { filter, state ->
            filter.enable = state
            filter.filterGroup.setEnable(state)
        }

        adapter?.onToggleChangeListener = FiltersAdapter.OnToggleChangeListener { filter, state ->
            filter.filterGroup.setEnable(false)
            state.enable = true
        }

        binding?.on?.setOnClickListener {
            filterGroup?.setEnable(true)
            updateView()
        }

        binding?.off?.setOnClickListener  {
            filterGroup?.setEnable(false)
            updateView()
        }
    }

    private fun showFiltersDialog(
        fm: FragmentManager?,
        caption: String?,
        filterGroup: FilterGroup<*>?,
        onBackListener: OnBackListener?
    ) {
        val dialog = FiltersDialog()
        if (caption != null) {
            dialog.title = caption
        }
        dialog.filterGroup = filterGroup
        dialog.onBackListener = onBackListener
        dialog.show(fm!!, "FilterDialog")
    }

    private fun updateView() {
        if (dialog == null) return

        binding?.apply {
            val noFilters = (filterGroup?.filters?.size ?: 0) == 0

            recyclerView.visibility =
                if (noFilters) View.GONE
                else View.VISIBLE

            message.visibility =
                if (noFilters) View.VISIBLE
                else View.GONE

            message.text =
                if (noFilters) getString(R.string.mes_no_filters)
                else ""

          binding?.topAppBar?.title  = title
        }

        adapter?.items = filterGroup?.filters ?: mutableListOf()
    }

    private fun exit() {
        onBackListener?.onBack()
        dismiss()
    }

    fun interface OnBackListener {
        fun onBack()
    }

    override fun onDestroyView() {
        adapter = null
        binding = null
        super.onDestroyView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                exit()
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
}