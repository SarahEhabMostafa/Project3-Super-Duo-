package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Sarah E. Mostafa on 31-Jan-16.
 */
public class TodayScoresWidgetProvider extends AppWidgetProvider {
    private static final String TAG = TodayScoresWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Intent i = new Intent(context, TodayScoresWidgetIntentService.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(i);
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context, TodayScoresWidgetIntentService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Intent intent = new Intent(context, TodayScoresWidgetIntentService.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(intent);
        Log.e(TAG, "onUpdate");
        context.startService(new Intent(context, TodayScoresWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
//        Intent intent = new Intent(context, TodayScoresWidgetIntentService.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startService(intent);
        Log.e(TAG, "onAppWidgetOptionsChanged");
        context.startService(new Intent(context, TodayScoresWidgetIntentService.class));
    }
}
