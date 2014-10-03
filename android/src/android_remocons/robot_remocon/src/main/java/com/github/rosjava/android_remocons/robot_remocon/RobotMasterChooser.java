/*
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2011, Willow Garage, Inc.
 * Copyright (c) 2013, OSRF.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *  * Neither the name of Willow Garage, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.rosjava.android_remocons.robot_remocon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rosjava.android_apps.application_management.RobotDescription;
import com.github.rosjava.android_apps.application_management.RobotId;
import com.github.rosjava.android_apps.application_management.RobotsContentProvider;
import com.github.rosjava.android_remocons.robot_remocon.zeroconf.MasterSearcher;
import com.github.rosjava.zeroconf_jmdns_suite.jmdns.DiscoveredService;
import com.google.zxing.IntentIntegrator;
import com.google.zxing.IntentResult;

import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hersh@willowgarage.com
 * @author murase@jsk.imi.i.u-tokyo.ac.jp (Kazuto Murase)
 */
public class RobotMasterChooser extends Activity {

	private static final int ADD_URI_DIALOG_ID = 0;
	private static final int ADD_DELETION_DIALOG_ID = 1;
	private static final int ADD_SEARCH_ROBOT_DIALOG_ID = 2;

    private static final int QR_CODE_SCAN_REQUEST_CODE = 101;
    private static final int NFC_TAG_SCAN_REQUEST_CODE = 102;

	private List<RobotDescription> robots;
	private boolean[] selections;
	private MasterSearcher masterSearcher;
	private ListView listView;

	public RobotMasterChooser() {
		robots = new ArrayList<RobotDescription>();
	}

	private void readRobotList() {
		String str = null;
		Cursor c = getContentResolver().query(
				RobotsContentProvider.CONTENT_URI, null, null, null, null);
		if (c == null) {
			robots = new ArrayList<RobotDescription>();
			Log.e("RobotRemocon", "robot master chooser provider failed!!!");
			return;
		}
		if (c.getCount() > 0) {
			c.moveToFirst();
			str = c.getString(c
					.getColumnIndex(RobotsContentProvider.TABLE_COLUMN));
			Log.i("RobotRemocon", "robot master chooser found a robot: " + str);
		}
		if (str != null) {
			Yaml yaml = new Yaml();
			robots = (List<RobotDescription>) yaml.load(str);
		} else {
			robots = new ArrayList<RobotDescription>();
		}
	}

	public void writeRobotList() {
		Log.i("RobotRemocon", "robot master chooser saving robot...");
		Yaml yaml = new Yaml();
		String txt = null;
		final List<RobotDescription> robot = robots; // Avoid race conditions
		if (robot != null) {
			txt = yaml.dump(robot);
		}
		ContentValues cv = new ContentValues();
		cv.put(RobotsContentProvider.TABLE_COLUMN, txt);
		Uri newEmp = getContentResolver().insert(
				RobotsContentProvider.CONTENT_URI, cv);
		if (newEmp != RobotsContentProvider.CONTENT_URI) {
			Log.e("RobotRemocon", "robot master chooser could not save robot, non-equal URI's");
		}
	}

	private void refresh() {
		readRobotList();
		updateListView();
	}

	private void updateListView() {
		setContentView(R.layout.robot_master_chooser);
		ListView listview = (ListView) findViewById(R.id.master_list);
		listview.setAdapter(new MasterAdapter(this, robots));
		registerForContextMenu(listview);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				choose(position);
			}
		});
	}

    /**
     * Called when the user clicks on one of the robots in master chooser
     * view. Should probably check the connection status before
     * proceeding here, but perhaps we can just rely on the user clicking
     * refresh so this process stays without any lag delay.
     *
     * @param position
     */
	private void choose(int position) {
		RobotDescription robot = robots.get(position);
		if (robot == null || robot.getConnectionStatus() == null
				|| robot.getConnectionStatus().equals(robot.ERROR)) {
			AlertDialog d = new AlertDialog.Builder(RobotMasterChooser.this)
					.setTitle("Error!")
					.setCancelable(false)
					.setMessage("Failed: Cannot contact robot")
					.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create();
			d.show();
        } else if ( robot.getConnectionStatus().equals(robot.UNAVAILABLE) ) {
            AlertDialog d = new AlertDialog.Builder(RobotMasterChooser.this)
                    .setTitle("Robot Unavailable!")
                    .setCancelable(false)
                    .setMessage("Currently busy serving another.")
                    .setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            }).create();
            d.show();
        } else {
			Intent resultIntent = new Intent();
			resultIntent
					.putExtra(RobotDescription.UNIQUE_KEY, robots.get(position));
			setResult(RESULT_OK, resultIntent);
			finish();
		}
	}

	private void addMaster(RobotId robotId) {
		addMaster(robotId, false);
	}

	private void addMaster(RobotId robotId, boolean connectToDuplicates) {
		Log.i("MasterChooserActivity", "adding master to the robot master chooser [" + robotId.toString() + "]");
		if (robotId == null || robotId.getMasterUri() == null) {
		} else {
			for (int i = 0; i < robots.toArray().length; i++) {
				RobotDescription robot = robots.get(i);
				if (robot.getRobotId().equals(robotId)) {
					if (connectToDuplicates) {
						choose(i);
						return;
					} else {
						Toast.makeText(this, "That robot is already listed.",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			Log.i("MasterChooserActivity", "creating robot description: "
					+ robotId.toString());
			robots.add(RobotDescription.createUnknown(robotId));
			Log.i("MasterChooserActivity", "description created");
			onRobotsChanged();
		}
	}

	private void onRobotsChanged() {
		writeRobotList();
		updateListView();
	}

	private void deleteAllRobots() {
		robots.clear();
		onRobotsChanged();
	}

	private void deleteSelectedRobots(boolean[] array) {
		int j = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i]) {
				robots.remove(j);
			} else {
				j++;
			}
		}
		onRobotsChanged();
	}

	private void deleteUnresponsiveRobots() {
		Iterator<RobotDescription> iter = robots.iterator();
		while (iter.hasNext()) {
			RobotDescription robot = iter.next();
			if (robot == null || robot.getConnectionStatus() == null
					|| robot.getConnectionStatus().equals(robot.ERROR)) {
				Log.i("RobotRemocon", "robot master chooser removing robot with connection status '"
						+ robot.getConnectionStatus() + "'");
				iter.remove();
			}
		}
		onRobotsChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		readRobotList();
		updateListView();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Sub-activity to gather robot connection data completed: can be QR code or NFC tag scan
        // TODO: cannot unify both calls?

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            return;
        }

        String scanned_data = null;

        if (requestCode == QR_CODE_SCAN_REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, intent);
            if (scanResult != null && scanResult.getContents() != null) {
                scanned_data = scanResult.getContents().toString();
            }
        }
        else if (requestCode == NFC_TAG_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (intent.hasExtra("tag_data")) {
                scanned_data = intent.getExtras().getString("tag_data");
            }
        }
        else {
            Log.w("RobotRemocon", "Unknown activity request code: " + requestCode);
            return;
        }

        if (scanned_data == null) {
            Toast.makeText(this, "Scan failed", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                Yaml yaml = new Yaml();
                Map<String, Object> data = (Map<String, Object>) yaml.load(scanned_data);
                Log.d("RobotRemocon", "RobotMasterChooser OBJECT: " + data.toString());
                addMaster(new RobotId(data), false);
            } catch (Exception e) {
                Toast.makeText(this,
                        "Invalid robot description: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		readRobotList();
		final Dialog dialog;
		Button button;
		AlertDialog.Builder builder;
		switch (id) {
		case ADD_URI_DIALOG_ID:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.add_uri_dialog);
			dialog.setTitle("Add a robot");
			dialog.setOnKeyListener(new DialogKeyListener());
			EditText uriField = (EditText) dialog.findViewById(R.id.uri_editor);
			EditText controlUriField = (EditText) dialog
					.findViewById(R.id.control_uri_editor);
			uriField.setText("http://localhost:11311/",
					TextView.BufferType.EDITABLE);
			// controlUriField.setText("http://prX1.willowgarage.com/cgi-bin/control.py",TextView.BufferType.EDITABLE
			// );
			button = (Button) dialog.findViewById(R.id.enter_button);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					enterRobotInfo(dialog);
					removeDialog(ADD_URI_DIALOG_ID);
				}
			});
            button = (Button) dialog.findViewById(R.id.qr_code_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanQRCodeClicked(v);
                }
            });
            button = (Button) dialog.findViewById(R.id.nfc_tag_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanNFCTagClicked(v);
                }
            });
			button = (Button) dialog.findViewById(R.id.search_master_button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					searchRobotClicked(v);
				}
			});

			button = (Button) dialog.findViewById(R.id.cancel_button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(ADD_URI_DIALOG_ID);
				}
			});
			break;
		case ADD_DELETION_DIALOG_ID:
			builder = new AlertDialog.Builder(this);
			String newline = System.getProperty("line.separator");
			if (robots.size() > 0) {
				selections = new boolean[robots.size()];
				Spannable[] robot_names = new Spannable[robots.size()];
				Spannable name;
				for (int i = 0; i < robots.size(); i++) {
					name = Factory.getInstance().newSpannable(
							robots.get(i).getRobotName() + newline
									+ robots.get(i).getRobotId());
					name.setSpan(new ForegroundColorSpan(0xff888888), robots
							.get(i).getRobotName().length(), name.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					name.setSpan(new RelativeSizeSpan(0.8f), robots.get(i)
							.getRobotName().length(), name.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					robot_names[i] = name;
				}
				builder.setTitle("Delete a robot");
				builder.setMultiChoiceItems(robot_names, selections,
						new DialogSelectionClickHandler());
				builder.setPositiveButton("Delete Selections",
						new DeletionDialogButtonClickHandler());
				builder.setNegativeButton("Cancel",
						new DeletionDialogButtonClickHandler());
				dialog = builder.create();
			} else {
				builder.setTitle("No robots to delete.");
				dialog = builder.create();
				final Timer t = new Timer();
				t.schedule(new TimerTask() {
					public void run() {
						removeDialog(ADD_DELETION_DIALOG_ID);
					}
				}, 2 * 1000);
			}
			break;
		case ADD_SEARCH_ROBOT_DIALOG_ID:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Scanning on the local network...");
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listView = (ListView) layoutInflater.inflate(
					R.layout.zeroconf_master_list, null);
			masterSearcher = new MasterSearcher(this, listView);
			builder.setView(listView);
			builder.setPositiveButton("Select",
					new SearchRobotDialogButtonClickHandler());
			builder.setNegativeButton("Cancel",
					new SearchRobotDialogButtonClickHandler());
			dialog = builder.create();
			dialog.setOnKeyListener(new DialogKeyListener());

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			return;
		}
	}

	public class DeletionDialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				deleteSelectedRobots(selections);
				removeDialog(ADD_DELETION_DIALOG_ID);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				removeDialog(ADD_DELETION_DIALOG_ID);
				break;
			}
		}
	}

	public class SearchRobotDialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				SparseBooleanArray positions = listView
						.getCheckedItemPositions();

				for (int i = 0; i < positions.size(); i++) {
					if (positions.valueAt(i)) {
						enterRobotInfo((DiscoveredService) listView.getAdapter()
								.getItem(positions.keyAt(i)));
					}
				}
				removeDialog(ADD_DELETION_DIALOG_ID);
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				removeDialog(ADD_DELETION_DIALOG_ID);
				break;
			}
		}
	}

	public void enterRobotInfo(DiscoveredService discovered_service) {
        /*
          This could be better - it should actually contact and check off each
          resolvable zeroconf address looking for the master. Instead, we just grab
          the first ipv4 address and totally ignore the possibility of an ipv6 master.
         */
        String newMasterUri = null;
        if ( discovered_service.ipv4_addresses.size() != 0 ) {
            newMasterUri = "http://" + discovered_service.ipv4_addresses.get(0) + ":"
                    + discovered_service.port + "/";
        }
        if (newMasterUri != null && newMasterUri.length() > 0) {
            android.util.Log.i("RobotRemocon", newMasterUri);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("URL", newMasterUri);
            try {
                addMaster(new RobotId(data));
            } catch (Exception e) {
                Toast.makeText(RobotMasterChooser.this, "Invalid Parameters.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RobotMasterChooser.this, "No valid resolvable master URI.",
                    Toast.LENGTH_SHORT).show();
        }
	}

	public void enterRobotInfo(Dialog dialog) {
		EditText uriField = (EditText) dialog.findViewById(R.id.uri_editor);
		String newMasterUri = uriField.getText().toString();
		EditText controlUriField = (EditText) dialog
				.findViewById(R.id.control_uri_editor);
		String newControlUri = controlUriField.getText().toString();
		EditText wifiNameField = (EditText) dialog
				.findViewById(R.id.wifi_name_editor);
		String newWifiName = wifiNameField.getText().toString();
		EditText wifiPasswordField = (EditText) dialog
				.findViewById(R.id.wifi_password_editor);
		String newWifiPassword = wifiPasswordField.getText().toString();
		if (newMasterUri != null && newMasterUri.length() > 0) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("URL", newMasterUri);
			if (newControlUri != null && newControlUri.length() > 0) {
				data.put("CURL", newControlUri);
			}
			if (newWifiName != null && newWifiName.length() > 0) {
				data.put("WIFI", newWifiName);
			}
			if (newWifiPassword != null && newWifiPassword.length() > 0) {
				data.put("WIFIPW", newWifiPassword);
			}
			try {
				addMaster(new RobotId(data));
			} catch (Exception e) {
				Toast.makeText(RobotMasterChooser.this, "Invalid Parameters.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(RobotMasterChooser.this, "Must specify Master URI.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public class DialogKeyListener implements DialogInterface.OnKeyListener {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& keyCode == KeyEvent.KEYCODE_ENTER) {
				Dialog dlg = (Dialog) dialog;
				enterRobotInfo(dlg);
				removeDialog(ADD_URI_DIALOG_ID);
				return true;
			}
			return false;
		}
	}

	public void addRobotClicked(View view) {
		showDialog(ADD_URI_DIALOG_ID);
	}

	public void refreshClicked(View view) {
		refresh();
	}

    public void scanQRCodeClicked(View view) {
        dismissDialog(ADD_URI_DIALOG_ID);
        IntentIntegrator.initiateScan(this, IntentIntegrator.DEFAULT_TITLE,
                IntentIntegrator.DEFAULT_MESSAGE, IntentIntegrator.DEFAULT_YES,
                IntentIntegrator.DEFAULT_NO, IntentIntegrator.QR_CODE_TYPES);
    }

    public void scanNFCTagClicked(View view) {
        dismissDialog(ADD_URI_DIALOG_ID);
        Intent i = new Intent(this,
                com.github.rosjava.android_remocons.robot_remocon.nfc.ForegroundDispatch.class);
        // Set the request code so we can identify the callback via this code
        startActivityForResult(i, NFC_TAG_SCAN_REQUEST_CODE);
    }

	public void searchRobotClicked(View view) {
		removeDialog(ADD_URI_DIALOG_ID);
		showDialog(ADD_SEARCH_ROBOT_DIALOG_ID);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.robot_master_chooser_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_robot) {
			showDialog(ADD_URI_DIALOG_ID);
			return true;
		} else if (id == R.id.delete_selected) {
			showDialog(ADD_DELETION_DIALOG_ID);
			return true;
		} else if (id == R.id.delete_unresponsive) {
			deleteUnresponsiveRobots();
			return true;
		} else if (id == R.id.delete_all) {
			deleteAllRobots();
			return true;
		} else if (id == R.id.kill) {
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
