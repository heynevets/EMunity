package com.tingyuyeh.a268demo.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
//    public String _userId;
    public int _totalWorkMinutes = 0;
    public String _selfIntroduction;
    public String _idOfActiveProblem = null;
    public List<String> _idOfCompletedProblems = new ArrayList<>();
    public List<String> _idOfFavouriteProblems = new ArrayList<>();
    public List<String> _idOfProblemsReportedByMe = new ArrayList<>();
    public Map<String, Integer> _voteStatusForEachProblem = new HashMap<>();
    public String _imageUri = "";
    public String _thumbnail = "";
    public Object _startTimeStamp;
    public User(String selfIntroduction) {
        _selfIntroduction = selfIntroduction;
//        _totalWorkMinutes = 0;
//        _idOfActiveProblem = null;
//        _IdOfCompletedProblems = new ArrayList<>();
//        _IdOfFavouriteProblems = new ArrayList<>();
//        _IdOfProblemsReportedByMe = new ArrayList<>();
//        _voteStatusForEachProblem = new HashMap<>();
    }
    public User() {
    }
}
