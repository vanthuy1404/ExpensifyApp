package com.example.expensify;


import android.app.Application;

public class AndroidApplication extends Application {
  public AndroidApplication() {
    super();
    instance = this;
  }

  public static AndroidApplication getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  private static AndroidApplication instance;
}
