package com.example.modamedicandroidapplication;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import Model.Users.Login;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.modamedicandroidapplication", appContext.getPackageName());
    }

    @Test
    public void checkToken() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String token = Login.getToken(appContext);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySUQiOiJXeVNzejNSS3g4ZFcxdnloRDIzRlUydkQwRUpxbHAwMmg4REJURVZZY2pNPSIsIlR5cGUiOlsicGF0aWVudCJdLCJpYXQiOjE2MTk3MTQzNzEsImV4cCI6MTY1MTI1MDM3MX0.c6FFoehuqtsnB7MFtxFwH-b3pXNCDa4Q_55t8N2h3pU", token );

    }
}
