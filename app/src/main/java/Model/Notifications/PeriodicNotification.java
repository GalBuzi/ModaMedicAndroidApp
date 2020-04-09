package Model.Notifications;

import android.content.Context;
import android.content.Intent;

import com.example.modamedicandroidapplication.R;

import java.util.Map;

import Model.Questionnaires.AnswersManager;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Utils.Constants;
import Model.Utils.HttpRequests;
import Model.Utils.PropertiesManager;
import View.MainActivity;

public class PeriodicNotification extends AbstractNotification{



    //TODO: change Main Activity to this Question activity
    @Override
    public void onReceive(Context context, Intent intent) {
        Map<Long,String> Questionnaires = getAllQuestionairesOfUser();
        for (Long questionnaireID: Questionnaires.keySet()) {
            if (questionnaireID == 0)
                continue;
            boolean answered = HasUserAnswered(questionnaireID.toString(), context);
            if (!answered) {
                String notification_text = context.getString(R.string.periodic_questionnaire_notification_pref) +
                        Questionnaires.get(questionnaireID) + context.getString(R.string.periodic_questionnaire_notification_suffix);
                int id = 100;
                notify(MainActivity.class, context, notification_text, id);
                System.out.println("Periodically for questionnaire " + Questionnaires.get(questionnaireID));
                try {
                    Thread.sleep(5000); //for avoid android block our app from posting notifications
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Map<Long, String> getAllQuestionairesOfUser() {
      return QuestionnaireSenderAndReceiver.getUserQuestionnaires(HttpRequests.getInstance());


}

}