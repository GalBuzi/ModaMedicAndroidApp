package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Users.Settings;
import Model.Users.User;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;

public class SettingsTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void getSurgeryDate() {
        //take surgery date of gal's user from db
        long date = Long.parseLong("1620226800");

        //compare with result from method
        long ans = Settings.getSurgeryDate(httpRequests);

        assertEquals(date,ans);
    }

    @Test
    public void setSurgeryDate() {
        long new_date = Long.parseLong("1620226800");
        boolean ans = Settings.setSurgeryDate(httpRequests,new_date);
        long from_db = Settings.getSurgeryDate(httpRequests);
        assertTrue(ans);
        assertEquals(new_date,from_db);
    }

    @Test
    public void setUserQuestionnaires() {
        Questionnaire q0 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("0"),httpRequests);
        Questionnaire q1 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("1"),httpRequests);
        Questionnaire q2 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("2"),httpRequests);
        Questionnaire q3 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("3"),httpRequests);
        Questionnaire q5 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("5"),httpRequests);
        Questionnaire q6 = QuestionnaireSenderAndReceiver.getUserQuestionnaireById(Long.parseLong("6"),httpRequests);

        List<Questionnaire> list_q = new ArrayList<>();
        list_q.add(q0);
        list_q.add(q1);
        list_q.add(q2);
        list_q.add(q3);
        list_q.add(q5);
        list_q.add(q6);

        boolean ans = Settings.setUserQuestionnaires(httpRequests,list_q);

        assertTrue(ans);


    }
}