package com.example.modamedicandroidapplication.UnitTesting;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import Model.Questionnaires.Answer;
import Model.Questionnaires.Question;
import Model.Questionnaires.Questionnaire;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class QuestionnaireTest {
    private Questionnaire questionnaire;

    @Before
    public void createQuestionnaire() {
        Question q1 = new Question();
        List<Question> questions = new ArrayList<>();
        q1.setQuestionID(1);
        q1.setQuestionText("מי הקבוצת כדורגל הטובה בעולם?");
        Answer ans1 = new Answer(1,"Barcelona");
        Answer ans2 = new Answer(2,"Real Madrid");
        Answer ans3 = new Answer(3,"Manchester United");
        List<Answer> answers = new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);
        answers.add(ans3);
        q1.setAnswers(answers);
        q1.setWorst("קבוצה על הפנים, בזבוז זמן מוחלט! לעשות ניקוי אורוות מהר!");
        q1.setBest("קבוצה מצויינת אחלה שחקנים!");
        questions.add(q1);
        questionnaire = new Questionnaire("2121",1,"שאלון טסט",questions,"football");
    }

    @Test
    public void getCategory() {
        assertEquals("football",questionnaire.getCategory());
    }

    @Test
    public void setCategory() {
        questionnaire.setCategory("basketball");
        assertEquals("basketball",questionnaire.getCategory());
    }

    @Test
    public void getMongoID() {
        assertEquals("2121",questionnaire.getMongoID());
    }

    @Test
    public void setMongoID() {
        questionnaire.setMongoID("1212");
        assertEquals("1212",questionnaire.getMongoID());
    }

    @Test
    public void getQuestionaireID() {
        assertEquals(1,questionnaire.getQuestionaireID());
    }

    @Test
    public void setQuestionaireID() {
        questionnaire.setQuestionaireID(2);
        assertEquals(2,questionnaire.getQuestionaireID());
    }

    @Test
    public void getTitle() {
        assertEquals("שאלון טסט",questionnaire.getTitle());
    }

    @Test
    public void setTitle() {
        questionnaire.setTitle("בדיקה");
        assertEquals("בדיקה",questionnaire.getTitle());
    }

    @Test
    public void getQuestions() {
        assertEquals(1,questionnaire.getQuestions().size());

    }

    @Test
    public void setQuestions() {
        List<Question> questions2 = new ArrayList<>();
        questionnaire.setQuestions(questions2);
        assertEquals(0,questionnaire.getQuestions().size());
    }

    @Test
    public void testEquals() {
        Question q1 = new Question();
        List<Question> questions = new ArrayList<>();
        q1.setQuestionID(1);
        q1.setQuestionText("מי הקבוצת כדורגל הטובה בעולם?");
        Answer ans1 = new Answer(1,"Barcelona");
        Answer ans2 = new Answer(2,"Real Madrid");
        Answer ans3 = new Answer(3,"Manchester United");
        List<Answer> answers = new ArrayList<>();
        answers.add(ans1);
        answers.add(ans2);
        answers.add(ans3);
        q1.setAnswers(answers);
        q1.setWorst("קבוצה על הפנים, בזבוז זמן מוחלט! לעשות ניקוי אורוות מהר!");
        q1.setBest("קבוצה מצויינת אחלה שחקנים!");
        questions.add(q1);
        Questionnaire questionnaire2 = new Questionnaire("2121",1,"שאלון טסט",questions,"football");
        assertTrue(questionnaire.equals(questionnaire2));
        Questionnaire questionnaire3 = new Questionnaire();
        assertFalse(questionnaire.equals(questionnaire3));

    }


    @Test
    public void testHashCode() {
    }
}