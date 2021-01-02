package Model.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.modamedicandroidapplication.R;

import org.json.JSONObject;

import Model.Exceptions.ServerFalseException;
import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Users.Login;
import Model.Utils.Configurations;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.PropertiesManager;
import Model.Utils.Urls;
import View.QuestionnaireActivity;
import View.ViewUtils.BindingValues;

public abstract class AbstractNotification extends BroadcastReceiver {

    private final static String TAG = "Notification";

    /*
   this method should send notifications to user
    */
    public void notifyAboutQuestionnaire(Context context, String notification_text, int id, long questionnaire_id) {
        Log.i(TAG,"notifyUser");
        Intent intent = setQuestionnaireActivity(questionnaire_id,context);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_ONE_SHOT);

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
        Log.i(TAG,"end notifyUser");

    }

    protected boolean HasUserAnswered(String questionnaire_id, Context context) {
        String days;
        if (questionnaire_id.equals("0")) // daily questionnaire
            days = "0";
        else
            days = PropertiesManager.getProperty(Configurations.daysWithoutAnsweringQuestionnaireBeforeSendingPeriodicNotification,context);
        return AnswersManager.hasUserAnswered(questionnaire_id,days, HttpRequests.getInstance(context));
    }

    private Intent setQuestionnaireActivity(Long questionnaire_id, Context context) {
        Questionnaire questionnaire = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(questionnaire_id, HttpRequests.getInstance(context));
        Intent intent;
        intent = new Intent(context, QuestionnaireActivity.class);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        return intent;
    }

    protected JSONObject getCurrentStepsStatus (Context context) {
        String url = Urls.urlCurrentStepsStatus; //complete the path to server
        HttpRequests http = HttpRequests.getInstance(context);
        JSONObject result = null;
        try {
            result = http.sendGetRequest(url, Login.getToken(HttpRequests.getContext()));
            return result;
        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
            Log.i(TAG, "problem in asking if user has been answered to server " + serverFalseException.getLocalizedMessage());
        }
        return null;
    }

    public void notifyAboutDailyStepsStatus(Context context, String notification_text, int id){
        Log.i(TAG,"notifyAboutQuestionnaire");

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
        Log.i(TAG,"end notifyAboutQuestionnaire");
    }

}
