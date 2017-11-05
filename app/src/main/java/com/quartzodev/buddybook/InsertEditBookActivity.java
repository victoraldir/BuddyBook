package com.quartzodev.buddybook;

import android.*;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.ImageLink;
import com.quartzodev.data.VolumeInfo;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class InsertEditBookActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_CAMERA_PERM = 2;
    private static final int RC_HANDLE_WRITE_PERM = 3;
    private static final int RC_IMAGE_PICK = 4;
    private static final int RC_CAMERA = 5;

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "argFolderId";
    public static final String ARG_FOLDER_NAME = "argFolderName";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.insert_imageview_thumb)
    ImageView mPhoto;
    @BindView(R.id.container_no_picture)
    FrameLayout mPhotoNoImage;

    private String mUserId;
    private String mBookId;
    private String mFolderId;
    private String mFolderName;
    private Uri mSavedImageUri;
    private Book mBookSelected;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        ButterKnife.bind(this);

        mContext = this;

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFolderId = getIntent().getExtras().getString(ARG_FOLDER_ID);
        mFolderName = getIntent().getExtras().getString(ARG_FOLDER_NAME);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.insert_new_book));

        if(mFolderName == null){
            mFolderName = getString(R.string.tab_my_books);
        }

        getSupportActionBar().setSubtitle(String.format(getString(R.string.folder_subtitle),mFolderName));

        mPhoto.setOnClickListener(this);
        mPhotoNoImage.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE_PICK || requestCode == RC_CAMERA) {

            Object img;

            if (data.getData() != null) {
                img = data.getData();
            } else {
                img = mSavedImageUri;
            }

            //saveImageDb(img);
            renderImage(img);
        }
    }

    private void renderImage(Object image){
        GlideApp.with(this)
                .asBitmap()
                .load(image)
                .placeholder(R.drawable.iconerror)
                .error(R.drawable.iconerror)
                .into(simpleTarget);
    }

    private void saveImageDb(Object image){

        VolumeInfo volumeInfo = mBookSelected.getVolumeInfo();
        ImageLink imageLink = new ImageLink();
        imageLink.setThumbnail(image.toString());
        volumeInfo.setImageLink(imageLink);
        mBookSelected.setVolumeInfo(volumeInfo);
        FirebaseDatabaseHelper.getInstance().updateBook(mUserId,mFolderId,mBookSelected);
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void callCamera() {
        ContentValues values = new ContentValues();
        File imagePath = new File(getFilesDir(), "covers");
        if(!imagePath.exists()) imagePath.mkdir();
        File newFile = new File(imagePath, System.currentTimeMillis() + "photo.png");

        if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
            mSavedImageUri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", newFile);
        } else {
            mSavedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        it.putExtra(MediaStore.EXTRA_OUTPUT, mSavedImageUri);
        startActivityForResult(it, RC_CAMERA);
    }

    private void callPickPhoto() {
        Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        items[0] = "Camera";
        items[1] = "Gallery";

        android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(mContext);
        alertdialog.setTitle("Add Image");
        alertdialog.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Camera")) {

                    int rc = ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA);
                    if (rc == PackageManager.PERMISSION_GRANTED) {
                        callCamera();
                    } else {
                        requestCameraPermission();
                    }

                } else if (items[item].equals("Gallery")) {

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

    SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {

            int valueWidth = (int) getResources().getDimension(R.dimen.book_cover_width);
            int valueHeight = (int) getResources().getDimension(R.dimen.book_cover_height);


            final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, valueWidth, valueHeight, true);
            mPhoto.setImageBitmap(scaledBitmap);
            mPhoto.invalidate();

            mPhoto.setVisibility(View.VISIBLE);
            mPhotoNoImage.setVisibility(View.GONE);

        }
    };
}
