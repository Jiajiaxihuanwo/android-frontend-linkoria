package com.xinlei.frontend.linkoria.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xinlei.frontend.linkoria.app.databinding.FragmentMainBinding
import com.xinlei.frontend.linkoria.app.menu.DmListFragment
import com.xinlei.frontend.linkoria.app.menu.FriendListFragment
import com.xinlei.frontend.linkoria.app.menu.NotificationsFragment
import com.xinlei.frontend.linkoria.app.menu.ProfileFragment
import com.xinlei.frontend.linkoria.app.menu.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNav()
        // Pantalla inicial
        showFragment(DmListFragment())
    }

    private fun setupBottomNav() {
        binding.bottomNavigationMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats         -> showFragment(DmListFragment())
                R.id.nav_friends       -> showFragment(FriendListFragment())
                R.id.nav_search        -> showFragment(SearchFragment())
                R.id.nav_notifications -> showFragment(NotificationsFragment())
                R.id.nav_profile       -> showFragment(ProfileFragment())
            }
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.container_main, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}