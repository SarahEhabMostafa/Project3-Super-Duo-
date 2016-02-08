package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by Sarah E. Mostafa on 06-Feb-16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetProvider extends AppWidgetProvider {
    private final static String TAG = ScoresWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.e(TAG, "onRecieve");
//        context.startService(new Intent(context, ScoresWidgetRemoteViewsService.class));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.scores_list);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.e(TAG, "onUpdate");
//        context.startService(new Intent(context, ScoresWidgetRemoteViewsService.class));

        for (int appWidgetId: appWidgetIds) {
            Log.e(TAG, appWidgetId + "");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_scores_list);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.layout_row, pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                remoteViews.setRemoteAdapter(R.id.scores_list,
                        new Intent(context, ScoresWidgetRemoteViewsService.class));
            } else {
                remoteViews.setRemoteAdapter(0, R.id.scores_list,
                        new Intent(context, ScoresWidgetRemoteViewsService.class));
            }

            Intent clickIntent = new Intent(context, MainActivity.class);

            PendingIntent clickPendingIntent = android.support.v4.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.scores_list, clickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
