package com.wshuman3.weighttracker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DatabaseCursorAdapter extends CursorAdapter {

	public DatabaseCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView textViewWeight = (TextView) view.findViewById(R.id.weight);
		textViewWeight
				.setText(cursor.getString(cursor.getColumnIndex("weight")));
		TextView textViewDate = (TextView) view.findViewById(R.id.date);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date;
		try {
			date = DateFormat.getDateInstance().format(
					sdf.parse(cursor.getString(cursor.getColumnIndex("date"))));
		} catch (ParseException e) {
			date = cursor.getString(cursor.getColumnIndex("date"));
		}
		textViewDate.setText(date);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.weight_list, parent, false);
		return retView;
	}

}
