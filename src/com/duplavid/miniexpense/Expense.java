package com.duplavid.miniexpense;

public class Expense {
    
    //private variables
    int _id;
    int _inout;
    String _date;
    Double _amount;
    String _additional;
    Double _sum;
     
    // Empty constructor
    public Expense(){
         
    }
    // constructor
    public Expense(int id, int _inout, String _date, Double _amount, String _additional){
        this._id = id;
        this._inout = _inout;
        this._date = _date;
        this._amount = _amount;
        this._additional = _additional;
    }
     
    // constructor
    public Expense(int _inout, String _date, Double _amount, String _additional){
        this._inout = _inout;
        this._date = _date;
        this._amount = _amount;
        this._additional = _additional;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting ID
    public void setID(int id){
        this._id = id;
    }
     
    // getting in/out value
    public int getInout(){
        return this._inout;
    }
     
    // setting in/out value
    public void setInout(int inout){
        this._inout = inout;
    }
    
    // getting date
    public String getDate(){
        return this._date;
    }
     
    // setting date
    public void setDate(String date){
        this._date = date;
    }
     
    // getting amount
    public Double getAmount(){
        return this._amount;
    }
     
    // setting amount
    public void setAmount(Double amount){
        this._amount = amount;
    }
    
    // getting additional info
    public String getAdditional(){
        return this._additional;
    }
     
    // setting additional info
    public void setAdditional(String additional){
        this._additional = additional;
    }
    
    // getting sum
    public Double getSum(){
        return this._sum;
    }
     
    // setting sum
    public void setSum(Double sum){
        this._sum = sum;
    }
}