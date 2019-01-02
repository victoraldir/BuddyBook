package com.quartzodev.inserteditbook;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.quartzodev.buddybook.GlideApp;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.VolumeInfo;
import com.quartzodev.utils.AnimationUtils;
import com.quartzodev.utils.DialogUtils;

import java.io.File;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class InsertEditBookActivity extends AppCompatActivity implements InsertEditBookContract.View {

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String ARG_USER_ID = "userId";
    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "argFolderId";
    public static final String ARG_PHOTO_PATH = "photoPath";
    public static final String ARG_FLAG_MORE = "flagMore";

    private static final int RC_PERMISSION = 1;
    private static final int RC_IMAGE_PICK = 2;
    private static final int RC_CAMERA = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container_no_picture)
    FrameLayout mPhotoNoImage;
    @BindView(R.id.insert_imageview_thumb)
    ImageView mPhoto;
    @BindView(R.id.container_more_fields)
    LinearLayout mMoreFieldsContainer;
    @BindView(R.id.btn_more_fields)
    TextView btnMoreFields;
    @BindView(R.id.insert_container)
    ScrollView mInsertContainer;
    @BindView(R.id.insert_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.loading_picture)
    ProgressBar mLoadingPhotoProgressBar;
    @BindView(R.id.insert_edit_editext_title)
    TextInputEditText mTitle;
    @BindView(R.id.insert_edit_editext_author)
    TextInputEditText mAuthor;
    @BindView(R.id.insert_edit_editext_publisher)
    TextInputEditText mPublisher;
    @BindView(R.id.insert_edit_editext_number_pages)
    TextInputEditText mNumberPages;
    @BindView(R.id.insert_edit_editext_language)
    TextInputEditText mLanguage;
    @BindView(R.id.insert_edit_editext_print_type)
    TextInputEditText mPrintType;
    @BindView(R.id.insert_edit_editext_isbn10)
    TextInputEditText mIsbn10;
    @BindView(R.id.insert_edit_editext_isbn13)
    TextInputEditText mIsbn13;
    @BindView(R.id.insert_edit_editext_description)
    TextInputEditText mDescription;
    @BindView(R.id.insert_edit_editext_annotation)
    TextInputEditText mAnnotation;

    private InsertEditBookContract.Presenter mPresenter;
    private String mPicturePath;

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new Answers());
        setContentView(R.layout.activity_insert_book);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.insert_new_book));

        setupPresenter(savedInstanceState);

        btnMoreFields.setOnClickListener(onMoreFieldsListener);
        mPhotoNoImage.setOnClickListener(onOpenCameraGalleryListener);
        mPhoto.setOnClickListener(onOpenCameraGalleryListener);
    }

    public void setupPresenter(Bundle savedInstanceState) {

        String mBookId, mUserId, mFolderId;
        boolean isMoreFieldsOpen = false;

        if (savedInstanceState != null) {
            mBookId = savedInstanceState.getString(ARG_BOOK_ID);
            mUserId = savedInstanceState.getString(ARG_USER_ID);
            mFolderId = savedInstanceState.getString(ARG_FOLDER_ID);
            mPicturePath = savedInstanceState.getString(ARG_PHOTO_PATH);
            isMoreFieldsOpen = savedInstanceState.getBoolean(ARG_FLAG_MORE);
        } else {
            mBookId = getIntent().getExtras().getString(ARG_BOOK_ID);
            mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
            mPicturePath = getIntent().getExtras().getString(ARG_PHOTO_PATH);
        }

        mPresenter = new InsertEditBookPresenter(this, mUserId, mFolderId, mBookId, mPicturePath, isMoreFieldsOpen);
        mPresenter.loadForm();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        InsertEditBookPresenter currentPresenter = (InsertEditBookPresenter) mPresenter;

        outState.putString(ARG_PHOTO_PATH, currentPresenter.getBookId() == null ? mPicturePath : currentPresenter.getImagePath());
        outState.putString(ARG_BOOK_ID, currentPresenter.getBookId());
        outState.putString(ARG_FOLDER_ID, currentPresenter.getFolderId());
        outState.putBoolean(ARG_FLAG_MORE, currentPresenter.getFlagFieldsOpen());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void showBook(Book book) {
        VolumeInfo volumeInfo = book.getVolumeInfo();

        mTitle.setText(volumeInfo.getTitle());
        if (volumeInfo.getAuthors() != null)
            mAuthor.setText(volumeInfo.getAuthors().get(0));

        if (volumeInfo.getImageLink() != null && volumeInfo.getImageLink().getThumbnail() != null) {
            loadChosenImage(Uri.parse(volumeInfo.getImageLink().getThumbnail()));
        }

        mDescription.setText(volumeInfo.getDescription());
        mIsbn13.setText(volumeInfo.getIsbn13());
        mIsbn10.setText(volumeInfo.getIsbn10());
        mLanguage.setText(volumeInfo.getLanguage());
        mNumberPages.setText(volumeInfo.getPageCount());
        mPrintType.setText(volumeInfo.getPrintType());
        mPublisher.setText(volumeInfo.getPublisher());
        mAnnotation.setText(book.getAnnotation());
    }

    @Override
    public void showCaptureOptions() {
        final CharSequence[] items = getResources().getTextArray(R.array.capture_list);
        DialogUtils.showMultipleOptionDialog(this, getResources().getTextArray(R.array.capture_list),
                new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals(getString(R.string.camera))) {
                            mPresenter.openCamera();
                        } else {
                            mPresenter.openGallery();
                        }
                    }
                });
    }

    @Override
    public void showNoPictureAvailable() {
        setLoadingPhoto(false);

        mPhoto.setVisibility(View.GONE);
        mPhotoNoImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void launchCameraActivity() {
        ContentValues values = new ContentValues();
        File imagePath = new File(getFilesDir(), "covers");
        if (!imagePath.exists()) imagePath.mkdir();
        File newFile = new File(imagePath, System.currentTimeMillis() + "photo.png");

        Uri picturePath;

        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            picturePath = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
        } else {
            picturePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        mPicturePath = picturePath.toString();

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        it.putExtra(MediaStore.EXTRA_OUTPUT, picturePath);
        startActivityForResult(it, RC_CAMERA);
    }

    @Override
    public void launchGalleryActivity() {
        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent2.setType("image/*");
        startActivityForResult(intent2, RC_IMAGE_PICK);
    }

    @Override
    public void loadChosenImage(Object image) {

        GlideApp.with(this)
                .asBitmap()
                .load(image)
                .placeholder(R.drawable.iconerror)
                .error(R.drawable.iconerror)
                .into(simpleTarget);
    }

    SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {

            mPhoto.setImageBitmap(resource);
            mPhoto.invalidate();

            mPhoto.setVisibility(View.VISIBLE);
            mPhotoNoImage.setVisibility(View.GONE);
            mLoadingPhotoProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            super.onLoadFailed(errorDrawable);
        }
    };


    @Override
    public void expandMoreFields() {

        if (mMoreFieldsContainer.getVisibility() == View.VISIBLE) {
            AnimationUtils.collapse(mMoreFieldsContainer);
        } else {
            btnMoreFields.setVisibility(View.GONE);
            AnimationUtils.expand(mMoreFieldsContainer);
        }
    }

    @Override
    public void setLoading(boolean flag) {
        if (!flag) {
            mInsertContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mInsertContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setLoadingPhoto(boolean flag) {
        if (!flag) {
            mLoadingPhotoProgressBar.setVisibility(View.GONE);
        } else {
            mLoadingPhotoProgressBar.setVisibility(View.VISIBLE);
            mPhotoNoImage.setVisibility(View.GONE);
            mPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
                return true;

            case R.id.action_save_book:
                if (mInsertContainer.getVisibility() == View.VISIBLE) {
                    mPresenter.saveBook(mTitle.getText().toString(),
                            Collections.singletonList(mAuthor.getText().toString()),
                            mIsbn13.getText().toString(),
                            mIsbn10.getText().toString(),
                            mLanguage.getText().toString(),
                            mNumberPages.getText().toString(),
                            mPrintType.getText().toString(),
                            mPublisher.getText().toString(),
                            mDescription.getText().toString(),
                            mAnnotation.getText().toString(),
                            mPicturePath);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setErrorMessage(String msg) {
        mTitle.setError(msg);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestCameraPermission() {
        requestPermissions(PERMISSIONS, RC_PERMISSION);
    }

    @Override
    public boolean hasExternalPermission() {
        for (int x = 0; x < PERMISSIONS.length; x++) {
            if (checkCallingOrSelfPermission(PERMISSIONS[x]) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "All privileges given successfully", Toast.LENGTH_SHORT).show();
            mPresenter.openCameraGallery();
        } else {
            Toast.makeText(this, "Buddybook needs privileges to capture images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_CAMERA) {
                mPresenter.setCameraResult(mPicturePath);
            } else if (requestCode == RC_IMAGE_PICK) {
                mPicturePath = data.getData().toString();
                mPresenter.setGalleryResult(data.getData());
            }
        }
    }

    @Override
    public void finishActivity() {
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }

    @Override
    public void setPresenter(InsertEditBookContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    private View.OnClickListener onMoreFieldsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.clickMoreFields();
        }
    };

    private View.OnClickListener onOpenCameraGalleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mPresenter.openCameraGallery();
        }
    };
}
