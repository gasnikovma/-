package app.gasnikov.helper.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.gasnikov.helper.Map_Activity;
import app.gasnikov.helper.Message_Activity;
import app.gasnikov.helper.fragments.helper.fragments.Chats;
import retrofit2.http.Url;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    public static final int ID_SMALL_NOTIFICATION=235;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String user2=remoteMessage.getData().get("user");
        SharedPreferences preferences=getSharedPreferences("PREFERS",MODE_PRIVATE);
        String curuser=preferences.getString("curuser","nobody");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            if (!curuser.equals(user2)) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String CHANNEL_ID = "my_channel_01";
                CharSequence name = "my_channel";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                String icon = remoteMessage.getData().get("icon");
                String body = remoteMessage.getData().get("body");
                String title = remoteMessage.getData().get("title");
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                notificationManager.createNotificationChannel(mChannel);
                Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(Integer.parseInt(icon)).setContentTitle(title).setContentText(body).setAutoCancel(true).setSound(defaultsound);
                Intent intent = new Intent(this, Message_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putString("userid", user2);
                intent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, ID_SMALL_NOTIFICATION, intent, PendingIntent.FLAG_ONE_SHOT);
                notificationbuilder.setContentIntent(pendingIntent);

                notificationManager.notify(ID_SMALL_NOTIFICATION, notificationbuilder.build());

            }
        }
    }



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String refreshtoken = task.getResult();
                        updatetoken(refreshtoken);


                    }
                });

    }

    private void updatetoken(String refreshtoken) {
        Token token=new Token(refreshtoken);
        db.getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

    }
}

