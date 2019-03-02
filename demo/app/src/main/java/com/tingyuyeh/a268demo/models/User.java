package com.tingyuyeh.a268demo.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
//    public String _userId;
    public int _totalWorkMinutes;
    public String _selfIntroduction;
    public String _idOfActiveProblem;
    public List<String> _IdOfCompletedProblems;
    public List<String> _IdOfFavouriteProblems;
    public List<String> _IdOfProblemsReportedByMe;
    public Map<String, Integer> _voteStatusForEachProblem;

    public User(String selfIntroduction) {
        _selfIntroduction = selfIntroduction;
        _totalWorkMinutes = 0;
        _idOfActiveProblem = null;
        _IdOfCompletedProblems = new ArrayList<>();
        _IdOfFavouriteProblems = new ArrayList<>();
        _IdOfProblemsReportedByMe = new ArrayList<>();
        _voteStatusForEachProblem = new HashMap<>();
    }
    public User() {}
}
