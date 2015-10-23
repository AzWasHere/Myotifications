package com.azwashere.myotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.darken.myolib.Myo;
import eu.darken.myolib.MyoCmds;
import eu.darken.myolib.MyoConnector;
import eu.darken.myolib.msgs.MyoMsg;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Myotifications";
    private Toast mToast;
    private Myo myo;
    ListView list;
    CustomListAdapter adapter;
    ArrayList<Model> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modelList = new ArrayList<Model>();
        adapter = new CustomListAdapter(getApplicationContext(), modelList);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        /*
        TODO check and enable BT
        TODO enable notifications check
        */

        MyoConnector connector = new MyoConnector(getApplicationContext());
        connector.scan(5000, new MyoConnector.ScannerCallback() {

            /*
            TODO Set connector to search for a Myo until found and connected
            TODO Require sync, then unsync to activate
            */

            @Override
            public void onScanFinished(List<Myo> myos) {
                Myo myo = myos.get(0);
                myo.connect();
                myo.writeUnlock(MyoCmds.UnlockType.HOLD, new Myo.MyoCommandCallback() {
                    @Override
                    public void onCommandDone(Myo myo, MyoMsg msg) {
                        /*
                        TODO Look into sending a vibration command while device is asleep, or...
                        TODO Turn EMG off and classifiers off, set connection speed to slow
                        */
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
        /*
        TODO create settings menu to enable or disable notifications for apps
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    TODO create disconnect button
    TODO create wake up button
    */

    private BroadcastReceiver onNotice = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            // String pack = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            //int id = intent.getIntExtra("icon",0);

            myo.writeVibrate(MyoCmds.VibrateType.LONG, null);
            Context remotePackageContext = null;
            try {
//                remotePackageContext = getApplicationContext().createPackageContext(pack, 0);
//                Drawable icon = remotePackageContext.getResources().getDrawable(id);
//                if(icon !=null) {
//                    ((ImageView) findViewById(R.id.imageView)).setBackground(icon);
//                }
                byte[] byteArray = intent.getByteArrayExtra("icon");
                Bitmap bmp = null;
                if (byteArray != null) {
                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                }
                Model model = new Model();
                model.setName(title + " " + text);
                model.setImage(bmp);


                if (modelList != null) {
                    modelList.add(model);
                    adapter.notifyDataSetChanged();
                } else {
                    modelList = new ArrayList<Model>();
                    modelList.add(model);
                    adapter = new CustomListAdapter(getApplicationContext(), modelList);
                    list = (ListView) findViewById(R.id.list);
                    list.setAdapter(adapter);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}

