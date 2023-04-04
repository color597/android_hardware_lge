/*
 * Copyright (C) 2016 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lineageos.settings.device.dac

import android.database.Cursor
import android.database.MatrixCursor
import android.provider.SearchIndexableResource
import android.provider.SearchIndexablesContract
import android.provider.SearchIndexablesProvider

class QuadDACPanelSearchIndexablesProvider : SearchIndexablesProvider() {

    override fun onCreate() = true

    override fun queryXmlResources(projection: Array<String>): Cursor {
        val cursor = MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS)
        cursor.addRow(generateResourceRef(INDEXABLE_RES[SEARCH_IDX_QUADDAC_PANEL]))
        return cursor
    }

    override fun queryRawData(projection: Array<String>) =
        MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS)

    override fun queryNonIndexableKeys(projection: Array<String>) =
        MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS)

    companion object {
        private const val TAG = "QuadDACPanelSearchIndexablesProvider"
        const val SEARCH_IDX_QUADDAC_PANEL = 0
        private val INDEXABLE_RES = arrayOf(
            SearchIndexableResource(
                1, R.xml.quaddac_panel,
                QuadDACPanelFragment::class.java.name,
                R.drawable.ic_quad_dac_icon
            )
        )

        private fun generateResourceRef(sir: SearchIndexableResource): Array<Any?> {
            val ref = arrayOfNulls<Any>(7)
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_RANK] = sir.rank
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_RESID] = sir.xmlResId
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_CLASS_NAME] = null
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_ICON_RESID] = sir.iconResId
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_ACTION] =
                "com.android.settings.action.EXTRA_SETTINGS"
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_PACKAGE] =
                sir.packageName
            ref[SearchIndexablesContract.COLUMN_INDEX_XML_RES_INTENT_TARGET_CLASS] = sir.className
            return ref
        }
    }
}