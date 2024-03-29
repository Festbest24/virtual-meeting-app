package com.webilestudio.vmeet.home;

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
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.webilestudio.vmeet.R;
import com.webilestudio.vmeet.bean.MeetingHistory;
import com.webilestudio.vmeet.bean.UserBean;
import com.webilestudio.vmeet.firebase_db.DatabaseManager;
import com.webilestudio.vmeet.meeting.MeetingActivity;
import com.webilestudio.vmeet.meeting_history.MeetingHistoryAdapter;
import com.webilestudio.vmeet.schedule.ScheduleMeetingActivity;
import com.webilestudio.vmeet.utils.AppConstants;
import com.webilestudio.vmeet.utils.SharedObjects;
import com.webilestudio.vmeet.utils.SimpleDividerItemDecoration;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
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

public class HomeFragment extends Fragment implements DatabaseManager.OnDatabaseDataChanged {

    private SharedObjects sharedObjects;
    @BindView(R.id.txtUserName) TextView txtUserName;
    @BindView(R.id.imgUser)
    CircularImageView imgUser;

    @BindView(R.id.llJoin) LinearLayout llJoin;
    @BindView(R.id.llSchedule) LinearLayout llSchedule;
    @BindView(R.id.llNewMeeting) LinearLayout llNewMeeting;

    UserBean userBean;

    private ArrayList<MeetingHistory> arrMeetingHistory = new ArrayList<>();
    MeetingHistoryAdapter meetingHistoryAdapter;
    DatabaseManager databaseManager ;
    private DatabaseReference databaseReferenceMeetingHistory;

    @BindView(R.id.llError)
    LinearLayout llError;
    @BindView(R.id.rvHistory)
    RecyclerView rvHistory;

    @BindView(R.id.txtError) TextView txtError;

    String[] appPermissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private static final int PERMISSION_REQUEST_CODE = 10001;
    private static final int SETTINGS_REQUEST_CODE = 10002;

    private AdView adView;
    InterstitialAd mInterstitialAd;
    private ViewDialog viewDialog;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedObjects = new SharedObjects(getActivity());
        databaseManager = new DatabaseManager(getActivity());
        databaseManager.setDatabaseManagerListener(this);
        setUserData();

        databaseReferenceMeetingHistory = FirebaseDatabase.getInstance().getReference(AppConstants.Table.MEETING_HISTORY);

        rvHistory.setNestedScrollingEnabled(false);

        adView = view.findViewById(R.id.adView);

        if (AppConstants.showAds){
           /* mInterstitialAd = new InterstitialAd(getActivity()) {
                @Nullable
                @Override
                public FullScreenContentCallback getFullScreenContentCallback() {
                    return null;
                }

                @Nullable
                @Override
                public OnPaidEventListener getOnPaidEventListener() {
                    return null;
                }

                @NonNull
                @Override
                public ResponseInfo getResponseInfo() {
                    return null;
                }

                @NonNull
                @Override
                public String getAdUnitId() {
                    return null;
                }

                @Override
                public void setFullScreenContentCallback(@Nullable FullScreenContentCallback fullScreenContentCallback) {

                }

                @Override
                public void setImmersiveMode(boolean b) {

                }

                @Override
                public void setOnPaidEventListener(@Nullable OnPaidEventListener onPaidEventListener) {

                }

                @Override
                public void show(@NonNull Activity activity) {

                }
            };*/

            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(getActivity(), getString(R.string.vmeet_interstitial), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    showInterstitial();
                                }
                            }, 5000);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            mInterstitialAd = null;
                        }
                    });


            // set the ad unit ID
          //  mInterstitialAd.setAdUnitId(getString(R.string.vmeet_interstitial));

            bindAdvtView();
        }else{
            adView.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkAppPermissions(appPermissions)) {
                requestAppPermissions(appPermissions);
            }
        }

        return view;
    }

    private  void loadInterstitial(){
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("23F1C653C3AF44D748738885C1F91FDA")
                .build();

        // Load ads into Interstitial Ads
       // mInterstitialAd.load(adRequest);

        InterstitialAd.load(getActivity(), getString(R.string.vmeet_interstitial), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        showInterstitial();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

      /*  mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });*/
    }

    private void showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        }
    }

    private void bindAdvtView() {

        if (SharedObjects.isNetworkConnected(getActivity())) {
            final AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice("23F1C653C3AF44D748738885C1F91FDA")
                    .build();

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public int hashCode() {
                    return super.hashCode();
                }

                @Override
                public boolean equals(@Nullable Object obj) {
                    return super.equals(obj);
                }

                @NonNull
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @NonNull
                @Override
                public String toString() {
                    return super.toString();
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            });

           /* adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    adView.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
//                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });*/
            adView.loadAd(adRequest);
            //adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    boolean isMeetingExist = false;

    private boolean checkMeetingExists(final String meeting_id) {
        isMeetingExist = false;
        Query query = databaseReferenceMeetingHistory.orderByChild("meeting_id").equalTo(meeting_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.e("Meeting", "exists");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getValue(MeetingHistory.class).getMeeting_id().equals(meeting_id)) {
                            isMeetingExist = true;
                        }
                    }
                }else{
                    isMeetingExist = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isMeetingExist = false;
            }
        });
        return isMeetingExist;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setUserData() {
        userBean = sharedObjects.getUserInfo();
        if (userBean != null) {
            if (!TextUtils.isEmpty(userBean.getProfile_pic())) {
                Picasso.get().load(userBean.getProfile_pic())
                        .error(R.drawable.avatar).into(imgUser);
            } else {
                imgUser.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avatar));
            }

            if (!TextUtils.isEmpty(userBean.getName())) {
                txtUserName.setText("Hi, " + userBean.getName());
            } else {
                txtUserName.setText("Hi, ");
            }
            databaseManager.getMeetingHistoryByUser(sharedObjects.getUserInfo().getId());
        } else {
            txtUserName.setText("Hi, ");
            imgUser.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avatar));
        }
    }

    @Override
    public void onDataChanged(String url, DataSnapshot dataSnapshot) {
        if (url.equalsIgnoreCase(AppConstants.Table.MEETING_HISTORY)){
            if (HomeFragment.this.isVisible()){
                arrMeetingHistory = new ArrayList<>();
                if (databaseManager.getUserMeetingHistory().size() > 0){
                    for (int i = 0; i < databaseManager.getUserMeetingHistory().size(); i++) {
                        MeetingHistory bean = databaseManager.getUserMeetingHistory().get(i);
                        if (!TextUtils.isEmpty(bean.getStartTime())){
                            String date = SharedObjects.convertDateFormat(bean.getStartTime()
                                    ,AppConstants.DateFormats.DATETIME_FORMAT_24,AppConstants.DateFormats.DATE_FORMAT_DD_MMM_YYYY);
                            if (date.equalsIgnoreCase(SharedObjects.getTodaysDate(AppConstants.DateFormats.DATE_FORMAT_DD_MMM_YYYY))){
                                arrMeetingHistory.add(bean);
                            }
                        }
                    }
                }
                setMeetingHistoryAdapter();
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        arrMeetingHistory = new ArrayList<>();
        setMeetingHistoryAdapter();
    }

    private void setMeetingHistoryAdapter() {
        if (arrMeetingHistory.size() > 0) {

            Collections.sort(arrMeetingHistory, new Comparator<MeetingHistory>() {

                @Override
                public int compare(MeetingHistory arg0, MeetingHistory arg1) {
                    SimpleDateFormat format = new SimpleDateFormat(
                            AppConstants.DateFormats.DATETIME_FORMAT_24);
                    int compareResult = 0;
                    try {
                        Date arg0Date = format.parse(arg0.getStartTime());
                        Date arg1Date = format.parse(arg1.getStartTime());
                        compareResult = arg1Date.compareTo(arg0Date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return compareResult;
                }
            });

            meetingHistoryAdapter = new MeetingHistoryAdapter(arrMeetingHistory, getActivity());
            rvHistory.setAdapter(meetingHistoryAdapter);
            rvHistory.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

            meetingHistoryAdapter.setOnItemClickListener(new MeetingHistoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position, MeetingHistory bean) {
                }

                @Override
                public void onDeleteClickListener(int position, MeetingHistory bean) {
                    databaseManager.deleteMeetingHistory(bean);
                }

                @Override
                public void onJoinClickListener(int position, MeetingHistory bean) {
                    AppConstants.MEETING_ID = bean.getMeeting_id();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkAppPermissions(appPermissions)) {
                            startActivity(new Intent(getActivity(), MeetingActivity.class));
                        } else {
                            requestAppPermissions(appPermissions);
                        }
                    } else {
                        startActivity(new Intent(getActivity(), MeetingActivity.class));
                    }
                }
            });

            rvHistory.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
        } else {
            rvHistory.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.llJoin, R.id.llNewMeeting, R.id.llSchedule})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.llJoin:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAppPermissions(appPermissions)) {
                        showMeetingCodeDialog();
                    } else {
                        requestAppPermissions(appPermissions);
                    }
                } else {
                    showMeetingCodeDialog();
                }
                break;
            case R.id.llNewMeeting:
                if (AppConstants.showAds) {
                    loadInterstitial();
                }
                AppConstants.MEETING_ID = AppConstants.getMeetingCode();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkAppPermissions(appPermissions)) {
                        showMeetingShareDialog();
                    } else {
                        requestAppPermissions(appPermissions);
                    }
                } else {
                    showMeetingShareDialog();
                }
                break;
            case R.id.llSchedule:
                startActivity(new Intent(getActivity(), ScheduleMeetingActivity.class));
                break;
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

    TextInputLayout inputLayoutCode, inputLayoutName;
    TextInputEditText edtCode, edtName;

    public void showMeetingCodeDialog() {
        final Dialog dialogDate = new Dialog(getActivity());
        dialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDate.setContentView(R.layout.dialog_meeting_code);
        dialogDate.setCancelable(true);

        Window window = dialogDate.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        wlp.dimAmount = 0.8f;
        window.setAttributes(wlp);
        dialogDate.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        inputLayoutCode = dialogDate.findViewById(R.id.inputLayoutCode);
        inputLayoutName = dialogDate.findViewById(R.id.inputLayoutName);
        edtCode = dialogDate.findViewById(R.id.edtCode);
        edtName = dialogDate.findViewById(R.id.edtName);

        inputLayoutName.setEnabled(false);
        edtName.setEnabled(false);

        if (!TextUtils.isEmpty(sharedObjects.getUserInfo().getName())){
            edtName.setText(sharedObjects.getUserInfo().getName());
        }

        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayoutCode.setErrorEnabled(false);
                inputLayoutCode.setError("");
                /*if (charSequence.length() == 11){
                    checkMeetingExists(charSequence.toString());
                }else{
                    isMeetingExist = false;
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayoutName.setErrorEnabled(false);
                inputLayoutName.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Button btnAdd = dialogDate.findViewById(R.id.btnAdd);
        Button btnCancel = dialogDate.findViewById(R.id.btnCancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edtCode.getText().toString().trim())) {
                    inputLayoutCode.setErrorEnabled(true);
                    inputLayoutCode.setError(getString(R.string.errMeetingCode));
                    return;
                }
                if (edtCode.getText().toString().length() < 9) {
                    inputLayoutCode.setErrorEnabled(true);
                    inputLayoutCode.setError(getString(R.string.errMeetingCodeInValid));
                    return;
                }
                /*if (!isMeetingExist){
                    AppConstants.showAlertDialog(getResources().getString(R.string.meeting_not_exist),getActivity());
                    return;
                }*/
                if (TextUtils.isEmpty(edtName.getText().toString().trim())) {
                    inputLayoutName.setErrorEnabled(true);
                    inputLayoutName.setError(getString(R.string.err_name));
                    return;
                }
                AppConstants.MEETING_ID = edtCode.getText().toString().trim();
                AppConstants.NAME = edtName.getText().toString().trim();

                dialogDate.dismiss();

                startActivity(new Intent(getActivity(), MeetingActivity.class));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDate.dismiss();
            }
        });

        if (!dialogDate.isShowing()) {
            dialogDate.show();
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
        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        // App has all permissions
        return true;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

}
