package View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.modamedicandroidapplication.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Controller.AppController;
import Model.ConnectedDevices;
import Model.Questionnaires.Questionnaire;
import Model.Utils.Constants;
import Model.Utils.NetworkUtils;
import View.ViewUtils.BindingValues;
import View.ViewUtils.MessageUtils;

/*
Home page screen
 */
public class HomePageBodyActivity extends AbstractActivity {
    private static final String TAG = "HomePageBodyActivity";
    Map<Long,String> questionnaires; //key: questID, value: questionnaire Text
    String username;
    AppController appController;
    BroadcastReceiver mReceiver = null;
    ScheduledExecutorService execOfBT = null;
    public static boolean BAND_CONNECTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        saveLastLogin();
        super.onCreate(savedInstanceState);
        username = getUserName();
        setContentView(R.layout.activity_homepage_body_new);
        appController = AppController.getController(this);
        Thread t_backgroundTasks = new Thread(new Runnable() {
            @Override
            public void run() {
                appController.setNotifications(getApplicationContext());
                Log.i("IN THREAD","==========================================================================================");
                appController.setMissingMetricsTask(getApplicationContext());
                appController.setMetricsTask(getApplicationContext());
            }
        });
        t_backgroundTasks.start();

        checkIfBandIsConnected();

        Thread t_sensorData = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                appController.SendSensorData();
            }
        });
        t_sensorData.start();

        //questionnaires = getAllQuestionnaires();

        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("name",not_exists);
        if (name.equals(not_exists)) {
            throw new NullPointerException("can't find username");
        }
        TextView good_eve = findViewById(R.id.good_evening_textView);
        good_eve.setText(String.format("%s %s, %s", this.getString(R.string.hello), name, getString(R.string.choose_questionnaire_by_body_section)));
        //createAllButtons();
        updateBTState();
        if (!NetworkUtils.hasInternetConnection(HomePageBodyActivity.this)) {
            MessageUtils.showAlert(HomePageBodyActivity.this,getString(R.string.no_internet_connection));
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"OnResume has been called");
//        if (changedQuestionnaires()) {
//            questionnaires = getAllQuestionnaires();
//            LinearLayout  layout =  findViewById(R.id.lin_layout);
//            layout.removeAllViews();
//            createAllButtons();
//        }
        checkIfBandIsConnected();
        updateBTState();
        Thread t_sensorData = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                appController.SendSensorData();
            }
        });
        t_sensorData.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop has been called");
        //  appController.setNotifications(getApplicationContext());
        unregisterBluetoothReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy has been called");
        unregisterBluetoothReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause has been called");
        unregisterBluetoothReceiver();
    }


    private void unregisterBluetoothReceiver() {
        try {
            if (mReceiver != null) {
                getApplicationContext().unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            if (execOfBT != null) {
                execOfBT.shutdown();
                execOfBT = null;
            }
        } catch (IllegalArgumentException e) {
            //do nothing
            Log.d(TAG,"Unregistering Error again. ignoring");
        }
    }

    private boolean changedQuestionnaires() {
        boolean res;
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        res = sharedPreferences.getBoolean(Constants.CHANGED_QUESTIONNAIRES, false);
        sharedPreferences.edit().putBoolean(Constants.CHANGED_QUESTIONNAIRES,false).apply();
        return res;
    }


    private void saveLastLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencesName,MODE_PRIVATE);
        long lastLogin = System.currentTimeMillis();
        sharedPreferences.edit().putLong(Constants.LAST_LOGIN,lastLogin).apply();

    }

    private String getUserName() {
        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("username",not_exists);
        if (name.equals(not_exists))
            throw new NullPointerException("huge problem in getIUserName");
        return name;
    }

    private void createAllButtons() {
        LinearLayout  layout =  findViewById(R.id.lin_layout);

        Button[] questionnaire_buttons = new Button[questionnaires.size()];
        int i=0;
        for (Map.Entry<Long,String> entry : questionnaires.entrySet()) {
            questionnaire_buttons[i] = new Button(this);
            final Long QuestionnaireID = entry.getKey();
//            String text = getString(R.string.questionnaire) + " " + entry.getValue();
            String text = getString(R.string.questionnaire) + " " + entry.getValue().split(";")[0];
            questionnaire_buttons[i].setText(text);
            questionnaire_buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    openQuestionnaireActivity(questionnaires.get(QuestionnaireID),QuestionnaireID);
                    openQuestionnaireActivity(questionnaires.get(QuestionnaireID),QuestionnaireID);
                }
            });
            setButtonConfiguration(questionnaire_buttons[i]);
            layout.addView(questionnaire_buttons[i]);
            i++;
        }
    }

//    public void goToQuestionnaireByCategory(View view) {
//        //Integer.parseInt((String)v.getTag())
//        String category = (String)view.getTag();
//        AppController appController = AppController.getController(this);
//        Map<Long, String> userQuestionnaires  = appController.getUserQuestionnaires();
//        Map<Long, String> questionnaires_filtered_by_category = new HashMap<>();
//        for (Map.Entry<Long,String> entry : userQuestionnaires.entrySet()) {
//            long q_id = entry.getKey();
//            Questionnaire q = appController.getQuestionnaire(q_id);
//            if (category.equals(q.getCategory())) {
//                questionnaires_filtered_by_category.put(q.getQuestionaireID(), q.getTitle());
//            }
//        }
//        if (questionnaires_filtered_by_category.size() > 0) {
//            Intent intent = new Intent(this, HomePageActivity.class);
//            intent.putExtra("Category", category);
//            startActivity(intent);
//        }
//        else
//            MessageUtils.showAlert(HomePageBodyActivity.this,getString(R.string.alert_no_questionnaire_for_body_part));
//    }

    public void goToQuestionnaireByCategory(View view) {
        //Integer.parseInt((String)v.getTag())
        String category = (String) view.getTag();
        ArrayList<String> titles = appController.getUserCategoriesQuestionnaireTitles(category);
        if(titles != null) {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra("Category", category);
            startActivity(intent);
        }
        else
            MessageUtils.showAlert(HomePageBodyActivity.this,getString(R.string.alert_no_questionnaire_for_body_part));
    }

    public void goToDailyQuestionnaire(View view) {
        openQuestionnaireActivity(getString(R.string.daily), (long) 0);
    }

    public void goToLifeQualityQuestionnaire(View view) {
        openQuestionnaireActivity(getString(R.string.daily), (long) 5);
    }

//    private void buildVideosBtn(){
//        LinearLayout  layout =  findViewById(R.id.lin_layout);
//        Button videoPlayer = new Button(this);
//        videoPlayer.setText("test");
//        videoPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openVideoActivity();
//            }
//        });
//        setButtonConfiguration(videoPlayer);
//    }

//    private void openVideoActivity() {
//        Intent intent = new Intent(this, VideoYoutubeActivity.class);
//        intent.putExtra("videoid", "mtL4fOWm3vY");
//        startActivity(intent);
//    }

    private void setButtonConfiguration(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout .LayoutParams(
                LinearLayout .LayoutParams.WRAP_CONTENT, LinearLayout .LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10, 10, 10);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
        b.setBackground(getDrawable(R.drawable.custom_button));
    }

    private void openQuestionnaireActivity(String questionnaire_name, Long questionnaire_id) {
        Log.i("Home Page","questionnaire " + questionnaire_name + " has been opened");
        Questionnaire questionnaire = appController.getQuestionnaire(questionnaire_id);
        Intent intent = new Intent(this, QuestionnaireActivity.class);
        intent.putExtra(BindingValues.REQUESTED_QUESTIONNAIRE, questionnaire);
        startActivity(intent);
    }

    private Map<Long,String> getAllQuestionnaires() {
        AppController appController = AppController.getController(this);
        Map<Long, String> questionnaires  = appController.getUserQuestionnaires();
        return questionnaires;
    }

    public void changePasswordFunction(View view) {
        Intent intent = new Intent(this, SetNewPasswordForLoggedInUserActivity.class);
        startActivity(intent);
    }


    public void checkIfBandIsConnected(){
        this.mReceiver = appController.checkIfBandIsConnected();
    }

    public void showBTInfo(View view) {
        BAND_CONNECTED = ConnectedDevices.BAND_CONNECTED;
        if (BAND_CONNECTED)
            Toast.makeText(view.getContext(),R.string.watch_on , Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(view.getContext(),R.string.watch_off , Toast.LENGTH_SHORT).show();
    }

    public void updateBTState() {
        if (execOfBT == null) {
            execOfBT = Executors.newSingleThreadScheduledExecutor();
            execOfBT.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    ImageView bt_state = findViewById(R.id.bt_state);
                    BAND_CONNECTED = ConnectedDevices.BAND_CONNECTED;
                    if (BAND_CONNECTED) {
                        Log.d(TAG,"Band is checked at " + Calendar.getInstance().getTime().toString() + " and is Connected");
                        bt_state.setBackgroundResource(R.drawable.green_circle);
                    }
                    else {
                        Log.d(TAG,"Band is checked at " + Calendar.getInstance().getTime().toString() + " and is Disconnected");

                        bt_state.setBackgroundResource(R.drawable.red_circle);
                    }
                }
            }, 0, 3, TimeUnit.SECONDS);
        }
    }


    public void logoutFunction(View view) {
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(Constants.KEEP_USER_LOGGED, false).apply();
        openMainActivity();
    }

    private void openMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void settingsFunction(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }



}

//class goToQuestionnaireOnClickListener implements View.OnClickListener
//{
//
//    String name;
//    long id;
//    String category;
//
//    public goToQuestionnaireOnClickListener(String name, long id, String category) {
//        this.name = name;
//        this.category = category;
//        this.id = id;
//    }
//
//    @Override
//    public void onClick(View v)
//    {
//        //read your lovely variable
//    }
//
//};
