package xyz.jbapps.vplan.util;

import android.util.Patterns;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class Utils {
    public static boolean isValidURL(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }
}
