package Model.Utils;

import android.content.Context;

public class Configurations {
    private static final String timeForDailyNotification = "timeForDailyNotification";
    private static final String timeForPeriodicNotification = "timeForPeriodicNotification";
    public static final String daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification = "daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification";
    private static final String timeForMissedMetricsCheckTask = "timeForMissedMetricsCheckTask";

    private static final String timeForStepsTargetNotification = "timeForStepsTargetNotification";
    private static final String timeForStepsReminderNotification = "timeForStepsReminderNotification";



    public static int getNotificationHour(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else if (type.equals("stepsDest"))
            time = PropertiesManager.getProperty(Configurations.timeForStepsTargetNotification, context);
        else if (type.equals("stepsReminder"))
            time = PropertiesManager.getProperty(Configurations.timeForStepsReminderNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[0]);
    }

    public static int getNotificationMinute(Context context, String type) {
        String time;
        if (type.equals("daily"))
            time = PropertiesManager.getProperty(Configurations.timeForDailyNotification, context);
        else if (type.equals("stepsDest"))
            time = PropertiesManager.getProperty(Configurations.timeForStepsTargetNotification, context);
        else if (type.equals("stepsReminder"))
            time = PropertiesManager.getProperty(Configurations.timeForStepsReminderNotification, context);
        else
            time = PropertiesManager.getProperty(Configurations.timeForPeriodicNotification, context);
        assert time != null;
        return Integer.parseInt(time.split(":")[1]);
    }

    public static int getMetricsTaskMinute(Context context) {
        String time = PropertiesManager.getProperty(Configurations.timeForMissedMetricsCheckTask, context);
        return Integer.parseInt(time.split(":")[1]);
    }

    public static int getMetricsTaskHour(Context context) {
        String time = PropertiesManager.getProperty(Configurations.timeForMissedMetricsCheckTask, context);
        return Integer.parseInt(time.split(":")[0]);
    }
}
