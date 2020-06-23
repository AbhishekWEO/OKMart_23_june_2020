package com.okmart.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;

    public SharedPreferenceUtil(Context context) {
        this._prefs = context.getSharedPreferences("PREF_CLIM8",
                Context.MODE_PRIVATE);
        this._editor = this._prefs.edit();
    }

    public String getString(String key, String defaultvalue) {
        if (this._prefs == null) {
            return defaultvalue;
        }
        return this._prefs.getString(key, defaultvalue);
    }

    public void setString(String key, String value) {
        if (this._editor == null) {
            return;
        }
        this._editor.putString(key, value);
    }

    public String getNotification(String key, String defaultvalue2) {
        if (this._prefs == null) {
            return defaultvalue2;
        }
        return this._prefs.getString(key, "true");
    }

    public void setNotification(String key, String value2) {
        if (this._editor == null) {
            return;
        }
        this._editor.putString(key, value2);
    }

    public Boolean getBoolean(String key, Boolean defaultvalue) {
        if (this._prefs == null) {
            return defaultvalue;
        }
        return this._prefs.getBoolean(key, defaultvalue);
    }

    public void setBoolean(String key, Boolean value) {
        if (this._editor == null) {
            return;
        }
        this._editor.putBoolean(key, value);
    }

    public int getInt(String key, int defaultvalue) {
        if (this._prefs == null) {
            return defaultvalue;
        }
        return this._prefs.getInt(key, defaultvalue);
    }

    public void setInt(String key, int value) {
        if (this._editor == null) {
            return;
        }
        this._editor.putInt(key, value);
    }

    public void clearAll() {
        if (this._editor == null) {
            return;
        }
        this._editor.clear().commit();

    }

    public void removeOneItem(String key) {
        if (this._editor == null) {
            return;
        }
        this._editor.remove(key);
    }

    public void save() {
        if (this._editor == null) {
            return;
        }
        this._editor.commit();
    }

}