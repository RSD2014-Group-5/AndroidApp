package edu.rsd2014.mobilersd.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.rsd2014.mobilersd.R;

/**
 * Created by darkriddle on 12/12/14.
 */
public class OEEActivity extends NavActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.oee);

        Typeface custom_font = Typeface.createFromAsset(this.getAssets(),"fonts/EHSMB.ttf");

        ViewGroup oee_table = (ViewGroup)this.findViewById(R.id.my_oee_table);
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
    }
}
