package com.github.rosjava.android_apps.rsdapp.frobit;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabWidget;

import org.ros.android.view.VirtualJoystickView;
import org.ros.android.view.RosImageView;
import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.address.InetAddressFactory;
import org.ros.android.BitmapFromCompressedImage;

import org.ros.android.RosActivity;
import org.ros.node.ConnectedNode;

// RosJava Messages
import std_msgs.String;

// Android Core Imports
import org.ros.android.MessageCallable;
import org.ros.android.view.RosTextView;


public class MainActivity extends RosActivity {

    private VirtualJoystickView virtualJoystickView;
    private VirtualJoystickView_Stamped virtualJoystickView_stamped;
    private RosTextView rosTextView;
    private RosTextView rosTextView_cmdvel;
    private Talker talker;
    private Listener listener;

    public MainActivity() { super("Frobit app", "Frobit App");}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.rostext);
        rosTextView.setTopicName("frobitapp/listener");

        rosTextView.setMessageType(std_msgs.String._TYPE);
        rosTextView.setMessageToStringCallable(new MessageCallable<java.lang.String, std_msgs.String>() {
            @Override
            public java.lang.String call(std_msgs.String message) {
                return message.getData();
            }
        });

        rosTextView_cmdvel = (RosTextView<std_msgs.String>) findViewById(R.id.rostext_cmdvel);
        rosTextView_cmdvel.setTopicName("fmCommand/cmd_vel");
        rosTextView_cmdvel.setMessageType(geometry_msgs.TwistStamped._TYPE);
        rosTextView_cmdvel.setMessageToStringCallable(new MessageCallable<java.lang.String, geometry_msgs.TwistStamped>() {
            //Math.floor(valueToCheck * 100) / 100;
            @Override
            public java.lang.String call(geometry_msgs.TwistStamped message) {
                java.lang.String z_str ="", x_str = "";
                Double z_val, x_val;

                z_val = message.getTwist().getAngular().getZ();
                x_val = message.getTwist().getLinear().getX();


               // return "X: " +"\n" + x_str + "\n"+ "Z: " + "\n" + z_str + "\n";
                return "Linear: " +"\n" + java.lang.String.format("%.10f",x_val) + "\n"+ "Angular: " + "\n" + java.lang.String.format("%.10f",z_val);

            }
        });

       // virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
        virtualJoystickView_stamped = (VirtualJoystickView_Stamped) findViewById(R.id.virtual_joystick);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        talker = new Talker();
        listener = new Listener();
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(talker, nodeConfiguration);
        nodeMainExecutor.execute(listener, nodeConfiguration);
        //nodeMainExecutor.execute(rosTextView, nodeConfiguration);
        nodeMainExecutor.execute(rosTextView_cmdvel, nodeConfiguration);
//        nodeMainExecutor.execute(virtualJoystickView, nodeConfiguration.setNodeName("virtual_joystick"));
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
                    virtualJoystickView.EnableSnapping();
                } else {
                    item.setChecked(false);
                    virtualJoystickView.DisableSnapping();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
