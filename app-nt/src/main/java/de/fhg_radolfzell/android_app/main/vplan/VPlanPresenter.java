package de.fhg_radolfzell.android_app.main.vplan;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import de.fhg_radolfzell.android_app.data.VPlan;
import de.fhg_radolfzell.android_app.data.source.VPlanDataSource;
import de.fhg_radolfzell.android_app.util.Storage;

import static de.fhg_radolfzell.android_app.util.Preconditions.checkNotNull;


/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */
@VPlanScope
public class VPlanPresenter implements VPlanContract.Presenter {

    private final VPlanContract.View mVPlanView;
    private final Storage mStorage;
    private final VPlanDataSource mDataSource;

    @Inject
    public VPlanPresenter(@NonNull VPlanContract.View vPlanView, @NonNull Storage storage, @NonNull VPlanDataSource dataSource) {
        this.mVPlanView = checkNotNull(vPlanView, "vPlanView cannot be null");
        this.mStorage = checkNotNull(storage, "storage cannot be null");
        this.mDataSource = checkNotNull(dataSource, "dataSource cannot be null");
    }

    @Override
    public void start() {
        loadVPlan();
    }

    @Override
    public void loadVPlan() {
        mVPlanView.setLoadingIndicator(true);
        String[] grades = mStorage.getGrades();
        if(grades == null) {
            grades = new String[0];
        }
        mDataSource.loadVPlan(grades, new VPlanDataSource.LoadVPlanCallback() {
            @Override
            public void onVPlanLoaded(@NonNull VPlan[] vPlan) {
                if (mVPlanView.isActive()) {
                    mVPlanView.showVPlan(vPlan);
                    mVPlanView.setSubtitle(mStorage.getGradeString());
                    mVPlanView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onVPlanNotAvailable() {
                mVPlanView.setLoadingIndicator(false);
                mVPlanView.clearSubtitle();
                mVPlanView.showLoadingVPlanError();
            }
        });
    }
}
