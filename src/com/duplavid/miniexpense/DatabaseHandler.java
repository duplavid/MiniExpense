package com.duplavid.miniexpense;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "expenseManager";
 
    // Expenses table name
    private static final String TABLE_EXPENSES = "expenses";
 
    // Expenses Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_INOUT = "inout";
    private static final String KEY_DATE = "datetime";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_ADDIT = "additional";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INOUT + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + KEY_AMOUNT + " DECIMAL(10,5)," + KEY_ADDIT + " TEXT" + ")";
        db.execSQL(CREATE_EXPENSE_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new expense
    void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_INOUT, expense.getInout()); // In/Out
        values.put(KEY_DATE, expense.getDate()); // Date
        values.put(KEY_AMOUNT, expense.getAmount()); // Amount
        values.put(KEY_ADDIT, expense.getAdditional()); //Additional informations
 
        // Inserting Row
        db.insert(TABLE_EXPENSES, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single expense
    Expense getExpense(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_EXPENSES, new String[] { KEY_ID,
                KEY_INOUT, KEY_DATE, KEY_AMOUNT, KEY_ADDIT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Expense exp = new Expense(
    		Integer.parseInt(cursor.getString(0)),
    		Integer.parseInt(cursor.getString(1)),
    		cursor.getString(2),
    		Double.parseDouble(cursor.getString(3)),
    		cursor.getString(4)
    	);
        
        //Close database connection
        cursor.close();
        db.close();
        // return expense
        return exp;
    }
     
    // Getting all expenses
    public ArrayList<Expense> getAllExpenses() {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        // Select All Query
        String selectQuery = "SELECT * FROM "+TABLE_EXPENSES+" ORDER BY "+KEY_DATE+" DESC";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(Integer.parseInt(cursor.getString(0)));
                expense.setInout(Integer.parseInt(cursor.getString(1)));
                expense.setDate(cursor.getString(2));
                expense.setAmount(Double.parseDouble(cursor.getString(3)));
                expense.setAdditional(cursor.getString(4));
                // Adding expense to list
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        //Close database connection
        cursor.close();
        db.close();
        // return expense list
        return expenseList;
    }
    
    // Getting expenses grouped by year and month
    public ArrayList<Expense> getExpensesGrouped() {
        ArrayList<Expense> expenseList = new ArrayList<Expense>();
        // Select All Query
        String selectQuery = "SELECT SUM("+KEY_AMOUNT+") AS sumamount, * FROM "+TABLE_EXPENSES+" GROUP BY strftime('%Y-%m',"+KEY_DATE+") ORDER BY "+KEY_DATE+" DESC";
 
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setSum(Double.parseDouble(cursor.getString(0)));
                expense.setID(Integer.parseInt(cursor.getString(1)));
                expense.setInout(Integer.parseInt(cursor.getString(2)));
                expense.setDate(cursor.getString(3));
                expense.setAmount(Double.parseDouble(cursor.getString(4)));
                expense.setAdditional(cursor.getString(5));
                // Adding expense to list
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        //Close database connection
        cursor.close();
        db.close();
        // return expense list
        return expenseList;
    }
    
    // Getting expenses by date
    public ArrayList<Expense> getExpensesByDate(String date) {
		ArrayList<Expense> expenseList = new ArrayList<Expense>();
		String dt = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy MMMMM");
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM");
		try {
			Date dateStr = formatter.parse(date);
			dt = output.format(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String selectQuery = "SELECT * FROM "+TABLE_EXPENSES+" WHERE strftime('%Y-%m',"+KEY_DATE+") = '"+dt+"' ORDER BY "+KEY_DATE+" DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setID(Integer.parseInt(cursor.getString(0)));
                expense.setInout(Integer.parseInt(cursor.getString(1)));
                expense.setDate(cursor.getString(2));
                expense.setAmount(Double.parseDouble(cursor.getString(3)));
                expense.setAdditional(cursor.getString(4));
                // Adding expense to list
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        //Close data
        cursor.close();
        db.close();
        // return expense list
        return expenseList;
    }
 
    // Updating single expense
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_INOUT, expense.getInout());
        values.put(KEY_DATE, expense.getDate());
        values.put(KEY_AMOUNT, expense.getAmount());
        values.put(KEY_ADDIT, expense.getAdditional());
 
        // updating row
        return db.update(TABLE_EXPENSES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(expense.getID()) });
    }
 
    // Deleting single expense
    public void deleteExpense(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, KEY_ID + " = ?",
                new String[] {id});
        db.close();
    }
 
 
    // Getting expense Count
    public int getExpensesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXPENSES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}