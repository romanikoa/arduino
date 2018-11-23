package ru.profitcp.signalka;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;
import ru.profitcp.signalka.log;
import java.lang.reflect.Method;

//import static android.support.v4.content.ContextCompat.startActivity;

public class PhoneStateChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber;
        String phoneState = intent.getStringExtra (TelephonyManager.EXTRA_STATE);
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            log.phoneNumber=incomingNumber;
            Toast.makeText(context, incomingNumber, Toast.LENGTH_SHORT).show();
           // answerPhoneHeadsethook(context); // Поступил звонок с номера incomingNumber
        }else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // Intent MB = new Intent(context,MyIntentBuilder.class);
            //startService(MB);
            //  phoneEnd(context);
            final Context context1 = context.getApplicationContext();
           /* new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
             */      // LocalBroadcastManager.getInstance(context1).sendBroadcast(new Intent(MainActivity.PHONE_CALL));

            Intent intent1 = new Intent(MainActivity.PHONE_CALL);
                    context1.sendBroadcast(intent1);
                   // String phoneNumber = incomingNumber;
            if (log.phoneNumber.equals("+79517853719")) {
                    Intent intent2 = new Intent(context, MyIntentBuilder.class);
                    intent2.putExtra("doopen",true);
                    context.startService(intent2);
            }
            //}.start();

           // answerPhoneHeadsethook(context);

        }
    }
 /*   public static void answerPhoneHeadsethook(Context context) {
        // «Нажимаем» и «отпускаем» кнопку на гарнитуре
        Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
    }*/
    public Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }
  /*  public void phoneEnd(Context context){
        Thread gt = getThreadByName("SUPER");
        try {
            gt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // gt.start();
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
