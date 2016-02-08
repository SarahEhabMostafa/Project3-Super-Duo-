package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.database.DatabaseContract;

/**
 * Created by Sarah E. Mostafa on 06-Feb-16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresWidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = ScoresWidgetProvider.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e(TAG, "onGetViewFactory");
        return new RemoteViewsFactory() {
            Cursor cursor = null;

            @Override
            public void onCreate() {
                Log.e(TAG, "onCreate");
            }

            @Override
            public void onDataSetChanged() {
                Log.e(TAG, "onDataSetChanged; cursor=" + cursor);

                if(cursor != null)
                    cursor.close();

                final long identityToken = Binder.clearCallingIdentity();
                Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
                Date dateObj = new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(dateObj);
                cursor = getContentResolver().query(uri, null, null, new String[]{date}, DatabaseContract.scores_table.DATE_COL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                Log.e(TAG, "onDestroy");
                if (cursor != null) {
                    Log.e(TAG, "onDestroy; cursor not null");
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                Log.e(TAG, "getCount");
                if (cursor == null) {
                    Log.e(TAG, "getCount; cursor is null");
                    return 0;
                }
                return cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                Log.e(TAG, "getViewAt");
                if(i == AdapterView.INVALID_POSITION || cursor == null
                        || !cursor.moveToPosition(i)) {
                    Log.e(TAG, "getViewAt; in if condition");
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.scores_list_item);
                int rowId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table._ID));
                String home = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
                String away = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
                String date = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL));
                int score_home = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
                int score_away = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));
                String score = Utilies.getScores(score_home, score_away);
                double match_id = cursor.getDouble(cursor.getColumnIndex(DatabaseContract.scores_table.MATCH_ID));
                int home_icon_resource = Utilies.getTeamCrestByTeamName(cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
                int away_icon_resource = Utilies.getTeamCrestByTeamName(cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    remoteViews.setContentDescription(R.id.home_crest, "DESCRIPTION");
                }

                remoteViews.setTextViewText(R.id.home_name, home);
                remoteViews.setTextViewText(R.id.away_name, away);
                remoteViews.setTextViewText(R.id.score_textview, score);
                remoteViews.setTextViewText(R.id.data_textview, date);
                remoteViews.setImageViewResource(R.id.home_crest, home_icon_resource);
                remoteViews.setImageViewResource(R.id.away_crest, away_icon_resource);

                final Intent fillIntent = new Intent();
                Uri uri = DatabaseContract.scores_table.buildScoreWithDate();
                fillIntent.setData(uri);
                remoteViews.setOnClickFillInIntent(R.id.layout_widget, fillIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                Log.e(TAG, "getLoadingView");
                return new RemoteViews(getPackageName(), R.layout.scores_list_item);
            }

            @Override
            public int getViewTypeCount() {
                Log.e(TAG, "getViewTypeCount");
                return 1;
            }

            @Override
            public long getItemId(int i) {
                Log.e(TAG, "getItemId");
                if(cursor.moveToPosition(i))
                    return cursor.getLong(cursor.getColumnIndex(DatabaseContract.scores_table._ID));
                return i;
            }

            @Override
            public boolean hasStableIds() {
                Log.e(TAG, "hasStableIds");
                return true;
            }
        };
    }
}
