package com.tingyuyeh.a268demo.models;

import java.util.List;

public abstract class Callback {

    public void onComplete(boolean success){}
    public void onSuccess(int activeMinute){}
    public void onSuccess(List<Problem> problems){}
    public void onFailure(Exception e){}
}
