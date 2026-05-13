package com.xinlei.frontend.linkoria.app.chat.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private var _binding : ActivityChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        overrideActivityTransition()
        setContentView(binding.root)

        configInsets()
    }

    private fun overrideActivityTransition() {
        overrideActivityTransition(
            OVERRIDE_TRANSITION_OPEN,
            R.anim.slide_in_right,
            R.anim.static_on
        )
        overrideActivityTransition(
            OVERRIDE_TRANSITION_CLOSE,
            R.anim.static_on,
            R.anim.slide_out_right
        )
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}