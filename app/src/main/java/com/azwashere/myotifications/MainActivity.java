package com.azwashere.myotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import eu.darken.myolib.Myo;
import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Myotifications";
    private Toast mToast;
    Context context;
    private Myo myo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, NotificationService.class));
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("notify_rec"));

        MyoConnector connector = new MyoConnector(getContext());
        connector.scan(5000, new MyoConnector.ScannerCallback() {
            @Override
            public void onScanFinished(List<Myo> myos) {
                Myo myo = myos.get(0);
                myo.connect();
                myo.writeUnlock(MyoCmds.UnlockType.HOLD, new Myo.MyoCommandCallback() {
                    @Override
                    public void onCommandDone(Myo myo, MyoMsg msg) {
                        myo.writeVibrate(MyoCmds.VibrateType.LONG, null);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            myo.writeVibrate(MyoCmds.VibrateType.LONG, null);
        }
    };
}
