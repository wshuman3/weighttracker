package com.wshuman3.weighttracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wshuman3.weighttracker.utility.DBAdapter;
import com.wshuman3.weighttracker.utility.Utility;

public class WeighInFragment extends Fragment {

	private Button saveButton;
	private LinearLayout view;
	private ListView listView;
	private DatabaseCursorAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (container == null)
			return null;

		view = (LinearLayout) inflater
				.inflate(R.layout.weighin, container, false);
		this.saveButton = (Button) view.findViewById(R.id.saveWeight);
		this.saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				saveWeight();
			}
		});
		 
		listView = (ListView) view.findViewById(R.id.weightList);
		  
		DBAdapter db = new DBAdapter(getActivity());
		db.open();

		new Handler().post(new Runnable() {
		    public void run() {
		    	DBAdapter db = new DBAdapter(getActivity());
				db.open();
				adapter = new DatabaseCursorAdapter(getActivity(), db.getAllWeights(100, "desc"), true);
		        listView.setAdapter(adapter);
		        db.close();
		    }
		});
		
		
		return view;
	}

	private void saveWeight() {

		BigDecimal weight = null;
		EditText weightTextBox = (((EditText) view
				.findViewById(R.id.weightinput)));

		try {
			weight = new BigDecimal(weightTextBox.getText().toString());
			weight = weight.setScale(2, RoundingMode.HALF_DOWN);
		} catch (Exception e) {
			Utility.displayAlertDialog(getActivity(), "Invalid Weight",
					"You have entered an invalid weight.");
		}

		if (weight != null) {
			Calendar todayCal = Calendar.getInstance();
			todayCal.set(Calendar.HOUR, 0);
			todayCal.set(Calendar.MINUTE, 0);
			todayCal.set(Calendar.SECOND, 0);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String today = sdf.format(todayCal.getTime());

			DBAdapter db = new DBAdapter(getActivity());
			db.open();

			if (db.getWeightForDate(today) == null) {
				db.insertWeight(weight, today);
				weightTextBox.setText("");
			} else {
				Utility.displayAlertDialog(getActivity(),
						"Already Entered for Today",
						"You have already entered a weight for today.");
			}
			db.close();
		}
		new Handler().post(new Runnable() {
		    public void run() {
		    	DBAdapter db = new DBAdapter(getActivity());
				db.open();
				adapter.changeCursor(db.getAllWeights(100, "desc"));
		        db.close();
		        adapter.notifyDataSetChanged();
		    }
		});
		
	}
	

}