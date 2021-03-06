/*
 * Copyright (c) 2016 Elektrotehnicki fakultet
 *  Patre 5, Banja Luka
 *  <p/>
 *  All Rights Reserved
 *  <p/>
 *   \file ForecastService.java
 *   \brief
 *   This file contains a source code for class ForecastService
 *   <p/>
 *   Created on 28.05.2016
 *
 *   @Author Milan Maric
 *   <p/>
 *   \notes
 *   <p/>
 *   <p/>
 *   \history
 *   <p/>
 */

package net.etfbl.prs.prs1122_12_z3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;


public class ForecastService extends Service {
    public static final String BROADCAST_ACTION = "net.etfbl.prs.prs1122_12_z3.BROADCAST";
    public static final String NOTIFICATION_ACTION = "net.etfbl.prs.prs1122_12_z3.NOTIFICATION";
    public static final String BROADCAST_EXTRA = "net.etfbl.prs.prs1122_12_z3.RESULT";
    private static final String ACTION_GET_DATA = "net.etfbl.prs.prs1122_12_z3.action.GET_DATA";


    public ForecastService() {
        super();
    }

    /**
     * This method is used to make an intent for starting service.
     *
     * @param context context for starting intent
     */
    public static void startActionGetData(Context context) {
        Intent intent = new Intent(context, ForecastService.class);
        intent.setAction(ACTION_GET_DATA);
        context.startService(intent);
    }

    /**
     * This method returns string from shared preferences, that represents a name of the city.
     *
     * @param context context
     * @return String name of the city
     */
    public static String getCity(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("pref_city_name", context.getString(R.string.default_city));
    }

    /**
     * This method returns int from shared preferences, that represents a frequency of data sync.
     *
     * @param context context
     * @return int value of shared preference
     */
    public static int getFrequency(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String freq = prefs.getString("sync_frequency", "60");
        return Integer.parseInt(freq);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_STICKY;
    }

    /**
     * This method is used to start HTTP client and fetch informations from server
     *
     * @param intent calling intent
     */
    protected void handleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            ForecastHttpClient forecastHttpClient = new ForecastHttpClient(getBaseContext());
            if (ACTION_GET_DATA.equals(action)) {
                String place = getCity(this);
                forecastHttpClient.setPlace(place);
                forecastHttpClient.start();
                try {
                    forecastHttpClient.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf();
            }
        }
    }


}
