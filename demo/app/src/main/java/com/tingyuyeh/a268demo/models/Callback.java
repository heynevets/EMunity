package com.tingyuyeh.a268demo.models;

import java.util.List;

public class Callback {
    public void onSuccess(){}
    public void onSuccess(int activeMinute){}
    public void onSuccess(User cbUser){}
    public void onSuccess(List<Problem> problems){}
    public void onFailure(){}
}
