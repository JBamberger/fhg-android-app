package de.fhg_radolfzell.android_app.main;

import android.support.annotation.StringRes;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface MainContract {
    interface View {
        void setSubtitle(String subtitle);
        void setSubtitle(@StringRes int subtitle);
        void clearSubtitle();
    }
}
