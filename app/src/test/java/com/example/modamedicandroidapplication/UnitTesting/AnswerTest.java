package com.example.modamedicandroidapplication.UnitTesting;

import org.junit.Before;
import org.junit.Test;

import Model.Questionnaires.Answer;

import static org.junit.Assert.*;

public class AnswerTest {

    private Answer ans;

    @Before
    public void createQuestion() {
        ans = new Answer(1,"Barcelona");
    }

    @Test
    public void getAnswerID() {
        assertEquals(1,ans.getAnswerID());
    }

    @Test
    public void setAnswerID() {
        ans.setAnswerID(2);
        assertEquals(2,ans.getAnswerID());
    }

    @Test
    public void getAnswerText() {
        assertEquals("Barcelona",ans.getAnswerText());
    }

    @Test
    public void setAnswerText() {
        ans.setAnswerText("Real Madrid");
        assertEquals("Real Madrid",ans.getAnswerText());
    }

    @Test
    public void getAnswerValue() {
        assertEquals(1,ans.getAnswerValue());
    }

    @Test
    public void setAnswerValue() {
        ans.setAnswerValue(4);
        assertEquals(4,ans.getAnswerValue());
    }
}