package com.example.hermes.ui.general

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.hermes.R
import com.example.hermes.databinding.GeneralActivityBinding
import com.example.hermes.domain.models.Address
import com.example.hermes.ui.addressManager.AddressManagerFragmentActivity

class GeneralActivity: AppCompatActivity() {
    companion object {
        const val ARGUMENT_ID_PAGE_CURRENT= "ARGUMENT_ID_PAGE_CURRENT"
        val arguments: MutableMap<String, Any> = mutableMapOf()
    }

    private var _binding: GeneralActivityBinding? = null
    private val binding get() = _binding!!

    private var page: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GeneralActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        init()
        setEvents()
    }

    private fun init() {
        page = arguments[ARGUMENT_ID_PAGE_CURRENT] as Int?
        page?.let { binding.viewPager2.currentItem = it }
        val adapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
    }

    private fun exit() {
        finishAffinity()
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
    }

    private fun setEvents() {
        binding.bottomNavigation.setOnItemSelectedListener  { item ->
            when(item.itemId) {
                R.id.page_shops -> {
                    binding.viewPager2.currentItem = 0
                    true
                }
                R.id.page_basket -> {
                    binding.viewPager2.currentItem = 1
                    true
                }
                R.id.page_orders -> {
                    binding.viewPager2.currentItem = 2
                    true
                }
                R.id.page_profile -> {
                    binding.viewPager2.currentItem = 3
                    true
                }
                else -> false
            }
        }

    }
}