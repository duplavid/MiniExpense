package com.duplavid.miniexpense;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
 
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
 
public class SingleListItem extends ListActivity {
	private DatabaseHandler db;
	private Context context;
	private Context thisContext;
	private Menu menu;
	
	private String date;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_item);
        
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
        	final ActionBar bar = getActionBar();
            bar.setTitle(R.string.title_activity_main);
        }
    
        Intent i = getIntent();
        String date = i.getStringExtra("date");
        
        context = MainActivity.getContext();
        thisContext = this;
        
        
        setTitle(date);
        showList(date);
    }
    
    public void showList(String date){
    	this.date = date;
    	db = new DatabaseHandler(context);
    	
	    ArrayList<Expense> expenses = db.getExpensesByDate(date);
	    
	    ArrayList<String> dates = new ArrayList<String>();
	    ArrayList<String> additionals = new ArrayList<String>();
	    ArrayList<String> inouts = new ArrayList<String>();
	    ArrayList<String> amounts = new ArrayList<String>();
	    ArrayList<String> ids = new ArrayList<String>();
	    
	    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
	    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
	    for(Expense exp : expenses){
			Date dt;
			try {
				dt = inputFormat.parse(exp.getDate());
				inouts.add(""+exp.getInout()+"");
				amounts.add(""+exp.getAmount()+"");
				additionals.add(exp.getAdditional());
				dates.add(outputFormat.format(dt));
				ids.add(""+exp.getID()+"");
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
	    
	    setListAdapter(new CustomAdapter(this, dates, amounts, inouts, additionals, ids));  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String currency = settings.getString("currency", "euro");
        if(currency.equals("euro")){
        	MenuItem item = menu.findItem(R.id.euro);
            item.setIcon(R.drawable.euroblue);
        }else{
        	MenuItem item = menu.findItem(R.id.dollar);
            item.setIcon(R.drawable.dollarblue);
        }
        this.menu = menu;
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.euro:
            	MenuMethods.euroBlue(context, menu);
                return true;
            case R.id.dollar:
            	MenuMethods.dollarBlue(context, menu);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void downloadCSV(View view){
    	String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
	    	view.setEnabled(false);
	        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
	            private ProgressDialog pd;
	            @Override
	            protected void onPreExecute() {
	                     pd = new ProgressDialog(thisContext);
	                     pd.setTitle("File is being created...");
	                     pd.setMessage("Please wait.");
	                     pd.setCancelable(false);
	                     pd.setIndeterminate(true);
	                     pd.show();
	            }
	            @Override
	            protected Void doInBackground(Void... arg0) {
	                try {
	                	db = new DatabaseHandler(context);
	            	    ArrayList<Expense> expenses = db.getExpensesByDate(date);
	            	    
	            	    int total_row = expenses.size();
	            	    
	            	    ArrayList<String> dates = new ArrayList<String>();
	            	    ArrayList<String> additionals = new ArrayList<String>();
	            	    ArrayList<String> inouts = new ArrayList<String>();
	            	    ArrayList<String> amounts = new ArrayList<String>();
	            	    
	            	    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
	            	    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
	            	    for(Expense exp : expenses){
	            			Date dt;
	            			try {
	            				
	            				dt = inputFormat.parse(exp.getDate());
	            				inouts.add(""+exp.getInout()+"");
	            				amounts.add(""+exp.getAmount()+"");
	            				additionals.add(exp.getAdditional());
	            				dates.add(outputFormat.format(dt));
	            				
	            			} catch (java.text.ParseException e) {
	            				// TODO Auto-generated catch block
	            					e.printStackTrace();
	            			}
	            	    }
	                	
	            		try {
	            			File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/MiniExpense");
	            			if (!folder.exists()){
	            				folder.mkdir();
	            			}
	            			String filename = folder.toString() + "/"+date+".csv";
	            			FileWriter fw = new FileWriter(filename);
	
	            			fw.append("Date");
	        				fw.append(" ,");
	        				fw.append("Additional");
	        				fw.append(" ,");
	            			fw.append("Income");
	        				fw.append(" ,");
	        				fw.append("Expense");
	        				fw.append(" ,");
	        				fw.append("\n");
	        				
	            			// Write the string to the file
	            			for(int i=0; i<total_row; i++){
	            				fw.append(dates.get(i));
	            				fw.append(" ,");
	            				fw.append(additionals.get(i));
	            				fw.append(" ,");
	            				if(inouts.get(i).equals("1")){
	            					fw.append(amounts.get(i));
	                				fw.append(" ,");
	                				fw.append(" ");
	                				fw.append(" ,");
	            				}else{
	            					fw.append(" ");
	                				fw.append(" ,");
	            					fw.append(amounts.get(i));
	                				fw.append(" ,");
	            				}
	            				fw.append("\n");
	            			}
	            			fw.close();
	            			
	            		}
	            		catch (IOException ioe){
	            			ioe.printStackTrace();
	            		}
	                       Thread.sleep(5000);
	                } catch (InterruptedException e) {
	                       // TODO Auto-generated catch block
	                       e.printStackTrace();
	                }
	                return null;
	             }
	             @Override
	             protected void onPostExecute(Void result) {
	                    pd.dismiss();
	                    //Show a dialog that file was created
	             		AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisContext);
	             		alertDialog.setTitle("Saved");
	             		alertDialog.setMessage("File is saved on your SD card in the MiniExpense folder.");
	             		
	             		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	             		    public void onClick(DialogInterface dialog, int which) {}
	             	    });
	             		
	             		alertDialog.show();
	             		
	             		
	             		//Show the file in its default application
	             		File folder = new File(Environment.getExternalStorageDirectory().getPath()+"/MiniExpense");
            			String filename = folder.toString() + "/"+date+".csv";
	             		
	             		Intent viewDoc = new Intent(Intent.ACTION_VIEW);
	             		viewDoc.setDataAndType(
	             		    Uri.fromFile(getFileStreamPath(filename)), 
	             		    "application/csv");

	             		PackageManager pm = getPackageManager();
	             		List<ResolveInfo> apps = 
	             		    pm.queryIntentActivities(viewDoc, PackageManager.MATCH_DEFAULT_ONLY);

	             		if (apps.size() > 0)
	             		    startActivity(viewDoc);
	             		
	             }
	        };
	        task.execute((Void[])null);
		}else{
			//Show a dialog that file wasn't created, because the SD card wasn't mounted.
     		AlertDialog.Builder alertDialog = new AlertDialog.Builder(thisContext);
     		alertDialog.setTitle("File save error");
     		alertDialog.setMessage("There's no SD card in the phone.");
     		
     		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
     		    public void onClick(DialogInterface dialog, int which) {}
     	    });
     		
     		alertDialog.show();
		}
    }
    
    public void deleteRow(View view){
    	final String index = (String) view.getTag();
    	db = new DatabaseHandler(context);
    	
        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Delete row")
	        .setMessage("Are you sure you want to delete this transaction?")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	db.deleteExpense(index);
		        	finish();
		        	startActivity(getIntent());  
		        }
		
		    })
		    .setNegativeButton("No", null)
		    .show();
	    	
    	
    }
    
    
}