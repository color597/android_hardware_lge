package org.lineageos.settings.device.dac.utils

import android.os.RemoteException
import vendor.lge.hardware.audio.dac.control.V1_0.AdvancedFeature

import vendor.lge.hardware.audio.dac.control.V1_0.HalFeature

import vendor.lge.hardware.audio.dac.control.V1_0.IDacAdvancedControl

import vendor.lge.hardware.audio.dac.control.V1_0.IDacHalControl


object QuadDAC {
    private const val TAG = "QuadDAC"
    fun enable(dhc: IDacHalControl, dac: IDacAdvancedControl) {
        try {
            val digitalFilter = getDigitalFilter(dhc)
            val soundPreset = getSoundPreset(dhc)
            val leftBalance = getLeftBalance(dhc)
            val rightBalance = getRightBalance(dhc)
            val mode = getDACMode(dac)
            val avcVol = getAVCVolume(dac)
            dhc.setFeatureValue(HalFeature.QuadDAC, 1)
            setDACMode(dac, mode)
            setLeftBalance(dhc, leftBalance)
            setRightBalance(dhc, rightBalance)
            setDigitalFilter(dhc, digitalFilter)
            setSoundPreset(dhc, soundPreset)
            setAVCVolume(dac, avcVol)
        } catch (e: Exception) {
        }
    }

    @Throws(RemoteException::class)
    fun disable(dhc: IDacHalControl) {
        dhc.setFeatureValue(HalFeature.QuadDAC, 0)
    }

    @Throws(RemoteException::class)
    fun setDACMode(dac: IDacAdvancedControl, mode: Int) {
        dac.setFeatureValue(AdvancedFeature.HifiMode, mode)
    }

    @Throws(RemoteException::class)
    fun getDACMode(dac: IDacAdvancedControl): Int {
        return dac.getFeatureValue(AdvancedFeature.HifiMode)
    }

    @Throws(RemoteException::class)
    fun setAVCVolume(dac: IDacAdvancedControl, avcVolume: Int) {
        dac.setFeatureValue(AdvancedFeature.AVCVolume, avcVolume)
    }

    @Throws(RemoteException::class)
    fun getAVCVolume(dac: IDacAdvancedControl): Int {
        return dac.getFeatureValue(AdvancedFeature.AVCVolume)
    }

    @Throws(RemoteException::class)
    fun setDigitalFilter(dhc: IDacHalControl, filter: Int) {
        dhc.setFeatureValue(HalFeature.DigitalFilter, filter)
    }

    @Throws(RemoteException::class)
    fun getDigitalFilter(dhc: IDacHalControl): Int {
        return dhc.getFeatureValue(HalFeature.DigitalFilter)
    }

    @Throws(RemoteException::class)
    fun setSoundPreset(dhc: IDacHalControl, preset: Int) {
        dhc.setFeatureValue(HalFeature.SoundPreset, preset)
    }

    @Throws(RemoteException::class)
    fun getSoundPreset(dhc: IDacHalControl): Int {
        return dhc.getFeatureValue(HalFeature.SoundPreset)
    }

    @Throws(RemoteException::class)
    fun setLeftBalance(dhc: IDacHalControl, balance: Int) {
        dhc.setFeatureValue(HalFeature.BalanceLeft, balance)
    }

    @Throws(RemoteException::class)
    fun getLeftBalance(dhc: IDacHalControl): Int {
        return dhc.getFeatureValue(HalFeature.BalanceLeft)
    }

    @Throws(RemoteException::class)
    fun setRightBalance(dhc: IDacHalControl, balance: Int) {
        dhc.setFeatureValue(HalFeature.BalanceRight, balance)
    }

    @Throws(RemoteException::class)
    fun getRightBalance(dhc: IDacHalControl): Int {
        return dhc.getFeatureValue(HalFeature.BalanceRight)
    }

    @Throws(RemoteException::class)
    fun isEnabled(dhc: IDacHalControl): Boolean {
        return dhc.getFeatureValue(HalFeature.QuadDAC) == 1
    }
}