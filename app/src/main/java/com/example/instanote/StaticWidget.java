package com.example.instanote;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class StaticWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetTitle = context.getString(R.string.appwidget_text);
        CharSequence widgetText = "A data structure is a particular way of organizing data in a computer so that it can be used effectively.A data structure is a particular way of organizing data in a computer so that it can be used effectively.A data structure is a particular way of organizing data in a computer so that it can be used effectively.";
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.static_widget);
        views.setTextViewText(R.id.note_title, widgetTitle);
        views.setTextViewText(R.id.note_content, widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

