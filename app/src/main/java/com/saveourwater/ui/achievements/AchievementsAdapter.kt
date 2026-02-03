package com.saveourwater.ui.achievements

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.saveourwater.R
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementTier
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView Adapter for Achievement cards
 * PHASE3-UI-P1-029: Implement Achievement Adapter
 */
class AchievementsAdapter(
    private val onAchievementClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementsAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view, onAchievementClick)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(
        itemView: View,
        private val onAchievementClick: (Achievement) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val card: MaterialCardView = itemView.findViewById(R.id.achievementCard)
        private val tierBadge: View = itemView.findViewById(R.id.tierBadge)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val ivLock: ImageView = itemView.findViewById(R.id.ivLock)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressBar)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        private val tvUnlockedAt: TextView = itemView.findViewById(R.id.tvUnlockedAt)

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(achievement: Achievement) {
            // Set basic info
            tvName.text = achievement.name
            tvDescription.text = achievement.description

            // Set tier color
            val tierColor = getTierColor(achievement.tier)
            tierBadge.setBackgroundColor(ContextCompat.getColor(itemView.context, tierColor))

            // Set icon
            try {
                ivIcon.setImageResource(achievement.iconResId)
            } catch (e: Exception) {
                ivIcon.setImageResource(R.drawable.ic_achievement_default)
            }

            if (achievement.isUnlocked) {
                // Unlocked state
                ivLock.visibility = View.GONE
                progressBar.visibility = View.GONE
                tvProgress.visibility = View.GONE
                tvUnlockedAt.visibility = View.VISIBLE
                
                ivIcon.colorFilter = null // Full color
                ivIcon.alpha = 1.0f
                tvName.alpha = 1.0f
                tvDescription.alpha = 1.0f

                achievement.unlockedAt?.let {
                    tvUnlockedAt.text = "✓ ${dateFormat.format(Date(it))}"
                }
            } else {
                // Locked state with progress
                ivLock.visibility = View.VISIBLE
                tvUnlockedAt.visibility = View.GONE
                
                // Apply grayscale to icon
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                ivIcon.colorFilter = ColorMatrixColorFilter(matrix)
                ivIcon.alpha = 0.6f
                tvName.alpha = 0.8f
                tvDescription.alpha = 0.7f

                // Show progress
                if (achievement.progress > 0 || achievement.targetValue > 1) {
                    progressBar.visibility = View.VISIBLE
                    tvProgress.visibility = View.VISIBLE
                    
                    val progressPercent = (achievement.progress * 100) / achievement.targetValue
                    progressBar.setProgressCompat(progressPercent, true)
                    tvProgress.text = "${achievement.progress}/${achievement.targetValue}"
                } else {
                    progressBar.visibility = View.GONE
                    tvProgress.visibility = View.GONE
                }
            }

            // Click listener
            card.setOnClickListener {
                onAchievementClick(achievement)
            }

            // Add press animation
            card.setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start()
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }
                }
                false
            }
        }

        private fun getTierColor(tier: AchievementTier): Int {
            return when (tier) {
                AchievementTier.BRONZE -> R.color.bronze
                AchievementTier.SILVER -> R.color.silver
                AchievementTier.GOLD -> R.color.gold
                AchievementTier.PLATINUM -> R.color.platinum
            }
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}
