package com.example.garage.functions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class fragmentUtils {
    public static void activityReplaceFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment).commit();
    }
}
