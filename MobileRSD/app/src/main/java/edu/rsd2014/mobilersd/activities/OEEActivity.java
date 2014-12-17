package edu.rsd2014.mobilersd.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import edu.rsd2014.mobilersd.R;
import edu.rsd2014.mobilersd.util.Constants;

/**
 * Created by darkriddle on 12/12/14.
 */
public class OEEActivity extends NavActivity{

    TextView tvGood;
    TextView tvReject;
    TextView tvRate;
    TextView tvCycle;

    TextView tvRun;
    TextView tvDown;
    TextView tvSetup;
    TextView tvStandby;

    TextView tvOEE;
    TextView tvAvail;
    TextView tvPerform;
    TextView tvQuality;


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

        tvGood = (TextView)findViewById(R.id.txtGood);
        tvReject = (TextView)findViewById(R.id.txtReject);
        tvRate = (TextView)findViewById(R.id.txtRate);
        tvCycle = (TextView)findViewById(R.id.txtCycle);

        tvRun = (TextView)findViewById(R.id.txtRun);
        tvDown = (TextView)findViewById(R.id.txtDown);
        tvSetup = (TextView)findViewById(R.id.txtSetup);
        tvStandby = (TextView)findViewById(R.id.txtStandby);

        tvOEE = (TextView)findViewById(R.id.txtOEE);
        tvAvail = (TextView)findViewById(R.id.txtAvailable);
        tvPerform = (TextView)findViewById(R.id.txtPerformance);
        tvQuality = (TextView)findViewById(R.id.txtQuality);

        new AsyncXMLRequest().execute();
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
                result = (HashMap<String,String>)rpcClient.execute("get_OEE_data",params);
            } catch (MalformedURLException e) {
                Log.e("OverviewActivity", "MalformedURLException: " + e.getMessage());
            } catch (XmlRpcException e) {
                Log.e("OverviewActivity","XmlRpcException: " + e.getMessage());
            } catch (Exception e) {
                Log.e("OverviewActivity","Exception: " + e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> data) {
            tvGood.setText(data.get("good"));
            tvReject.setText(data.get("reject"));
            tvRate.setText(data.get("rate"));
            tvCycle.setText(data.get("cycle"));

            tvRun.setText(data.get("run"));
            tvDown.setText(data.get("down"));
            tvSetup.setText(data.get("setup"));
            tvStandby.setText(data.get("standby"));

            tvOEE.setText(data.get("oee")+"%");
            tvAvail.setText(data.get("available")+"%");
            tvPerform.setText(data.get("performance")+"%");
            tvQuality.setText(data.get("quality")+"%");
        }
    }
}
