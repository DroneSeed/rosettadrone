package sq.rogue.rosettadrone.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.util.Patterns;

import java.util.Map;
import java.util.regex.Pattern;

import sq.rogue.rosettadrone.R;

// Display value of preference in summary field

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferenceManager().getSharedPreferences();
//        for (Map.Entry<String, ?> preferenceEntry : sharedPreferences.getAll().entrySet()) {
//            Preference preference = (Preference) preferenceEntry.getValue();
//            if (preference instanceof EditTextPreference) {
//                addValidator(preference);
//            } else {
//
//            }
//        }
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     *
     * @param savedInstanceState
     * @param rootKey
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setListeners();
    }

    public void setListeners() {
        findPreference("pref_gcs_ip").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return validate_ip((String) newValue);
            }
        });

        findPreference("pref_video_ip").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return validate_ip((String) newValue);
            }
        });

        findPreference("pref_telem_port").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return Integer.parseInt((String) newValue) >= 1 && Integer.parseInt((String) newValue) <= 65535;
            }
        });

        findPreference("pref_video_port").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return (Integer) newValue >= 1 && (Integer) newValue <= 65535;
            }
        });
    }

    public boolean validate_ip(String ip) {
        return Patterns.IP_ADDRESS.matcher(ip).matches();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref);
                }
            } else {
                updatePreference(preference);
            }
        }
    }

    /**
     *
     */
    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

//        switch (key) {
//            case "pref_gcs_ip":
//                break;
//            case "pref_telem_port":
//                break;
//            case "pref_video_port":
//                break;
//            default:
//                break;
//        }
        updatePreference(findPreference(key));
    }

    /**
     *
     * @param preference
     */
    private void updatePreference(Preference preference) {
        if (preference == null) return;
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) preference;
            preference.setSummary(editTextPref.getText());
        } else if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            return;
        } else {
            return;
        }
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
        preference.setSummary(sharedPrefs.getString(preference.getKey(), "Default"));
    }
}