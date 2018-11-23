package ru.profitcp.signalka;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//import android.content.Context;


public class MyIntentBuilder extends Service {
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
   // public static final String ACTION_USB_READY = "com.felhr.connectivityservices.USB_READY";
    //public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    //ublic static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
public boolean keep;
    public static final String ACTION_USB_NOT_SUPPORTED = "com.felhr.usbservice.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "ru.profitcp.signalka.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "ru.profitcp.signalka.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "ru.profitcp.signalka.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "ru.profitcp.signalka.USB_DISCONNECTED";
    private static final String ACTION_USB_PERMISSION = "ru.profitcp.signalka.USB_PERMISSION";
    //public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.felhr.connectivityservices.ACTION_CDC_DRIVER_NOT_WORKING";
    //public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.felhr.connectivityservices.ACTION_USB_DEVICE_NOT_WORKING";
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            //Context context = getApplicationContext();
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                log.data += data;
              //  Toast.makeText(context, "Data: " + data, Toast.LENGTH_SHORT).show();
                // tvAppend(textView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();
    // Handler that receives messages from the thread
    public class LocalBinder extends Binder {
        MyIntentBuilder getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyIntentBuilder.this;
        }
    }
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.setName("SUPER");
        thread.start();
        //Context context = getApplicationContext();
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
public void phoneCall(){
    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:---"));
    startActivity(call);

}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        if (intent != null){
               if(intent.getBooleanExtra("need_call",false)){
                   Toast.makeText(this, "New Callllllllll", Toast.LENGTH_SHORT).show();
                   intent.putExtra("need_call",false);
               }else if(intent.getBooleanExtra("doopen",false)){

                   sendCommand();
               }
        }
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
          //      .setContentTitle(getString(R.string.app_name))
            //    .setContentText("SmartTracker is Running...")
              //  .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setAutoCancel(true);
     //   Notification notification = builder.build();
      //  startForeground(NOTIFICATION_ID, notification);
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        //Context context = getApplicationContext();
        CharSequence text = "Service Started " + msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);

        toast.show();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return mBinder;
    }
    public void sendCommand() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
       // Toast.makeText(this, "send func", Toast.LENGTH_SHORT).show();
        if (!usbDevices.isEmpty()) {
          //  Toast.makeText(this, "Device ready", Toast.LENGTH_SHORT).show();

                  keep = false;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                Toast.makeText(this, "Vid is" + deviceVID, Toast.LENGTH_SHORT).show();

               if (deviceVID == 6790 || deviceVID == 0x1A86)//Arduino Vendor ID
                {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {

                      /*  new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }*/
                                requestUserPermission();
/*
                            }
                        }.start();
*/
                                boolean so = false;
                                so = serialPort.open();
                                if (so) { //Set Serial Connection Parameters.
                                   // Toast.makeText(this, "Devices opened" , Toast.LENGTH_SHORT).show();
                                    // setUiEnabled(true);
                                    serialPort.setBaudRate(9600);
                                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                    serialPort.read(mCallback);
                                    keep=true;
                                }


                    }
                  //  return;
                }
                if (!keep){
                    break;}
                //return;
            }
            if(keep){
                //do send
                String string = "s";
                serialPort.write(string.getBytes());
            }
        }
    }
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
   // public void makeCall(TextView textView){
 //       Toast.makeText(this, "Callllllllll", Toast.LENGTH_SHORT).show();
   //     textView.setText(textView.getText() + "\n" + "Callled service" + getRandomNumber());
   // }
   private void requestUserPermission() {
       Context context = getApplicationContext();
       PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
       usbManager.requestPermission(device, mPendingIntent);
   }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

    }
}
