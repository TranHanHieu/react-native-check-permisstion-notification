package com.example.hieutran.checkPermisstionNotification;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.content.pm.ApplicationInfo;
import android.app.AppOpsManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.util.Log;
import com.facebook.react.bridge.Promise;

public class CheckPermisstionNotificationModule extends ReactContextBaseJavaModule {

    private ReactContext reactContext;
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";


    public CheckPermisstionNotificationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNCheckPermisstionNotificationModule";
    }

    //region React Native Methods
    @ReactMethod
    public void openSettings() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + reactContext.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        reactContext.startActivity(i);
    }
    @ReactMethod
    public void checkPermisstion(final Promise promise) {

        AppOpsManager mAppOps = (AppOpsManager) reactContext.getSystemService(ReactContext.APP_OPS_SERVICE);

        ApplicationInfo appInfo = reactContext.getApplicationInfo();

        String pkg = reactContext.getPackageName();

        int uid = appInfo.uid;

        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

        try {

            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int)opPostNotificationValue.get(Integer.class);
          
            promise.resolve(((int)checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg) == AppOpsManager.MODE_ALLOWED));
                

        } catch (ClassNotFoundException e) {
            promise.reject(e);
        } catch (NoSuchMethodException e) {
            promise.reject(e);
        } catch (NoSuchFieldException e) {
            promise.reject(e);
        } catch (InvocationTargetException e) {
            promise.reject(e);
        } catch (IllegalAccessException e) {
            promise.reject(e);
        }

    }
}
