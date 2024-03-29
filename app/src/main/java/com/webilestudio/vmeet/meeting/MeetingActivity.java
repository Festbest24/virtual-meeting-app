package com.webilestudio.vmeet.meeting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;
import com.webilestudio.vmeet.bean.MeetingHistory;
import com.webilestudio.vmeet.bean.UserBean;
import com.webilestudio.vmeet.firebase_db.DatabaseManager;
import com.webilestudio.vmeet.utils.AppConstants;
import com.webilestudio.vmeet.utils.SharedObjects;

import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MeetingActivity extends FragmentActivity implements JitsiMeetActivityInterface {

    private JitsiMeetView view;
    SharedObjects sharedObjects ;

    DatabaseManager mDatabaseManager;

    MeetingHistory meetingHistory = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new JitsiMeetView(this);

        sharedObjects = new SharedObjects(MeetingActivity.this);
        mDatabaseManager = new DatabaseManager(MeetingActivity.this);

        UserBean userBean = null;
        if (sharedObjects.getUserInfo() != null){
            userBean = sharedObjects.getUserInfo();
            meetingHistory = new MeetingHistory();
            meetingHistory.setId(mDatabaseManager.getKeyForMeetingHistory());
            meetingHistory.setUserId(sharedObjects.getUserInfo().getId());
            meetingHistory.setMeeting_id(AppConstants.MEETING_ID);
        }

        JitsiMeetUserInfo jitsiMeetUserInfo = new JitsiMeetUserInfo();
        if (userBean != null){
            jitsiMeetUserInfo.setDisplayName(userBean.getName());
            try {
                if (!TextUtils.isEmpty(userBean.getProfile_pic())){
                    jitsiMeetUserInfo.setAvatar(new URL(userBean.getProfile_pic()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            jitsiMeetUserInfo.setDisplayName(AppConstants.NAME);
        }

        JitsiMeetConferenceOptions options = null;
        try {
            options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(AppConstants.MEETING_URL))
                    .setRoom(AppConstants.MEETING_ID)
                    .setUserInfo(jitsiMeetUserInfo)
                    .setFeatureFlag("invite.enabled", false)
                    .setFeatureFlag("pip.enabled", true)
                    .setFeatureFlag("recording.enabled",false)
                    .setFeatureFlag("live-streaming.enabled",false)
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (meetingHistory != null) {
            meetingHistory.setSubject(options.getSubject());
        }
        view.join(options);

        view.setListener(new JitsiMeetViewListener() {
            @Override
            public void onConferenceJoined(Map<String, Object> map) {
//                setTimerToLeave();
                Log.e("Conference", " Joined: " + " "+ new Gson().toJson(map));
                if (meetingHistory != null){
                    meetingHistory.setStartTime(SharedObjects.getTodaysDate(AppConstants.DateFormats.DATETIME_FORMAT_24));
                    meetingHistory.setEndTime("");
                    Log.e("meetingHistory", new Gson().toJson(meetingHistory));
                    saveMeetingDetails();
                }
            }

            @Override
            public void onConferenceTerminated(Map<String, Object> map) {
                Log.e("Conference", " terminated: " + "data");
                if (meetingHistory != null) {
                    if (TextUtils.isEmpty(meetingHistory.getStartTime())){
                        meetingHistory.setStartTime(SharedObjects.getTodaysDate(AppConstants.DateFormats.DATETIME_FORMAT_24));
                    }
                    meetingHistory.setEndTime(SharedObjects.getTodaysDate(AppConstants.DateFormats.DATETIME_FORMAT_24));
                    updateMeetingDetails();
                }
                onBackPressed();
            }

            @Override
            public void onConferenceWillJoin(Map<String, Object> map) {
            }
        });

        setContentView(view);

    }

    public void setTimerToLeave(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.leave();
            }
        },30000);
    }

    private void saveMeetingDetails() {
        mDatabaseManager.addMeetingHistory(meetingHistory);
    }
    private void updateMeetingDetails() {
        mDatabaseManager.updateMeetingHistory(meetingHistory);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view.dispose();
        view = null;

        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
    }
}