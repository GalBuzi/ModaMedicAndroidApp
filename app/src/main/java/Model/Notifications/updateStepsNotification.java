package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import Model.Exceptions.ServerFalseException;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.TimeUtils;
import Model.Utils.Urls;
import View.HomePageActivity;
import View.HomePageBodyActivity;
import View.QuestionnaireActivity;

public class updateStepsNotification  extends AbstractNotification{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("stepsReminder","OnReceive");
        Log.i("stepsReminder","==========================================================================================");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPref = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
                long lastLogin = sharedPref.getLong(Constants.LAST_LOGIN, 0);
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long duration = currentTime - lastLogin;
                if (duration < TimeUtils.ONE_MINUTE) {
                    Log.i("Steps", "missing notification because I have been in the app in the last 1 min");
                    return;
                }

                /**
                 * make http req to server to get JSON of (did the user accomplished the target, current level, remaining iterations to next level)
                 */
                int id = 106;
                String notification_text = context.getString(R.string.tell_us_num_of_steps);
                notifyStepsReminder(context, notification_text, id);
            }
        });


        t.start();

    }

    private void notifyStepsReminder(Context context, String notification_text, int id){
        Log.i("stepsReminder","notifyUser");
        Intent intent = new Intent(context, HomePageBodyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 106, intent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = null;
        notification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.reminder))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.sym_action_chat, context.getString(R.string.notification_action), pendingIntent)
                .setSmallIcon(R.drawable.notif_icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notification_text))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
        Log.i("stepsReminder","end notifyUser");
    }

}
