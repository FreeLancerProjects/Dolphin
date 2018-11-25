package com.appzone.dolphin.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.tags.Tags;
import com.google.gson.Gson;

public class Preferences {

    private static Preferences preferences= null;
    private Preferences() {
    }

    public static Preferences getInstance()
    {
        if (preferences==null)
        {
            preferences = new Preferences();
        }
        return preferences;
    }

    public void Create_Update_UserModel(Context context, UserModel userModel)
    {
        Gson gson = new Gson();
        String user_data = gson.toJson(userModel);
        SharedPreferences preferences = context.getSharedPreferences("user_pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_data",user_data);
        editor.apply();
        Create_Update_Session(context, Tags.LOGIN_STATE);
    }

    public UserModel getUserModel(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("user_pref",Context.MODE_PRIVATE);
        String user_data =preferences.getString("user_data","");
        Gson gson = new Gson();

        UserModel userModel = gson.fromJson(user_data,UserModel.class);
        return userModel;
    }

    public void Create_Update_Session(Context context,String session)
    {
        SharedPreferences preferences = context.getSharedPreferences("session_pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("session",session);
        editor.apply();
    }

    public String getSession(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences("session_pref",Context.MODE_PRIVATE);
        String session = preferences.getString("session","");
        return session;
    }

    public void ClearData(Context context)
    {
        Gson gson = new Gson();
        String user_data = gson.toJson("0");
        SharedPreferences preferences = context.getSharedPreferences("user_pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_data",user_data);
        editor.apply();
        Create_Update_Session(context,Tags.LOGOUT_STATE);
    }
}
