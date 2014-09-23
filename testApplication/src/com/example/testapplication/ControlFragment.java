package com.example.testapplication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ControlFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.control, container,false);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(2);
	}
}
