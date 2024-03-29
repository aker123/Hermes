package com.example.hermes.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.hermes.databinding.BasketFragmentActivityBinding

class BasketFragmentActivity: FragmentActivity() {
    private var basketFragment: BasketFragment? = null

    private var _binding: BasketFragmentActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = BasketFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        if (savedInstanceState == null) {
            basketFragment = BasketFragment().apply {
                this.isVisibleBack = true
            }

            supportFragmentManager.apply {
                basketFragment?.let {
                    beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(binding.fragment.id, it)
                        .commit()
                }
            }
        }
    }

    private fun exit() {
        finish()
    }

    override fun onBackPressed() {
        exit()
        super.onBackPressed()
    }
}