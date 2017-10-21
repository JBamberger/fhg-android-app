package de.fhg_radolfzell.android_app.event;

import de.fhg_radolfzell.android_app.data.VPlan;

/**
 * @author Jannik
 * @version 30.07.2016.
 */
public class VPlanLoadedEvent {

    public VPlan[] vPlans;

    public VPlanLoadedEvent(VPlan[] vplans) {
        this.vPlans = vplans;
    }
}
