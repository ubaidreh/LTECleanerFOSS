/*
 * Copyright 2021 Hunter J Drum
 */

package theredspy15.ltecleanerfoss.controllers;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Arrays;

import theredspy15.ltecleanerfoss.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setHasOptionsMenu(true);

            findPreference("aggressive").setOnPreferenceChangeListener((preference, newValue) -> {
                boolean checked = ((CheckBoxPreference) preference).isChecked();
                if (!checked) {
                    String[] filtersFiles = getResources().getStringArray(R.array.aggressive_filter_folders);

                    AlertDialog alertDialog = new AlertDialog.Builder(requireContext(),R.style.MyAlertDialogTheme).create();
                    alertDialog.setTitle("What aggressive filter does");
                    alertDialog.setMessage("Adds the following filters:"+" "+ Arrays.toString(filtersFiles));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }

                return true;
            });
        }

        /**
         * Inflate Preferences
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
