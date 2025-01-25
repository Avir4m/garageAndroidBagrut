package com.example.garage.functions;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class fragmentManager {
    public static void activityReplaceFragment(FragmentActivity activity, Fragment fragment, int containerId) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment).commit();
    }

    public static void fragmentLoadFragment(Fragment fragment, Fragment selectedFragment, int containerId) {
        fragment.getChildFragmentManager().beginTransaction().replace(containerId, selectedFragment).commit();
    }
}
