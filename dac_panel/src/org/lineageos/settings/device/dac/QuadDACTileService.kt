package org.lineageos.settings.device.dac

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import org.lineageos.settings.device.dac.utils.QuadDAC
import vendor.lge.hardware.audio.dac.control.V1_0.IDacAdvancedControl
import vendor.lge.hardware.audio.dac.control.V1_0.IDacHalControl

class QuadDACTileService : TileService() {

    private val headsetPluggedTileReceiver = HeadsetPluggedTileReceiver()
    private lateinit var dac: IDacAdvancedControl
    private lateinit var dhc: IDacHalControl
    private var dacServiceAvailable = false

    override fun onClick() {
        super.onClick()
        try {
            if (QuadDAC.isEnabled(dhc)) {
                QuadDAC.disable(dhc)
                setTileInactive()
            } else {
                QuadDAC.enable(dhc, dac)
                setTileActive()
            }
        } catch (e: Exception) {
        }
    }

    override fun onStartListening() {
        super.onStartListening()
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        registerReceiver(headsetPluggedTileReceiver, filter)
        val am = getSystemService(AudioManager::class.java)
        try {
            dac = IDacAdvancedControl.getService(true)
            dhc = IDacHalControl.getService(true)
            dacServiceAvailable = true
        } catch (e: Exception) {
        }
        if (!am.isWiredHeadsetOn) {
            setTileUnavailable()
            return
        }
        try {
            if (QuadDAC.isEnabled(dhc)) {
                setTileActive()
            } else {
                setTileInactive()
            }
        } catch (e: Exception) {
        }
        if (!dacServiceAvailable) {
            setTileUnavailable()
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        unregisterReceiver(headsetPluggedTileReceiver)
    }

    private fun setTileActive() {
        qsTile.apply {
            state = Tile.STATE_ACTIVE
            label = resources.getString(R.string.quad_dac_on)
            updateTile()
        }
    }

    private fun setTileInactive() {
        qsTile.apply {
            state = Tile.STATE_INACTIVE
            label = resources.getString(R.string.quad_dac_off)
            updateTile()
        }
    }

    private fun setTileUnavailable() {
        qsTile.apply {
            state = Tile.STATE_UNAVAILABLE
            label = resources.getString(R.string.quad_dac_unavail)
            updateTile()
        }
    }

    private inner class HeadsetPluggedTileReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                when (state) {
                    1 -> try {
                        if (QuadDAC.isEnabled(dhc)) {
                            setTileActive()
                        } else {
                            setTileInactive()
                        }
                    } catch (e: Exception) {
                    }

                    0 -> setTileUnavailable()
                }
            }
        }
    }

    companion object {
        private const val TAG = "QuadDACTileService"
    }
}