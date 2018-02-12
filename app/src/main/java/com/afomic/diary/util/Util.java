package com.afomic.diary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by afomic on 11/28/17.
 *
 */

public class Util {
    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm==null){
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
    public static boolean isEmpty(EditText edt){
        return TextUtils.isEmpty(getString(edt));
    }
    public  static String getString(EditText edt){
        return edt.getText().toString();
    }
    public static void makeToast(Context ctx,String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
