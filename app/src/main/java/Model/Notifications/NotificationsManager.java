package Model.Notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import Model.Utils.Configurations;

import Model.Utils.Constants;
import Model.Utils.TimeUtils;
import static android.app.AlarmManager.INTERVAL_DAY;
import static android.content.Context.ALARM_SERVICE;

public class NotificationsManager {

    AlarmManager alarmManager;
    Context context;
    private static final String TAG = "NotificationsManager";


    public NotificationsManager(Context context) {
        this.context = context;
    }

    //todo: maybe this should be written only after HomePageActivity, because only there we have the logged in user.
    //todo: also, add an option to configure the time by a configurations file
    public void setNotifications() {
        createNotificationChannel(Constants.CHANNEL_ID);
        if (alarmManager == null)
            alarmManager = (AlarmManager) (context.getSystemService(ALARM_SERVICE));

        int daily_minute = Configurations.getNotificationMinute(context,"daily");
        int daily_hour = Configurations.getNotificationHour(context,"daily");
        int periodic_minute = Configurations.getNotificationMinute(context,"periodic");
        int periodic_hour =  Configurations.getNotificationHour(context,"periodic");

        int stepsTarget_minute = Configurations.getNotificationMinute(context,"stepsDest");
        int stepsTarget_hour =  Configurations.getNotificationHour(context,"stepsDest");

        int stepsReminder_minute = Configurations.getNotificationMinute(context,"stepsReminder");
        int stepsReminder_hour =  Configurations.getNotificationHour(context,"stepsReminder");

        //Daily notification
        Calendar daily_calendar = Calendar.getInstance();
        daily_calendar.setTimeInMillis(System.currentTimeMillis());
        daily_calendar.set(Calendar.HOUR_OF_DAY, daily_hour);
        daily_calendar.set(Calendar.MINUTE, daily_minute);

        //Periodic notification
        Calendar periodic_calendar = Calendar.getInstance();
        periodic_calendar.setTimeInMillis(System.currentTimeMillis());
        periodic_calendar.set(Calendar.HOUR_OF_DAY, periodic_hour);
        periodic_calendar.set(Calendar.MINUTE, periodic_minute);

        //stepsDest notification
        Calendar stepsTarget_calendar = Calendar.getInstance();
        stepsTarget_calendar.setTimeInMillis(System.currentTimeMillis());
        stepsTarget_calendar.set(Calendar.HOUR_OF_DAY, stepsTarget_hour);
        stepsTarget_calendar.set(Calendar.MINUTE, stepsTarget_minute);

        //stepsReminder notification
        Calendar stepsReminder_calendar = Calendar.getInstance();
        stepsReminder_calendar.setTimeInMillis(System.currentTimeMillis());
        stepsReminder_calendar.set(Calendar.HOUR_OF_DAY, stepsReminder_hour);
        stepsReminder_calendar.set(Calendar.MINUTE, stepsReminder_minute);

        setRepeatingNotification(DailyNotification.class, daily_calendar.getTimeInMillis() + TimeUtils.randomTime(),INTERVAL_DAY, 100);
        //Periodic notification
        setRepeatingNotification(PeriodicNotification.class, periodic_calendar.getTimeInMillis() + TimeUtils.randomTime(),INTERVAL_DAY,101);
        setRepeatingNotification(StepsTargetNotification.class, stepsTarget_calendar.getTimeInMillis(),INTERVAL_DAY,105);
        setRepeatingNotification(updateStepsNotification.class, stepsReminder_calendar.getTimeInMillis(),INTERVAL_DAY,106);


    }

    private void setRepeatingNotification(Class notification_class, long time, long interval, int requestCode) {
        Intent intent = new Intent(context, notification_class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
        Log.i(TAG,String.format("notification %s has been set to %s", notification_class.toString(), time));
    }


    private void createNotificationChannel(String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "שליחת נוטיפיקציות מדי יום עבור תזכורת למענה על שאלונים יומיים או תקופתיים או עמידה ביעדים מוגדרים";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
