package de.jbapps.jutils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;

/**
 * <h2>A class for toys that do not have a home.</h2>
 * <p/>
 * <h3>Common uses:</h3>
 * <code>MiscUtils.{@link #dpToPx dpToPx}(this, 100);</code> //Returns pixel equivalent of 100dp's (Density-independent pixels)<br />
 * <code>MiscUtils.{@link #getVersionName getVersionName}(this);</code> //"Your App name from manifest"<br />
 * <code>MiscUtils.{@link #getVersionName getVersionName}(this);</code> //"5.4.3" - your app version name from AndroidManifest"<br />
 * <code>MiscUtils.{@link #getVersionCode getVersionCode}(this);</code> //307 - your app version number from AndroidManifest"<br />
 * <code>MiscUtils.{@link #getApplicationName getApplicationName}(this);</code> /"Your App" - your app name from AndroidManifest"<br />
 */
public class MiscUtils {
    public static int dpToPx(Context context, int dps) {
        //DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        //return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static String getVersionName(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            versionName = "Unknown";
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            versionCode = -999;
        }
        return versionCode;
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }


    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String number) {
        return number != null && PhoneNumberUtils.isGlobalPhoneNumber(number);
    }

    public static boolean isValidURL(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }
}
