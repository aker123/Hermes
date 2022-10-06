package com.example.hermes.ui.welcome

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import com.example.hermes.R
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hermes.databinding.WelcomeActivityBinding
import com.example.hermes.ui.entrance.EntranceActivity
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission
import kotlinx.coroutines.*


class WelcomeActivity : AppCompatActivity() {

    private var _binding: WelcomeActivityBinding? = null
    private val binding get() = _binding!!
    lateinit var permissionResult: TedPermissionResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = WelcomeActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

           GlobalScope.launch {
                permissionResult =
                    TedPermission.create()
                        .setDeniedMessage(R.string.welcome_rationale_message)
                        .setPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        .check()
                if (permissionResult.isGranted){
                    val i = Intent()
                    i.setClass(this@WelcomeActivity, EntranceActivity::class.java)
                    startActivity(i)
                }else
                    Toast.makeText(
                        this@WelcomeActivity,
                        "Доступ запрещен\n",
                        Toast.LENGTH_SHORT
                    ).show()

            }
    }
}