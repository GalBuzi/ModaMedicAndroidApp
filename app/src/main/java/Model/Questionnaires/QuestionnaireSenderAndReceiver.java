package Model.Questionnaires;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Exceptions.ServerFalseException;
import Model.Users.Login;
import Model.Utils.HttpRequests;
import Model.Utils.Urls;

public class QuestionnaireSenderAndReceiver {

    private static final String TAG = "QuestionnaireSender";
    public static void sendAnswers(Map<Long, List<Long>> questionsAndAnswers, Long questionnaireID, HttpRequests httpRequests) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject request = AnswersManager.createJsonAnswersOfQuestionnaire(questionsAndAnswers,questionnaireID);
                try {
                    httpRequests.sendPostRequest(request, Urls.urlPostAnswersOfQuestionnaireByID, Login.getToken(HttpRequests.getContext()));
                    Log.i(TAG,"sent to server");

                } catch (ServerFalseException serverFalseException) {
                    serverFalseException.printStackTrace();
                    Log.i(TAG,"problem in sending questionaire to server "+ serverFalseException.getLocalizedMessage());
                }
            }
        });
        t.start();
    }

    public static Map<Long, String> getUserQuestionnaires(HttpRequests httpRequests) {
        JSONObject user_questionnaires;
        Map<Long,String> result = new HashMap<>();
        try {
           user_questionnaires = httpRequests.sendGetRequest(Urls.urlGetUserQuestionnaires, Login.getToken(HttpRequests.getContext()) );

            JSONArray array = user_questionnaires.getJSONArray("data");
            for (int i=0; i<array.length(); i++) {
                Long id = Long.valueOf( (Integer)array.getJSONObject(i).get("QuestionnaireID"));
                String text = (String)array.getJSONObject(i).get("QuestionnaireText");
                if (id!=6)
                    result.put(id,text);
                else
                    System.out.println("skipping questionnaire 6");
            }
        } catch (ServerFalseException | JSONException serverFalseException) {
            serverFalseException.printStackTrace();
        }

        return result;
    }

    public static Questionnaire getUserQuestionnaireById(Long questionnaire_id, HttpRequests httpRequests) {
        JSONObject jsonObject = getQuestionnaireFromDB(Urls.urlGetQuestionnaireByID+questionnaire_id, httpRequests);

        try {
            assert jsonObject != null;
            Log.i(TAG, jsonObject.toString());
            jsonObject = (JSONObject) jsonObject.get("data");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return QuestionnaireManager.createQuestionnaireFromJSON(jsonObject);
    }

    private static JSONObject getQuestionnaireFromDB(String questionnaire_name, HttpRequests httpRequests) {
        try {
            return httpRequests.sendGetRequest(questionnaire_name);
        } catch (ServerFalseException serverFalseException) {
            serverFalseException.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, String> getAllQuestionnaires(HttpRequests httpRequests) {
        Map<Integer, String> result = new HashMap<>();
        try {
            JSONObject response = httpRequests.sendGetRequest(Urls.urlOfGetAllQuestionnaires);
            JSONArray array = response.getJSONArray("data");
            for (int i=0; i<array.length(); i++) {
                JSONObject question = (JSONObject) array.get(i);
                int id = question.getInt("QuestionnaireID");
                if (id == 5 || id ==6 || id == 0) //daily and EQ5 special question should not be setted
                    continue;
                String text = question.getString("QuestionnaireText");
                result.put(id,text);
            }
        } catch (ServerFalseException | JSONException e) {
            Log.i(TAG,"error in getting all questionnaies:");
            e.printStackTrace();
            result=null;
        }
        return result;


    }

    //getUserQuestionnaireTitlesByCategory
    public static ArrayList<String> getUserCategoriesQuestionnaireTitles(HttpRequests httpRequests, String category) {
        ArrayList<String> titles = new ArrayList<>();
        JSONObject json = new JSONObject();
        String token = Login.getToken(HttpRequests.getContext());
        try {
            json.put("Category", category);
            JSONObject response = httpRequests.sendPostRequest(json, Urls.getUserQuestionnaireTitlesByCategory,token);
            JSONArray array = response.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                titles.add((String)array.get(i));
            }
            return titles;
        } catch (JSONException | ServerFalseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isLastAnswerFromToday(HttpRequests httpRequests) {
        JSONObject jsonObject = null;
        try {
            jsonObject = httpRequests.sendGetRequest(Urls.daily_last_response, Login.getToken(HttpRequests.getContext()));

            long last_response = jsonObject.getLong("data");

            Calendar midnight = Calendar.getInstance();

            midnight.set(Calendar.HOUR_OF_DAY, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);

            long last_midnight = midnight.getTimeInMillis();
            if (last_midnight - last_response > 0 )
                return false;
            else
                return true;
        }
        catch (JSONException | ServerFalseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isLastAnswerFromLastTwoWeeks(HttpRequests httpRequests, long questionnaireID) {
        JSONObject jsonObject = null;
        JSONObject send = new JSONObject();
        try {
            send.put("QuestionnaireID", questionnaireID);
            jsonObject = httpRequests.sendPostRequest(send, Urls.periodic_last_response, Login.getToken(HttpRequests.getContext()));

            long last_response = jsonObject.getLong("data");

            Calendar midnight = Calendar.getInstance();

            midnight.set(Calendar.HOUR_OF_DAY, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);
            midnight.set(Calendar.MILLISECOND, 0);
            midnight.add(Calendar.DAY_OF_YEAR,-13);

            long last_midnight_14_days_ago = midnight.getTimeInMillis();
            if (last_midnight_14_days_ago - last_response > 0 )
                return false;
            else
                return true;
        }
        catch (JSONException | ServerFalseException e) {
            e.printStackTrace();
        }

        return false;
    }


}
