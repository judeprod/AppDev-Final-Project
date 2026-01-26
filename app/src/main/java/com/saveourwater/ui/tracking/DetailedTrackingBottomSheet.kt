package com.saveourwater.ui.tracking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.saveourwater.R
import com.saveourwater.data.local.entities.FlowPressure
import com.saveourwater.data.local.entities.WaterSource
import com.saveourwater.utils.WaterCalculator

/**
 * Bottom Sheet for detailed water activity tracking
 * PHASE2-FEAT-P1-023: Enhanced Estimation Logic (Professor Feedback)
 * 
 * Allows user to calculate water volume based on:
 * - Water source (Shower vs Bucket/Faucet)
 * - Flow pressure (Low, Normal, High)
 * - Duration (minutes)
 * - Eco-Mode (intermittent water use)
 */
class DetailedTrackingBottomSheet : BottomSheetDialogFragment() {

    private var onActivityLogged: ((Double) -> Unit)? = null

    // UI Components
    private lateinit var toggleSourceType: MaterialButtonToggleGroup
    private lateinit var togglePressure: MaterialButtonToggleGroup
    private lateinit var switchEcoMode: MaterialSwitch
    private lateinit var etDuration: TextInputEditText
    private lateinit var tvEstimatedVolume: TextView
    private lateinit var btnLogActivity: MaterialButton

    // State
    private var currentSource: WaterSource = WaterSource.SHOWER
    private var currentPressure: FlowPressure = FlowPressure.NORMAL
    private var isEcoMode: Boolean = false
    private var durationMinutes: Int = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_detailed_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bindViews(view)
        setupListeners()
        updateEstimatedVolume()
    }

    private fun bindViews(view: View) {
        toggleSourceType = view.findViewById(R.id.toggleSourceType)
        togglePressure = view.findViewById(R.id.togglePressure)
        switchEcoMode = view.findViewById(R.id.switchEcoMode)
        etDuration = view.findViewById(R.id.etDuration)
        tvEstimatedVolume = view.findViewById(R.id.tvEstimatedVolume)
        btnLogActivity = view.findViewById(R.id.btnLogActivity)
    }

    private fun setupListeners() {
        // Source type selection
        toggleSourceType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentSource = when (checkedId) {
                    R.id.btnShower -> WaterSource.SHOWER
                    R.id.btnBucket -> WaterSource.BUCKET_FAUCET
                    else -> WaterSource.SHOWER
                }
                updateEstimatedVolume()
            }
        }

        // Pressure selection
        togglePressure.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentPressure = when (checkedId) {
                    R.id.btnPressureLow -> FlowPressure.LOW
                    R.id.btnPressureNormal -> FlowPressure.NORMAL
                    R.id.btnPressureHigh -> FlowPressure.HIGH
                    else -> FlowPressure.NORMAL
                }
                updateEstimatedVolume()
            }
        }

        // Eco-mode toggle
        switchEcoMode.setOnCheckedChangeListener { _, isChecked ->
            isEcoMode = isChecked
            updateEstimatedVolume()
        }

        // Duration input
        etDuration.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                durationMinutes = s?.toString()?.toIntOrNull() ?: 0
                updateEstimatedVolume()
            }
        })

        // Log activity button
        btnLogActivity.setOnClickListener {
            if (durationMinutes <= 0) {
                Toast.makeText(context, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val estimatedVolume = calculateVolume()
            onActivityLogged?.invoke(estimatedVolume)
            dismiss()
        }
    }

    private fun calculateVolume(): Double {
        return WaterCalculator.calculateEstimatedVolume(
            source = currentSource,
            pressure = currentPressure,
            durationMinutes = durationMinutes,
            isIntermittent = isEcoMode
        )
    }

    private fun updateEstimatedVolume() {
        val volume = calculateVolume()
        tvEstimatedVolume.text = getString(R.string.liters_format, volume)
    }

    /**
     * Set callback for when activity is logged
     */
    fun setOnActivityLoggedListener(listener: (Double) -> Unit) {
        onActivityLogged = listener
    }

    companion object {
        const val TAG = "DetailedTrackingBottomSheet"

        fun newInstance(): DetailedTrackingBottomSheet {
            return DetailedTrackingBottomSheet()
        }
    }
}
