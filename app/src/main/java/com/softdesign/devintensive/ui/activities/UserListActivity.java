package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.fragments.RetainedFragment;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    @BindView(R.id.coordinator_container_list)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar_list)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer_list)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.user_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.navigation_view_list)
    NavigationView mNavigationView;

    private ImageView mAvatar;
    private TextView mUserName, mUserEmailMailTxt;

    private RetainedFragment mFragment;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<UserListRes.UserData> mUsers;
    private List<UserListRes.UserData> mFilteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);

        View headerView = mNavigationView.getHeaderView(0);
        mAvatar = ButterKnife.findById(headerView, R.id.user_avatar);
        mUserName = ButterKnife.findById(headerView, R.id.user_name_txt);
        mUserEmailMailTxt = ButterKnife.findById(headerView, R.id.user_email_txt);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        android.app.FragmentManager fm = getFragmentManager();
        mFragment = (RetainedFragment) fm.findFragmentByTag(ConstantManager.RETAIN_FRAGMENT_KEY);

        if (mFragment == null) {
            mFragment = new RetainedFragment();
            fm.beginTransaction()
                    .add(mFragment, ConstantManager.RETAIN_FRAGMENT_KEY)
                    .commit();
            loadUsers();
        } else {
            mUsers = mFragment.getUserData();
            setUsersAdapter();
        }

        setupToolbar();
        setupDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        mFilteredList = new ArrayList<>();

        for (UserListRes.UserData user : mUsers) {
            final String text = user.getFullName().toLowerCase();
            if (text.contains(newText)) {
                mFilteredList.add(user);
            }
        }

        mUsersAdapter.setData(mFilteredList);
        mUsersAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragment.setUserData(mUsers);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setUsersAdapter() {
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                UserDTO userDTO = new UserDTO(mFilteredList.get(position));

                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
            }
        });

        mRecyclerView.setAdapter(mUsersAdapter);
    }

    private void loadUsers() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            showProgress();
            Call<UserListRes> call = mDataManager.getUserList();
            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    if (response.code() == 200) {
                        try {
                            hideProgress();
                            mUsers = response.body().getData();
                            mFilteredList = mUsers;
                            setUsersAdapter();

                        } catch (NullPointerException e) {
                            hideProgress();
                            showSnackbar("Что-то пошло не так");
                        }
                    } else if (response.code() == 401) {
                        mDataManager.getPreferencesManager().saveAuthToken("");
                        mDataManager.getPreferencesManager().saveUserId("");
                        Intent intent = new Intent(UserListActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        hideProgress();
                        showSnackbar("Сервер недоступен");
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                    hideProgress();
                    showSnackbar("Ошибка запроса к серверу");
                }
            });
        } else {
            showSnackbar("Сеть недоступна");
        }
    }

    private void setupDrawer() {
        initNavigationViewInfo();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                              @Override
                                                              public boolean onNavigationItemSelected(MenuItem item) {
                                                                  switch (item.getItemId()){
                                                                      case R.id.user_profile_menu:
                                                                          Intent myProfileIntent = new Intent(UserListActivity.this, MainActivity.class);
                                                                          startActivity(myProfileIntent);
                                                                          break;
                                                                      case R.id.users_menu:

                                                                          break;
                                                                      case R.id.exit_menu:
                                                                          mDataManager.getPreferencesManager().saveAuthToken("");
                                                                          mDataManager.getPreferencesManager().saveUserId("");
                                                                          Intent exitIntent = new Intent(UserListActivity.this, LoginActivity.class);
                                                                          startActivity(exitIntent);
                                                                          break;
                                                                  }
                                                                  item.setChecked(true);
                                                                  mNavigationDrawer.closeDrawer(GravityCompat.START);
                                                                  return false;
                                                              }
                                                          }
        );

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initNavigationViewInfo() {
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .placeholder(R.drawable.userphoto)
                .transform(new CircleTransform())
                .into(mAvatar);

        mUserName.setText((CharSequence) mDataManager.getPreferencesManager().loadUserName());
        mUserEmailMailTxt.setText(mDataManager.getPreferencesManager().loadUserProfileData().get(1));

    }
}
