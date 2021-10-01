package theredspy15.ltecleanerfoss.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;

import theredspy15.ltecleanerfoss.FileScanner;
import theredspy15.ltecleanerfoss.R;
import theredspy15.ltecleanerfoss.controllers.MainActivity;

public class CleanWorker extends Worker {
    private static final String CHANNEL_ID = CleanWorker.class.getSimpleName();

    public CleanWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (MainActivity.prefs.getBoolean("dailyclean",false) && !FileScanner.isRunning) {
            try {
                scan();
            } catch (Exception e) {
                Log.e(CHANNEL_ID,"error running cleanworker",e);
                return Result.failure();
            }
        }

        return Result.success();
    }

    private void scan() {
        File path = Environment.getExternalStorageDirectory();

        // scanner setup
        FileScanner fs = new FileScanner(path,getApplicationContext())
                .setEmptyDir(MainActivity.prefs.getBoolean("empty", false))
                .setAutoWhite(MainActivity.prefs.getBoolean("auto_white", true))
                .setDelete(true)
                .setCorpse(MainActivity.prefs.getBoolean("corpse", false))
                .setGUI(null)
                .setContext(getApplicationContext())
                .setUpFilters(
                        MainActivity.prefs.getBoolean("generic", true),
                        MainActivity.prefs.getBoolean("aggressive", false),
                        MainActivity.prefs.getBoolean("apk", false));

        // kilobytes found/freed text
        long kilobytesTotal = fs.startScan();

        // notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String title = "Cleaned:"+" "+MainActivity.convertSize(kilobytesTotal);

            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_MIN);

            PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
            Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_cleaning_services_24)
                    .build();

            NotificationManager notificationManager=(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(1159864,notification);
        }
    }
}