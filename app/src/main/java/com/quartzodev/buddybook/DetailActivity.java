package com.quartzodev.buddybook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.DetailActivityFragment;
import com.quartzodev.utils.Constants;
import com.quartzodev.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity implements
        DetailActivityFragment.OnDetailInteractionListener,
        FirebaseDatabaseHelper.OnPaidOperationListener {

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";
    public static final String ARG_BOOK_JSON = "bookJson";
    public static final String ARG_FLAG_LEND_OPERATION = "flagLendOperation";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mUserId;
    private DetailActivityFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE))
            initAdView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        final String bookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        final String folderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mUserId = getIntent().getExtras().getString(ARG_USER_ID);
        String folderListId = getIntent().getExtras().getString(ARG_FOLDER_LIST_ID);
        final String bookJson = getIntent().getExtras().getString(ARG_BOOK_JSON);
        boolean flagLendOp = getIntent().getExtras().getBoolean(ARG_FLAG_LEND_OPERATION);

        mFragment = DetailActivityFragment.newInstance(mUserId, bookId, folderId, folderListId, bookJson, flagLendOp);

        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.detail_container, mFragment).commit();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.launchResultActivity();
            }
        });

    }

    public void setFabVisible(){
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Snackbar.make(mCoordinatorLayout, getString(R.string.annotation_saved), Snackbar.LENGTH_SHORT).show();
    }

    private void initAdView(){
        //Ad main
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void expandDescription(View view){
        mFragment.expandDescription(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            supportFinishAfterTransition();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onLendBook(Book bookApi) {
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUserId, bookApi,null);
    }

    @Override
    public void onReturnBook(Book bookApi) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUserId, bookApi);
    }

    public void loadBook(Book book) {
        ((DetailActivityFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container)).loadBook(book);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onInsertBook(boolean success) {
        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        } else {
            Snackbar.make(mCoordinatorLayout, getString(R.string.insert_success), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInsertFolder(boolean success) {
        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        }
    }
}
