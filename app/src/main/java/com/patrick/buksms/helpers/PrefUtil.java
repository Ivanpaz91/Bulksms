package com.patrick.buksms.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.patrick.buksms.app.ApplicationController;

/**
 * Created by ivan on 10/31/15.
 */
public   class PrefUtil {


    private static Context context;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static final String DEFAULT_PREF_NAME = "shakePref";


    private static final PrefUtil instance = new PrefUtil();


    public PrefUtil() {
        super();
        context = ApplicationController.getInstance().getApplicationContext();
        sp = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE);
    }


    public static PrefUtil getInstance(Context _context, String prefName, int mode) {
        context = _context;
        sp = context.getSharedPreferences(prefName, mode);
        return instance;
    }

    public static PrefUtil getInstance(Context _context, String prefName) {
        return getInstance(_context, prefName, Context.MODE_PRIVATE);
    }

    public static PrefUtil getInstance(Context _context) {
        return getInstance(_context, DEFAULT_PREF_NAME);
    }

    public PrefUtil remove(String key) {
        editor = sp.edit();
        editor.remove(key);
        editor.commit();
        return instance;
    }

    /* ----- Boolean ----- */
    public PrefUtil addBoolean(String key, Boolean value) {
        editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
        return instance;
    }

    public static void setLong(String key, long value) {

        editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();

    }
    public static long getLong(String key, long dd) {
        return sp.getLong(key, dd);
    }
    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /* ----- String ----- */
    public PrefUtil addString(String key, String value) {
        editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
        return instance;
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /* ----- Integer ----- */
    public PrefUtil addInt(String key, int value) {
        editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
        return instance;
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

}