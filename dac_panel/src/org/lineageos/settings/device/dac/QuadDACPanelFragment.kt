package org.lineageos.settings.device.dac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import org.lineageos.settings.device.dac.ui.BalancePreference
import org.lineageos.settings.device.dac.utils.Constants
import org.lineageos.settings.device.dac.utils.QuadDAC
import vendor.lge.hardware.audio.dac.control.V1_0.AdvancedFeature
import vendor.lge.hardware.audio.dac.control.V1_0.HalFeature
import vendor.lge.hardware.audio.dac.control.V1_0.IDacAdvancedControl
import vendor.lge.hardware.audio.dac.control.V1_0.IDacHalControl

class QuadDACPanelFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

    private lateinit var quadDacSwitch: SwitchPreference
    private lateinit var soundPresetList: ListPreference
    private lateinit var digitalFilterList: ListPreference
    private lateinit var modeList: ListPreference
    private lateinit var balancePreference: BalancePreference
    private lateinit var avcVolume: SeekBarPreference

    private lateinit var headsetPluggedFragmentReceiver: HeadsetPluggedFragmentReceiver

    private lateinit var dac: IDacAdvancedControl
    private lateinit var dhc: IDacHalControl

    private lateinit var dacFeatures: ArrayList<Int>
    private lateinit var dhcFeatures: ArrayList<Int>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        activity.actionBar!!.setDisplayHomeAsUpEnabled(true)
        headsetPluggedFragmentReceiver = HeadsetPluggedFragmentReceiver()
        try {
            dac = IDacAdvancedControl.getService(true)
            dhc = IDacHalControl.getService(true)
            dacFeatures = dac.supportedAdvancedFeatures
            dhcFeatures = dhc.supportedHalFeatures
        } catch (e: Exception) {
            Log.d(TAG, "onCreatePreferences: $e")
        }
        addPreferencesFromResource(R.xml.quaddac_panel)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        try {
            if (preference is SwitchPreference) {
                val setDacOn = newValue as Boolean
                return if (setDacOn) {
                    enableExtraSettings()
                    QuadDAC.enable(dhc, dac)
                    true
                } else {
                    disableExtraSettings()
                    QuadDAC.disable(dhc)
                    true
                }
            }
            if (preference is ListPreference) {
                when (preference.key) {
                    Constants.HIFI_MODE_KEY -> {
                        val mode = preference.findIndexOfValue(newValue as String)
                        QuadDAC.setDACMode(dac, mode)
                        return true
                    }
                    Constants.DIGITAL_FILTER_KEY -> {
                        val digitalFilter = preference.findIndexOfValue(newValue as String)
                        QuadDAC.setDigitalFilter(dhc, digitalFilter)
                        return true
                    }
                    Constants.SOUND_PRESET_KEY -> {
                        val soundPreset = preference.findIndexOfValue(newValue as String)
                        QuadDAC.setSoundPreset(dhc, soundPreset)
                        return true
                    }
                }
                return false
            }
            if (preference is SeekBarPreference) {
                if (preference.key == Constants.AVC_VOLUME_KEY) {
                    return if (newValue is Int) {
                        //avc_volume.setSummary( ((double)avc_vol) + " db");
                        QuadDAC.setAVCVolume(dac, newValue)
                        true
                    } else {
                        false
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "onPreferenceChange: $e")
        }
        return false
    }

    override fun onResume() {
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        activity.registerReceiver(headsetPluggedFragmentReceiver, filter)
        super.onResume()
    }

    override fun onPause() {
        activity.unregisterReceiver(headsetPluggedFragmentReceiver)
        super.onPause()
    }

    override fun addPreferencesFromResource(preferencesResId: Int) {
        super.addPreferencesFromResource(preferencesResId)
        // Initialize preferences
        val am = context.getSystemService(AudioManager::class.java)

        quadDacSwitch = findPreference(Constants.DAC_SWITCH_KEY)!!
        quadDacSwitch.onPreferenceChangeListener = this
        soundPresetList = findPreference(Constants.SOUND_PRESET_KEY)!!
        soundPresetList.onPreferenceChangeListener = this
        digitalFilterList = findPreference(Constants.DIGITAL_FILTER_KEY)!!
        digitalFilterList.onPreferenceChangeListener = this
        modeList = findPreference(Constants.HIFI_MODE_KEY)!!
        modeList.onPreferenceChangeListener = this
        avcVolume = findPreference(Constants.AVC_VOLUME_KEY)!!
        avcVolume.onPreferenceChangeListener = this
        balancePreference = findPreference(Constants.BALANCE_KEY)!!
        try {
            if (dhcFeatures.contains(HalFeature.QuadDAC)) {
                quadDacSwitch.isVisible = true
            }
            if (dhcFeatures.contains(HalFeature.SoundPreset)) {
                soundPresetList.isVisible = true
                soundPresetList.setValueIndex(dhc.getFeatureValue(HalFeature.SoundPreset))
            }
            if (dhcFeatures.contains(HalFeature.DigitalFilter)) {
                digitalFilterList.isVisible = true
                digitalFilterList.setValueIndex(dhc.getFeatureValue(HalFeature.DigitalFilter))
            }
            if (dhcFeatures.contains(HalFeature.BalanceLeft)
                && dhcFeatures.contains(HalFeature.BalanceRight)
            ) {
                balancePreference.isVisible = true
                balancePreference.setDhc(dhc)
            }
            if (dacFeatures.contains(AdvancedFeature.AVCVolume)) {
                avcVolume.isVisible = true
                val range = dac.getSupportedAdvancedFeatureValues(AdvancedFeature.AVCVolume).range
                avcVolume.min = range.min.toInt()
                avcVolume.max = range.max.toInt()
                avcVolume.value = dac.getFeatureValue(AdvancedFeature.AVCVolume)
            }
            if (dacFeatures.contains(AdvancedFeature.HifiMode)) {
                modeList.isVisible = true
                modeList.setValueIndex(dac.getFeatureValue(AdvancedFeature.HifiMode))
            }
            if (am.isWiredHeadsetOn) {
                quadDacSwitch.isEnabled = true
                if (QuadDAC.isEnabled(dhc)) {
                    quadDacSwitch.isChecked = true
                    enableExtraSettings()
                } else {
                    quadDacSwitch.isChecked = false
                    disableExtraSettings()
                }
            } else {
                quadDacSwitch.isEnabled = false
                disableExtraSettings()
                if (QuadDAC.isEnabled(dhc)) {
                    quadDacSwitch.isChecked = true
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "addPreferencesFromResource: $e")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableExtraSettings() {
        soundPresetList.isEnabled = true
        digitalFilterList.isEnabled = true
        modeList.isEnabled = true
        avcVolume.isEnabled = true
        balancePreference.isEnabled = true
    }

    private fun disableExtraSettings() {
        soundPresetList.isEnabled = false
        digitalFilterList.isEnabled = false
        modeList.isEnabled = false
        avcVolume.isEnabled = false
        balancePreference.isEnabled = false
    }

    private inner class HeadsetPluggedFragmentReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                when (intent.getIntExtra("state", -1)) {
                    1 -> {
                        quadDacSwitch.isEnabled = true
                        if (quadDacSwitch.isChecked) {
                            enableExtraSettings()
                        }
                    }

                    0 -> {
                        quadDacSwitch.isEnabled = false
                        disableExtraSettings()
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "QuadDACPanelFragment"
    }
}