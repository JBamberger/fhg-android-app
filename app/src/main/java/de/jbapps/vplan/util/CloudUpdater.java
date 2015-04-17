package de.jbapps.vplan.util;

import android.os.AsyncTask;
import android.util.Log;

/**
 * This task sends the headers to the backend to notify all users of the VPlan-changes.
 * params[0] = header1
 * params[1] = header2
 */
public class CloudUpdater extends AsyncTask<String, Void, Void> {

    private static final String TAG = "CloudUpdater";

    @Override
    protected Void doInBackground(String... params) {
        String header1 = params[0];
        String header2 = params[1];
        if (header1 == null || header2 == null) {
            Log.w(TAG, "Cloud update aborted");
            return null;
        }

        //TODO: check backend and update if necessary


        return null;
    }
}
