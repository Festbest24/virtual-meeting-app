package com.webilestudio.vmeet.schedule;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.webilestudio.vmeet.BuildConfig;
import com.webilestudio.vmeet.R;
import com.webilestudio.vmeet.bean.Schedule;
import com.webilestudio.vmeet.firebase_db.DatabaseManager;
import com.webilestudio.vmeet.meeting.MeetingActivity;
import com.webilestudio.vmeet.utils.AppConstants;
import com.webilestudio.vmeet.utils.SharedObjects;
import com.webilestudio.vmeet.utils.SimpleDividerItemDecoration;
import com.webilestudio.vmeet.utils.ViewDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.CLIPBOARD_SERVICE;


public class ScheduleFragment extends Fragment implements DatabaseManager.OnDatabaseDataChanged {

    @BindView(R.id.llError)
    LinearLayout llError;
    @BindView(R.id.rvEvents)
    RecyclerView rvEvents;
    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    @BindView(R.id.txtError) TextView txtError;

    private ArrayList<Schedule> arrSchedule = new ArrayList<>();
    ScheduleAdapter scheduleAdapter;

    SharedObjects sharedObjects ;

    DatabaseManager databaseManager ;

    String[] appPermissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private static final int PERMISSION_REQUEST_CODE = 10001;
    private static final int SETTINGS_REQUEST_CODE = 10002;
    private ViewDialog viewDialog;

    public ScheduleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedObjects = new SharedObjects(getActivity());
        databaseManager = new DatabaseManager(getActivity());
        databaseManager.setDatabaseManagerListener(this);
        getData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkAppPermissions(appPermissions)) {
                requestAppPermissions(appPermissions);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getData(){
        if (sharedObjects.getUserInfo() != null){
            showProgressDialog();
            databaseManager.getScheduleByUser(sharedObjects.getUserInfo().getId());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setScheduleAdapter() {
        dismissProgressDialog();
        if (arrSchedule.size() > 0) {

            Collections.sort(arrSchedule, new Comparator<Schedule>() {

                @Override
                public int compare(Schedule arg0, Schedule arg1) {
                    SimpleDateFormat format = new SimpleDateFormat(AppConstants.DateFormats.DATE_FORMAT_DASH);
                    int compareResult = 0;
                    try {
                        Date arg0Date = format.parse(arg0.getDate());
                        Date arg1Date = format.parse(arg1.getDate());
                        compareResult = arg1Date.compareTo(arg0Date);
//                                            return (arg0Date.getTime() > arg1Date.getTime() ? 1 : -1);
                    } catch (ParseException e) {
                        e.printStackTrace();
//           compareResult = arg0.compareTo(arg1);
                    }
                    return compareResult;
                }
            });

            scheduleAdapter = new ScheduleAdapter(arrSchedule, getActivity());
            rvEvents.setAdapter(scheduleAdapter);
            rvEvents.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

            scheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position, Schedule bean) {
                    startActivity(new Intent(getActivity(),ScheduleMeetingActivity.class)
                    .putExtra(AppConstants.INTENT_BEAN,bean));
                }

                @Override
                public void onDeleteClickListener(int position, Schedule bean) {
                    databaseManager.deleteSchedule(bean);
                }

                @Override
                public void onStartClickListener(int position, Schedule bean) {

                    AppConstants.MEETING_ID = bean.getMeeetingId();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkAppPermissions(appPermissions)) {
                            showMeetingShareDialog();
                        } else {
                            requestAppPermissions(appPermissions);
                        }
                    } else {
                        showMeetingShareDialog();
                    }
                }

                @Override
                public void onShareClickListener(int position, Schedule bean) {
                    String meetingCodeMessage ;
                    meetingCodeMessage = getString(R.string.share_meeting_code, bean.getMeeetingId(),
                            AppConstants.MEETING_SHARE_URL + bean.getMeeetingId()) + "\n" +
                            "Download the app - " + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID ;

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, meetingCodeMessage);
                    startActivity(Intent.createChooser(sharingIntent, "Share meeting id"));
                }
            });

            rvEvents.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
        } else {
            rvEvents.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
        }
    }

    public void showMeetingShareDialog() {
        final Dialog dialogDate = new Dialog(getActivity());
        dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDate.setContentView(R.layout.dialog_meeting_share);
        dialogDate.setCancelable(true);

        Window window = dialogDate.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        wlp.dimAmount = 0.8f;
        window.setAttributes(wlp);
        dialogDate.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView txtMeetingURL = dialogDate.findViewById(R.id.txtMeetingURL);
        ImageView imgCopy = dialogDate.findViewById(R.id.imgCopy);
        txtMeetingURL.setText(AppConstants.MEETING_SHARE_URL + AppConstants.MEETING_ID);

        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", txtMeetingURL.getText().toString());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getActivity(), "Link copied", Toast.LENGTH_SHORT).show();
            }
        });

        txtMeetingURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", txtMeetingURL.getText().toString());
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getActivity(), "Link copied", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnContinue = dialogDate.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDate.dismiss();
                startActivity(new Intent(getActivity(), MeetingActivity.class));
            }
        });

        if (!dialogDate.isShowing()) {
            dialogDate.show();
        }
    }

    @OnClick({R.id.imgAdd})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.imgAdd:
                startActivity(new Intent(getActivity(),ScheduleMeetingActivity.class));
                break;
        }
    }

    @Override
    public void onDataChanged(String url, DataSnapshot dataSnapshot) {
        if (url.equalsIgnoreCase(AppConstants.Table.SCHEDULE)){
            if (ScheduleFragment.this.isVisible()){
                arrSchedule = new ArrayList<>();
                arrSchedule.addAll(databaseManager.getUserSchedule());
                Log.e("getUserSchedule", arrSchedule.size() + " s");
                setScheduleAdapter();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        if (ScheduleFragment.this.isVisible()) {
            arrSchedule = new ArrayList<>();
            setScheduleAdapter();
        }
    }

    public boolean checkAppPermissions(String[] appPermissions) {
        //check which permissions are granted
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        //Ask for non granted permissions
        //            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
        return listPermissionsNeeded.isEmpty();
        // App has all permissions
    }

    private void requestAppPermissions(String[] appPermissions) {
        ActivityCompat.requestPermissions(getActivity(), appPermissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                HashMap<String, Integer> permissionResults = new HashMap<>();
                int deniedCount = 0;

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionResults.put(permissions[i], grantResults[i]);
                        deniedCount++;
                    }
                }
                if (deniedCount == 0) {
                    Log.e("Permissions", "All permissions are granted!");
                    //invoke ur method
                } else {
                    //some permissions are denied
                    for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                        String permName = entry.getKey();
                        int permResult = entry.getValue();
                        //permission is denied and never asked is not checked
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permName)) {
                            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                            materialAlertDialogBuilder.setMessage(getString(R.string.permission_msg));
                            materialAlertDialogBuilder.setCancelable(false)
                                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setPositiveButton(getString(R.string.yes_grant_permission), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            if (!checkAppPermissions(appPermissions)) {
                                                requestAppPermissions(appPermissions);
                                            }
                                        }
                                    });
                            materialAlertDialogBuilder.show();

                            break;
                        } else {//permission is denied and never asked is checked
                            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                            materialAlertDialogBuilder.setMessage(getString(R.string.permission_msg_never_checked));
                            materialAlertDialogBuilder.setCancelable(false)
                                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            openSettings();
                                        }
                                    });
                            materialAlertDialogBuilder.show();

                            break;
                        }

                    }
                }

        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, SETTINGS_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case SETTINGS_REQUEST_CODE:
                Log.e("Settings", "onActivityResult!");
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        if (checkAppPermissions(appPermissions)) {

                        } else {
                            requestAppPermissions(appPermissions);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showProgressDialog() {
        viewDialog = new ViewDialog(getActivity());
        if (!getActivity().isFinishing()) {
            viewDialog.showDialog();
        }
    }

    public void dismissProgressDialog() {
        viewDialog.hideDialog();
    }
    
   /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ((HomeActivity) getActivity()).openDrawer();
                return true;
            case R.id.action_notification:
//                Log.e("Notification","clicked");
                return true;
//            case R.id.action_logout:
//                ((MainActivity)getActivity()).removeAllPreferenceOnLogout();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
