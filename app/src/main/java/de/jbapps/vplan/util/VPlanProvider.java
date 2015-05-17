package de.jbapps.vplan.util;

import android.content.Context;
import android.util.Log;

import de.jbapps.vplan.data.VPlanSet;

/**
 * This class manages the whole loading and caching process for the MainActivity.
 * Some work is delegated to other classes.
 */
public class VPlanProvider implements VPlanLoader.IOnLoadingFinished {

    private static final String TAG = "VPlanProvider";
    /**
     * receives callbacks for specific actions
     */
    private final IVPlanLoader mListener;
    private VPlanSet mVPlanSet;
    private VPlanLoader mVPlanLoader;

    public VPlanProvider(Context context, IVPlanLoader listener) {
        mVPlanSet = new VPlanSet(context);
        mListener = listener;
    }

    public void cancel() {
        if (mVPlanLoader != null) {
            mVPlanLoader.cancel(true);
            mVPlanLoader = null;
        }
    }

    public void getVPlan(boolean forceLoad) {
        cancel();
        mVPlanLoader = new VPlanLoader(this, mVPlanSet);
        mVPlanLoader.execute(forceLoad);
    }

    public void getCachedVPlan() {
        if (mVPlanSet.readVPlan()) {
            mListener.vPlanLoaded(mVPlanSet);
        } else {
            mListener.vPlanLoaded(null);
            Log.w(TAG, "VPlanSet empty!");
        }
    }

    @Override
    public void loaderFinished(boolean loadCache) {
        mVPlanLoader = null;
        if (loadCache) {
            getCachedVPlan();
        } else {

            mListener.vPlanLoaded(mVPlanSet);
        }
    }

    /**
     * This interface is the connection to the given Activity
     */
    public interface IVPlanLoader {
        void vPlanLoaded(VPlanSet vplanset);
    }



}
