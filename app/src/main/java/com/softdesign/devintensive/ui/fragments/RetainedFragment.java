package com.softdesign.devintensive.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.softdesign.devintensive.data.network.res.UserListRes;

import java.util.List;

public class RetainedFragment extends Fragment {

    private List<UserListRes.UserData> mUserData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    public List<UserListRes.UserData> getUserData() {
        return mUserData;
    }

    public void setUserData(List<UserListRes.UserData> userData) {
        mUserData = userData;
    }
}
