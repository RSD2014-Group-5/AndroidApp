package edu.rsd2014.mobilersd;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import edu.rsd2014.mobilersd.fragments.ControlFragment;
import edu.rsd2014.mobilersd.fragments.HelpFragment;
import edu.rsd2014.mobilersd.fragments.NavigationDrawerFragment;
import edu.rsd2014.mobilersd.fragments.OEEFragment;
import edu.rsd2014.mobilersd.fragments.OverviewFragment;

public class MainActivity extends RosActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
//public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public MainActivity() {
        super("MobileRSD", "MobileRSD");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = ((Activity)this).getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.container, new OverviewFragment()).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, new ControlFragment()).commit();
                break;
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, new OEEFragment()).commit();
                break;
            case 3:
                fragmentManager.beginTransaction().replace(R.id.container, new HelpFragment()).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
                break;
        }
    }

    /*public void onSectionAttached(int resource_id) {
        mTitle = getString(resource_id);
    }*/
    public void onSectionAttached(Bundle nodeBundle) {
        //mTitle = getString(resource_id);
        int titleId = nodeBundle.getInt("titleId");
        mTitle = getString(titleId);

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
            NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
            nodeConfiguration.setMasterUri(getMasterUri());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            //((MainActivity) activity).onSectionAttached(R.string.app_name);
            Bundle nodeBundle = new Bundle();
            nodeBundle.putInt("titleId",R.string.app_name);
            ((MainActivity) activity).onSectionAttached(nodeBundle);
        }
    }

}
