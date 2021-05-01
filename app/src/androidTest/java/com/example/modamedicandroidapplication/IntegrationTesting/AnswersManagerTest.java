package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Questionnaires.AnswersManager;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;

public class AnswersManagerTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void createJsonAnswersOfQuestionnaire() throws JSONException {
        //answers of daily Questionnaire
//        JSONObject jsonObj = new JSONObject("{\"QuestionnaireID\":0,\"ValidTime\":1619883727442,\"Answers\":[{\"QuestionID\":0,\"AnswerID\":[3]},{\"QuestionID\":1,\"AnswerID\":[1]}]}");

//        System.out.println("gfhs");
//        Map<Long, List<Long>> questionsAndAnswers = new HashMap<>();

    }

    @Test
    public void hasUserAnswered() {
        String q_id = "0";
        String days = "3";
        assertTrue(AnswersManager.hasUserAnswered(q_id,days,httpRequests));
    }
}