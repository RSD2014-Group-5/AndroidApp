package edu.rsd2014.mobilersd.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rsd2014.mobilersd.R;
import edu.rsd2014.mobilersd.util.Constants;

/**
 * Created by darkriddle on 12/12/14.
 */
public class OverviewActivity extends NavActivity {

    private static final Integer MaxListItems = 10;

    TextView tvBattery;
    TextView tvXcoord;
    TextView tvYcoord;
    TextView tvLocation;
    TextView tvTilt;
    TextView tvSpeed;
    ListView lvTasks;

    ArrayList<String> lstTaskItems = new ArrayList<String>();
    ArrayAdapter<String> taskItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.overview);

        tvBattery   = (TextView)findViewById(R.id.txtBattery);
        tvXcoord    = (TextView)findViewById(R.id.txtXcoord);
        tvYcoord    = (TextView)findViewById(R.id.txtYcoord);
        tvLocation  = (TextView)findViewById(R.id.txtLocation);
        tvTilt      = (TextView)findViewById(R.id.txtTilt);
        tvSpeed     = (TextView)findViewById(R.id.txtSpeed);

        lvTasks     = (ListView)findViewById(R.id.lstTasks);
        taskItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstTaskItems);
        lvTasks.setAdapter(taskItemsAdapter);

        /*Button btnTest = (Button)findViewById(R.id.button);
        btnTest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskItem("Testing");
            }
        });*/

        new AsyncXMLRequest().execute();
    }

    private void addTaskItem(String task) {
        lstTaskItems.add(task);
        if (lstTaskItems.size()>=MaxListItems){
            int diff = lstTaskItems.size()-MaxListItems;
            for(int i=0;i<diff;i++) {
                lstTaskItems.remove(i);
            }
        }
        taskItemsAdapter.notifyDataSetChanged();
    }

    private class AsyncXMLRequest extends AsyncTask<String,String,HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(String... strings) {
            HashMap<String,String> result = new HashMap<String, String>();
            XmlRpcClientConfigImpl rpcCfg = new XmlRpcClientConfigImpl();
            XmlRpcClient rpcClient = new XmlRpcClient();

            try {
                rpcCfg.setServerURL(new URL(Constants.RPC_URL));
                Object[] params = new Object[]{};
                rpcClient.setConfig(rpcCfg);
                result = (HashMap<String,String>)rpcClient.execute("get_overview_data",params);
            } catch (MalformedURLException e) {
                Log.e("OverviewActivity","MalformedURLException: " + e.getMessage());
            } catch (XmlRpcException e) {
                Log.e("OverviewActivity","XmlRpcException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("OverviewActivity","Exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> data) {
            tvBattery.setText(data.get("battery")+"%");
            tvXcoord.setText(data.get("x-coordinate"));
            tvYcoord.setText(data.get("y-coordinate"));
            tvLocation.setText(data.get("location"));
            tvTilt.setText(data.get("tilt")+"%");
            tvSpeed.setText(data.get("speed")+"m/s");

            addTaskItem(data.get("running_task"));
//            lvTasks.addView();
        }
    }

}
