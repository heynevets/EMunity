package com.tingyuyeh.a268demo.models;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    public String _problemId;
    public String _userIdOfReporter;
    public List<Double> _GPS;
    public int _ratings;
    public String _title;
    public String _description;
    public String _imageUri;
    public String _thumbnail;
    public Problem(String problemId, String userId, Double[] GPS, String title, String description, String imageUri, String thumbnail) {
        _problemId = problemId;
        _userIdOfReporter = userId;
        _GPS = new ArrayList<>();
        _GPS.add(GPS[0]);
        _GPS.add(GPS[1]);
        _title = title;
        _description = description;
        _ratings = 0;
        _imageUri = imageUri;
        _thumbnail = thumbnail;
    }
    public Problem() {}
//    public User getReporter() {
//        // Fetch user use _userIdOfReporter
//
//    }

}




