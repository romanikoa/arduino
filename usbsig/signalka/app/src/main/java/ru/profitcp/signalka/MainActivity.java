package ru.profitcp.signalka;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ru.profitcp.signalka.log;
import java.util.HashMap;
import java.util.Map;
//import com.felhr.usbserial.UsbSerialDevice;
//import com.felhr.usbserial.UsbSerialInterface;
//import

public class MainActivity extends AppCompatActivity {
    TextView textView;
    MyIntentBuilder mService;
    UsbDevice device;
    UsbManager usbManager;
    private static final String ACTION_USB_PERMISSION = "ru.profitcp.signalka.USB_PERMISSION";
    public static final String ACTION_USB_PERMISSION_GRANTED = "ru.profitcp.signalka.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "ru.profitcp.signalka.USB_PERMISSION_NOT_GRANTED";
    boolean mBound;
    public static final String PHONE_CALL = "ru.profitcp.signalka.PHONE_CALL";
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
       @Override
       public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PHONE_CALL)) {
               Toast.makeText(context, "Call broadcast", Toast.LENGTH_SHORT).show();
               if(mBound){
                    textView.setText(textView.getText() + "\n" + "Callled" + mService.getRandomNumber());
                   // mService.makeCall(textView);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        IntentFilter filter = new IntentFilter();
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
       // filter.addAction(ACTION_USB_PERMISSION);
       // filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
       // filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(PHONE_CALL);
        registerReceiver(broadcastReceiver, filter);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(ACTION_USB_PERMISSION);
        filter1.addAction(ACTION_USB_PERMISSION_GRANTED);
        filter1.addAction(ACTION_USB_PERMISSION_NOT_GRANTED);
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter1.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        //filter1.addAction(MyIntentBuilder.ACTION_USB_NOT_SUPPORTED);
        //filter1.addAction(MyIntentBuilder.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter1);
    }
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //textView.setText(textView.getText() + "\n" + "on receive" + mService.getRandomNumber());
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
               // textView.setText(textView.getText() + "\n" + "not Granted" + mService.getRandomNumber());
                //Toast.makeText(context, "not Granted", Toast.LENGTH_SHORT).show();
                if(granted)
                {
                    textView.setText(textView.getText() + "\n" + "Granted " + mService.getRandomNumber());
                    Toast.makeText(context, "Granted", Toast.LENGTH_SHORT).show();
                }
            }else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
                HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
                if (!usbDevices.isEmpty()) {
                    for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                        device = entry.getValue();
                        int deviceVID = device.getVendorId();
                        Toast.makeText(context, "Device: " + deviceVID, Toast.LENGTH_SHORT).show();
                        if (deviceVID == 6790 || deviceVID == 0x1A86)//Arduino Vendor ID
                        {
                            PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            usbManager.requestPermission(device, mPendingIntent);
                        }
                        break;
                    }
                }
            }    else
            {
                textView.setText(textView.getText() + "\n" + "action:" + intent.getAction());
            }
        }
    };
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyIntentBuilder.class);
 //       MainActivity.this.startForegroundService(intent);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
      //  IntentFilter filter = new IntentFilter();
        // filter.addAction(ACTION_USB_PERMISSION);
        // filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        // filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        //filter.addAction(PHONE_CALL);
        //registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyIntentBuilder.LocalBinder binder = (MyIntentBuilder.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    public void onClickStart(View view){
        Context context = getApplicationContext();
textView.setText(log.data);
     //   Intent intent = new Intent(this, MyIntentBuilder.class);//
        // try {
        //LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(MainActivity.PHONE_CALL));
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            int num = mService.getRandomNumber();
           // mService.requestUserPermission();
            Toast.makeText(context, "number: " + num, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "fuck", Toast.LENGTH_SHORT).show();
        }


        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
       // toast.show();
   //     Intent intent = new Intent(this, MyIntentBuilder.class);//
       // try {
         //   startService(intent);
        //}catch (Exception e){
        //    textView.setText(e.getMessage());

        //}
      //  bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
   // textView.setText(intent.toString());
    }
    public void onClickStop(View view){
        stopService(new Intent(this, MyIntentBuilder.class));

    }
    public void onDestroy(Intent intent){
        super.onDestroy();
        stopService(intent);
    }


}
