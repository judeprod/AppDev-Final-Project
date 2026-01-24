package com.saveourwater.ui.tracking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saveourwater.R

/**
 * Adapter for activity buttons grid
 * PHASE2-UI-P1-013: Build Activity Button Components
 */
class ActivityAdapter(
    private val onActivityClick: (ActivityItem) -> Unit
) : ListAdapter<ActivityItem, ActivityAdapter.ActivityViewHolder>(ActivityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_button, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position), onActivityClick)
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivActivityIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvActivityName)
        private val tvUsage: TextView = itemView.findViewById(R.id.tvEstimatedUsage)

        fun bind(item: ActivityItem, onClick: (ActivityItem) -> Unit) {
            ivIcon.setImageResource(item.iconRes)
            tvName.text = item.name
            tvUsage.text = item.estimatedUsage
            itemView.setOnClickListener { onClick(item) }
        }
    }

    class ActivityDiffCallback : DiffUtil.ItemCallback<ActivityItem>() {
        override fun areItemsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem == newItem
        }
    }
}
