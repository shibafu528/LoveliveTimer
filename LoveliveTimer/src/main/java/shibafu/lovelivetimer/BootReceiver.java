package shibafu.lovelivetimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by shibafu on 14/01/31.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ACTION_PACKAGE_REPLACEDを受信した場合はトリガーとなったパッケージが自分であるか確認
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String uri = intent.getDataString();
            if (!"package:shibafu.lovelivetimer".equals(uri))
                return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int lp = sp.getInt(context.getString(R.string.pref_timer_lp), 0);
        long time = sp.getLong(context.getString(R.string.pref_timer_time), 0);
        if (lp > 0) {
            Intent requestIntent = new Intent(context, RequestReceiver.class);
            requestIntent.putExtra(context.getString(R.string.pref_timer_lp), lp);
            requestIntent.putExtra(context.getString(R.string.pref_timer_time), time);

            context.sendBroadcast(requestIntent);
        }
    }
}
