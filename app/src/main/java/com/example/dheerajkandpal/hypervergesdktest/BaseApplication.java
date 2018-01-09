package com.example.dheerajkandpal.hypervergesdktest;

import android.app.Application;

import co.hyperverge.hyperdocssdk.HyperDocsSDK;

/**
 * Created by dheeraj.kandpal on 11/20/2017.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

       HyperDocsSDK.init(getApplicationContext(), " 5da1bf", "3070510e5a71411cbdba");
    }
    public interface PermissionCallInterface{
        void permissionSuccessCallback();
    }
}
