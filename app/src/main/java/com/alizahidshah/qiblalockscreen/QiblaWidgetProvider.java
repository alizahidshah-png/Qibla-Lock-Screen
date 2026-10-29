package com.alizahidshah.qiblalockscreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class QiblaWidgetProvider extends AppWidgetProvider {
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) updateWidget(context, manager, id);
    }

    static void updateWidget(Context context, AppWidgetManager manager, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.qibla_widget);
        Calendar now = Calendar.getInstance();
        views.setTextViewText(R.id.widget_date,
            new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(now.getTime()));
        views.setTextViewText(R.id.widget_hijri, fallbackHijri(now));
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pending);
        views.setOnClickPendingIntent(R.id.widget_compass_button, pending);
        manager.updateAppWidget(id, views);
    }

    private static int gregorianJD(Calendar c) {
        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH) + 1, d = c.get(Calendar.DAY_OF_MONTH);
        int a = (14 - m) / 12, yy = y + 4800 - a, mm = m + 12 * a - 3;
        return d + (153 * mm + 2) / 5 + 365 * yy + yy / 4 - yy / 100 + yy / 400 - 32045;
    }
    private static int islamicJD(int y, int m, int d) {
        return d + (int)Math.ceil(29.5 * (m - 1)) + (y - 1) * 354 + (3 + 11 * y) / 30 + 1948439;
    }
    private static String fallbackHijri(Calendar c) {
        int jd = gregorianJD(c);
        int y = (30 * (jd - 1948439) + 10646) / 10631;
        int m = Math.min(12, (int)Math.ceil((jd - (29 + islamicJD(y, 1, 1))) / 29.5) + 1);
        m = Math.max(1, m);
        int d = jd - islamicJD(y, m, 1) + 1;
        String[] months = {"Muharram","Safar","Rabi al-Awwal","Rabi al-Thani","Jumada al-Awwal","Jumada al-Thani","Rajab","Sha’ban","Ramadan","Shawwal","Dhu al-Qi’dah","Dhu al-Hijjah"};
        return d + " " + months[m - 1] + " " + y + " AH";
    }
}