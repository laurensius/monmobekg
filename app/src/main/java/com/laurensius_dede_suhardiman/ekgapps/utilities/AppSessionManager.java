package com.laurensius_dede_suhardiman.ekgapps.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.laurensius_dede_suhardiman.ekgapps.R;

public class AppSessionManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editorPreferences;
    public String endpoint;

    public AppSessionManager(Context context){
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.sharedpreferences), 0);
        editorPreferences = sharedPreferences.edit();
        this.context = context;
        this.getSharedPreferences();
    }

    void getSharedPreferences(){
        this.endpoint = sharedPreferences.getString(context.getResources().getString(R.string.param_endpoint),null);
        if(this.endpoint == null){
            setSharedPreferences(context.getResources().getString(R.string.default_ip));
        }
    }

    public void setSharedPreferences(String setEndpoint){
        editorPreferences.putString(context.getResources().getString(R.string.param_endpoint),setEndpoint);
        editorPreferences.commit();
        getSharedPreferences();
    }


}
