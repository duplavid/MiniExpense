package com.duplavid.miniexpense;
 
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 
public class ExpenseView extends ListFragment {
	
	private Context context;
    private DatabaseHandler db;
    public ArrayAdapter<String> adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.expenseview, container, false);
		context = getActivity().getApplicationContext();
		
		return myFragmentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    showList();
	  }
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
	    //Get the selected item's date
		String selection = l.getItemAtPosition(position).toString();
		
		Intent i = new Intent(context, SingleListItem.class);
        // sending data to new activity
        i.putExtra("date", selection);
        startActivity(i);
	}
    
    public void showList(){
    	db = new DatabaseHandler(context);
	    
	    ArrayList<Expense> expenses = db.getExpensesGrouped();
	    ArrayList<String> values = new ArrayList<String>();
	    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
	    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM",Locale.UK);
	    for(Expense exp : expenses){
		Date date;
		try {
			date = inputFormat.parse(exp.getDate());
			values.add(outputFormat.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	        R.layout.list_item, R.id.list, values);
	    this.adapter = adapter;
	    setListAdapter(adapter);
    }
    
    public void refresh(){
    	showList();
    	this.adapter.notifyDataSetChanged();
    }
	



}