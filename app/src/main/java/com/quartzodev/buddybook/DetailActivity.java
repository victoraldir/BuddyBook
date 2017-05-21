package com.quartzodev.buddybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.quartzodev.fragments.DetailActivityFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";
    public static final String ARG_BOOK_JSON = "bookJson";
    public static final String ARG_FLAG_IS_LENT_BOOK = "isLentBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String bookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        String folderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        String userId = getIntent().getExtras().getString(ARG_USER_ID);
        String folderListId = getIntent().getExtras().getString(ARG_FOLDER_LIST_ID);
        String bookJson = getIntent().getExtras().getString(ARG_BOOK_JSON);
        boolean isLent = getIntent().getExtras().getBoolean(ARG_FLAG_IS_LENT_BOOK);

        DetailActivityFragment newFragment = DetailActivityFragment.newInstance(userId, bookId, folderId, folderListId, bookJson,isLent);

        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.detail_container, newFragment).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
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
}
