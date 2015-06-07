package com.buildmlearn.labeldiagram.resources;

import java.util.List;

import com.example.labelthediagram.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DiagramCategoryAdapter extends ArrayAdapter<DiagramCategoryRawItem> {

	Context context;
	Typeface tfThin;
	Typeface tfLight;

	public DiagramCategoryAdapter(Context context, int resource,
			List<DiagramCategoryRawItem> objects) {
		super(context, resource, objects);
		this.context = context;
		
		tfThin = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Thin.ttf");
		tfLight = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Light.ttf");
	}

	private class ViewHolder {

		ImageView categoryIcon;
		ImageView goIcon;
		TextView titleTxt;
		TextView descriptionTxt;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final int index = position;
		ViewHolder holder = null;
		DiagramCategoryRawItem rawItem = getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.category_row_item, null);
			holder = new ViewHolder();
			holder.titleTxt = (TextView) convertView.findViewById(R.id.categoryTitle);
			holder.descriptionTxt = (TextView) convertView.findViewById(R.id.catDescription);
			holder.categoryIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
			holder.goIcon = (ImageView) convertView.findViewById(R.id.navigatorBtn);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.titleTxt.setText(rawItem.getTitle());
		holder.descriptionTxt.setText(rawItem.getDescription());
		holder.categoryIcon.setImageResource(rawItem.getImageId());
		holder.goIcon.setImageResource(R.drawable.proceed_btn);
		
		holder.titleTxt.setTypeface(tfLight);
		holder.descriptionTxt.setTypeface(tfThin);
		
		holder.goIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getContext(), "Dispatching to Diagram Play screen", Toast.LENGTH_LONG).show();;
				
			}
		});
		
		return convertView;

	}

}
