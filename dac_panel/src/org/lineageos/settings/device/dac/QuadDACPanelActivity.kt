package org.lineageos.settings.device.dac

import android.R
import android.os.Bundle
import android.preference.PreferenceActivity

class QuadDACPanelActivity : PreferenceActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction().replace(
            R.id.content,
            QuadDACPanelFragment()
        ).commit()
    }
}