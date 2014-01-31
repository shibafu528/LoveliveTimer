package shibafu.lovelivetimer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by shibafu on 14/01/31.
 */
public class MainActivity extends Activity {

    private static final int REQUEST_RINGTONE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LpFragment fragment = new LpFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_timer:
            {
                TimeSupplier supplier =
                        (TimeSupplier) getFragmentManager().findFragmentById(android.R.id.content);

                Intent intent = new Intent(this, RequestReceiver.class);
                intent.putExtra(getString(R.string.pref_timer_lp), supplier.getRequestedLp());
                intent.putExtra(getString(R.string.pref_timer_time), supplier.getTimerTime());

                sendBroadcast(intent);
                return true;
            }
            case R.id.action_remove_timer:
                sendBroadcast(new Intent(this, RequestReceiver.class));
                return true;
            case R.id.action_change_timer_sound:
            {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String ringtone = sp.getString(getString(R.string.pref_timer_sound), null);

                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.action_pick_sound));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                if (ringtone == null) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                } else if (!ringtone.equals("")) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(ringtone));
                }

                startActivityForResult(intent, REQUEST_RINGTONE_PICKER);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RINGTONE_PICKER && resultCode == RESULT_OK) {
            Uri uri = (Uri) data.getExtras().get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (uri == null) {
                sp.edit().putString(getString(R.string.pref_timer_sound), "").commit();
            } else {
                sp.edit().putString(getString(R.string.pref_timer_sound), uri.toString()).commit();
            }
        }
    }
}
