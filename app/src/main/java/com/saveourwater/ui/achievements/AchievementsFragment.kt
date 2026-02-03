package com.saveourwater.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.saveourwater.R
import com.saveourwater.SaveOurWaterApplication
import com.saveourwater.data.local.entities.AchievementCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment for Achievements Gallery
 * PHASE3-UI-P0-026: Design Achievements Fragment Layout
 */
class AchievementsFragment : Fragment() {

    private val viewModel: AchievementsViewModel by viewModels {
        val app = requireActivity().application as SaveOurWaterApplication
        AchievementsViewModel.Factory(app.achievementManager)
    }

    private lateinit var rvAchievements: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var tvProgressCount: TextView
    private lateinit var tvProgressMessage: TextView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: AchievementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupTabs()
        setupRecyclerView()
        observeViewModel()

        // Refresh achievements on load
        viewModel.refreshAchievements()
    }

    private fun initViews(view: View) {
        rvAchievements = view.findViewById(R.id.rvAchievements)
        tabLayout = view.findViewById(R.id.tabLayout)
        progressBar = view.findViewById(R.id.progressBar)
        tvProgressCount = view.findViewById(R.id.tvProgressCount)
        tvProgressMessage = view.findViewById(R.id.tvProgressMessage)
        emptyState = view.findViewById(R.id.emptyState)
    }

    private fun setupTabs() {
        // Add "All" tab
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        
        // Add category tabs
        AchievementCategory.values().forEach { category ->
            val tabName = category.name.lowercase().replaceFirstChar { it.uppercase() }
            tabLayout.addTab(tabLayout.newTab().setText(tabName))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> viewModel.selectCategory(null) // All
                    else -> viewModel.selectCategory(AchievementCategory.values()[tab.position - 1])
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = AchievementsAdapter { achievement ->
            // Show detail bottom sheet
            val bottomSheet = AchievementDetailBottomSheet.newInstance(achievement)
            bottomSheet.show(childFragmentManager, "achievement_detail")
        }

        rvAchievements.layoutManager = GridLayoutManager(requireContext(), 2)
        rvAchievements.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe achievements list
                launch {
                    viewModel.achievements.collectLatest { achievements ->
                        adapter.submitList(achievements)
                        emptyState.visibility = if (achievements.isEmpty()) View.VISIBLE else View.GONE
                        rvAchievements.visibility = if (achievements.isEmpty()) View.GONE else View.VISIBLE
                    }
                }

                // Observe progress
                launch {
                    viewModel.unlockedCount.collectLatest { unlocked ->
                        val total = viewModel.totalCount.value
                        tvProgressCount.text = "$unlocked/$total"
                        updateProgressMessage(unlocked, total)
                    }
                }

                launch {
                    viewModel.progressPercentage.collectLatest { percentage ->
                        progressBar.setProgressCompat(percentage, true)
                    }
                }
            }
        }
    }

    private fun updateProgressMessage(unlocked: Int, total: Int) {
        val message = when {
            unlocked == 0 -> "Start tracking to unlock achievements!"
            unlocked < total / 4 -> "Great start! Keep going!"
            unlocked < total / 2 -> "You're making progress!"
            unlocked < total -> "Almost there! Keep it up!"
            else -> "🎉 Amazing! All achievements unlocked!"
        }
        tvProgressMessage.text = message
    }
}
