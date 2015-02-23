package de.jbapps.vplan;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.jbapps.vplan.data.VPlanBaseData;
import de.jbapps.vplan.util.VPlanJSONParser;
import de.jbapps.vplan.util.VPlanLoader;

public class VPlanService extends Service implements VPlanLoader.IOnFinishedLoading, VPlanJSONParser.IOnFinishedLoading {

    private static final String TAG = "VPlanService";

    //TODO: private Messenger mSensorService;
    //TODO: private final Messenger mServer = new Messenger(new IncomingHandler());
    //TODO: private ServiceConnection mSensorConnection = new SensorConnection();

    @Override
    public IBinder onBind(Intent intent) {
        return null;//TODO: mServer.getBinder(); //return the communication channel
    }

    private void sendMessage(Messenger client, int value) {
        //TODO: implement
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Started.");

        //start and bind consumed Services
        //TODO: startService(new Intent(this, SensorService.class));
        //TODO: bindSensorService();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY; //TODO: make sticky
    }

    @Override
    public synchronized void onVPlanHeaderLoaded(Header[] vPlanHeader1, Header[] vPlanHeader2) {
        try {
            readVPlanHeader();
            if (mVPlanHeader1[0].getValue().equals(vPlanHeader1[0].getValue()) && mVPlanHeader2[0].getValue().equals(vPlanHeader2[0].getValue())) {
                if (mVPlanHeader1[1].getValue().equals(vPlanHeader1[1].getValue()) && mVPlanHeader2[1].getValue().equals(vPlanHeader2[1].getValue())) {
                    invokeVPlanCacheRestore(false);
                    return;
                }
            }
            invokeVPlanDownload(false);
        } catch (Exception e) {
            invokeVPlanDownload(false);
        }
        Log.i("MainActivity#onVPlanHeaderLoaded()", "VPlan header loading finished");
    }

    @Override
    public synchronized void onVPlanHeaderLoadingFailed() {
        Log.w("MainActivity#onVPlanHeaderLoadingFailed()", "VPlan header loading failed");
        restore(true);
    }

    @Override
    public synchronized void onVPlanLoaded(JSONObject vPlan1, JSONObject vPlan2, Header[] vPlanHeader1, Header[] vPlanHeader2) {

        mVPlanHeader1 = vPlanHeader1;
        mVPlanHeader2 = vPlanHeader2;
        mVPlan1 = vPlan1;
        mVPlan2 = vPlan2;

        invokeJSONParser();

        try {
            writeVPlanHeader();
            writeVPlanContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("MainActivity#onVPlanLoaded()", "VPlanLoading finished");
    }

    @Override
    public synchronized void onVPlanLoadingFailed() {
        restore(true);
        Log.w("MainActivity#onVPlanLoaded()", "VPlanLoading failed");
    }

    @Override
    public void onVPlanParsed(List<VPlanBaseData> dataList) {
        applyVPlan(dataList);
        toggleLoading(false);
        Log.i("MainActivity#onVPlanParsed()", "VPlan parsed and applied");
    }

    @Override
    public void onVPlanParsingFailed() {
        Toast.makeText(mContext, "Daten konnten nicht verarbeitet werden.", Toast.LENGTH_LONG).show();
        toggleLoading(false);
        Log.w("MainActivity#onVPlanParsed()", "VPlan parsing failed");
    }

    /*void bindSensorService() {
        bindService(new Intent(this, SensorService.class), mSensorConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "SensorService bound");
    }

    private class SensorConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSensorService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, COM_REGISTER_CLIENT);
                msg.replyTo = mServer;
                mSensorService.send(msg);
            } catch (RemoteException e) {
                Log.e(TAG, "SensorService unreachable");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "SensorService crashed");
            mSensorService = null;
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    }*/
}
