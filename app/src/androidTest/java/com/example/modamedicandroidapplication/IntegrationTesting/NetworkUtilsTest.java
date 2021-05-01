package com.example.modamedicandroidapplication.IntegrationTesting;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import Model.Utils.HttpRequests;
import Model.Utils.NetworkUtils;

import static org.junit.Assert.*;

public class NetworkUtilsTest {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private HttpRequests httpRequests = HttpRequests.getInstance(appContext);

    @Test
    public void hasInternetConnection() {
        boolean ans = NetworkUtils.hasInternetConnection(appContext);

        assertTrue(ans);
    }
}