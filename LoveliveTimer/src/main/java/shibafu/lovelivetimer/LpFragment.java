package shibafu.lovelivetimer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button saveButton1, saveButton2, saveButton3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_lp, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int recentLp = sp.getInt(getString(R.string.pref_recent_lp), 1);

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

        saveButton1 = (Button) v.findViewById(R.id.button1);
        saveButton1.setText(getString(R.string.text_lp, sp.getInt(getString(R.string.pref_saved_lp_1), 1)));
        saveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreLp(1);
            }
        });
        saveButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveLp(1, numberPicker.getValue());
                return true;
            }
        });

        saveButton2 = (Button) v.findViewById(R.id.button2);
        saveButton2.setText(getString(R.string.text_lp, sp.getInt(getString(R.string.pref_saved_lp_2), 1)));
        saveButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreLp(2);
            }
        });
        saveButton2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveLp(2, numberPicker.getValue());
                return true;
            }
        });

        saveButton3 = (Button) v.findViewById(R.id.button3);
        saveButton3.setText(getString(R.string.text_lp, sp.getInt(getString(R.string.pref_saved_lp_3), 1)));
        saveButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreLp(3);
            }
        });
        saveButton3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveLp(3, numberPicker.getValue());
                return true;
            }
        });

        return v;
    }

    private void saveLp(int i, int value) {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        switch (i) {
            case 1:
                e.putInt(getString(R.string.pref_saved_lp_1), value);
                saveButton1.setText(getString(R.string.text_lp, value));
                break;
            case 2:
                e.putInt(getString(R.string.pref_saved_lp_2), value);
                saveButton2.setText(getString(R.string.text_lp, value));
                break;
            case 3:
                e.putInt(getString(R.string.pref_saved_lp_3), value);
                saveButton3.setText(getString(R.string.text_lp, value));
                break;
        }
        e.commit();
    }

    private void restoreLp(int i) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        switch (i) {
            case 1:
                numberPicker.setValue(sp.getInt(getString(R.string.pref_saved_lp_1), 1));
                break;
            case 2:
                numberPicker.setValue(sp.getInt(getString(R.string.pref_saved_lp_2), 1));
                break;
            case 3:
                numberPicker.setValue(sp.getInt(getString(R.string.pref_saved_lp_3), 1));
                break;
        }
        textView.setText(sdf.format(new Date(getRecoverdTimeFromLp(numberPicker.getValue()))));
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
