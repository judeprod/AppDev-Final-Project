package com.saveourwater.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.saveourwater.R
import com.saveourwater.data.local.entities.ActivityType
import kotlinx.coroutines.launch

/**
 * Fragment for water tracking
 * PHASE2-UI-P0-012: Design Tracking Fragment Layout
 * PHASE2-FEAT-P1-018: Build Real-time Progress Updates
 */
class TrackingFragment : Fragment() {

    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var activityAdapter: ActivityAdapter

    // Views
    private lateinit var tvTodayUsage: TextView
    private lateinit var tvGoalStatus: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var rvActivities: RecyclerView
    private lateinit var fabManualEntry: ExtendedFloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun bindViews(view: View) {
        tvTodayUsage = view.findViewById(R.id.tvTodayUsage)
        tvGoalStatus = view.findViewById(R.id.tvGoalStatus)
        progressBar = view.findViewById(R.id.progressBar)
        rvActivities = view.findViewById(R.id.rvActivities)
        fabManualEntry = view.findViewById(R.id.fabManualEntry)
    }

    private fun setupRecyclerView() {
        activityAdapter = ActivityAdapter { item ->
            if (item.type == ActivityType.CUSTOM) {
                showManualEntryDialog()
            } else {
                viewModel.logQuickActivity(item.type)
                Toast.makeText(
                    context,
                    "Logged ${item.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        rvActivities.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = activityAdapter
        }
    }

    private fun setupClickListeners() {
        fabManualEntry.setOnClickListener {
            showManualEntryDialog()
        }
    }

    private fun showManualEntryDialog() {
        // TODO: Implement LogActivityBottomSheet
        Toast.makeText(context, "Manual Entry - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        // Observe activity items
        viewModel.activityItems.observe(viewLifecycleOwner) { items ->
            activityAdapter.submitList(items)
        }

        // Observe daily goal
        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            updateGoalDisplay(goal)
        }

        // Observe today's usage (StateFlow)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayUsage.collect { usage ->
                    updateUsageDisplay(usage)
                }
            }
        }
    }

    private fun updateUsageDisplay(usage: Double) {
        tvTodayUsage.text = getString(R.string.tracking_usage_label, usage)
        val goal = viewModel.dailyGoal.value ?: 150.0
        tvGoalStatus.text = getString(R.string.tracking_goal_status, usage, goal)
        
        val progress = viewModel.calculateProgress(usage)
        progressBar.setProgress(progress, true)
    }

    private fun updateGoalDisplay(goal: Double) {
        val usage = viewModel.todayUsage.value
        tvGoalStatus.text = getString(R.string.tracking_goal_status, usage, goal)
    }
}
