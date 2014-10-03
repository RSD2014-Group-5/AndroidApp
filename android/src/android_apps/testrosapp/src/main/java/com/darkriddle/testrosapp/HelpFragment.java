package com.darkriddle.testrosapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkriddle.testrosapp.views.JoystickView;

/**
 * Created by darkriddle on 9/30/14.
 */
public class HelpFragment extends Fragment {

    private JoystickView mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.help, container,false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(R.string.section_help);
    }
}
