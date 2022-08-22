package com.example.hermes.ui.general

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.hermes.R
import com.example.hermes.databinding.GeneralActivityBinding

class GeneralActivity: AppCompatActivity() {
    private var _binding: GeneralActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GeneralActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        init()
        setEvents()
    }

    private fun init() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
    }

    private fun setEvents() {
        binding.bottomNavigation.setOnItemSelectedListener  { item ->
            when(item.itemId) {
                R.id.item1 -> {
                    binding.viewPager2.currentItem = 0
                    true
                }
                R.id.item2 -> {
                    binding.viewPager2.currentItem = 1
                    true
                }
                else -> false
            }
        }

    }
}