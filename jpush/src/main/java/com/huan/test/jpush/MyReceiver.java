package com.huan.test.jpush;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.huan.test.jpush.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            processIDMessage(context,regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
            processNotifyClick(context);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e("liao","mymy message:"+message+extras);
//        Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//        msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//        if (extras != null && !"".equals(extras)) {
//            try {
//                JSONObject extraJson = new JSONObject(extras);
//                if (null != extraJson && extraJson.length() > 0) {
//                    msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                }
//            } catch (JSONException e) {
//
//            }
//
//        }
//        context.sendBroadcast(msgIntent);
    }

    private void processIDMessage(Context context, String  id) {
        Log.e("liao","mymy id:"+id);
//        Intent msgIntent = new Intent(MainActivity.ID_RECEIVED_ACTION);
//        msgIntent.putExtra(MainActivity.ID, id);
//        context.sendBroadcast(msgIntent);
    }

    private void processNotifyMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e("liao","mymy message:"+message+extras);
//        Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//        msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//        if (extras != null && !"".equals(extras)) {
//            try {
//                JSONObject extraJson = new JSONObject(extras);
//                if (null != extraJson && extraJson.length() > 0) {
//                    msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                }
//            } catch (JSONException e) {
//
//            }
//
//        }
//        context.sendBroadcast(msgIntent);
    }

    private void processNotifyClick(Context context) {
        Log.e("liao","mymy click:");
        try {
            String appPackageName = "com.sobey.tvcust";
            boolean runningApp = AppUtils.isRunningApp(context, appPackageName);
            if (!runningApp){
                AppUtils.startAPP(context,appPackageName);
            }
        }catch (Exception e){
            Log.e("startapp","启动app失败");
            e.printStackTrace();
        }

    }


}
