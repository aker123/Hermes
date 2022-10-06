package com.example.hermes.ui.orderHistory

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.hermes.databinding.OrderHistoryFragmentActivityBinding

class OrderHistoryFragmentActivity : FragmentActivity() {
    private var orderHistoryFragment: OrderHistoryFragment? = null

    private var _binding: OrderHistoryFragmentActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OrderHistoryFragmentActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        if (savedInstanceState == null) {
            orderHistoryFragment = OrderHistoryFragment().apply {
                this.isVisibleBack = true
                this.onlyActiveOrders = false
            }

            supportFragmentManager.apply {
                orderHistoryFragment?.let {
                    beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(binding.fragment.id, it)
                        .commit()
                }
            }
        }


    }
}