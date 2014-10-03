/*
 * Software License Agreement (BSD License)
 *
 * Copyright (c) 2011, Willow Garage, Inc.
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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.github.rosjava.android_apps.application_management.RobotDescription;
import java.util.ArrayList;
import java.util.List;
/**
 * @author hersh@willowgarage.com
 */
public class MasterAdapter extends BaseAdapter {
  private Context context;
  private List<MasterItem> masterItems;
  public MasterAdapter(RobotMasterChooser rmc, List<RobotDescription> robots) {
    context = rmc;
    masterItems = new ArrayList<MasterItem>();
    if (robots != null) {
      for (int i = 0; i < robots.size(); i++) {
        masterItems.add(new MasterItem(robots.get(i), rmc));
      }
    }
  }
  @Override
  public int getCount() {
    if (masterItems == null) {
      return 0;
    }
    return masterItems.size();
  }
  @Override
  public Object getItem(int position) {
    return null;
  }
  @Override
  public long getItemId(int position) {
    return 0;
  }
  // create a new View for each item referenced by the Adapter
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return masterItems.get(position).getView(context, convertView, parent);
  }
}