package org.lineageos.settings.device.dac.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.android.material.button.MaterialButton
import org.lineageos.settings.device.dac.R
import org.lineageos.settings.device.dac.utils.QuadDAC
import vendor.lge.hardware.audio.dac.control.V1_0.HalFeature
import vendor.lge.hardware.audio.dac.control.V1_0.IDacHalControl

class BalancePreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = defStyleAttr
) : Preference(context!!, attrs, defStyleAttr, defStyleRes) {
    private var leftBalance = 0
    private var rightBalance = 0
    private var maxAllowedValue = 0
    private var minAllowedValue = -12
    private lateinit var btLeftPlus: MaterialButton
    private lateinit var btLeftMinus: MaterialButton
    private lateinit var btRightPlus: MaterialButton
    private lateinit var btRightMinus: MaterialButton
    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private lateinit var dhc: IDacHalControl

    init {
        layoutResource = R.layout.balance_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.itemView.apply {
            isClickable = false
            btLeftPlus = findViewById(R.id.bt_left_plus)
            btLeftMinus = findViewById(R.id.bt_left_minus)
            btRightPlus = findViewById(R.id.bt_right_plus)
            btRightMinus = findViewById(R.id.bt_right_minus)
            tvLeft = findViewById(R.id.tv_left_val)
            tvRight = findViewById(R.id.tv_right_val)
        }
        btLeftPlus.setOnClickListener { updateLeftBalance(true) }
        btLeftMinus.setOnClickListener { updateLeftBalance(false) }
        btRightPlus.setOnClickListener { updateRightBalance(true) }
        btRightMinus.setOnClickListener { updateRightBalance(false) }
        loadBalanceConfiguration()
    }

    private fun loadBalanceConfiguration() {
        try {
            val range = dhc.getSupportedHalFeatureValues(HalFeature.BalanceLeft).range
            minAllowedValue = range.min.toInt()
            maxAllowedValue = range.max.toInt()
            leftBalance = -QuadDAC.getLeftBalance(dhc)
            rightBalance = -QuadDAC.getRightBalance(dhc)
        } catch (e: Exception) {
            Log.d(TAG, "loadBalanceConfiguration: $e")
        }
        tvLeft.text = "${-leftBalance.toDouble() / 2} db"
        tvRight.text = "${-rightBalance.toDouble() / 2} db"
        when (leftBalance) {
            maxAllowedValue -> {
                btLeftPlus.isEnabled = false
            }
            minAllowedValue -> {
                btLeftMinus.isEnabled = false
            }
            else -> {
                btLeftPlus.isEnabled = true
                btLeftMinus.isEnabled = true
            }
        }
        when (rightBalance) {
            maxAllowedValue -> {
                btRightPlus.isEnabled = false
            }
            minAllowedValue -> {
                btRightMinus.isEnabled = false
            }
            else -> {
                btRightPlus.isEnabled = true
                btRightMinus.isEnabled = true
            }
        }
    }

    private fun updateLeftBalance(increase: Boolean) {
        if (increase) {
            if (leftBalance < maxAllowedValue) {
                leftBalance += 1
                btLeftMinus.isEnabled = true
                if (leftBalance == maxAllowedValue) {
                    btLeftPlus.isEnabled = false
                }
            }
        } else {
            if (leftBalance > minAllowedValue) {
                leftBalance -= 1
                btLeftPlus.isEnabled = true
                if (leftBalance == minAllowedValue) {
                    btLeftMinus.isEnabled = false
                }
            }
        }
        try {
            QuadDAC.setLeftBalance(dhc, -leftBalance)
        } catch (e: Exception) {
            Log.d(TAG, "updateLeftBalance: $e")
        }
        tvLeft.text = "${leftBalance.toDouble() / 2} db"
    }

    private fun updateRightBalance(increase: Boolean) {
        if (increase) {
            if (rightBalance < maxAllowedValue) {
                rightBalance += 1
                btRightMinus.isEnabled = true
                if (rightBalance == maxAllowedValue) {
                    btRightPlus.isEnabled = false
                }
            }
        } else {
            if (rightBalance > minAllowedValue) {
                rightBalance -= 1
                btRightPlus.isEnabled = true
                if (rightBalance == minAllowedValue) {
                    btRightMinus.isEnabled = false
                }
            }
        }
        try {
            QuadDAC.setRightBalance(dhc, -rightBalance)
        } catch (e: Exception) {
            Log.d(TAG, "updateRightBalance: $e")
        }
        tvRight.text = "${rightBalance.toDouble() / 2} db"
    }

    fun setDhc(iDhc: IDacHalControl) {
        dhc = iDhc
    }

    companion object {
        private const val TAG = "BalancePreference"
    }
}