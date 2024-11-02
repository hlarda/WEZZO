package com.example.wezzo.screens.settings.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.wezzo.R
import java.util.*

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
            val language = sharedPreferences?.getString(key, "en")
            val locale = when (language) {
                "ar" -> Locale("ar")
                else -> Locale("en")
            }
            setLocale(requireContext(), locale)
        }
        // Restart the app and launch MainActivity
        val intent = requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(intent)
        activity?.finishAffinity()
    }

    private fun setLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}