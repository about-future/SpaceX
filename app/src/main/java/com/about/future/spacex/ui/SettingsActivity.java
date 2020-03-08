package com.about.future.spacex.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.about.future.spacex.R;
import com.about.future.spacex.databinding.ActivitySettingsBinding;
import com.about.future.spacex.utils.Constants;
import com.about.future.spacex.utils.SpaceXPreferences;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private String[] mUnits;
    private int mSelectedUnit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Notifications
        binding.settingsNotificationsSwitch.setChecked(SpaceXPreferences.getTopicSubscriptionStatus(this));
        binding.notificationsLayout.setOnClickListener(v -> {
            binding.settingsNotificationsSwitch.setChecked(!binding.settingsNotificationsSwitch.isChecked());
            // Set the new notifications preference
            SpaceXPreferences.setTopicSubscriptionStatus(this, binding.settingsNotificationsSwitch.isChecked());
        });

        // Acronyms
        binding.settingsAcronymsSwitch.setChecked(SpaceXPreferences.getAcronymsStatus(this));
        setAcronymsHint();
        binding.acronymsLayout.setOnClickListener(v -> {
            binding.settingsAcronymsSwitch.setChecked(!binding.settingsAcronymsSwitch.isChecked());
            setAcronymsHint();

            // Set the new acronym preference
            SpaceXPreferences.setAcronymsStatus(this, binding.settingsAcronymsSwitch.isChecked());
        });

        // Units
        mUnits = getResources().getStringArray(R.array.units);
        // Get unit preference and set it to mUnitsHintTextView
        if (SpaceXPreferences.getUnits(this).equals(Constants.UNIT_IMPERIAL)) {
            binding.unitsHint.setText(mUnits[1]);
            mSelectedUnit = 1;
        } else {
            binding.unitsHint.setText(mUnits[0]);
            mSelectedUnit = 0;
        }
        binding.settingsUnitsCardview.setOnClickListener(v -> showUnitsDialog());
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

    private void showUnitsDialog() {
        if (SpaceXPreferences.getUnits(this).equals(Constants.UNIT_IMPERIAL)) {
            mSelectedUnit = 1;
        } else {
            mSelectedUnit = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(R.array.units, mSelectedUnit, (dialog, which) -> {
            Toast.makeText(this, mUnits[which], Toast.LENGTH_SHORT).show();
            binding.unitsHint.setText(mUnits[which]);

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
        if (binding.settingsAcronymsSwitch.isChecked()) {
            binding.acronymsHint.setText(Constants.ACRONYMS_ON);
        } else {
            binding.acronymsHint.setText(Constants.ACRONYMS_OFF);
        }
    }


}
