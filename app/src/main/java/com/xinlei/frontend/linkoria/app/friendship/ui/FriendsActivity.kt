package com.xinlei.frontend.linkoria.app.friendship.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.databinding.ActivityFriendsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendsActivity : AppCompatActivity() {

    private var _binding : ActivityFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overrideActivityTransition()

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentFriend.id)
                as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        setUpListeners()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpListeners() {
        binding.ivArrowBack.setOnClickListener {
            finish()
        }
    }
}