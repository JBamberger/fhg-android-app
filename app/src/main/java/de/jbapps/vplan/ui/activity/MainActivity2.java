package de.jbapps.vplan.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import de.jbapps.vplan.VPlanService;

public class MainActivity2 extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private Messenger mVPlanService;
    private final Messenger mServer = new Messenger(new IncomingHandler());
    private ServiceConnection mVPlanConnection = new VPlanConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, VPlanService.class));
        bindVPlanService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindVPlanService();
    }

    private void bindVPlanService() {
        bindService(new Intent(this, VPlanService.class), mVPlanConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "SensorService bound");
    }

    private void unbindVPlanService() {
        unbindService(mVPlanConnection);
        Log.i(TAG, "SensorService unbound");
    }

    private class VPlanConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mVPlanService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, VPlanService.INIT_CLIENT);
                msg.replyTo = mServer;
                mVPlanService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "SensorService unreachable");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "SensorService crashed");
            mVPlanService = null;
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //TODO: implement
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
