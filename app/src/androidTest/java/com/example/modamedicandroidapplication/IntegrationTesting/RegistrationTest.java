package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;
import android.service.autofill.RegexValidator;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Questionnaires.Questionnaire;
import Model.Questionnaires.QuestionnaireSenderAndReceiver;
import Model.Users.Registration;
import Model.Users.User;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RegistrationTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private User valid_user;
    private User invalid_user;
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Before
    public void createUser(){
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

        valid_user = new User("test@gmail.com","test123",
                "0544846512",
                "זכר",
                "לא מעשן","ניתוח מתוכנן",
                "השכלה אקדמאית",
                75,
                185,
                Long.parseLong("786146400000") ,
                "soroka372abc",
                0,
                "טסט",
                Long.parseLong("1717789143109"),
                list_q,
                "טסטטט",
                "test@gmail.com");

        invalid_user = new User("test111@gmail.com","test123",
                "0544846512",
                "זכר",
                "לא מעשן","ניתוח מתוכנן",
                "השכלה אקדמאית",
                75,
                185,
                Long.parseLong("786146400000") ,
                "invalidCode",
                0,
                "טסט",
                Long.parseLong("1717789143109"),
                list_q,
                "טסטטט",
                "test111@gmail.com");
    }

    @Test
    public void validRegister() {
        //valid registration
        String ans = Registration.register(valid_user,httpRequests);
        assertEquals("OK",ans);
    }

    @Test
    public void invalidRegisterTakenEmail() {
            //invalid registration
            String ans = Registration.register(valid_user,httpRequests);
            assertEquals("Taken Email",ans);
        }

        @Test
        public void invalidRegisterWrongCode() {
            //invalid registration
            String ans = Registration.register(invalid_user,httpRequests);
        assertEquals("Wrong Code",ans);
    }



    @Test
    public void update() {
        String gender = "נקבה";
        String isSmoker = "לא מעשן";
        String education = "השכלה אקדמית";
        int weight = 65;
        int height = 180;
        long birthday = Long.parseLong("679849200");

        boolean update_details = Registration.update(gender,isSmoker,education,weight,height,birthday,httpRequests);
        assertTrue(update_details);

    }

    @Test
    public void getAllVerificationQuestions() {
        String q0 = "מה שם חיית המחמד הראשונה שלך?";
        String q1 = "מהו שם הילדות של אמך?";
        String q2 = "מהו שם בית הספר היסודי בו למדת?";
        String q3 = "מהו שם הרחוב ומספר הבית בו התגוררת בילדותך?";
        String q4 = "באיזו עיר פגשת את בן/בת זוגך?";
        Map<Integer,String> all_questions = Registration.getAllVerificationQuestions(httpRequests);

        assertEquals(q0,all_questions.get(0));
        assertEquals(q1,all_questions.get(1));
        assertEquals(q2,all_questions.get(2));
        assertEquals(q3,all_questions.get(3));

        assertNotEquals(q1, "זוהי לא שאלת אימות מתוך השאלות הקיימות");

    }

    @Test
    public void updateSurgeryQuestionnairesFields() {
        Registration.updateSurgeryQuestionnairesFields("true","true",httpRequests);
        Map<String, Boolean> ans = Registration.getChangeWithSurgeryOrQuestionnaires(httpRequests);

        assertTrue(ans.get("changedQuestionnaires"));
        assertTrue(ans.get("changedSurgeryDate"));

    }
}