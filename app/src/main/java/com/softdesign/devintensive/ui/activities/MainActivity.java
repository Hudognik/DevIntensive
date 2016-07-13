package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.FieldHelper;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private Boolean mCurrentEditMode = false;
    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";
    private DataManager mDataManager;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private String[] selectItems;

    @BindView(R.id.call_img)
    ImageView mCallImg;
    @BindView(R.id.main_coordinator_conteiner)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.phone_et)
    EditText mUserPhone;
    @BindView(R.id.email_et)
    EditText mUserEmail;
    @BindView(R.id.vk_et)
    EditText mUserVK;
    @BindView(R.id.git_et)
    EditText mUserGit;
    @BindView(R.id.bio_et)
    EditText mUserBio;
    @BindView(R.id.user_avatar)
    ImageView mUserAvatar;
    @BindView(R.id.user_name_txt)
    TextView mUserNameTxt;
    @BindView(R.id.user_email_txt)
    TextView mUserEmailTxt;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;

    @BindViews({R.id.phone_et, R.id.email_et, R.id.vk_et, R.id.git_et, R.id.bio_et})
    List<EditText> mUserInfoViews;
    @BindViews({R.id.user_value_code_line, R.id.user_value_code_line, R.id.user_value_project})
    List<TextView> mUserValueViews;

    // Strings
    @BindString(R.string.user_profile_dialog_gallery)
    String userProfileDialogGallery;
    @BindString(R.string.user_profile_dialog_camera)
    String userProfileDialogCamera;
    @BindString(R.string.user_profile_dialog_cancel)
    String userProfileDialogCancel;
    @BindString(R.string.user_profile_dialog_title)
    String userProfileDialogTitle;
    @BindString(R.string.user_profile_choose_message)
    String userProfileChooseMessage;
    @BindString(R.string.err_msg_phone)
    String errPhoneMsg;
    @BindString(R.string.err_msg_email)
    String errEmailMsg;
    @BindString(R.string.err_msg_url)
    String errUrlMsg;

    @BindColor(R.color.transparent)
    int transparent;
    @BindColor(R.color.white)
    int white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();

        selectItems = new String[]{
            userProfileDialogGallery,
            userProfileDialogCamera,
            userProfileDialogCancel
        };

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();

        mUserPhone.addTextChangedListener(new inputTextWatcher(mUserPhone));
        mUserEmail.addTextChangedListener(new inputTextWatcher(mUserEmail));
        mUserVK.addTextChangedListener(new inputTextWatcher(mUserVK));
        mUserGit.addTextChangedListener(new inputTextWatcher(mUserGit));

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .into(mProfileImage);

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .into(mUserAvatar);

        if (savedInstanceState != null) {
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
        }

        changeEditMode(mCurrentEditMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    @OnClick(R.id.fab)
    void toggleEditMode() {
        if (mCurrentEditMode) {
            if (!validatePhone() || !validateEmail() || !validateVK() || !validateGit()) {
                return;
            }
        }

        changeEditMode(!mCurrentEditMode);
    }

    private void changeEditMode(Boolean mode) {
        int imageuri;
        int toolbarColor;

        mCurrentEditMode = mode;

        if (mode) {
            imageuri = R.drawable.ic_done_black_24dp;
            toolbarColor = transparent;

            FieldHelper.requestFocus(mUserPhone, this);
            saveUserFields();
        } else {
            imageuri = R.drawable.ic_create_black_24dp;
            toolbarColor = white;
        }

        mFab.setImageResource(imageuri);
        mCollapsingToolbar.setExpandedTitleColor(toolbarColor);
        setVisibleProfilePlaceHolder(mode);
        setLockedToolbar(mode);

        for (EditText userValue : mUserInfoViews) {
            userValue.setEnabled(mode);
            userValue.setFocusableInTouchMode(mode);
        }
    }

    @OnClick(R.id.profile_placeholder)
    void showDialog() {
        showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
    }

    public void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void setupToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        mUserAvatar.setImageBitmap(getRoundBitmap(R.drawable.userphoto));
        mUserEmailTxt.setText(mUserEmail.getText().toString());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));

        }
    }

    private void saveUserFields() {
        List<String> userData = new ArrayList<>();

        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }

        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private Bitmap getRoundBitmap(int drawableRes) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableRes);

        return RoundedAvatarDrawable.getRoundedBitmap(bitmap);
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, userProfileChooseMessage), ConstantManager.REQUEST_GALERRY_PICTURE);
    }

    private void loadPhotoFromCamera() {
        Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            mPhotoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mPhotoFile != null) {
            takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
        }
    }

    private void setVisibleProfilePlaceHolder(Boolean show) {
        if (show) {
            mProfilePlaceholder.setVisibility(View.VISIBLE);
        } else {
            mProfilePlaceholder.setVisibility(View.GONE);
        }
    }

    private void setLockedToolbar(Boolean lock) {
        if (lock) {
            mAppBarLayout.setExpanded(true, true);
            mAppBarParams.setScrollFlags(0);
        } else {
            mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }

        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImage = null;

            switch (requestCode) {
                case ConstantManager.REQUEST_GALERRY_PICTURE:
                    if (data != null) {
                        selectedImage = data.getData();
                    }
                    break;
                case ConstantManager.REQUEST_CAMERA_PICTURE:
                    if (mPhotoFile != null) {
                        selectedImage = Uri.fromFile(mPhotoFile);
                    }
                    break;
            }

            insertProfileImage(selectedImage);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(userProfileDialogTitle);
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                dialog.cancel();
                                break;
                        }

                        showSnackbar(selectItems[choiceItem]);
                    }
                });

                return builder.create();
            }
        }

        return null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void insertProfileImage(Uri selectedImage) {
        if (selectedImage != null) {
            Picasso.with(this)
                    .load(selectedImage)
                    .into(mProfileImage);

            mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        }
    }

    @OnClick(R.id.call_img)
    void callPhone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(ConstantManager.REQUEST_PHONE_CALL, new String[]{Manifest.permission.CALL_PHONE});
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", mUserPhone.getText().toString(), null));

            startActivity(callIntent);
        }
    }

    @OnClick(R.id.email_img)
    void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mUserEmail.getText().toString(), null));

        startActivity(emailIntent);
    }

    @OnClick(R.id.vk_img)
    void vk() {
        Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("https", mUserVK.getText().toString(), null));

        startActivity(vkIntent);
    }

    @OnClick(R.id.git_img)
    void git() {
        Intent gitIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("https", mUserGit.getText().toString(), null));

        startActivity(gitIntent);
    }

    public void requestPermissions(int requestCode, String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    public Boolean checkPermissions(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ConstantManager.REQUEST_PHONE_CALL:
                if (checkPermissions(grantResults)) {
                    callPhone();
                    Log.d(TAG, "CALL PERMISSION GRANTED");
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class inputTextWatcher implements TextWatcher {

        private View view;

        private inputTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.phone_et:
                    validatePhone();
                    break;
                case R.id.email_et:
                    validateEmail();
                    break;
                case R.id.vk_et:
                    validateVK();
                    break;
                case R.id.git_et:
                    validateGit();
                    break;
            }
        }
    }

    private boolean validatePhone() {
        String phone = mUserPhone.getText().toString().trim();

        if (!FieldHelper.isValidPhone(phone)) {
            mUserPhone.setError(errPhoneMsg);
            FieldHelper.requestFocus(mUserPhone, this);

            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        String email = mUserEmail.getText().toString().trim();

        if (!FieldHelper.isValidEmail(email)) {
            mUserEmail.setError(errEmailMsg);
            FieldHelper.requestFocus(mUserEmail, this);

            return false;
        }

        return true;
    }

    private boolean validateVK() {
        String url = mUserVK.getText().toString().toLowerCase().trim();

        if (!FieldHelper.isValidUrl("vk.com/", url)) {
            mUserVK.setError(errUrlMsg);
            FieldHelper.requestFocus(mUserVK, this);

            return false;
        }

        return true;
    }

    private boolean validateGit() {
        String url = mUserGit.getText().toString().toLowerCase().trim();

        if (!FieldHelper.isValidUrl("github.com/", url)) {
            mUserGit.setError(errUrlMsg);
            FieldHelper.requestFocus(mUserGit, this);

            return false;
        }

        return true;
    }
}
