package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import Model.Utils.Configurations;
import Model.Utils.HttpRequests;

import static org.junit.Assert.*;

public class ConfigurationsTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void getNotificationHour() {
        String daily = "daily";
        String stepsDest = "stepsDest";
        String stepsReminder = "stepsReminder";
        String none = "periodic";

        assertEquals(20, Configurations.getNotificationHour(appContext,daily));
        assertEquals(0, Configurations.getNotificationMinute(appContext,daily));

        assertEquals(20, Configurations.getNotificationHour(appContext,none));
        assertEquals(5, Configurations.getNotificationMinute(appContext,none));

        assertEquals(21, Configurations.getNotificationHour(appContext,stepsDest));
        assertEquals(0, Configurations.getNotificationMinute(appContext,stepsDest));

        assertEquals(20, Configurations.getNotificationHour(appContext,stepsReminder));
        assertEquals(30, Configurations.getNotificationMinute(appContext,stepsReminder));

        assertEquals(23, Configurations.getMetricsTaskHour(appContext));
        assertEquals(50, Configurations.getMetricsTaskMinute(appContext));
    }

}