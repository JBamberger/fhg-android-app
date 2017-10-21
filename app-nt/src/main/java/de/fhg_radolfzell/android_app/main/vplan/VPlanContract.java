package de.fhg_radolfzell.android_app.main.vplan;

import android.support.annotation.StringRes;

import de.fhg_radolfzell.android_app.BasePresenter;
import de.fhg_radolfzell.android_app.BaseView;
import de.fhg_radolfzell.android_app.data.VPlan;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public interface VPlanContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showVPlan(VPlan[] vplan);

        void showLoadingVPlanError();

        void setSubtitle(String subtitle);

        void setSubtitle(@StringRes int subtitle);

        void clearSubtitle();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void loadVPlan();
    }
}
