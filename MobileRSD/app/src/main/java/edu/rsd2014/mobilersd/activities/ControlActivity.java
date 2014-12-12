package edu.rsd2014.mobilersd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.ros.address.InetAddressFactory;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.android.view.VirtualJoystickView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import edu.rsd2014.mobilersd.R;
import edu.rsd2014.mobilersd.nodes.Listener;
import edu.rsd2014.mobilersd.nodes.Talker;
import edu.rsd2014.mobilersd.views.VirtualJoystickView_Stamped;
import std_msgs.*;

/**
 * Created by darkriddle on 12/12/14.
 */
public class ControlActivity extends RosActivity {

    //private VirtualJoystickView virtualJoystickView;
    private VirtualJoystickView_Stamped virtualJoystickView_stamped;
    //private RosTextView rosTextView;
    //private RosTextView rosTextView_cmdvel;
    private Talker talker;
    private Listener listener;

    public ControlActivity() {
        super("MobileRSD", "MobileRSD");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.control_nomap);

        virtualJoystickView_stamped = (VirtualJoystickView_Stamped) findViewById(R.id.virtual_joystick);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        talker = new Talker();
        listener = new Listener();
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(talker, nodeConfiguration);
        nodeMainExecutor.execute(listener, nodeConfiguration);
        nodeMainExecutor.execute(virtualJoystickView_stamped, nodeConfiguration.setNodeName("fmCommand"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.virtual_joystick_snap:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    virtualJoystickView_stamped.EnableSnapping();
                } else {
                    item.setChecked(false);
                    virtualJoystickView_stamped.DisableSnapping();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
