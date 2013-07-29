package com.duplavid.miniexpense;
 
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class CustomAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> dates;
	private final ArrayList<String> amounts;
	private final ArrayList<String> inouts;
	private final ArrayList<String> additionals;
	private final ArrayList<String> ids;
 
	public CustomAdapter(Context context, ArrayList<String> dates, 
			ArrayList<String> amounts, ArrayList<String> inouts, ArrayList<String> additionals, ArrayList<String> ids) {
		super(context, R.layout.list_item, dates);
		this.context = context;
		this.dates = dates;
		this.amounts = amounts;
		this.inouts = inouts;
		this.additionals = additionals;
		this.ids = ids;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String currency = settings.getString("currency", "euro");
        String curr = null;
        if(currency.equals("euro")){
        	curr = "€";
        }else{
        	curr = "$";
        }
		
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.list_view_permonth, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.list);
		TextView additional = (TextView) rowView.findViewById(R.id.additionalRow);
		TextView payinrow = (TextView) rowView.findViewById(R.id.payinrow);
		TextView payoutrow = (TextView) rowView.findViewById(R.id.payoutrow);

		if(inouts.get(position).equals("1")){
			payinrow.setText(curr+""+amounts.get(position));
		}else{
			payoutrow.setText(curr+""+amounts.get(position));
		}
		
		textView.setText(dates.get(position));
		additional.setText(additionals.get(position));
		
		ImageView deleteButton = (ImageView) rowView.findViewById(R.id.deleteRow);
         deleteButton.setTag(ids.get(position));
 
		return rowView;
	}
}