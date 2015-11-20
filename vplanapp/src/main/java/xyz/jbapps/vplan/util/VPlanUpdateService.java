package xyz.jbapps.vplan.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service intended to update home screen widgets and provide update notifications.
 *
 * Constantly polling the FHG server is extremely discouraged due to huge battery drain and data
 * usage. Server side implementation of something like gcm necessary.
 *
 * //TODO: Implement server side and updater service class
 *
 * @author Jannik Bamberger
 * @version 1.0
 */
public class VPlanUpdateService extends Service {
    public VPlanUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
