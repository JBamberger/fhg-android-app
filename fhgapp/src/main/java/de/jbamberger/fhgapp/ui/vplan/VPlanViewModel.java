package de.jbamberger.fhgapp.ui.vplan;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import de.jbamberger.fhgapp.source.Repository;
import de.jbamberger.fhgapp.source.Resource;
import de.jbamberger.fhgapp.source.model.VPlan;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class VPlanViewModel extends ViewModel {
    private LiveData<Resource<VPlan>> vplan;
    private Repository repo;

    @Inject
    public VPlanViewModel(Repository repo) {
        this.repo = repo;
    }


    public void init() {
        if (this.vplan != null) {
            return;
        }
        vplan = repo.getVPlan();
    }

    public LiveData<Resource<VPlan>> getVPlan() {
        return vplan;
    }
}