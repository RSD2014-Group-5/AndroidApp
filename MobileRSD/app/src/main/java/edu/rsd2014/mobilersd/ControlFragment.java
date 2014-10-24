package edu.rsd2014.mobilersd;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by darkriddle on 10/22/14.
 */
public class ControlFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.control, container,
                false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(R.string.section_control);
        Bundle nodeBundle = new Bundle();
        nodeBundle.putInt("titleId",R.string.section_control);
        ((MainActivity) activity).onSectionAttached(nodeBundle);
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}

