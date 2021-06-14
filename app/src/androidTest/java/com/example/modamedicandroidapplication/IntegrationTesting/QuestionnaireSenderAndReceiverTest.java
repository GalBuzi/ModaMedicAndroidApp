package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Users.User;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;

public class QuestionnaireSenderAndReceiverTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void sendAnswers() {
        Map<Long, List<Long>> answers = new HashMap<>();
        //daily questionnaire answers
        Long questionnaire_id = Long.parseLong("0");
        //Q1
        long answer_num1 = Long.parseLong("7");
        long quest_num1 = Long.parseLong("0");
        //Q2
        long answer_num2 = Long.parseLong("2");
        long quest_num2 = Long.parseLong("1");

        List<Long> arr1 = new ArrayList<>();
        arr1.add(answer_num1);
        answers.put(quest_num1,arr1);

        List<Long> arr2 = new ArrayList<>();
        arr2.add(answer_num2);
        answers.put(quest_num2,arr2);

    }

    @Test
    public void getUserQuestionnaires() {
    }

    @Test
    public void getUserQuestionnaireById() {
    }

    @Test
    public void getAllQuestionnaires() {
    }

    @Test
    public void getUserCategoriesQuestionnaireTitles() {
    }

    @Test
    public void isLastAnswerFromToday() {
    }

    @Test
    public void isLastAnswerFromLastTwoWeeks() {
    }

    @Test
    public void getChangeWithSurgeryOrQuestionnaires() {
    }
}