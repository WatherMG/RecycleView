package com.drwat.lab2;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by drwat on 05.04.2016.
 */
public class Prefs extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
