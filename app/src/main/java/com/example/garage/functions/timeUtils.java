package com.example.garage.functions;

import android.text.format.DateUtils;

import java.util.Date;

public class timeUtils {
    public static String getTimeAgo(Date date) {
        if (date == null) return "";

        return DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_TIME
        ).toString();
    }
}
