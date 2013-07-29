package com.duplavid.miniexpense;
 
import com.duplavid.miniexpense.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
public class ExpenseAdd extends Fragment {
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		
		View myFragmentView = inflater.inflate(R.layout.expenseadd, container, false);
		return myFragmentView;
		
	}
	

}