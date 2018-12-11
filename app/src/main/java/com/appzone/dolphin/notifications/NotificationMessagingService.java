package com.appzone.dolphin.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.appzone.dolphin.Models.NotificationCountModel;
import com.appzone.dolphin.Models.UserModel;
import com.appzone.dolphin.R;
import com.appzone.dolphin.preferences.Preferences;
import com.appzone.dolphin.tags.Tags;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class NotificationMessagingService extends FirebaseMessagingService {
    Preferences preferences = Preferences.getInstance();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> map = remoteMessage.getData();
        for (String key:map.keySet())
        {
            Log.e("Notification","Key"+key+" value="+map.get(key));
        }

        manageNotification(map);
    }

    private void manageNotification(final Map<String, String> map) {

        if (getSession().equals(Tags.LOGIN_STATE))
        {
            String user_id = getUserData().getUser_id();
            String to_id = map.get("to_user_id");

            if (user_id.equals(to_id))
            {
                new Handler(Looper.getMainLooper())
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                createNotification(map);
                            }
                        },100);
            }
        }
    }

    private void createNotification(final Map<String, String> map) {
        String sound_path = "android.resource://"+getPackageName()+"/"+ R.raw.not;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            String CHANNEL_ID = "my_channel_12";
            CharSequence CHANNEL_NAME = "my_channel_name";
            int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,IMPORTANCE);
            channel.setSound(Uri.parse(sound_path),new AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()
            );

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setChannelId(CHANNEL_ID);
            builder.setSound(Uri.parse(sound_path));
            builder.setSmallIcon(R.mipmap.ic_launcher);

            if (map.get("from_user_type").equals("admin"))
            {
                builder.setContentTitle(getString(R.string.admin));

            }else
                {
                    builder.setContentTitle(map.get("from_user_name"));

                }

            String notification_type = map.get("notification_type");
            if (notification_type.equals("confirm_reservation"))
            {
                builder.setContentText(getString(R.string.confirm_reservation));

            }else if (notification_type.equals("add_table_order"))
            {
                builder.setContentText(getString(R.string.work_schedules));

            }
            else if (notification_type.equals("add_arrival_time"))
            {
                builder.setContentText(getString(R.string.confirm_technical_arrive));

            }
            else if (notification_type.equals("finish_order"))
            {
                builder.setContentText(getString(R.string.Finished_add_evaluation));

            }else if (notification_type.equals("office_evaluation"))
            {
                builder.setContentText(getString(R.string.Evaluation_official));

            }
            final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Target target =new Target() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (manager!=null)
                    {
                        builder.setLargeIcon(bitmap);
                        manager.createNotificationChannel(channel);
                        manager.notify(1,builder.build());
                        EventBus.getDefault().post(new NotificationCountModel());
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };


            if (map.get("from_user_type").equals("admin")||map.get("from_user_type").equals(Tags.USER_MEMBER))
            {
                Picasso.with(this).load(R.drawable.logo).into(target);
            }else
                {
                    Picasso.with(this).load(Uri.parse(Tags.IMAGE_PATH+map.get("from_user_image"))).into(target);
                }




        }else
            {
                final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setSound(Uri.parse(sound_path));
                builder.setSmallIcon(R.mipmap.ic_launcher);


                final NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Target target =new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (manager!=null)
                        {
                            if (map.get("from_user_type").equals("admin"))
                            {
                                builder.setContentTitle(getString(R.string.admin));

                            }else
                            {
                                builder.setContentTitle(map.get("from_user_name"));

                            }

                            String notification_type = map.get("notification_type");
                            if (notification_type.equals("confirm_reservation"))
                            {
                                builder.setContentText(getString(R.string.confirm_reservation));

                            }else if (notification_type.equals("add_table_order"))
                            {
                                builder.setContentText(getString(R.string.work_schedules));

                            }
                            else if (notification_type.equals("add_arrival_time"))
                            {
                                builder.setContentText(getString(R.string.confirm_technical_arrive));

                            }
                            else if (notification_type.equals("finish_order"))
                            {
                                builder.setContentText(getString(R.string.Finished_add_evaluation));

                            }else if (notification_type.equals("office_evaluation"))
                            {
                                builder.setContentText(getString(R.string.Evaluation_official));

                            }
                            builder.setLargeIcon(bitmap);
                            manager.notify(1,builder.build());
                            EventBus.getDefault().post(new NotificationCountModel());

                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };


                if (map.get("from_user_type").equals("admin")||map.get("from_user_type").equals(Tags.USER_MEMBER))
                {
                    Picasso.with(this).load(R.drawable.logo).into(target);
                }else
                {
                    Picasso.with(this).load(Uri.parse(Tags.IMAGE_PATH+map.get("from_user_image"))).into(target);
                }

            }
    }


    private String getSession()
    {
        return preferences.getSession(this);
    }

    private UserModel getUserData()
    {
        return preferences.getUserModel(this);
    }


}
