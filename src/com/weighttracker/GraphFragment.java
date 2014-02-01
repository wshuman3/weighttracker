package com.weighttracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weighttracker.utility.DBAdapter;
import com.weighttracker.R;

public class GraphFragment extends Fragment {

	private LinearLayout view;
	private ArrayList<BigDecimal> weights = new ArrayList<BigDecimal>();
	private ArrayList<Date> weightdates = new ArrayList<Date>();
	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	private static final int MENU_REFRESH = 0;
	SharedPreferences sp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		view = (LinearLayout) inflater
				.inflate(R.layout.graph, container, false);
		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			refreshGraph();
		}
	}

	private Handler dataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			drawGraph();
			// getParent().setProgressBarIndeterminateVisibility(false);
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_REFRESH, 0, "Refresh Data");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			this.refreshGraph();
			return true;
		}
		return false;
	}

	private void refreshGraph() {
		new Thread() {
			public void run() {
				getLatestWeighIns();
				dataHandler.sendEmptyMessage(0);
			}
		}.start();

	}

	private void drawGraph() {
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.chartparent);
		TextView tv = (TextView) view.findViewById(R.id.graph_error);
		tv.setVisibility(View.GONE);

		String[] titles = new String[] { "Weight" };
		List<Date[]> dates = new ArrayList<Date[]>();
		List<double[]> values = new ArrayList<double[]>();

		int length = this.weightdates.size();
		if (length > 1) {
			dates.add(new Date[length]);

			for (int i = 0; i < length; i++) {
				dates.get(0)[i] = this.weightdates.get(i);
			}

			length = this.weights.size();
			values.add(new double[length]);

			for (int i = 0; i < length; i++) {
				values.get(0)[i] = this.weights.get(i).doubleValue();
			}

			length = values.get(0).length;

			// get max and min weights
			double minWeight = weights.get(0).doubleValue();
			double maxWeight = weights.get(0).doubleValue();

			for (int i = 0; i < weights.size(); i++) {
				if (weights.get(i).doubleValue() > maxWeight) {
					maxWeight = weights.get(i).doubleValue();
				}
				if (weights.get(i).doubleValue() < minWeight) {
					minWeight = weights.get(i).doubleValue();
				}
			}

			int[] colors = new int[] { Color.BLACK };
			PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
			this.renderer = new XYMultipleSeriesRenderer();
			this.renderer.setAxisTitleTextSize(20);
			this.renderer.setLabelsTextSize(20);
			this.renderer.setShowLabels(true);
			this.renderer.setLabelsColor(Color.BLACK);
			this.renderer.setAxesColor(Color.BLACK);
			this.renderer.setMarginsColor(Color.WHITE);
			this.renderer.setShowLegend(false);
			this.renderer.setPointSize(7f);
			this.renderer.setAntialiasing(true);
			this.renderer.setMargins(new int[] { 10, 55, 35, 10 });
			this.renderer.setApplyBackgroundColor(true);
			this.renderer.setBackgroundColor(Color.WHITE);
			// top,left,bottom,right
			int colorslength = colors.length;
			for (int i = 0; i < colorslength; i++) {
				XYSeriesRenderer r = new XYSeriesRenderer();
				r.setColor(colors[i]);
				r.setPointStyle(styles[i]);
				r.setLineWidth(5f);
				this.renderer.addSeriesRenderer(r);
			}
			this.renderer.setXTitle("Date");
			this.renderer.setYTitle("Weight");
			this.renderer.setXAxisMin(dates.get(0)[0].getTime());
			this.renderer.setXAxisMax(dates.get(0)[length - 1].getTime());
			this.renderer.setYAxisMin(minWeight - 5);
			this.renderer.setYAxisMax(maxWeight + 5);
			this.renderer.setXLabels(5);
			this.renderer.setYLabels(10);
			this.renderer.setDisplayValues(false);
			this.renderer.setZoomEnabled(false, false);
			this.renderer.setPanEnabled(false);
			this.renderer.setXLabelsAlign(Align.CENTER);
			this.renderer.setYLabelsAlign(Align.RIGHT);

			
			
			try {
				View graphView = view.findViewById(R.id.graphview);
				View myChartView = ChartFactory.getTimeChartView(getActivity(),
						buildDateDataset(titles, dates, values), this.renderer,
						"MM/dd");
				layout.removeViewAt(layout.indexOfChild(graphView));
				myChartView.setId(R.id.graphview);
				layout.addView(myChartView, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
			} catch (Exception e) {
				
				tv.setText("Graphing is not supported on your device.");
				tv.setVisibility(View.VISIBLE);
			}
		} else {
			tv.setText("You do not have enough data to graph.");
			tv.setVisibility(View.VISIBLE);
		}

	}

	private void getLatestWeighIns() {

		this.weights = new ArrayList<BigDecimal>();
		this.weightdates = new ArrayList<Date>();
		DBAdapter db = new DBAdapter(getActivity());
		db.open();
		int numberOfWeights;
		try {
			numberOfWeights = Integer.parseInt((sp.getString(
					"weightcount_preference", "365")));
		} catch (Exception e) {
			numberOfWeights = 365;
		}
		Cursor weightsCursor = db.getAllWeights(numberOfWeights, "desc");
		while (weightsCursor.moveToNext()) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date = sdf.parse(weightsCursor.getString(weightsCursor
						.getColumnIndex("date")));
				BigDecimal weight = new BigDecimal(
						weightsCursor.getDouble((weightsCursor
								.getColumnIndex("weight"))));
				weight = weight.setScale(2, RoundingMode.HALF_DOWN);
				this.weightdates.add(date);
				this.weights.add(weight);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		db.close();
		Collections.reverse(this.weightdates);
		Collections.reverse(this.weights);
	}

	protected XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<Date[]> xValues, List<double[]> yValues) {

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;

		for (int i = 0; i < length; i++) {

			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;

			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}

			dataset.addSeries(series);
		}

		return dataset;
	}
}