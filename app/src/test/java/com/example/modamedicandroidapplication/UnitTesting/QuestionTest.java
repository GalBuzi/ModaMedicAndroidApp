package com.example.modamedicandroidapplication.UnitTesting;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Model.Questionnaires.Answer;
import Model.Questionnaires.Question;
import Model.Questionnaires.Questionnaire;
import Model.Users.User;

import static org.junit.Assert.*;

public class QuestionTest {
    private Question q;

    @Before
    public void createQuestion() {
        q = new Question();
    }

    @Test
    public void getAlone() {
        assertEquals(null,q.getAlone());
    }

    @Test
    public void setAlone() {
        List<Long> alone  = new ArrayList<>();
        alone.add(new Long(1));
        alone.add(new Long(2));
        q.setAlone(alone);
        assertEquals(2,q.getAlone().size());
    }

    @Test
    public void getType() {
        assertEquals(null,q.getType());
    }

    @Test
    public void getBest() {
        assertEquals(null,q.getBest());
    }

    @Test
    public void setBest() {
        q.setBest("קבוצה מצויינת אחלה שחקנים!");
        assertEquals("קבוצה מצויינת אחלה שחקנים!",q.getBest());
    }

    @Test
    public void getWorst() {
        assertEquals(null,q.getWorst());
    }

    @Test
    public void setWorst() {
        q.setWorst("קבוצה על הפנים, בזבוז זמן מוחלט! לעשות ניקוי אורוות מהר!");
        assertEquals("קבוצה על הפנים, בזבוז זמן מוחלט! לעשות ניקוי אורוות מהר!",q.getWorst());
    }

    @Test
    public void setType() {
        q.setType("eq5");
        assertEquals("EQ5",q.getType().name());
        q.setType("multi");
        assertEquals("MULTI",q.getType().name());
        q.setType("SINGLE");
        assertEquals("SINGLE",q.getType().name());
        q.setType("vas");
        assertEquals("VAS",q.getType().name());
        q.setType("check");
        assertEquals(null,q.getType());


    }

    @Test
    public void getQuestionID() {
        assertEquals(0,q.getQuestionID());

    }

    @Test
    public void setQuestionID() {
        q.setQuestionID(1);
        assertEquals(1,q.getQuestionID());
    }

    @Test
    public void getQuestionText() {
        assertEquals(null,q.getQuestionText());
    }

    @Test
    public void setQuestionText() {
        q.setQuestionText("מי הקבוצת כדורגל הטובה בעולם?");
        assertEquals("מי הקבוצת כדורגל הטובה בעולם?",q.getQuestionText());

    }

    @Test
    public void getAnswers() {
        assertEquals(null,q.getAnswers());
    }

    @Test
    public void setAnswers() {
        Answer ans1 = new Answer(1,"Barcelona");
        Answer ans2 = new Answer(2,"Real Madrid");
        Answer ans3 = new Answer(3,"Manchester United");
        List<Answer> answers = new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);
        answers.add(ans3);
        q.setAnswers(answers);
        assertEquals(3,q.getAnswers().size());
    }
}