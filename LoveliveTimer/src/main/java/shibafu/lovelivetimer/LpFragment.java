package shibafu.lovelivetimer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shibafu on 14/01/31.
 */
public class LpFragment extends Fragment implements TimeSupplier{

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private TextView textView;
    private NumberPicker numberPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_main, container, false);

        int recentLp =
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getInt(getString(R.string.pref_recent_lp), 1);

        textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(sdf.format(new Date(getRecoverdTimeFromLp(recentLp))));

        numberPicker = (NumberPicker) v.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(256);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                textView.setText(sdf.format(new Date(getRecoverdTimeFromLp(newVal))));
            }
        });
        numberPicker.setValue(recentLp);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.edit().putInt(getString(R.string.pref_recent_lp), numberPicker.getValue()).commit();
    }

    private long getRecoverdTimeFromLp(int lp) {
        return System.currentTimeMillis() + lp * 360000;
    }

    @Override
    public long getTimerTime() {
        return getRecoverdTimeFromLp(numberPicker.getValue());
    }

    @Override
    public int getRequestedLp() {
        return numberPicker.getValue();
    }
}
