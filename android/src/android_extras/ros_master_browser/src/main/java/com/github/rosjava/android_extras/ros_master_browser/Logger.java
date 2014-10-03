/*
 * Copyright (C) 2013 Yujin Robot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.rosjava.android_extras.ros_master_browser;

import java.lang.String;
import com.github.rosjava.zeroconf_jmdns_suite.jmdns.ZeroconfLogger;

public class Logger implements ZeroconfLogger {

	public void println(String msg) {
		android.util.Log.i("zeroconf", msg);
	}
}
