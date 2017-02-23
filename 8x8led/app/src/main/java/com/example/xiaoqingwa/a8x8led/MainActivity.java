package com.example.xiaoqingwa.a8x8led;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "8x8led";
    private Gpio ser;
    private Gpio srck;
    private Gpio rck;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PeripheralManagerService manager = new PeripheralManagerService();

        try {
            ser = manager.openGpio("BCM16");
            srck = manager.openGpio("BCM21");
            rck = manager.openGpio("BCM20");

            ser.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            srck.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            rck.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

        byte[] screen = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

        while(true){
        for(int i=0;i<8;i++){
            try {
                rck.setValue(false);
                shitOut((byte)0x00);
                shitOut((byte)(1<<i));
                rck.setValue(true);
                Thread.sleep(20);
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

        //for (int i = 0; i < 100; i++) {
          //  updateScreen(screen,50);
        //}

    }

    protected void shitOut(byte d) {
        try {
            for (int i = 0; i < 8; i++) {
                srck.setValue(false);
                if ((0x1 & (d >> i)) == 1) {
                    ser.setValue(true);
                } else {
                    ser.setValue(false);
                }
                srck.setValue(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void updateScreen(byte[] data, long rtime) {
        Date dt = new Date();
        Long start = dt.getTime();
        while (dt.getTime() - start < rtime) {
            for (int i = 0; i < 8; i++) {
                byte col = (byte) (0x1 << i);
                try {
                    rck.setValue(false);
                    shitOut((byte) ~col);
                    shitOut(data[i]);
                    rck.setValue(true);
                    //Thread.sleep(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ser.close();
            srck.close();
            rck.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
