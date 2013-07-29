package com.duplavid.miniexpense;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MenuMethods{
    public static void euroBlue(Context context, Menu menu){
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("currency", "euro");
        editor.commit();
        MenuItem item = menu.findItem(R.id.euro);
        item.setIcon(R.drawable.euroblue);
        MenuItem item2 = menu.findItem(R.id.dollar);
        item2.setIcon(R.drawable.dollar);
    }
    
    public static void dollarBlue(Context context, Menu menu){
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("currency", "dollar");
        editor.commit();
        MenuItem item = menu.findItem(R.id.dollar);
        item.setIcon(R.drawable.dollarblue);
        MenuItem item2 = menu.findItem(R.id.euro);
        item2.setIcon(R.drawable.euro);
    }
}