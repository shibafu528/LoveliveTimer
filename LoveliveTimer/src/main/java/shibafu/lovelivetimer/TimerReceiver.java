package shibafu.lovelivetimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;

public class TimerReceiver extends BroadcastReceiver {

    public static final String LL_PACKAGE = "klb.android.lovelive";
    public static final String LL_ACTIVITY = "klb.android.GameEngine.GameEngineActivity";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(context.getString(R.string.text_notify));
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.text_notify));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_LIGHTS);

        String ringtone = sp.getString(context.getString(R.string.pref_timer_sound), null);
        if (ringtone == null) {
            builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        } else if (!ringtone.equals("")) {
            builder.setSound(Uri.parse(ringtone));
        }

        builder.setContentIntent(PendingIntent.getActivity(
                context, 0, new Intent(context, MainActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK));

        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(LL_PACKAGE, PackageManager.GET_META_DATA);

            //例外が出ていなければアプリがインストールされているとする

            //ゲーム起動アクションを追加
            Intent launchGameIntent = new Intent();
            launchGameIntent.setClassName(LL_PACKAGE, LL_ACTIVITY);
            launchGameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            builder.addAction(R.drawable.ic_stat_play,
                    context.getString(R.string.action_launch_game),
                    PendingIntent.getActivity(context, 0, launchGameIntent, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //同じLPでタイマーを再設定するアクションを追加
        int lp = intent.getIntExtra(context.getString(R.string.pref_timer_lp), 0);
        long time = System.currentTimeMillis() + lp * 360000;
        Intent restartTimerIntent = new Intent(context, RequestReceiver.class);
        restartTimerIntent.putExtra(context.getString(R.string.pref_timer_lp), lp);
        restartTimerIntent.putExtra(context.getString(R.string.pref_timer_time), time);
        builder.addAction(R.drawable.ic_stat_replay,
                context.getString(R.string.action_snooze_timer, lp),
                PendingIntent.getBroadcast(context, 0, restartTimerIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        nm.notify(0, builder.build());

        sp.edit().remove(context.getString(R.string.pref_timer_lp)).commit();
    }
}
