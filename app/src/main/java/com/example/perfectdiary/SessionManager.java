package com.example.perfectdiary;

import android.content.Context;
import android.content.SharedPreferences;

//Session manager to save and fetch user login status and user details
public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "PerfectDiaryPrefs";
    private static final String IS_LOGIN = "isLoggedIn";
    public static final String KEY_USERNAME = "loggedInUser";
    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create log in session
    public void createLoginSession (String username){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    //Check Login Method: Check user login status. False: Redirect user to login page
    public boolean checkLogin(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    //Get stored session data
    public String getUserDetails(){
        return pref.getString(KEY_USERNAME, null);
    }

    public void logoutUser(){
        //clear all data from shared preferences
        editor.clear();
        editor.commit();
    }
}
