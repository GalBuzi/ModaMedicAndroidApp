package Model.Metrics.GoogleFit;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.HistoryClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import Model.Utils.HttpRequests;
import Model.Users.Login;
import Model.Metrics.DataSender;
import Model.Utils.Urls;

import static com.google.android.gms.fitness.data.Field.FIELD_CALORIES;

public class CaloriesGoogleFit implements DataSender {

    private static final String TAG = "CaloriesGoogleFit";
    private float calories = 0;
    private boolean calculated = false;
    private int extractionCounter = 0;

    public CaloriesGoogleFit() {
    }

    public void getDataFromPrevDay(Context context, GoogleSignInOptionsExtension fitnessOptions){

        extractionCounter++;

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        Calendar midnight = Calendar.getInstance();

        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        long endTime = System.currentTimeMillis();
        long startTime = midnight.getTimeInMillis();

        /**
         * Calories
         */

        DataReadRequest request = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .build();

        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        Task<DataReadResponse> task =historyClient.readData(request);

        task.addOnSuccessListener(response -> {

            extractionCounter = 0;

            DataSet dataset = response.getDataSets().get(0);

            for (DataPoint datapoint:
                 dataset.getDataPoints()) {
                calories += datapoint.getValue(FIELD_CALORIES).asFloat();
            }

            calculated = true;

            Log.i("Total cal of the day:", "************ " + Float.toString(calories) + " *************");
        })
        .addOnFailureListener(response -> {

            Log.e(TAG, "getDataFromPrevDay: failed to extract calories data");
            if (extractionCounter < 3){
                Log.i(TAG, "getDataFromPrevDay: retry extract calories data. counter value = " + extractionCounter);
                getDataFromPrevDay(context, fitnessOptions);
            }
            else{
                calculated = true;
                extractionCounter = 0;
            }
        });

    }

    public void getDataByDate(Context context, GoogleSignInOptionsExtension fitnessOptions, long startTime, long endTime){

        Log.i(TAG, "getDataByDate: got startTime = " + Long.toString(startTime )+ ", endTime = " + Long.toString(endTime));

        extractionCounter++;

        GoogleSignInAccount googleSignInAccount =
                GoogleSignIn.getAccountForExtension(context, fitnessOptions);

        /**
         * Calories
         */

        DataReadRequest request = new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .build();

        HistoryClient historyClient = Fitness.getHistoryClient(context, googleSignInAccount);
        Task<DataReadResponse> task =historyClient.readData(request);

        task.addOnSuccessListener(response -> {

            extractionCounter = 0;

            DataSet dataset = response.getDataSets().get(0);

            for (DataPoint datapoint:
                    dataset.getDataPoints()) {
                calories += datapoint.getValue(FIELD_CALORIES).asFloat();
            }

            Log.i("Total cal of the day:", "************ " + Float.toString(calories) + " *************");

            sendDataToServer(HttpRequests.getInstance());

        })
                .addOnFailureListener(response -> {

                    Log.e(TAG, "getDataByDate: failed to extract calories data");
                    if (extractionCounter < 3){
                        Log.i(TAG, "getDataByDate: retry extract calories data. counter value = " + Integer.toString(extractionCounter));
                        getDataByDate(context, fitnessOptions, startTime, endTime);
                    }
                    else{
                        extractionCounter = 0;
                    }
                });

    }

    public JSONObject makeBodyJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("ValidTime", System.currentTimeMillis());
            json.put("Data", this.calories);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public boolean hadBeenCalc() {
        return calculated;
    }

    public void sendDataToServer(HttpRequests httpRequests) {
        try {
            httpRequests.sendPostRequest(makeBodyJson(), Urls.urlPostCalories, Login.getToken());
        }
        catch (Exception e){
            Log.e(TAG, "No data in calories.");
            e.printStackTrace();
        }
    }
}
