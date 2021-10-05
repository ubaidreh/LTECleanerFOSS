package theredspy15.ltecleanerfoss.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
                return Result.retry();
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
        String title = "Cleaned:"+" "+MainActivity.convertSize(kilobytesTotal);
        makeStatusNotification(title,getApplicationContext());
    }

    static void makeStatusNotification(String message, Context context) {

        // Name of Notification Channel for verbose notifications of background work
        final CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose WorkManager Notifications";
        final String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work finishes";
        final CharSequence NOTIFICATION_TITLE = "LTE Clean Worker Finished";
        final String CHANNEL_ID = "VERBOSE_NOTIFICATION" ;
        final int NOTIFICATION_ID = 1;

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, VERBOSE_NOTIFICATION_CHANNEL_NAME, importance);
            channel.setDescription(VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_cleaning_services_24)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        // Show the notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }
}
