package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
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
import Model.Users.Login;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.TimeUtils;
import Model.Utils.Urls;

public class StepsTargetNotification extends AbstractNotification {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StepsDest","OnReceive");
        Log.i("StepsTargetNotification","==========================================================================================");

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
                JSONObject status = getCurrentStepsStatus(context);
                boolean targetDone = false;
                int currentSteps = -1;
                int lastDaySteps = -1;

                if (status != null){
                    try {
                        JSONObject data = status.getJSONObject("data");
                        targetDone = data.getBoolean("targetDone");
                        currentSteps = data.getInt("currentSteps");
                        lastDaySteps = data.getInt("lastDaySteps");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    int id = 105;
                    if(targetDone && currentSteps >0 && lastDaySteps>0){
                        String notification_text = context.getString(R.string.improve_daily_steps) + "\n" +
                                    context.getString(R.string.yesterday_num_of_steps)+" "+ lastDaySteps + "\n" +
                                    context.getString(R.string.today_num_of_steps)+" "+ currentSteps;
                            notifyAboutDailyStepsStatus(context, notification_text, id);
                        Log.i("Steps_Target","-------------------------------------------------------");

                    }else if (!targetDone && currentSteps >0 && lastDaySteps>0){
                        String notification_text = context.getString(R.string.no_improve_daily_steps) + "\n" +
                                context.getString(R.string.yesterday_num_of_steps)+" "+ lastDaySteps + "\n" +
                                context.getString(R.string.today_num_of_steps)+" "+ currentSteps;
                        notifyAboutDailyStepsStatus(context, notification_text, id);
                        Log.i("Steps_Target","sending notification for failed steps target");
                    }
                }
            }
        });
        t.start();
    }

    private void notifyAboutDailyStepsStatus(Context context, String notification_text, int id){
        Log.i("TAG","notifyAboutSteps---------------------------------------------------------------------");

        Notification notification = null;
        notification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.step_reminder))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notif_icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notification_text))
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    private JSONObject getCurrentStepsStatus (Context context) {
        String url = Constants.urlPrefix + Urls.urlDailyStepsStatus;
        HttpRequests http = HttpRequests.getInstance(context);
        JSONObject result = null;
        String token = Login.getToken(context);
        try {
            result = http.sendGetRequestTest(url, token);
            return result;
        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
            Log.i("error", "problem in asking if user has been answered to server " + serverFalseException.getLocalizedMessage());
        }
        return null;
    }
}
