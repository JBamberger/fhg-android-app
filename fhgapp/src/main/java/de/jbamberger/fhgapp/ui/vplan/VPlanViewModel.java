package de.jbamberger.fhgapp.ui.vplan;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.jbamberger.api.data.VPlan;
import de.jbamberger.fhgapp.source.Repository;
import de.jbamberger.fhgapp.source.Resource;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanViewModel extends ViewModel {
    private LiveData<Resource<VPlan>> vplan;
    private Repository repo;

    @Inject
    VPlanViewModel(Repository repo) {
        this.repo = repo;
    }


    void init() {
        if (this.vplan != null) {
            return;
        }
        vplan = repo.getVPlan();
    }

    void refresh() {
        vplan = repo.getVPlan();
    }

    LiveData<Resource<VPlan>> getVPlan() {
        return vplan;
    }
}