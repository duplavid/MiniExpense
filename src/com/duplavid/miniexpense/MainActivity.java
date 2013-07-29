package com.duplavid.miniexpense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DatePickerDialog;
import android.app.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
 
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
 
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
	
	private Context context;
	public static Context statContext;
	private Menu menu;
	
	ViewPager ViewPager;
	TabsAdapter TabsAdapter;
	
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	
	private static String date;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
        	ViewPager = new ViewPager(this);
            ViewPager.setId(R.id.pager);
            setContentView(ViewPager);
            
        	final ActionBar bar = getActionBar();
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            bar.setTitle(R.string.title_activity_main);
            
            //Attach the Tabs to the fragment classes and set the tab title.
            TabsAdapter = new TabsAdapter(this, ViewPager);
            TabsAdapter.addTab(bar.newTab().setText("View expenses"),
            		ExpenseView.class, null);
            TabsAdapter.addTab(bar.newTab().setText("Add expense"),
                    ExpenseAdd.class, null);
            
            if (savedInstanceState != null) {
                bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
            }
            
        }else{
        	setContentView(R.layout.tabs_layout);
            // Step 2: Setup TabHost
            initialiseTabHost(savedInstanceState);
            if (savedInstanceState != null) {
                mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
            } 
            
        }
        
        context = getApplicationContext();
        statContext = context;
    }
    
    private static void addTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
    tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();
     
        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
     
        tabHost.addTab(tabSpec);
    }
    
    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;
        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

   }
    
    class TabFactory implements TabContentFactory {
    	 
        private final Context mContext;
 
        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }
 
        /** (non-Javadoc)
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
 
    }
    
    @SuppressWarnings("unchecked")
	private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("View expenses"), ( tabInfo = new TabInfo("Tab1", ExpenseView.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        MainActivity.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Add expense"), ( tabInfo = new TabInfo("Tab2", ExpenseAdd.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        // Default to first tab
        this.onTabChanged("Tab1");
        //
        mTabHost.setOnTabChangedListener(this);
    }
    
    public void onTabChanged(String tag) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
        if (mLastTab != newTab) {
            android.support.v4.app.FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    newTab.fragment = Fragment.instantiate(this,
                            newTab.clss.getName(), newTab.args);
                    ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
                } else {
                    ft.attach(newTab.fragment);
                }
            }
     
            mLastTab = newTab;
            ft.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }
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
    
    public static class DatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener {

    	public TextView setDate;
    	
    	public DatePickerFragment() {}
    	
    	@SuppressLint("ValidFragment")
		public DatePickerFragment(TextView edit_text) {
    		setDate = edit_text;
    	}
    	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			//play with the month value
			String mnth = ("0"+(month+1));
			if(mnth.length() > 2){
				mnth = mnth.substring(1,3);
			}
			
			//play with the day value
			String dy = ("0"+day);
			if(dy.length() > 2){
				dy = dy.substring(1,3);
			}
			date = ""+year+"-"+mnth+"-"+dy+"";

			setDate.setText(date);
		}
	}
    
    public void showDatePickerDialog(View v) {
    	TextView setDate = (TextView)findViewById(R.id.setDate); 
        DialogFragment newFragment = new DatePickerFragment(setDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

	public void saveContents(View view){
		Spinner mInout = (Spinner) findViewById(R.id.spinner1);
		EditText mAmount = (EditText) findViewById(R.id.editText1);
		EditText mAdditional = (EditText) findViewById(R.id.editText2);
		TextView mDate = (TextView)findViewById(R.id.setDate);
		
		String am = mAmount.getText().toString();
		String additional = mAdditional.getText().toString();
		String setDate = mDate.getText().toString();

		//Validate form
		if(am.equals("")){
			mAmount.setError("Please enter an amount.");
		}else if(additional.equals("")){
			mAdditional.setError("Please enter additional information about the payment.");
		}else if(setDate.equals("Payment date wasn't selected")){
			mDate.setError("Please set the payment date.");
		}else{
			DatabaseHandler db = new DatabaseHandler(this);
			
			//Play around with the in/out value
			String io = mInout.getSelectedItem().toString();
			int inout;
			if(io.equals("Pay out")){inout = 0;}else{inout = 1;}
			
			//Parse the amount string to double
			Double amount = Double.parseDouble(am);
			
			db.addExpense(new Expense(inout, setDate, amount, additional));
			
		    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
	        	ExpenseView ex = (ExpenseView)
	                    getSupportFragmentManager().findFragmentByTag(
	                            "android:switcher:" + TabsAdapter.mViewPager.getId() + ":0");
	    	    ex.refresh();
	        }
			
		    //Show a dialog that payment was saved
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("Saved");
			alertDialog.setMessage("Payment is saved");
			
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			    	EditText mAmount = (EditText)findViewById(R.id.editText1);
			    	mAmount.setText("");
			    	EditText mAdditional = (EditText)findViewById(R.id.editText2);
			    	mAdditional.setText("");
			    	TextView mDate = (TextView)findViewById(R.id.setDate);
			    	mDate.setText("Payment date wasn't selected");
			    	mDate.setError(null);
			    }
		    });
			
			alertDialog.show();
		}
		
	}
	
	public static Context getContext(){
		return statContext;
	}
	
 
	private String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

 	public static class TabsAdapter extends FragmentPagerAdapter
 	implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		
		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
			
			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}
	
		public TabsAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}
		
		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub
		}
	
		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mActionBar.setSelectedNavigationItem(position);
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i=0; i<mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}
		
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}
		
		@Override
		public int getCount() {
			return mTabs.size();
		}
	
	}
 
}
