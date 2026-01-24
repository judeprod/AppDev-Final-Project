package com.saveourwater.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.saveourwater.R
import kotlinx.coroutines.launch

/**
 * Home Dashboard Fragment
 * PHASE2-UI-P1-019: Create Home Dashboard Fragment
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recentActivitiesAdapter: RecentActivitiesAdapter

    // Views
    private lateinit var tvTodayUsage: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvGoalProgress: TextView
    private lateinit var tvInsight: TextView
    private lateinit var weeklyChart: BarChart
    private lateinit var rvRecentActivities: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecyclerView()
        setupChart()
        observeViewModel()
    }

    private fun bindViews(view: View) {
        tvTodayUsage = view.findViewById(R.id.tvTodayUsage)
        tvStreak = view.findViewById(R.id.tvStreak)
        tvGoalProgress = view.findViewById(R.id.tvGoalProgress)
        tvInsight = view.findViewById(R.id.tvInsight)
        weeklyChart = view.findViewById(R.id.weeklyChart)
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities)
    }

    private fun setupRecyclerView() {
        recentActivitiesAdapter = RecentActivitiesAdapter()
        rvRecentActivities.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentActivitiesAdapter
        }
    }

    private fun setupChart() {
        weeklyChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)
        }

        // Placeholder data (will be replaced with real data)
        val entries = listOf(
            BarEntry(0f, 100f),
            BarEntry(1f, 120f),
            BarEntry(2f, 80f),
            BarEntry(3f, 150f),
            BarEntry(4f, 90f),
            BarEntry(5f, 110f),
            BarEntry(6f, 0f)
        )

        val dataSet = BarDataSet(entries, "Weekly Usage").apply {
            color = resources.getColor(R.color.primary_500, null)
            setDrawValues(false)
        }

        weeklyChart.data = BarData(dataSet)
        weeklyChart.invalidate()
    }

    private fun observeViewModel() {
        // Observe today's usage
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayUsage.collect { usage ->
                    tvTodayUsage.text = "${usage.toInt()} L"
                    val progress = viewModel.calculateProgress(usage)
                    tvGoalProgress.text = "$progress%"
                }
            }
        }

        // Observe streak
        viewModel.currentStreak.observe(viewLifecycleOwner) { streak ->
            tvStreak.text = "🔥 $streak"
        }

        // Observe insight message
        viewModel.insightMessage.observe(viewLifecycleOwner) { message ->
            tvInsight.text = message
        }

        // Observe recent activities
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recentActivities.collect { activities ->
                    recentActivitiesAdapter.submitList(activities)
                }
            }
        }
    }
}
