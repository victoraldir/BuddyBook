package com.quartzodev.buddybook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.fragments.DetailActivityFragment;
import com.quartzodev.utils.Constants;
import com.quartzodev.utils.DialogUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements
        DetailActivityFragment.OnDetailInteractionListener,
        FirebaseDatabaseHelper.OnPaidOperationListener {

    private static final int RC_INSERT_BOOK = 7;

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";
    public static final String ARG_BOOK_JSON = "bookJson";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.detail_coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mUserId;
    private String mFolderId;
    private String mBookId;
    private DetailActivityFragment mFragment;
    private String mFolderListId;
    private String mBookJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE))
            initAdView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        mBookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mUserId = getIntent().getExtras().getString(ARG_USER_ID);
        mFolderListId = getIntent().getExtras().getString(ARG_FOLDER_LIST_ID);
        mBookJson = getIntent().getExtras().getString(ARG_BOOK_JSON);

        mFragment = DetailActivityFragment.newInstance(mUserId, mBookId, mFolderId, mFolderListId, mBookJson);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setFabVisible() {
        mFab.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RC_INSERT_BOOK == requestCode){

            mFragment = DetailActivityFragment.newInstance(mUserId, mBookId, mFolderId, mFolderListId, mBookJson);
            getSupportFragmentManager().popBackStackImmediate();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detail_container, mFragment).commit();

        }else{
            Snackbar.make(mCoordinatorLayout, getString(R.string.annotation_saved), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initAdView() {
        //Ad main
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void expandDescription(View view) {
        mFragment.expandDescription(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_edit:
                lauchInsertEditActivity(mBookId);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lauchInsertEditActivity(String bookId) {

        Intent it = new Intent(this, com.quartzodev.inserteditbook.InsertEditBookActivity.class);

        if (bookId != null)
            it.putExtra(InsertEditBookActivity.ARG_BOOK_ID, bookId);

//        it.putExtra(InsertEditBookActivity.ARG_FOLDER_NAME, mFolderName);
        it.putExtra(InsertEditBookActivity.ARG_FOLDER_ID, mFolderId);
        it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(it, RC_INSERT_BOOK);

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
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUserId, mFolderId, bookApi, null);
    }

    @Override
    public void onReturnBook(Book bookApi) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUserId, mFolderId, bookApi);
    }

    public void loadBook(Book book) {
        ((DetailActivityFragment) getSupportFragmentManager().findFragmentById(R.id.detail_container)).loadBook(book);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

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
