package com.quartzodev.buddybook;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.ImageLink;
import com.quartzodev.data.VolumeInfo;
import com.quartzodev.utils.AnimationUtils;

import java.io.File;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class InsertEditBookActivity extends AppCompatActivity implements View.OnClickListener, FirebaseDatabaseHelper.OnPaidOperationListener {

    private static final int RC_CAMERA_PERM = 2;
    private static final int RC_HANDLE_WRITE_PERM = 3;
    private static final int RC_IMAGE_PICK = 4;
    private static final int RC_CAMERA = 5;

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "argFolderId";
    public static final String ARG_FOLDER_NAME = "argFolderName";

    public static final String SAVE_PICTURE = "savePicure";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.insert_imageview_thumb)
    ImageView mPhoto;
    @BindView(R.id.container_no_picture)
    FrameLayout mPhotoNoImage;
    @BindView(R.id.container_more_fields)
    LinearLayout mMoreFieldsContainer;
    @BindView(R.id.btn_more_fields)
    TextView mBtnMoreFields;

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

    @BindView(R.id.insert_container)
    ScrollView mInsertContainer;
    @BindView(R.id.insert_progressbar)
    ProgressBar mProgressBar;

    private String mBookId;
    private String mFolderId;
    private String mFolderName;
    private Uri mPicturePath;
    private String mPictureChosen;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics(), new Answers());

        setContentView(R.layout.activity_insert_book);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mPictureChosen = savedInstanceState.getString(SAVE_PICTURE);
            if (mPictureChosen != null)
                renderImage(mPictureChosen);
        }

        mContext = this;

        mBookId = getIntent().getExtras().getString(ARG_BOOK_ID);
        mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mFolderName = getIntent().getExtras().getString(ARG_FOLDER_NAME);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.insert_new_book));

        if (mFolderId == null) {
            mFolderId = FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;
        }

        if (mFolderName == null) {
            mFolderName = getString(R.string.tab_my_books);
        }

        getSupportActionBar().setSubtitle(String.format(getString(R.string.folder_subtitle), mFolderName));

        mPhoto.setOnClickListener(this);
        mPhotoNoImage.setOnClickListener(this);

        if (mBookId == null) {
            setLoading(false);
        } else {
            loadBook();
        }
    }

    public void loadBook() {

        FirebaseDatabaseHelper.getInstance().findBook(mFolderId, mBookId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setLoading(false);

                if (dataSnapshot.getValue() != null) {

                    Book book = dataSnapshot.getValue(Book.class);

                    populateForm(book);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "Error loading book", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populateForm(Book book) {

        VolumeInfo volumeInfo = book.getVolumeInfo();

        mTitle.setText(volumeInfo.getTitle());
        if (volumeInfo.getAuthors() != null)
            mAuthor.setText(volumeInfo.getAuthors().get(0));

        if (volumeInfo.getImageLink() != null && volumeInfo.getImageLink().getThumbnail() != null)
            renderImage(volumeInfo.getImageLink().getThumbnail());

        mDescription.setText(volumeInfo.getDescription());
        mIsbn13.setText(volumeInfo.getIsbn13());
        mIsbn10.setText(volumeInfo.getIsbn10());
        mLanguage.setText(volumeInfo.getLanguage());
        mNumberPages.setText(volumeInfo.getPageCount());
        mPrintType.setText(volumeInfo.getPrintType());
        mPublisher.setText(volumeInfo.getPublisher());
        mAnnotation.setText(book.getAnnotation());
    }

    private void setLoading(boolean flag) {
        if (!flag) {
            mInsertContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        } else {
            mInsertContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(SAVE_PICTURE, mPictureChosen);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == RC_IMAGE_PICK || requestCode == RC_CAMERA) {

                Object img;

                if (data != null && data.getData() != null) {
                    img = data.getData();
                } else {
                    img = mPicturePath;
                }

                renderImage(img);
            }
        }
    }

    public void renderImage(Object image) {

        mPictureChosen = image.toString();

        GlideApp.with(this)
                .asBitmap()
                .load(image)
                .placeholder(R.drawable.iconerror)
                .error(R.drawable.iconerror)
                .into(simpleTarget);
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
                    saveBook();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callCamera() {
        ContentValues values = new ContentValues();
        File imagePath = new File(getFilesDir(), "covers");
        if (!imagePath.exists()) imagePath.mkdir();
        File newFile = new File(imagePath, System.currentTimeMillis() + "photo.png");

        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            mPicturePath = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", newFile);
        } else {
            mPicturePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        it.putExtra(MediaStore.EXTRA_OUTPUT, mPicturePath);
        startActivityForResult(it, RC_CAMERA);
    }

    private void callPickPhoto() {
        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent2.setType("image/*");
        startActivityForResult(intent2, RC_IMAGE_PICK);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(String permissionName, int permissionRequestCode) {
        requestPermissions(new String[]{permissionName}, permissionRequestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.CAMERA)) {
            requestPermission(Manifest.permission.CAMERA, RC_CAMERA_PERM);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWriteExternalStoragePermission(int requestCode) {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RC_HANDLE_WRITE_PERM) {
                callPickPhoto();
            } else if (requestCode == RC_CAMERA_PERM) {

                int rc = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    callCamera();
                } else {
                    requestWriteExternalStoragePermission(RC_CAMERA_PERM);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        final CharSequence[] items = new CharSequence[2];
        items[0] = getString(R.string.camera);
        items[1] = getString(R.string.gallery);

        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(mContext);
        alertdialog.setTitle(getString(R.string.add_image));
        alertdialog.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.camera))) {

                    int rc = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        callCamera();
                    } else {
                        requestCameraPermission();
                    }

                } else if (items[item].equals(getString(R.string.gallery))) {

                    int rc = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        callPickPhoto();
                    } else {
                        requestWriteExternalStoragePermission(RC_HANDLE_WRITE_PERM);
                    }
                }
            }
        });
        alertdialog.show();

    }

    public void moreFields(View view) {

        if (mMoreFieldsContainer.getVisibility() == View.VISIBLE) {
            AnimationUtils.collapse(mMoreFieldsContainer);
        } else {
            view.setVisibility(View.GONE);
            AnimationUtils.expand(mMoreFieldsContainer);
        }

    }

    private Book buildBookFromForm() {

        Book book = new Book();
        VolumeInfo volumeInfo = new VolumeInfo();
        ImageLink imageLink = new ImageLink();

        book.setId(mBookId);

        if (mPictureChosen != null) {
            imageLink.setThumbnail(mPictureChosen);
            imageLink.setSmallThumbnail(mPictureChosen);
        }

        volumeInfo.setTitle(mTitle.getText().toString());
        volumeInfo.setAuthors(Collections.singletonList(mAuthor.getText().toString()));
        volumeInfo.setIsbn13(mIsbn13.getText().toString());
        volumeInfo.setIsbn10(mIsbn10.getText().toString());
        volumeInfo.setLanguage(mLanguage.getText().toString());
        volumeInfo.setPageCount(mNumberPages.getText().toString());
        volumeInfo.setPrintType(mPrintType.getText().toString());
        volumeInfo.setPublisher(mPublisher.getText().toString());
        volumeInfo.setDescription(mDescription.getText().toString());

        book.setAnnotation(mAnnotation.getText().toString());

        volumeInfo.setImageLink(imageLink);
        book.setVolumeInfo(volumeInfo);
        book.setCustom(true);
        return book;

    }


    private boolean saveBook() {

        Book newBook = buildBookFromForm();

        if (validateBook(newBook)) {
            if (mFolderId == null)
                mFolderId = FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER;

            FirebaseDatabaseHelper.getInstance().insertBookFolder(mFolderId, newBook, this);

            return true;
        }

        return false;
    }

    private boolean validateBook(Book book) {

        if (book.getVolumeInfo().getTitle() == null) {
            mTitle.setError("Title cannot be empty");
            return false;
        } else if (book.getVolumeInfo().getTitle().isEmpty()) {
            mTitle.setError("Title cannot be empty");
            return false;
        }

        return true;
    }

    SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {

            mPhoto.setImageBitmap(resource);
            mPhoto.invalidate();

            mPhoto.setVisibility(View.VISIBLE);
            mPhotoNoImage.setVisibility(View.GONE);

        }
    };

    @Override
    public void onInsertBook(boolean success) {
        if (success) {
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
    }

    @Override
    public void onInsertFolder(boolean success) {

    }
}
