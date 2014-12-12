package edu.rsd2014.mobilersd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.rsd2014.mobilersd.R;

/**
 * Created by darkriddle on 12/12/14.
 */
public class HelpActivity extends NavActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.help);
    }
}
