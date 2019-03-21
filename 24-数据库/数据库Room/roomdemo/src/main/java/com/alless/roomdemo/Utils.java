package com.alless.roomdemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ${程杰} on 2018/3/29.
 * 描述:
 */

public class Utils {
    private static Toast toast;
    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(),
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
