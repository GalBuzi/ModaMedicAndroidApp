package Controller;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.FitnessOptions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import Model.CaloriesGoogleFit;
import Model.DistanceGoogleFit;
import Model.GPS;
import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireManager;
import Model.StepsGoogleFit;

import static com.google.android.gms.fitness.data.DataType.TYPE_CALORIES_EXPENDED;
import static com.google.android.gms.fitness.data.DataType.TYPE_DISTANCE_DELTA;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class AppController {

    private static AppController appController;
    private StepsGoogleFit stepsGoogleFit;
    private DistanceGoogleFit distanceGoogleFit;
    private CaloriesGoogleFit caloriesGoogleFit;
    private Activity activity;
    private LocationManager locationManager;
    private LocationListener gpsLocationListener;


    private AppController(Activity activity) {
        this.activity = activity;
        this.stepsGoogleFit = new StepsGoogleFit();
        this.distanceGoogleFit = new DistanceGoogleFit();
        this.caloriesGoogleFit = new CaloriesGoogleFit();
        //TODO: need to ask for permission before this command
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.gpsLocationListener = new GPS(locationManager, activity);
    }

    public static AppController getController(Activity activity){
        if (appController == null){
            appController = new AppController(activity);
        }
        return appController;
    }

    public void ExtractSensorData(){

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_STEP_COUNT_DELTA,FitnessOptions.ACCESS_READ)
                        .addDataType(TYPE_CALORIES_EXPENDED,FitnessOptions.ACCESS_READ)
                        .build();

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)){
            int steps = stepsGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            float distance = distanceGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
            float calories = caloriesGoogleFit.getDataFromPrevDay(this.activity, fitnessOptions);
        }
        else{
            GoogleSignIn.requestPermissions(
                    this.activity, // your activity
                    1,
                    GoogleSignIn.getLastSignedInAccount(this.activity),
                    fitnessOptions);
        }

        //Weather
        String json =((GPS)gpsLocationListener).getLocationJSON();
        if (json == null){
            System.out.println("Did not found location");
        }


    }

    public Questionnaire getQuestionnaire(String questionnaire_name) {
        JSONObject jsonObject = getQuestionnaireFromDB(questionnaire_name);
        JSONParser parser = new JSONParser();
        //todo: remove this and get it from server
        String daily_from_server = "{\n" +
                "    \"error\": false,\n" +
                "    \"message\": null,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"_id\": \"5e148d34e4652b6457c2c034\",\n" +
                "            \"QuestionnaireID\": 0,\n" +
                "            \"QuestionnaireText\": \"Daily\",\n" +
                "            \"Questions\": [\n" +
                "                {\n" +
                "                    \"Answers\": [\n" +
                "                        {\n" +
                "                            \"answerID\": 0,\n" +
                "                            \"answerText\": \"0\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 1,\n" +
                "                            \"answerText\": \"1\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 2,\n" +
                "                            \"answerText\": \"2\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 3,\n" +
                "                            \"answerText\": \"3\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 4,\n" +
                "                            \"answerText\": \"4\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 5,\n" +
                "                            \"answerText\": \"5\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 6,\n" +
                "                            \"answerText\": \"6\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 7,\n" +
                "                            \"answerText\": \"7\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 8,\n" +
                "                            \"answerText\": \"8\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 9,\n" +
                "                            \"answerText\": \"9\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 10,\n" +
                "                            \"answerText\": \"10\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"QuestionID\": 0,\n" +
                "                    \"QuestionText\": \"מהי רמת הכאב הנוכחית שלך?\",\n" +
                "                    \"Type\": \"VAS\",\n" +
                "                    \"Best\": \"אין כאב בכלל\",\n" +
                "                    \"Worst\": \"כאב בלתי נסבל\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"Answers\": [\n" +
                "                        {\n" +
                "                            \"answerID\": 0,\n" +
                "                            \"answerText\": \"לא נטלתי\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 1,\n" +
                "                            \"answerText\": \"בסיסית\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 2,\n" +
                "                            \"answerText\": \"מתקדמת\"\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"answerID\": 3,\n" +
                "                            \"answerText\": \"נרקוטית\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"QuestionID\": 1,\n" +
                "                    \"QuestionText\": \"איזה סוג תרופה נטלת היום?\",\n" +
                "                    \"Type\": \"multi\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {
            Object obj = parser.parse(daily_from_server);
            jsonObject = (JSONObject) obj;
            System.out.println(jsonObject.toString());
            Log.i("AppController",jsonObject.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jssonArray = (JSONArray) jsonObject.get("data");
        if (jssonArray != null      ) {
            jsonObject = (JSONObject) jssonArray.get(0);
        }

        return QuestionnaireManager.createQuestionnaireFromJSON(jsonObject);
    }

    private JSONObject getQuestionnaireFromDB(String questionnaire_name) {
        //todo: implement this with anael code of rest requests
        return null;
    }

}
