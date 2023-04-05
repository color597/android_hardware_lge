package org.lineageos.settings.device.dac

import android.os.Bundle
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity

class QuadDACPanelActivity : CollapsingToolbarBaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().replace(
            R.id.content_frame,
            QuadDACPanelFragment()
        ).commit()
    }
}