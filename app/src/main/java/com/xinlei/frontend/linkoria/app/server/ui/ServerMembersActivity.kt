package com.xinlei.frontend.linkoria.app.server.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xinlei.frontend.linkoria.app.core.ui.UiState
import com.xinlei.frontend.linkoria.app.databinding.FragmentServerMembersBinding
import com.xinlei.frontend.linkoria.app.server.domain.model.ServerMember
import com.xinlei.frontend.linkoria.app.server.ui.adapter.server_member.ServerMembersAdapter
import com.xinlei.frontend.linkoria.app.server.ui.members.ServerMembersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServerMembersActivity : AppCompatActivity() {

    private lateinit var binding: FragmentServerMembersBinding
    private val viewModel: ServerMembersViewModel by viewModels()
    private lateinit var adapter: ServerMembersAdapter

    private var serverId: Long = -1
    private var serverName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用现有的 Fragment 布局
        binding = FragmentServerMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 从 Intent 获取参数
        serverId = intent.getLongExtra("extra_server_id", -1)
        serverName = intent.getStringExtra("extra_server_name") ?: ""

        if (serverId != -1L) {
            binding.tvServerName.text = serverName
            setupRecyclerView()
            setupSearchListener()
            observeMembers()
            viewModel.loadServerMembers(serverId)
        } else {
            Toast.makeText(this, "Error: No server selected", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.ivArrowBack.setOnClickListener {
            finish()  // Activity 用 finish() 返回
        }
    }

    private fun setupRecyclerView() {
        adapter = ServerMembersAdapter { member ->
            onMemberClick(member)
        }
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = adapter
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchMembers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeMembers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.membersState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvMembers.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvMembers.visibility = View.VISIBLE
                            adapter.submitList(state.data)
                            binding.tvMemberCount.text = "Miembros (${state.data.size})"
                        }

                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@ServerMembersActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        UiState.Idle -> {}
                    }
                }
            }
        }
    }

    private fun onMemberClick(member: ServerMember) {
        // 点击成员，打开私聊
        Toast.makeText(this, "Member: ${member.username}", Toast.LENGTH_SHORT).show()

        // 打开私聊
        // ChatActivity.startDM(this, member.userId, member.username, member.avatarUrl)
    }
}