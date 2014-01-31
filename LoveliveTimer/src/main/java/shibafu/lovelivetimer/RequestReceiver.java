package shibafu.lovelivetimer;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shibafu on 14/01/31.
 */
public class RequestReceiver extends BroadcastReceiver {
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    @Override
    public void onReceive(Context context, Intent intent) {
        int lp = intent.getIntExtra(context.getString(R.string.pref_timer_lp), 0);
        long time = intent.getLongExtra(
                context.getString(R.string.pref_timer_time), System.currentTimeMillis());
        if (lp > 0) {
            setTimer(context, time, lp);
            Toast.makeText(context,
                    context.getString(R.string.text_set_timer, sdf.format(new Date(time))),
                    Toast.LENGTH_LONG).show();
        } else {
            removeTimer(context);
            Toast.makeText(context, context.getString(R.string.text_remove_timer), Toast.LENGTH_LONG).show();
        }

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(0);
    }

    private void setTimer(Context context, long time, int lp) {
        //タイマー時刻と回復予定LPを書き込んでおく
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(context.getString(R.string.pref_timer_time), time);
        edit.putInt(context.getString(R.string.pref_timer_lp), lp);
        edit.commit();

        Intent intent = new Intent(context, TimerReceiver.class);
        intent.putExtra(context.getString(R.string.pref_timer_lp), lp);
        intent.putExtra(context.getString(R.string.pref_timer_time), time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, time, pendingIntent);
    }

    private void removeTimer(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, TimerReceiver.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
    }
}
