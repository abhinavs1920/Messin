package com.appfree.messin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.firebase.messaging.RemoteMessage;

public class NotificationActivity extends AppCompatActivity {

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Bundle extras = getIntent().getExtras();

        Log.d("hulu", "NotificationActivity - onCreate - extras: " + extras);

        if (extras == null) {
            context.finish();
            return;
        }

        RemoteMessage msg = (RemoteMessage) extras.get("msg");

        if (msg == null) {
            context.finish();
            return;
        }

        RemoteMessage.Notification notification = msg.getNotification();

        if (notification == null) {
            context.finish();
            return;
        }

        String dialogMessage;
        try {
            dialogMessage = notification.getBody();
        } catch (Exception e) {
            context.finish();
            return;
        }
        String dialogTitle = notification.getTitle();
        if (dialogTitle == null || dialogTitle.length() == 0) {
            dialogTitle = "";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper());
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);
        builder.setPositiveButton(getResources().getString(R.string.project_id), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}