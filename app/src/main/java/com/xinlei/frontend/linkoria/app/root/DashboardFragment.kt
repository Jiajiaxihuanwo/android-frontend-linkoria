package com.xinlei.frontend.linkoria.app.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xinlei.frontend.linkoria.app.R
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.core.ui.image.ImageLoader
import com.xinlei.frontend.linkoria.app.databinding.FragmentDashboardBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.Server
import com.xinlei.frontend.linkoria.app.server.ui.adapter.server.ServersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    @Inject
    lateinit var imageLoader: ImageLoader

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var serversAdapter: ServersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(binding.navHostFragmentDashboard.id)
                as NavHostFragment
        navController = navHostFragment.navController

        configInsets()

        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        viewModel.observerServerList()
    }

    private fun configInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )
            insets
        }
    }

    private fun setupRecyclerView() {
        serversAdapter = ServersAdapter(imageLoader) { server ->
            navController.navigate(R.id.action_DMListFragment_to_channelListFragment)
        }
        binding.rvServers.layoutManager = LinearLayoutManager(context)
        binding.rvServers.adapter = serversAdapter
    }

    private fun setupClickListeners() {
        binding.btnAddServer.setOnClickListener {
            showCreateServerDialog()
        }

        binding.btnDms.setOnClickListener {
            navController.navigate(R.id.DMListFragment)
        }
    }

    private fun showCreateServerDialog() {
        val input = android.widget.EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Crear un servidor")
            .setMessage("Introduce el nombre del servidor")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val serverName = input.text.toString()
                if (serverName.isNotBlank()) {
                    viewModel.createSever(serverName)//方法写成变量名
                } else {
                    Toast.makeText(requireContext(), "Introduce el nombre del servidor", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeUiState() {
        observeServerListState()
        observeCreateServerState()
    }

    private fun observeServerListState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userListState.collect { state ->//StateFlow lleva collect
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> showServerList(state.data)
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun observeCreateServerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createServerState.collect { state ->
                    when (state) {
                        is UiState.Loading -> Unit
                        is UiState.Success -> {
                            Toast.makeText(context, "Creado con éxito", Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Error -> Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        is UiState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun showServerList(servers: List<Server>) {
        serversAdapter.submitList(servers)

        if (servers.isEmpty()) {
            //TODO: cambiar feedback de que no hay servidor
            Unit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}