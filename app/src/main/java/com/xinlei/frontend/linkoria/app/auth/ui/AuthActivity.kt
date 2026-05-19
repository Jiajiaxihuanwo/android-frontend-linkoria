package com.xinlei.frontend.linkoria.app.auth.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xinlei.frontend.linkoria.app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}