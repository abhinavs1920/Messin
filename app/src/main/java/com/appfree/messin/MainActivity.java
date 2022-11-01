package com.appfree.messin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
  //////////////////

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private ReceiveBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
    ///////////////////////
    public EditText usertext;
    // creating variable for button
    public Button submitCourseBtn;

    // creating a strings for storing
    // our values from edittext fields.
    public String usernumber,token;
    public TextView textv;
    // creating a variable
    // for firebasefirestore.
    private FirebaseFirestore db;

    private String TAG = "MainActivity";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usertext = findViewById(R.id.editTextNumber);
        submitCourseBtn = findViewById(R.id.button);
        usernumber = usertext.getText().toString().trim();
        ////////////startBackground();
        //String title = getIntent().getStringExtra("title");
        //String message = getIntent().getStringExtra("message");
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("hulu", "Extras received at onCreate:  Key: " + key + " Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if (message!=null && message.length()>0) {
                getIntent().removeExtra("body");
                textv.setText(message);
            }
        }











































        //setTitle(title);
        textv = findViewById(R.id.textView);

        SharedPreferences.Editor editor = getSharedPreferences("PrefName",
                MODE_PRIVATE).edit();
       // editor.putString("message", message);
        editor.apply();
        textv.setVisibility(View.VISIBLE);
        if(textv.length()==0){
            textv.setText("No new message");
        }
        else {
            Intent intent = getIntent();
            // receive the value by getStringExtra() method and
            // key must be same which is send by first activity
            String str = intent.getStringExtra("message_key");
            // display the string into textView
            textv.setText(str);
        }

        Intent intent = getIntent();
        String str = intent.getStringExtra("message_key");
        imageChangeBroadcastReceiver = new ReceiveBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.appfree.messin");
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);
        ////////////////////////////////////////



        ////////////////////////////////////////
        submitCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ADebugTag", "Value: " + "body");
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                usertext = findViewById(R.id.editTextNumber);
                                usernumber = usertext.getText().toString().trim();
                                textv = findViewById(R.id.textView);
                                usertext.setText(token);
                                writeData();

                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });


    }

    private void addDataToFirestore(String token, String usernumber) {

        // creating a collection reference
        // for our Firebase Firetore database.
        CollectionReference Courses = db.collection("Courses");

        // adding our data to our courses object class.
        Courses courses = new Courses(token,usernumber);

        // below method is use to add data to Firebase Firestore.
        Courses.add(courses).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(MainActivity.this, "Your Course has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(MainActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void readData()  {

        StringBuilder temp = null;
        FileInputStream fin = null;
        try {
            String filename = "tok";
            fin = openFileInput(filename);
            int a;
            temp = new StringBuilder();
            while ((a = fin.read()) != -1) {
                temp.append((char) a);
            }
            textv = findViewById(R.id.textView);
            textv.setVisibility(View.VISIBLE);
            textv.setText("you user ID is "+ temp.toString() );
            fin.close();
            // setting text from the file.

        } catch (IOException e) {

            e.printStackTrace();
        }
        usertext.setVisibility(View.INVISIBLE);
        submitCourseBtn.setVisibility(View.INVISIBLE);
        textv = findViewById(R.id.textView);
        textv.setVisibility(View.VISIBLE);


    }


    private void writeData()
    {
        try
        {
            String filename = "tok";
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            String data = usertext.getText().toString();
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            String message = "null";
            intent.putExtra("message_key", message);
            startActivity(intent);
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            message = String.valueOf(remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.

                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow() {
    }

    private void scheduleJob() {
        //send message to main activity

        //ring sound

    }
//////
public class ReceiveBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
        String packages = intent.getStringExtra("package");
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        if(text != null) {

            if(!text.contains("new messages") && !text.contains("WhatsApp Web is currently active") && !text.contains("WhatsApp Web login")) {

                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String devicemodel = android.os.Build.MANUFACTURER+android.os.Build.MODEL+android.os.Build.BRAND+android.os.Build.SERIAL;

                DateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
                String date = ((SimpleDateFormat) df).format(Calendar.getInstance().getTime());

                textv.setText("Notification : " + receivedNotificationCode + "\nPackages : " + packages + "\nTitle : " + title + "\nText : " + text + "\nId : " + date+ "\nandroid_id : " + android_id+ "\ndevicemodel : " + devicemodel);
                /**
                 Log.d("DetailsEzraatext2 :", "Notification : " + receivedNotificationCode + "\nPackages : " + packages + "\nTitle : " + title + "\nText : " + text + "\nId : " + date+ "\nandroid_id : " + android_id+ "\ndevicemodel : " + devicemodel);
                 */
            }
        }
    }
}

    ////////
public void startBackground(){
        Intent  intent = new Intent(this,MyReceiver.class);
    intent.setAction("BackgroundProcess");
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_MUTABLE);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 10, pendingIntent);

}























    public void onStartServiceClick(View v) {
        startService();
    }

    public void onStopServiceClick(View v) {
        stopService();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called");
        stopService();
        super.onDestroy();
    }

    public void startService() {
        Log.d(TAG, "startService called");
        if (!MyFirebaseMessagingService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyFirebaseMessagingService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void stopService() {
        Log.d(TAG, "stopService called");
        if (MyFirebaseMessagingService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyFirebaseMessagingService.class);
            stopService(serviceIntent);
        }
    }

    public void startServiceViaWorker() {
        Log.d(TAG, "startServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                       MyWorker.class,
                        16,
                        TimeUnit.MINUTES)
                       .build();

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
     //   workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);

    }




    @Override
    public void onNewIntent(Intent intent){
        //called when a new intent for this class is created.
        // The main case is when the app was in background, a notification arrives to the tray, and the user touches the notification

        super.onNewIntent(intent);

        Log.d("hulu", "onNewIntent - starting");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("hulu", "Extras received at onNewIntent:  Key: " + key + " Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if (message!=null && message.length()>0) {
                getIntent().removeExtra("body");
                showNotificationInADialog(title, message);
            }
        }
    }


    private void showNotificationInADialog(String title, String message) {

        // show a dialog with the provided title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }









}