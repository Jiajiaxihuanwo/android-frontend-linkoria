package com.xinlei.frontend.linkoria.app.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.databinding.FragmentDashboardBinding
import com.xinlei.frontend.linkoria.app.server.ui.adapter.ServersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var navController: NavController
    private lateinit var adapter: ServersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(binding.navHostFragmentDashboard.id)
                as NavHostFragment
        navController = navHostFragment.navController

        setupClickListeners()
        setupServerRecyclerView()
    }

    private fun setupServerRecyclerView() {
        adapter = ServersAdapter { server ->
            // 点击已经在 Adapter 内部跳转了
        }
        binding.rvServers.layoutManager = LinearLayoutManager(context)
        binding.rvServers.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnAddServer.setOnClickListener {
            // Lógica para añadir servidor
        }

        binding.btnDms.setOnClickListener {
            navController.navigate(R.id.DMListFragment)
        }
    }
}