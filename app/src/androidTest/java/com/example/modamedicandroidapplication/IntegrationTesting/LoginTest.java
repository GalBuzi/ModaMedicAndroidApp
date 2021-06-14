package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import Model.Exceptions.InvalidTokenException;
import Model.Exceptions.WrongAnswerException;
import Model.Users.Login;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class LoginTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void login() {
        //login with existing user
        String username = "galb411@gmail.com";
        String pass = "gg";
        assertTrue(Login.login(username,pass,appContext,httpRequests));

        //login with fake user
        String pass_wrong = "gal";
        assertFalse(Login.login(username,pass_wrong,appContext,httpRequests));
    }

    @Test
    public void setInitSteps() {
        JSONObject json = new JSONObject();
        try {
            json.put("UserID", "galb411@gmail.com");
            json.put("Password", "g");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = Login.getToken(appContext);

        //gal's user already initialized
        assertEquals("Initial data exist" , Login.setInitSteps(json,httpRequests,token,"Daily"));

        //for new user:
//        assertEquals("OK" , Login.setInitSteps(json,httpRequests,token,"Daily"));
    }

    @Test
    public void getVerificationQuestion() {
        String q0 = "מה שם חיית המחמד הראשונה שלך?";
        String ans = Login.getVerificationQuestion("galb411@gmail.com",httpRequests);

        assertEquals(q0,ans);
    }

    @Test
    public void checkVerificationOfAnswerToUserQuestion() throws WrongAnswerException {
        long bday = Long.parseLong("679784400000");
        String answer_to_question = "שושה";
        String username = "galb411@gmail.com";
        String wrong_answer_to_question = "לילי";
        String wrong_username = "galb@gmail.com";

        assertTrue(Login.checkVerificationOfAnswerToUserQuestion(username,bday,answer_to_question,httpRequests));

        assertFalse(Login.checkVerificationOfAnswerToUserQuestion(wrong_username,bday,answer_to_question,httpRequests));
        assertFalse(Login.checkVerificationOfAnswerToUserQuestion(username,bday,wrong_answer_to_question,httpRequests));
    }

    @Test
    public void setNewPasswordForLoggedOutUser() throws InvalidTokenException, WrongAnswerException {
        long bday = Long.parseLong("679784400000");
        String answer_to_question = "שושה";
        String username = "galb411@gmail.com";

        //answer about verification question
        assertTrue(Login.checkVerificationOfAnswerToUserQuestion(username,bday,answer_to_question,httpRequests));

        String new_pass = "gg";
        //change password
        assertTrue(Login.setNewPasswordForLoggedOutUser(new_pass,httpRequests));

        //login with new pass
        assertTrue(Login.login("galb411@gmail.com",new_pass,appContext,httpRequests));
    }

    @Test
    public void askForChangePassword() {
        //ask permission to change password
        //existing user
        assertTrue(Login.askForChangePassword(httpRequests));
        //fake user
//        assertFalse(Login.askForChangePassword(httpRequests));
    }
}