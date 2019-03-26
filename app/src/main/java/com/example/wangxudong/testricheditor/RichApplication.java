package com.example.wangxudong.testricheditor;

import android.app.Application;

import com.example.wangxudong.testricheditor.utils.SharedPreferencesMgr;

public class RichApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesMgr.init(this,"edit_text");
    }
}
