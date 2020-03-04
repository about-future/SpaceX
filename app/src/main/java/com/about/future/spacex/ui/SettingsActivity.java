package com.about.future.spacex.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.about.future.spacex.R;
import com.about.future.spacex.utils.Constants;
import com.about.future.spacex.utils.SpaceXPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.notifications_layout)
    ConstraintLayout mNotificationsLayout;
    @BindView(R.id.notifications_label)
    TextView mNotificationsLabelTextView;
    @BindView(R.id.notifications_hint)
    TextView mNotificationsHintTextView;
    @BindView(R.id.settings_notifications_switch)
    Switch mNotificationSwitch;

    @BindView(R.id.acronyms_layout)
    ConstraintLayout mAcronymsLayout;
    @BindView(R.id.acronyms_label)
    TextView mAcronymsLabelTextView;
    @BindView(R.id.acronyms_hint)
    TextView mAcronymsHintTextView;
    @BindView(R.id.settings_acronyms_switch)
    Switch mAcronymsSwitch;

    @BindView(R.id.settings_units_cardview)
    CardView mUnitsCardView;
    @BindView(R.id.units_layout)
    ConstraintLayout mUnitsLayout;
    @BindView(R.id.units_label)
    TextView mUnitsLabelTextView;
    @BindView(R.id.units_hint)
    TextView mUnitsHintTextView;

    private Unbinder mUnbinder;

    private String[] mUnits;
    private int mSelectedUnit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind the views
        mUnbinder = ButterKnife.bind(this);

        // Notifications
        mNotificationSwitch.setChecked(SpaceXPreferences.getTopicSubscriptionStatus(this));
        mNotificationsLayout.setOnClickListener(v -> {
            mNotificationSwitch.setChecked(!mNotificationSwitch.isChecked());
            // Set the new notifications preference
            SpaceXPreferences.setTopicSubscriptionStatus(this, mNotificationSwitch.isChecked());
        });

        // Acronyms
        mAcronymsSwitch.setChecked(SpaceXPreferences.getAcronymsStatus(this));
        setAcronymsHint();
        mAcronymsLayout.setOnClickListener(v -> {
            mAcronymsSwitch.setChecked(!mAcronymsSwitch.isChecked());
            setAcronymsHint();

            // Set the new acronym preference
            SpaceXPreferences.setAcronymsStatus(this, mAcronymsSwitch.isChecked());
        });

        // Units
        mUnits = getResources().getStringArray(R.array.units);
        // Get unit preference and set it to mUnitsHintTextView
        if (SpaceXPreferences.getUnits(this).equals(Constants.UNIT_IMPERIAL)) {
            mUnitsHintTextView.setText(mUnits[1]);
            mSelectedUnit = 1;
        } else {
            mUnitsHintTextView.setText(mUnits[0]);
            mSelectedUnit = 0;
        }
        mUnitsCardView.setOnClickListener(v -> showUnitsDialog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void showUnitsDialog() {
        if (SpaceXPreferences.getUnits(this).equals(Constants.UNIT_IMPERIAL)) {
            mSelectedUnit = 1;
        } else {
            mSelectedUnit = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(R.array.units, mSelectedUnit, (dialog, which) -> {
            Toast.makeText(this, mUnits[which], Toast.LENGTH_SHORT).show();
            mUnitsHintTextView.setText(mUnits[which]);

            if (which == 0) {
                SpaceXPreferences.setUnits(this, Constants.UNIT_METRIC);
            } else {
                SpaceXPreferences.setUnits(this, Constants.UNIT_IMPERIAL);
            }

            // User selected an option, so dismiss the language dialog.
            if (dialog != null) {
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.dialog_cancel, (dialog, id) -> {
            // User clicked the "Cancel" button, so dismiss the language dialog.
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setAcronymsHint() {
        if (mAcronymsSwitch.isChecked()) {
            mAcronymsHintTextView.setText(Constants.ACRONYMS_ON);
        } else {
            mAcronymsHintTextView.setText(Constants.ACRONYMS_OFF);
        }
    }


}
