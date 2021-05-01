package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import Model.Utils.HttpRequests;

import static org.junit.Assert.*;

public class ConfigurationsTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void getNotificationHour() {
    }

    @Test
    public void getNotificationMinute() {
    }

    @Test
    public void getMetricsTaskMinute() {
    }

    @Test
    public void getMetricsTaskHour() {
    }
}