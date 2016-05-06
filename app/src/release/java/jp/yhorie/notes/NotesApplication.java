package com.example.yhorie.template;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

public class NotesApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    setDebugTools();
    FlowManager.init(this);
  }

  private void setDebugTools() {
    // do nothing in release build
  }
}