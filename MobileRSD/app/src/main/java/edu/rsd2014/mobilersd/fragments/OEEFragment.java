package edu.rsd2014.mobilersd.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.rsd2014.mobilersd.MainActivity;
import edu.rsd2014.mobilersd.R;

/**
 * Created by darkriddle on 10/23/14.
 */
public class OEEFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.oee, container,false);

        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/EHSMB.ttf");

        ViewGroup oee_table = (ViewGroup)rootView.findViewById(R.id.my_oee_table);
        for(int row_id=0; row_id<oee_table.getChildCount();row_id++) {
            ViewGroup row_group = (ViewGroup)oee_table.getChildAt(row_id);
            for(int v_id=0; v_id<row_group.getChildCount();v_id++) {
                View v = row_group.getChildAt(v_id);
                if (v instanceof TextView) {
                    TextView tv = (TextView)v;
                    tv.setTypeface(custom_font);
                }
            }
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(R.string.section_oee);
        Bundle nodeBundle = new Bundle();
        nodeBundle.putInt("titleId",R.string.section_oee);
        ((MainActivity) activity).onSectionAttached(nodeBundle);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}