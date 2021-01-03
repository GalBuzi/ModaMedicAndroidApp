package Model.Notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.modamedicandroidapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import Model.Utils.Constants;
import Model.Utils.TimeUtils;

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
                boolean completedDailySteps = false;
                boolean levelUp = false;
                int currLevel = -1;
                int remainingIteration = -1;
                int stepsTarget = -1;

                if (status != null){
                    try {
                        JSONObject data = status.getJSONObject("data");
                        completedDailySteps = data.getBoolean("targetDone");
                        levelUp = data.getBoolean("inNewLevel");
                        currLevel = data.getInt("currentLevel");
                        remainingIteration = data.getInt("repeatsLeft");
                        stepsTarget = data.getInt("stepsTarget");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    int id = 105;
                    if(completedDailySteps && currLevel >0 && remainingIteration>0 && stepsTarget>0){
                        if(levelUp){
                            String notification_text = context.getString(R.string.dailyStepsCompleted) + "\n" +
                                    context.getString(R.string.levelUp) + "\n" +
                                    context.getString(R.string.curr_level)+" "+ currLevel + "\n" +
                                    context.getString(R.string.stepsTarget)+" "+ stepsTarget + "\n" +
                                    context.getString(R.string.numOfRepeats)+" "+ remainingIteration;
                            notifyAboutDailyStepsStatus(context, notification_text, id);
                        }else{
                            String notification_text = context.getString(R.string.dailyStepsCompleted) + "\n" +
                                    context.getString(R.string.stepsTarget)+" "+ stepsTarget + "\n" +
                                    context.getString(R.string.curr_level)+" "+ currLevel + "\n" +
                                    context.getString(R.string.numOfRepeats)+" "+ remainingIteration;
                            notifyAboutDailyStepsStatus(context, notification_text, id);
                        }

                        Log.i("Steps_Target","-------------------------------------------------------");

                    }else if (!completedDailySteps &&currLevel >0 && remainingIteration>0 && stepsTarget>0){
                        String notification_text = context.getString(R.string.dailyStepsFailed) + "\n" +
                                context.getString(R.string.curr_level)+" "+ currLevel + "\n" +
                                context.getString(R.string.numOfRepeats)+" "+ remainingIteration;
                        notifyAboutDailyStepsStatus(context, notification_text, id);
                        Log.i("Steps_Target","sending notification for failed steps target ");

                    }
                }
            }
        });

        t.start();

    }
}
