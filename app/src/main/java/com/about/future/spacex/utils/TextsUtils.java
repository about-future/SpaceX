package com.about.future.spacex.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.about.future.spacex.R;

import java.text.DecimalFormat;

public class TextsUtils {

    public static String firstLetterUpperCase(String format) {
        return format.substring(0,1).toUpperCase() + format.substring(1);
    }

    public static String formatThrust(int thrust) {
        DecimalFormat formatter = new DecimalFormat("#,###,###,### ");
        return formatter.format(thrust);
    }

    public static String formatFuel(double fuel) {
        DecimalFormat formatter = new DecimalFormat("#,###,###.# ");
        return formatter.format(fuel);
    }

    public static void formatPayloadMass(Context context, TextView view, TextView pair, int kg, int lbs) {
        view.setText(String.format(
                context.getString(R.string.mass2),
                formatThrust(kg),
                formatThrust(lbs)));
        view.setVisibility(View.VISIBLE);
        pair.setVisibility(View.VISIBLE);
    }
}
