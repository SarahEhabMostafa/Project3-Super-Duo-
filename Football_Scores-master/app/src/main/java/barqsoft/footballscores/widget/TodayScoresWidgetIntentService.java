package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.Constants;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.database.DatabaseContract.scores_table;

/**
 * Created by Sarah E. Mostafa on 31-Jan-16.
 */
public class TodayScoresWidgetIntentService extends IntentService {
    private static final String TAG = TodayScoresWidgetProvider.class.getSimpleName();

    public TodayScoresWidgetIntentService() {
        super(TodayScoresWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Log.e(TAG, "onHandleIntent");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds
                (new ComponentName(this, TodayScoresWidgetProvider.class));

        Date dateObj = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

        Cursor data = getContentResolver().query(scores_table.buildScoreWithDate(),
                null, null, new String[] {simpleDateFormat.format(dateObj)}, scores_table.TIME_COL + " ASC");

//        Log.e(TAG, "data=" + data);

        if (data == null) {
            return;
        }

//        Log.e(TAG, "data size=" + data.getCount());

        if (!data.moveToFirst()) {
//            Log.e(TAG, "!data.moveToFirst()");
            data.close();
            return;
        }

        String home = data.getString(data.getColumnIndex(scores_table.HOME_COL));
        String away = data.getString(data.getColumnIndex(scores_table.AWAY_COL));
        int home_goals = data.getInt(data.getColumnIndex(scores_table.HOME_GOALS_COL));
        int away_goals = data.getInt(data.getColumnIndex(scores_table.AWAY_GOALS_COL));
        String score = Utilies.getScores(home_goals, away_goals);
        String date = /*data.getString(data.getColumnIndex(scores_table.DATE_COL))
                + " " + */data.getString(data.getColumnIndex(scores_table.TIME_COL));
        data.close();

//        Log.e(TAG, "Home=" + home + ", Away=" + away);


        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_scores;
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), layoutId);

            remoteViews.setTextViewText(R.id.home_name, home);
            remoteViews.setTextViewText(R.id.away_name, away);
            remoteViews.setTextViewText(R.id.score_textview, score);
            remoteViews.setTextViewText(R.id.data_textview, date);

            //Create an intent to start the application
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.layout_widget, pendingIntent);

            //Tell the app to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
