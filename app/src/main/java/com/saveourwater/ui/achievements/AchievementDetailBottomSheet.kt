package com.saveourwater.ui.achievements

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.saveourwater.R
import com.saveourwater.data.local.entities.Achievement
import com.saveourwater.data.local.entities.AchievementTier
import java.text.SimpleDateFormat
import java.util.*

/**
 * BottomSheet dialog for achievement details
 * PHASE3-UI-P2-031: Create Achievement Detail Dialog
 */
class AchievementDetailBottomSheet : BottomSheetDialogFragment() {

    private var achievement: Achievement? = null
    private val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_TIER = "arg_tier"
        private const val ARG_ICON = "arg_icon"
        private const val ARG_TARGET = "arg_target"
        private const val ARG_PROGRESS = "arg_progress"
        private const val ARG_IS_UNLOCKED = "arg_is_unlocked"
        private const val ARG_UNLOCKED_AT = "arg_unlocked_at"

        fun newInstance(achievement: Achievement): AchievementDetailBottomSheet {
            return AchievementDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, achievement.id)
                    putString(ARG_NAME, achievement.name)
                    putString(ARG_DESC, achievement.description)
                    putString(ARG_TIER, achievement.tier.name)
                    putInt(ARG_ICON, achievement.iconResId)
                    putInt(ARG_TARGET, achievement.targetValue)
                    putInt(ARG_PROGRESS, achievement.progress)
                    putBoolean(ARG_IS_UNLOCKED, achievement.isUnlocked)
                    achievement.unlockedAt?.let { putLong(ARG_UNLOCKED_AT, it) }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_achievement_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments ?: return
        val name = args.getString(ARG_NAME) ?: return
        val description = args.getString(ARG_DESC) ?: ""
        val tier = AchievementTier.valueOf(args.getString(ARG_TIER) ?: "BRONZE")
        val iconResId = args.getInt(ARG_ICON)
        val targetValue = args.getInt(ARG_TARGET)
        val progress = args.getInt(ARG_PROGRESS)
        val isUnlocked = args.getBoolean(ARG_IS_UNLOCKED)
        val unlockedAt = if (args.containsKey(ARG_UNLOCKED_AT)) args.getLong(ARG_UNLOCKED_AT) else null

        // Bind views
        val tvTierBadge: TextView = view.findViewById(R.id.tvTierBadge)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val statusCard: MaterialCardView = view.findViewById(R.id.statusCard)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val progressBar: LinearProgressIndicator = view.findViewById(R.id.progressBar)
        val tvProgressDetail: TextView = view.findViewById(R.id.tvProgressDetail)
        val tvUnlockedAt: TextView = view.findViewById(R.id.tvUnlockedAt)
        val btnShare: MaterialButton = view.findViewById(R.id.btnShare)
        val btnClose: MaterialButton = view.findViewById(R.id.btnClose)

        // Set tier badge
        tvTierBadge.text = getTierEmoji(tier) + " " + tier.name
        tvTierBadge.backgroundTintList = ContextCompat.getColorStateList(requireContext(), getTierColor(tier))

        // Set icon
        try {
            ivIcon.setImageResource(iconResId)
        } catch (e: Exception) {
            ivIcon.setImageResource(R.drawable.ic_achievement_default)
        }

        // Set name and description
        tvName.text = name
        tvDescription.text = description

        // Set status
        if (isUnlocked) {
            tvStatus.text = "✓ Unlocked!"
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
            statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.success_light))
            
            progressBar.visibility = View.GONE
            tvProgressDetail.visibility = View.GONE
            
            unlockedAt?.let {
                tvUnlockedAt.text = dateFormat.format(Date(it))
                tvUnlockedAt.visibility = View.VISIBLE
            }

            // Show share button
            btnShare.visibility = View.VISIBLE
            btnShare.setOnClickListener {
                shareAchievement(name, description, tier)
            }
        } else {
            tvStatus.text = "🔒 Locked"
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_600))
            
            if (targetValue > 1) {
                progressBar.visibility = View.VISIBLE
                tvProgressDetail.visibility = View.VISIBLE
                
                val percentage = (progress * 100) / targetValue
                progressBar.setProgressCompat(percentage, false)
                tvProgressDetail.text = "$progress of $targetValue completed"
            }
            
            tvUnlockedAt.visibility = View.GONE
            btnShare.visibility = View.GONE
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun shareAchievement(name: String, description: String, tier: AchievementTier) {
        val shareText = """
            ${getTierEmoji(tier)} I just unlocked "$name" in Save Our Water!
            
            $description
            
            Join me in saving water! 💧
            #SaveOurWater #WaterConservation
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Achievement"))
    }

    private fun getTierEmoji(tier: AchievementTier): String {
        return when (tier) {
            AchievementTier.BRONZE -> "🥉"
            AchievementTier.SILVER -> "🥈"
            AchievementTier.GOLD -> "🥇"
            AchievementTier.PLATINUM -> "💎"
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
