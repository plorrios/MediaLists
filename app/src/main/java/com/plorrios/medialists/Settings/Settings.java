package com.plorrios.medialists.Settings;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.plorrios.medialists.Main.MainActivity;
import com.plorrios.medialists.R;

public class Settings extends AppCompatActivity {

    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences obtainedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();
        obtainedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (obtainedPreferences.getString("AppliedStyle",res.getString(R.string.Light)).equals(res.getString(R.string.Light))) {
            setTheme(R.style.AppThemeLight);
        }else {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals("AppliedStyle")) {

                    Intent intent = new Intent(Settings.this, MainActivity.class);
                    Intent mintent = getIntent();
                    String tipoFragment = mintent.getStringExtra("tipoFragment");
                    intent.putExtra("BackFromSettings",true);
                    intent.putExtra("tipoFragment",tipoFragment);
                    if (tipoFragment!="Main") {
                        intent.putExtra("tipolista", mintent.getStringExtra("tipolista"));
                        if (tipoFragment!="ListDetails"){
                            intent.putExtra("elemento",mintent.getStringExtra("elemento"));
                        }
                    }

                    TaskStackBuilder.create(Settings.this)
                            .addNextIntent(intent)
                            .addNextIntent(Settings.this.getIntent())
                            .startActivities();
                }
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(listener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_layout, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        // Write your code here
        Log.d("Back","Pressed");
        super.onBackPressed();
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_settings, rootKey);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        obtainedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause(){
        super.onPause();
        obtainedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume(){
        super.onResume();
        obtainedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
}