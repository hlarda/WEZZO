package com.example.wezzo.screens.settings.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.wezzo.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val unitsPreference: ListPreference? = findPreference("units")
        val languagePreference: ListPreference? = findPreference("language")

        unitsPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        languagePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "language") {
            activity?.recreate() // Restart the activity to apply the new language
        }
    }
}