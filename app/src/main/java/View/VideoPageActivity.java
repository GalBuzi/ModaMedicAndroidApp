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

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/*
Home page screen
 */
public class VideoPageActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "HomePageActivity";
    Map<String,ArrayList<String>> all_exercises = null; //key: category, value: array of videos
    String username;
    AppController appController;
    BroadcastReceiver mReceiver = null;
    ScheduledExecutorService execOfBT = null;
    public static boolean BAND_CONNECTED = false;
    private Map<String,String> playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        saveLastLogin();
        super.onCreate(savedInstanceState);
        username = getUserName();
        setContentView(R.layout.activity_video_page_new);
        appController = AppController.getController(this);
        initialPlaylists();
//        Thread t_backgroundTasks = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                appController.setNotifications(getApplicationContext());
//                Log.i("IN THREAD","==========================================================================================");
//                appController.setMissingMetricsTask(getApplicationContext());
//                appController.setMetricsTask(getApplicationContext());
//            }
//        });
//        t_backgroundTasks.start();

        checkIfBandIsConnected();

//        Thread t_sensorData = new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void run() {
//                appController.SendSensorData();
//            }
//        });
//        t_sensorData.start();

        YouTubePlayerView player = findViewById(R.id.player);
        player.initialize(Constants.Youtube_API_key,this);

//        if (all_exercises == null)
//            all_exercises = getAllExercises();

//        String not_exists = "not exists";
//        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
//        String name = sharedPref.getString("name",not_exists);
//        if (name.equals(not_exists)) {
//            throw new NullPointerException("can't find username");
//        }
//        TextView good_eve = findViewById(R.id.good_evening_textView);
//        good_eve.setText(String.format("%s %s, %s", this.getString(R.string.hello), name, getString(R.string.choose_questionnaire)));

//        createAllButtons();
        updateBTState();
        if (!NetworkUtils.hasInternetConnection(VideoPageActivity.this)) {
            MessageUtils.showAlert(VideoPageActivity.this,getString(R.string.no_internet_connection));
            return;
        }

    }

    private void initialPlaylists() {
        playlists = new HashMap<>();
        playlists.put("Leg","PLP7fCOgQdN3OfWqIPfcwGGOzDxyCV3Hoc");
        playlists.put("Neck","PLP7fCOgQdN3Ozwa1eOxgxK-ilrzgdGD60");
        playlists.put("Spine","PLP7fCOgQdN3PWfguuNX0m_JWkcuUBdwOl");
        playlists.put("Hand","PLP7fCOgQdN3PuKGysNZfcpt4Y9BGx4NLa");
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
//        Thread t_sensorData = new Thread(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void run() {
//                appController.SendSensorData();
//            }
//        });
//        t_sensorData.start();
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

//    private void createAllButtons() {
//        LinearLayout  layout =  findViewById(R.id.lin_layout);

//        int total_vids = all_exercises.size();

//        YouTubePlayerView[] tubePlayerViews = new YouTubePlayerView[total_vids];
//        YouTubePlayer.OnInitializedListener[] inits = new YouTubePlayer.OnInitializedListener[total_vids];
//        Button[] play_btns = new Button[total_vids];
//        String category = getIntent().getStringExtra("Category");
//        final ArrayList<String> filtered = all_exercises.get(category);
//        Button play = new Button(this);

//        YouTubePlayerView playerView = new YouTubePlayerView(this);
//        YouTubePlayer.OnInitializedListener init = new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                youTubePlayer.loadVideos(filtered);
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        };
//        play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    playerView.initialize(Constants.Youtube_API_key, init);
//                }
//            });
//        play.setText("Play");
//        setButtonConfiguration(play);
//        setPlayerConfiguration(playerView);
//        layout.addView(playerView);
//        layout.addView(play);
//        int i=0;
//        for (String entry : all_exercises) {
//            play_btns[i] = new Button(this);
//            tubePlayerViews[i] = new YouTubePlayerView(this);
//            play_btns[i].setText("Play");
//            final int num = i;
//            final String id_video = entry;
//            inits[i] = new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                    youTubePlayer.loadVideo(id_video);
//
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//                }
//            };
//
//            play_btns[i].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tubePlayerViews[num].initialize(Constants.Youtube_API_key, inits[num]);
//                }
//            });
//            setButtonConfiguration(play_btns[i]);
//            setPlayerConfiguration(tubePlayerViews[i]);
//            layout.addView(tubePlayerViews[i]);
//            layout.addView(play_btns[i]);
//
//            i++;
//        }
//    }

    private void setButtonConfiguration(Button b) {
        LinearLayout.LayoutParams params = new LinearLayout .LayoutParams(
                LinearLayout .LayoutParams.WRAP_CONTENT, LinearLayout .LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10, 10, 10);
        b.setGravity(Gravity.CENTER);
        b.setLayoutParams(params);
        b.setBackground(getDrawable(R.drawable.custom_button));
    }

    private void setPlayerConfiguration(YouTubePlayerView p) {
        LinearLayout.LayoutParams params = new LinearLayout .LayoutParams(
                LinearLayout .LayoutParams.MATCH_PARENT, 500);
        params.setMargins(10,10, 10, 10);
        p.setLayoutParams(params);
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
        Map<Long, String> userQuestionnaires  = appController.getUserQuestionnaires();
        Map<Long, String> questionnaires_filtered_by_category = new HashMap<>();
        String category = getIntent().getStringExtra("Category");
        for (Map.Entry<Long,String> entry : userQuestionnaires.entrySet()) {
            long q_id = entry.getKey();
            Questionnaire q = appController.getQuestionnaire(q_id);
            System.out.println("test");
            if (category.equals(q.getCategory())){
                questionnaires_filtered_by_category.put(q.getQuestionaireID(),q.getTitle());
            }

        }
        return questionnaires_filtered_by_category;
    }

//    private Map<String, ArrayList<String>> getAllExercises() {
////        public ArrayList<String> getAllExercises() {
//        AppController appController = AppController.getController(this);
//        Map<String, ArrayList<String>> userQuestionnaires  = appController.getAllExercises();
////        ArrayList<String> userQuestionnaires  = appController.getAllExercises();
//        return userQuestionnaires;
//    }

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
        Intent intent = new Intent(this, HomePageBodyActivity.class);
        startActivity(intent);
    }

    public void openMainActivity(View view) {
        finish();
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra("Category", getIntent().getStringExtra("Category"));
        startActivity(intent);
    }

    public void settingsFunction(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        if (!b) {
            String category = getIntent().getStringExtra("Category");
            String list = playlists.get(category);
            youTubePlayer.cuePlaylist(list);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };
}
