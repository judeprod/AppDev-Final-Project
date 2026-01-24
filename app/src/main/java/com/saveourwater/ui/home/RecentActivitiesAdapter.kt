package com.saveourwater.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saveourwater.R
import com.saveourwater.data.local.entities.WaterActivity
import com.saveourwater.utils.WaterCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Adapter for recent activities list
 * PHASE2-UI-P2-022: Create Recent Activities Adapter
 */
class RecentActivitiesAdapter : 
    ListAdapter<WaterActivity, RecentActivitiesAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivActivityIcon)
        private val tvType: TextView = itemView.findViewById(R.id.tvActivityType)
        private val tvTime: TextView = itemView.findViewById(R.id.tvActivityTime)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvUsageAmount)

        fun bind(activity: WaterActivity) {
            tvType.text = WaterCalculator.getActivityLabel(activity.activityType)
            tvTime.text = getRelativeTime(activity.timestamp)
            tvAmount.text = "${activity.litersUsed.toInt()} L"
            // TODO: Set appropriate icon based on activity type
            ivIcon.setImageResource(android.R.drawable.ic_menu_compass)
        }

        private fun getRelativeTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> {
                    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
                    "$mins min ago"
                }
                diff < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    "$hours hours ago"
                }
                diff < TimeUnit.DAYS.toMillis(7) -> {
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    "$days days ago"
                }
                else -> {
                    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<WaterActivity>() {
        override fun areItemsTheSame(oldItem: WaterActivity, newItem: WaterActivity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WaterActivity, newItem: WaterActivity): Boolean {
            return oldItem == newItem
        }
    }
}
