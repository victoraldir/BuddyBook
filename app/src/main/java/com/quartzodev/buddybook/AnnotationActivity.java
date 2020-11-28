package com.quartzodev.buddybook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.quartzodev.data.FirebaseDatabaseHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AnnotationActivity extends AppCompatActivity {

    public static final String ARG_FOLDER_ID = "argFolderId";
    public static final String ARG_BOOK_ID = "argBookId";
    public static final String ARG_BOOK_TITLE = "argBookTitle";
    public static final String ARG_CONTENT = "argContent";

    @BindView(R.id.editText_content)
    EditText mContentEditText;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public String mBookId;
    public String mFolderId;
    public String mContent;
    public String mBookTitle;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mContent = getIntent().getExtras().getString(ARG_CONTENT);
        mBookTitle = getIntent().getExtras().getString(ARG_BOOK_TITLE);


        getSupportActionBar().setTitle(getString(R.string.annotations));
        getSupportActionBar().setSubtitle(mBookTitle);

        mContentEditText.setText(mContent);

        int position = mContentEditText.length();

        Editable etext = mContentEditText.getText();
        Selection.setSelection(etext, position);

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                setResultContent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResultContent();
    }

    private void setResultContent() {
        Intent resultIntent = new Intent();
        mContent = mContentEditText.getText().toString().trim();
        resultIntent.putExtra(ARG_CONTENT, mContent);
        mFirebaseDatabaseHelper.updateBookAnnotation(mFolderId,
                mBookId,
                mContent);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
