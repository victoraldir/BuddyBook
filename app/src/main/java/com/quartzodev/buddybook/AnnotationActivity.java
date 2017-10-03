package com.quartzodev.buddybook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AnnotationActivity extends AppCompatActivity {

    public static final String ARG_FOLDER_ID = "argFolderId";
    public static final String ARG_BOOK_ID = "argBookId";
    public static final String ARG_CONTENT_ID = "argContentId";

    @BindView(R.id.editText_content)
    EditText mContentEditText;

    public String mBookId;
    public String mFolderId;
    public String mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mBookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mContent = getIntent().getExtras().getString(ARG_CONTENT_ID);

        String fakeText = getString(R.string.lorem_ipsum_large);

        mContentEditText.setText(fakeText);

        int position = mContentEditText.length();

        Editable etext = mContentEditText.getText();
        Selection.setSelection(etext, position);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // back button
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
