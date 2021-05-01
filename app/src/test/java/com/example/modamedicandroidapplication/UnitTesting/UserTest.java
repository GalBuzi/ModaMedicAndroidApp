package com.example.modamedicandroidapplication.UnitTesting;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Model.Questionnaires.Questionnaire;
import Model.Users.User;

import static org.junit.Assert.assertEquals;

public class UserTest {
    private User valid_user;

    @Before
    public void createUserTest() {
        Questionnaire q0 = new Questionnaire();
        Questionnaire q1 = new Questionnaire();
        List<Questionnaire> list_q = new ArrayList<>();
        list_q.add(q0);
        list_q.add(q1);

        valid_user = new User("test@gmail.com", "test123",
                "0544846512",
                "זכר",
                "לא מעשן", "ניתוח מתוכנן",
                "השכלה אקדמאית",
                75,
                185,
                Long.parseLong("786146400000"),
                "soroka372abc",
                0,
                "טסט",
                Long.parseLong("1717789143109"),
                list_q,
                "טסטטט",
                "test@gmail.com");

    }
    @Test
    public void getBMI_NUMBER() {
        assertEquals(Double.parseDouble("21.913805697589478"),valid_user.getBMI_NUMBER(),0);
    }

    @Test
    public void getEmail() {
        assertEquals("test@gmail.com",valid_user.getEmail());
    }

    @Test
    public void setEmail() {
        valid_user.setEmail("test2@gmail.com");
        assertEquals("test2@gmail.com",valid_user.getEmail());
    }

    @Test
    public void getPassword() {
        assertEquals("test123",valid_user.getPassword());
    }

    @Test
    public void setPassword() {
        valid_user.setPassword("321test");
        assertEquals("321test",valid_user.getPassword());
    }

    @Test
    public void getPhoneNumber() {
        assertEquals("0544846512",valid_user.getPhoneNumber());
    }

    @Test
    public void setPhoneNumber() {
        valid_user.setPhoneNumber("052876234");
        assertEquals("052876234",valid_user.getPhoneNumber());
    }

    @Test
    public void getGender() {
        assertEquals("זכר",valid_user.getGender());
    }

    @Test
    public void setGender() {
        valid_user.setGender("נקבה");
        assertEquals("נקבה",valid_user.getGender());
    }

    @Test
    public void getSmoke() {
        assertEquals("לא מעשן",valid_user.getSmoke());
    }

    @Test
    public void setSmoke() {
        valid_user.setSmoke("מעשן");
        assertEquals("מעשן",valid_user.getSmoke());
    }

    @Test
    public void getSurgeryType() {
        assertEquals("ניתוח מתוכנן",valid_user.getSurgeryType());
    }

    @Test
    public void setSurgeryType() {
        valid_user.setSurgeryType("ניתוח דחוף");
        assertEquals("ניתוח דחוף",valid_user.getSurgeryType());
    }

    @Test
    public void getEducation() {
        assertEquals("השכלה אקדמאית",valid_user.getEducation());
    }

    @Test
    public void setEducation() {
        valid_user.setEducation("השכלה תיכונית");
        assertEquals("השכלה תיכונית",valid_user.getEducation());
    }

    @Test
    public void getWeight() {
        assertEquals(75,valid_user.getWeight());
    }

    @Test
    public void setWeight() {
        valid_user.setWeight(90);
        assertEquals(90,valid_user.getWeight());
    }

    @Test
    public void getHeight() {
        assertEquals(185,valid_user.getHeight());
    }

    @Test
    public void setHeight() {
        valid_user.setHeight(190);
        assertEquals(190,valid_user.getHeight());
    }

    @Test
    public void getBmi() {
        assertEquals("21.913805697589478",valid_user.getBmi());
    }

    @Test
    public void getBirthday() {
        assertEquals(Long.parseLong("786146400000"),valid_user.getBirthday());
    }

    @Test
    public void setBirthday() {
        valid_user.setBirthday(Long.parseLong("786146411111"));
        assertEquals(Long.parseLong("786146411111"),valid_user.getBirthday());
    }

    @Test
    public void getCode() {
        assertEquals("soroka372abc",valid_user.getCode());
    }

    @Test
    public void setCode() {
        valid_user.setCode("abc372Soroka");
        assertEquals("abc372Soroka",valid_user.getCode());
    }

    @Test
    public void getVerificationQuestion() {
        assertEquals(0,valid_user.getVerificationQuestion());
    }

    @Test
    public void setVerificationQuestion() {
        valid_user.setVerificationQuestion(1);
        assertEquals(1,valid_user.getVerificationQuestion());
    }

    @Test
    public void getVerificationAnswer() {
        assertEquals("טסט",valid_user.getVerificationAnswer());
    }

    @Test
    public void setVerificationAnswer() {
        valid_user.setVerificationAnswer("שינוי טסט");
        assertEquals("שינוי טסט",valid_user.getVerificationAnswer());
    }

    @Test
    public void getSurgeryDate() {
        assertEquals(Long.parseLong("1717789143109"),valid_user.getSurgeryDate());
    }

    @Test
    public void setSurgeryDate() {
        valid_user.setSurgeryDate(Long.parseLong("1717789141111"));
        assertEquals(Long.parseLong("1717789141111"),valid_user.getSurgeryDate());
    }

    @Test
    public void getQuestionnaires() {
        assertEquals(2,(valid_user.getQuestionnaires()).size());
    }

    @Test
    public void setQuestionnaires() {
        Questionnaire q0 = new Questionnaire();
        List<Questionnaire> list_q2 = new ArrayList<>();
        list_q2.add(q0);
        valid_user.setQuestionnaires(list_q2);
        assertEquals(1,(valid_user.getQuestionnaires()).size());
    }

    @Test
    public void getFirstName() {
        assertEquals("טסטטט",valid_user.getFirstName());
    }

    @Test
    public void setFirstName() {
        valid_user.setFirstName("שינוי");
        assertEquals("שינוי",valid_user.getFirstName());
    }

    @Test
    public void getLastName() {
        assertEquals("test@gmail.com",valid_user.getLastName());
    }

    @Test
    public void setLastName() {
        valid_user.setLastName("change@gmail.com");
        assertEquals("change@gmail.com",valid_user.getLastName());
    }
}