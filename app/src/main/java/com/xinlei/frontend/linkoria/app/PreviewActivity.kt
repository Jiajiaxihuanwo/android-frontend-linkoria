package com.xinlei.frontend.linkoria.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PreviewActivity : AppCompatActivity() {

    private val layouts = listOf(
        "fragment_auth"          to R.layout.fragment_auth,
        "fragment_login"         to R.layout.fragment_login,
        "fragment_register"      to R.layout.fragment_register,
        "fragment_main"          to R.layout.fragment_main,
        "fragment_chat"          to R.layout.fragment_chat,
        "fragment_profile"       to R.layout.fragment_profile,
        "fragment_friend_list"   to R.layout.fragment_friend_list,
        "item_dm"                to R.layout.item_dm,
        "item_server"            to R.layout.item_server,
        "item_message_sent"      to R.layout.item_message_sent,
        "item_message_received"  to R.layout.item_message_received,
        "item_friend"            to R.layout.item_friend,
        "item_channel"           to R.layout.item_channel,
    )

    private val COLOR_SIDEBAR  = 0xFF1E1F22.toInt()
    private val COLOR_DM       = 0xFF2B2D31.toInt()
    private val COLOR_CHAT     = 0xFF313338.toInt()
    private val COLOR_ACCENT   = 0xFF5865F2.toInt()
    private val COLOR_DIVIDER  = 0xFF3F4147.toInt()
    private val COLOR_TEXT     = 0xFFFFFFFF.toInt()
    private val COLOR_MUTED    = 0xFF6D6F78.toInt()
    private val COLOR_SELECTED = 0xFF35373C.toInt()

    private var tabBar: LinearLayout? = null
    private var contentFrame: FrameLayout? = null
    private var selectedTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = intent.getIntExtra("layout_id", -1)
        if (layoutId != -1) {
            setContentView(layoutId)
            return
        }

        buildUI()
    }

    private fun buildUI() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(COLOR_SIDEBAR)
        }

        // Título
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(24, 48, 24, 16)
            setBackgroundColor(COLOR_SIDEBAR)
        }
        val dot = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(12, 12).also { it.marginEnd = 12 }
            background = roundedDrawable(COLOR_ACCENT, 6f)
        }
        val titleTv = TextView(this).apply {
            text = "Layout Preview"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(COLOR_TEXT)
        }
        header.addView(dot)
        header.addView(titleTv)
        root.addView(header)

        // Divider
        root.addView(dividerView())

        // Tab bar (horizontal scroll)
        val tabScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(COLOR_SIDEBAR)
        }
        tabBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 8, 16, 0)
        }
        tabScroll.addView(tabBar)
        root.addView(tabScroll)

        // Divider
        root.addView(dividerView())

        // Content
        contentFrame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
            setBackgroundColor(COLOR_DM)
        }
        root.addView(contentFrame)

        setContentView(root)

        buildTabs()
        showPage(0)
    }

    private fun buildTabs() {
        val categories = listOf(
            "Fragments" to layouts.filter { it.first.startsWith("fragment") },
            "Items"     to layouts.filter { it.first.startsWith("item") }
        )

        categories.forEachIndexed { index, (label, _) ->
            val tab = TextView(this).apply {
                text = label
                textSize = 13f
                setTextColor(if (index == 0) COLOR_TEXT else COLOR_MUTED)
                setPadding(20, 16, 20, 16)
                gravity = Gravity.CENTER
                background = if (index == 0) tabActiveDrawable() else null
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = 4 }
                setOnClickListener { selectTab(index) }
            }
            tabBar?.addView(tab)
        }
    }

    private fun selectTab(index: Int) {
        selectedTab = index
        for (i in 0 until (tabBar?.childCount ?: 0)) {
            val tab = tabBar?.getChildAt(i) as? TextView ?: continue
            tab.setTextColor(if (i == index) COLOR_TEXT else COLOR_MUTED)
            tab.background = if (i == index) tabActiveDrawable() else null
        }
        showPage(index)
    }

    private fun showPage(index: Int) {
        contentFrame?.removeAllViews()

        val isFragments = index == 0
        val filtered = if (isFragments)
            layouts.filter { it.first.startsWith("fragment") }
        else
            layouts.filter { it.first.startsWith("item") }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(COLOR_DM)
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 32)
        }

        val sectionLabel = TextView(this).apply {
            text = if (isFragments) "PANTALLAS" else "COMPONENTES DE LISTA"
            textSize = 11f
            setTextColor(COLOR_MUTED)
            letterSpacing = 0.15f
            setPadding(8, 0, 8, 12)
        }
        container.addView(sectionLabel)

        filtered.forEach { (name, id) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(20, 18, 20, 18)
                background = roundedDrawable(COLOR_CHAT, 8f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 8 }
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    startActivity(
                        Intent(this@PreviewActivity, PreviewActivity::class.java)
                            .putExtra("layout_id", id)
                    )
                }
            }

            // Icono lateral
            val icon = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(4, 36).also { it.marginEnd = 16 }
                background = roundedDrawable(COLOR_ACCENT, 2f)
            }

            // Nombre
            val nameTv = TextView(this).apply {
                text = name
                textSize = 14f
                setTextColor(COLOR_TEXT)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
             }

            // Flecha
            val arrow = TextView(this).apply {
                text = "›"
                textSize = 20f
                setTextColor(COLOR_MUTED)
            }

            card.addView(icon)
            card.addView(nameTv)
            card.addView(arrow)
            container.addView(card)
        }

        scroll.addView(container)
        contentFrame?.addView(scroll)
    }

    private fun dividerView() = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1
        )
        setBackgroundColor(COLOR_DIVIDER)
    }

    private fun tabActiveDrawable(radius: Float = 6f) =
        roundedDrawable(COLOR_SELECTED, radius)

    private fun roundedDrawable(color: Int, radius: Float) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius * resources.displayMetrics.density
        }
}