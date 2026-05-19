package com.xinlei.frontend.linkoria.app.root

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.auth.ui.AuthActivity
import com.xinlei.frontend.linkoria.app.core.session.SessionManager
import com.xinlei.frontend.linkoria.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val isLoggedIn = sessionManager.getAccessTokenOnce() != null

            if (!isLoggedIn) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
                return@launch
            }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentHome.id)
                as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        if (intent.getBooleanExtra(EXTRA_OPEN_DM_LIST, false)) {
            // navega a DashboardFragment con DmListFragment anidado
            openDashboardWithDmList()
        }
    }

    private fun openDashboardWithDmList() {
        navController.navigate(R.id.dashboardFragment)
        binding.bottomNav.selectedItemId = R.id.dashboardFragment
    }
    companion object {
        const val EXTRA_OPEN_DM_LIST = "extra_open_dm_list"
    }
}