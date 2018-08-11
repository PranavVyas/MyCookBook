package com.vyas.pranav.mycookbook.extrautils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

public class SharedPrefs {
    private static final String NAME_SHARED_PREFS = "RecepiesPrefs";
    private static final String KEY_CURRENT_RECEPIE = "RecepiesPrefs";
    private static final String DEFAULT_CURRENT_RECEPIE = "NoRecepieSelected";
    private static final String KEY_FIRST_TIME_RUN = "FirstTimeRun";
    private static final boolean DEFAULT_FIRST_TIME_RUN = true;

    public static boolean isRecepieAddedAlready(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(NAME_SHARED_PREFS,Context.MODE_PRIVATE);
        if(Objects.equals(mPrefs.getString(KEY_CURRENT_RECEPIE, DEFAULT_CURRENT_RECEPIE), DEFAULT_CURRENT_RECEPIE)){
            return false;
        }
        return true;
    }

    public static void addRecepieToSharedPrefs(Context context,String value){
        SharedPreferences mPrefs = context.getSharedPreferences(NAME_SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(KEY_CURRENT_RECEPIE, value);
        mEditor.apply();
    }

    public static String getCurrentRecepieFromPrefs(Context context){
        SharedPreferences mPrefs = context.getSharedPreferences(NAME_SHARED_PREFS,Context.MODE_PRIVATE);
        return mPrefs.getString(KEY_CURRENT_RECEPIE,DEFAULT_CURRENT_RECEPIE);
    }

    public static boolean isFirstTimeRun(Context context){
        boolean result;
        SharedPreferences mPrefs = context.getSharedPreferences(NAME_SHARED_PREFS,Context.MODE_PRIVATE);
        result = mPrefs.getBoolean(KEY_FIRST_TIME_RUN,DEFAULT_FIRST_TIME_RUN);
        return result;
    }

    public static void setFirstTimeRun(Context context,boolean isFirstimeRun){
        SharedPreferences mPrefs = context.getSharedPreferences(NAME_SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        if(isFirstimeRun){
            mEditor.putBoolean(KEY_FIRST_TIME_RUN,true);
        }else{
            mEditor.putBoolean(KEY_FIRST_TIME_RUN,false);
        }
        mEditor.apply();
    }

}
