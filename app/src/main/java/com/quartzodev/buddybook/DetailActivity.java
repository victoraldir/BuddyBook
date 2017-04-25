package com.quartzodev.buddybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.quartzodev.fragments.DetailActivityFragment;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String bookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        String folderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        String userId = getIntent().getExtras().getString(ARG_USER_ID);
        String folderListId = getIntent().getExtras().getString(ARG_FOLDER_LIST_ID);

        DetailActivityFragment newFragment = DetailActivityFragment.newInstance(userId, bookId, folderId, folderListId);

        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.detail_container, newFragment).commit();
        //transaction.addToBackStack(null);

        //transaction.commit();

    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }
}
