package com.example.garage.functions;

import android.text.format.DateUtils;

import java.util.Date;

public class formatUtils {
    public static String formatCount(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        }

        String[] suffixes = new String[]{"", "K", "M", "B", "T", "Q"};
        int i = 0;

        double scaledCount = count;
        while (scaledCount >= 1000 && i < suffixes.length - 1) {
            scaledCount /= 1000;
            i++;
        }

        scaledCount = Math.floor(scaledCount * 10) / 10.0;

        String formatted;
        if (scaledCount % 1 == 0) {
            formatted = String.format("%d%s", (int) scaledCount, suffixes[i]);
        } else {
            formatted = String.format("%.1f%s", scaledCount, suffixes[i]);
        }

        return formatted;
    }

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
