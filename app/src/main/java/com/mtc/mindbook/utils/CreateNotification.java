package com.mtc.mindbook.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mtc.mindbook.PlayerActivity;
import com.mtc.mindbook.R;
import com.mtc.mindbook.models.Track;
import com.mtc.mindbook.services.notification.NotificationActionService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREV = "actionprev";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_SKIP_TEN = "actionskipten";
    public static final String ACTION_REPLAY_TEN = "actionreplayten";

    public static Notification notification;
    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urlLink = urls[0];
            InputStream is = null;
            try {
                URL url = new URL(urlLink);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(is);
        }

    }


    public static void createNotification(Context context, Track track, int playBtn, int pos, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            PendingIntent pendingIntentPrev;
            int drwPrev;
            if (pos == 0) {
                pendingIntentPrev = null;
                drwPrev = 0;
            } else {
                Intent intentPrev = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREV);
                pendingIntentPrev = PendingIntent.getBroadcast(context, 0, intentPrev, PendingIntent.FLAG_CANCEL_CURRENT);
                drwPrev = R.drawable.ic_skip_previous;
            }

            Intent intentReplay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_REPLAY_TEN);
            PendingIntent pendingIntentReplayTen = PendingIntent.getBroadcast(context, 0, intentReplay, PendingIntent.FLAG_CANCEL_CURRENT);
            int drwReplay = R.drawable.ic_replay_10;

            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent intentSkip = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_SKIP_TEN);
            PendingIntent pendingIntentSkipTen = PendingIntent.getBroadcast(context, 0, intentSkip, PendingIntent.FLAG_CANCEL_CURRENT);
            int drwSkip = R.drawable.ic_forward_10;

            PendingIntent pendingIntentNext;
            int drwNext;
            if (pos == size) {
                pendingIntentNext = null;
                drwNext = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_CANCEL_CURRENT);
                drwNext = R.drawable.ic_skip_next;
            }

            Intent notificationIntent = new Intent(context, PlayerActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent noti = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);

            try {
                Bitmap bmp = new DownloadImageTask()
                        .execute(track.getCoverLink()).get();

                notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_mindbook)
                        .setContentTitle(track.getBookTitle())
                        .setContentText(track.getAuthor())
                        .setLargeIcon(bmp)
                        .setOnlyAlertOnce(true)
                        .setShowWhen(false)
                        .addAction(drwPrev, "Previous", pendingIntentPrev)
                        .addAction(drwReplay, "Replace 10s", pendingIntentReplayTen)
                        .addAction(playBtn, "Play", pendingIntentPlay)
                        .addAction(drwSkip, "Skip 10s", pendingIntentSkipTen)
                        .addAction(drwNext, "Next", pendingIntentNext)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.getSessionToken())
                        )
                        .setContentIntent(noti)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build();

                notificationManagerCompat.notify(1, notification);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }




        }
    }

}

