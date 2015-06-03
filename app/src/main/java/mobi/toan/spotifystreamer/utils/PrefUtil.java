package mobi.toan.spotifystreamer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by toan on 6/3/15.
 */
public class PrefUtil {
    private static final String TAG = PrefUtil.class.getSimpleName();
    private static SharedPreferences sSharedPreferences;

    public static void initialize(Context context) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getSharedPreferences("storage", Context.MODE_PRIVATE);
        } else {
            Log.e(TAG, "Already initialized PrefUtil");
        }
    }

    public static void setPref(String key, String value) {
        checkInitializingState();
        sSharedPreferences.edit().putString(key, value).commit();
    }

    public static String getPref(String key) {
        checkInitializingState();
        return sSharedPreferences.getString(key, "");
    }

    private static boolean checkInitializingState() {
        if (sSharedPreferences == null) {
            throw new IllegalStateException("PrefUtil is not initialized yet!");
        }
        return true;
    }
}
