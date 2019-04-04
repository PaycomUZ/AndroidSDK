package uz.paycom.payment.utils;

import android.util.Log;
import uz.paycom.payment.BuildConfig;

public class Logger {
    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, log);
        }
    }
}