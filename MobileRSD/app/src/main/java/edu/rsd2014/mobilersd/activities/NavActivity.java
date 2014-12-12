package edu.rsd2014.mobilersd.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * Created by darkriddle on 12/12/14.
 */
public class NavActivity extends Activity {

    public void OverviewClicked(View view) {
        Intent openOverviewActivity = new Intent(view.getContext(),OverviewActivity.class);
        openOverviewActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openOverviewActivity);

        /*Intent act = new Intent(view.getContext(),OverviewActivity.class);
        startActivity(act);*/
    }

    public void ControlClicked(View view) {
        //Intent act = new Intent(view.getContext(),ControlActivity.class);
        //startActivity(act);

        Intent openControlActivity= new Intent(view.getContext(),ControlActivity.class);
        openControlActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openControlActivity);
    }

    public void OEEClicked(View view) {
        Intent openOEEActivity = new Intent(view.getContext(),OEEActivity.class);
        openOEEActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openOEEActivity);

        /*Intent act = new Intent(view.getContext(),OEEActivity.class);
        startActivity(act);*/
    }

    public void HelpClicked(View view) {
        Intent openHelpActivity = new Intent(view.getContext(),HelpActivity.class);
        openHelpActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(openHelpActivity);

        /*Intent act = new Intent(view.getContext(),HelpActivity.class);
        startActivity(act);*/
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

}
