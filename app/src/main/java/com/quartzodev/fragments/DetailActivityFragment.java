package com.quartzodev.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.quartzodev.adapters.ProductDetailGridAdapter;
import com.quartzodev.buddybook.AnnotationActivity;
import com.quartzodev.buddybook.DetailActivity;
import com.quartzodev.buddybook.GlideApp;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.utils.DateUtils;
import com.quartzodev.utils.DialogUtils;
import com.quartzodev.utils.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.internal.zzahg.runOnUiThread;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener,
        ValueEventListener {

    public static final int RC_ANNOTATION = 1;
    private static final int RC_CAMERA_PERM = 2;
    private static final int RC_HANDLE_WRITE_PERM = 3;
    private static final int RC_IMAGE_PICK = 4;
    private static final int RC_CAMERA = 5;

    private static final String MOVIE_SHARE_HASHTAG = "#BuddyBook ";

    @BindView(R.id.detail_imageview_thumb)
    ImageView mPhoto;
    @BindView(R.id.detail_imageview_no_image)
    ImageView mPhotoNoImage;
    @BindView(R.id.photo_contatiner)
    FrameLayout mPhotoContainer;
    @BindView(R.id.detail_textview_title)
    TextView mTitle;
    @BindView(R.id.detail_textview_author)
    TextView mAuthor;
    @BindView(R.id.detail_textview_published_date)
    TextView mPublishedDate;
    @BindView(R.id.detail_textview_publisher)
    TextView mPublisher;
    @BindView(R.id.detail_textview_description)
    TextView mDescription;
    @BindView(R.id.detail_imageView_bookmark)
    ImageView mBtnBookMark;
    @BindView(R.id.detail_imageView_lend_book)
    @Nullable
    ImageView mBtnLendBook;
    @BindView(R.id.detail_textview_receiver_name)
    @Nullable
    TextView mTextReceiverName;
    @BindView(R.id.detail_textview_receiver_email)
    @Nullable
    TextView mTextReceiverEmail;
    @BindView(R.id.detail_textview_receiver_date)
    @Nullable
    TextView mTextLentDate;
    @BindView(R.id.card_book_borrowed)
    @Nullable
    CardView mCardViewBookBorrowed;
    @BindView(R.id.card_actions)
    CardView mCardViewActions;
    @BindView(R.id.detail_textview_annotations)
    TextView mTextViewAnnotation;

    @BindView(R.id.descriptionContainer)
    LinearLayout mDescriptionContainer;

    @BindView(R.id.btn_more_details)
    TextView mBtnMore;

    @BindView(R.id.grid_product_details)
    RecyclerView mGridProductDetails;

    @BindView(R.id.detail_cardview_annotation)
    CardView mCardViewAnnotation;

    @BindView(R.id.detail_cardview_product_details)
    CardView mCardViewProductDetails;

    @BindView(R.id.detail_btn_lend)
    LinearLayout mBtnLend;

    private String mBookJson;
    private String mUserId;
    private String mFolderListComma;
    private String mFolderId;
    private String mBookId;
    private Context mContext;
    private Book mBookSelected;
    private OnDetailInteractionListener mListener;

    private Uri mSavedImageUri;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailActivityFragment newInstance(String userId, String bookId, String folderId, String folderListId, String bookJson) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putString(DetailActivity.ARG_BOOK_ID, bookId);
        args.putString(DetailActivity.ARG_FOLDER_ID, folderId);
        args.putString(DetailActivity.ARG_USER_ID, userId);
        args.putString(DetailActivity.ARG_FOLDER_LIST_ID, folderListId);
        args.putString(DetailActivity.ARG_BOOK_JSON, bookJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getArguments().getString(DetailActivity.ARG_USER_ID);
        mFolderListComma = getArguments().getString(DetailActivity.ARG_FOLDER_LIST_ID);
        mBookJson = getArguments().getString(DetailActivity.ARG_BOOK_JSON);
        mFolderId = getArguments().getString(DetailActivity.ARG_FOLDER_ID);
        mContext = getContext();
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        mBookId = getArguments().getString(DetailActivity.ARG_BOOK_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this, rootView);


        Gson gson = new Gson();
        final Book currentBook = gson.fromJson(mBookJson, Book.class);

        if (mBookId != null) {
            loadAnnotationFromDb();
        }

        int numCol = 2;

        ProductDetailGridAdapter mProductDetailGridAdapter = new ProductDetailGridAdapter(currentBook.getVolumeInfo(), getActivity());

        mGridProductDetails.setLayoutManager(new GridLayoutManager(mContext, numCol));

        mGridProductDetails.setAdapter(mProductDetailGridAdapter);

        return rootView;
}

    public void loadAnnotationFromDb() {
        mFirebaseDatabaseHelper.findBook(mUserId, mFolderId, mBookId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Book book = dataSnapshot.getValue(Book.class);
                    setAnnotation(book.getAnnotation());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() instanceof MainActivity) {

            ((MainActivity) getActivity()).setIntentShareMenu(createShareBookIntent(mBookSelected));

        } else {

            inflater.inflate(R.menu.menu_detail, menu);
            MenuItem menushareItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menushareItem);
            if (mBookSelected != null) {
                menushareItem.setVisible(true);
                mShareActionProvider.setShareIntent(createShareBookIntent(mBookSelected));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadBook(null);
    }

    public void expandDescription(View view) {

        if (mDescriptionContainer.getVisibility() == View.VISIBLE) {
            mBtnMore.setText(mContext.getString(R.string.more));
            collapse(mDescriptionContainer);
        } else {
            mBtnMore.setText(mContext.getString(R.string.less));
            expand(mDescriptionContainer);
        }

    }

    public void loadBook(Book book) {

        if (book == null) {
            Gson gson = new Gson();
            mBookSelected = gson.fromJson(mBookJson, Book.class);
        } else {
            mBookSelected = book;
        }

        loadBookDetails(mBookSelected);
    }

    private void loadBookDetails(final Book bookApi) {

        if (bookApi != null) {

            loadImage(bookApi);
            loadAuthors(bookApi);
            loadToolbar(bookApi);

            setAnnotation(bookApi.getAnnotation());
            mTitle.setText(bookApi.getVolumeInfo().getTitle());
            mPublishedDate.setText(DateUtils.formatStringDate(bookApi.getVolumeInfo().getPublishedDate()));
            mPublisher.setText(bookApi.getVolumeInfo().getPublisher());

            if (bookApi.getVolumeInfo().getDescription() != null && !bookApi.getVolumeInfo().getDescription().isEmpty()) {
                Spanned bodyContent = fromHtml(bookApi.getVolumeInfo().getDescription());
                mDescription.setText(bodyContent);
            } else {
                mDescription.setText(getString(R.string.no_description));
            }

            if (mFolderListComma != null) {
                mBtnBookMark.setOnClickListener(this);
                mBtnBookMark.setContentDescription(getString(R.string.move_to_folder_cd));
            } else {
                mCardViewActions.setVisibility(View.GONE);
            }

            if (bookApi.getId() != null) {
                if (mFolderId != null && !mFolderId.equals(FirebaseDatabaseHelper.REF_POPULAR_FOLDER)) {
                    mCardViewAnnotation.setVisibility(View.VISIBLE);
                    mBtnLend.setVisibility(View.VISIBLE);
                    loadBookActions(bookApi);
                    if (getActivity() != null)
                        ((DetailActivity) getActivity()).setFabVisible();

                }
            }

        }

    }

    public void setAnnotation(String content) {

        if (content != null && !content.isEmpty()) {
            mTextViewAnnotation.setText(content);
        } else {
            mTextViewAnnotation.setText(mContext.getString(R.string.you_have_no_annotations));
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    private void loadToolbar(final Book book) {
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(book.getVolumeInfo().getTitle());
                actionBar.setSubtitle(book.getVolumeInfo().getAuthors() != null ? book.getVolumeInfo().getAuthors().get(0) : "");
            }
        }
    }

    private void loadImage(final Book book) {
        if (book.getVolumeInfo() != null && book.getVolumeInfo().getImageLink() != null) {

            if (book.getVolumeInfo().getImageLink().getThumbnail() != null) {

                renderImage(book.getVolumeInfo().getImageLink().getThumbnail());

                mPhotoNoImage.setVisibility(View.GONE);
                mPhoto.setVisibility(View.VISIBLE);
            }

            String str = String.format(getString(R.string.cover_book_cd), book.getVolumeInfo().getTitle());

            mPhoto.setContentDescription(str);
        }

        if (book.isCustom()) {

            mPhoto.setOnClickListener(onPhotoClick);
            mPhotoNoImage.setOnClickListener(onPhotoClick);
        }

        if (book.getVolumeInfo().getImageLink() == null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextDrawable drawable;

                    drawable = TextDrawable.builder()
                            .buildRect(TextUtils.getFirstLetterTitle(book), Color.BLUE);

                    mPhotoNoImage.setImageDrawable(drawable);
                    mPhotoNoImage.invalidate();
                }
            });

        }
    }

    View.OnClickListener onPhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final CharSequence[] items = new CharSequence[2];
            items[0] = "Camera";
            items[1] = "Gallery";

            android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(mContext);
            alertdialog.setTitle("Add Image");
            alertdialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (items[item].equals("Camera")) {

                        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                        if (rc == PackageManager.PERMISSION_GRANTED) {
                            callCamera();
                        } else {
                            requestCameraPermission();
                        }

                    } else if (items[item].equals("Gallery")) {

                        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
    };

    private void callPickPhoto() {
        Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent2.setType("image/*");
        startActivityForResult(intent2, RC_IMAGE_PICK);
    }

    private void callCamera() {
        ContentValues values = new ContentValues();
        mSavedImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, mSavedImageUri);
        startActivityForResult(it, RC_CAMERA);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestWriteExternalStoragePermission(int requestCode) {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            requestPermission(Manifest.permission.CAMERA, RC_CAMERA_PERM);
        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        requestPermissions(new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RC_HANDLE_WRITE_PERM) {
                callPickPhoto();
            } else if (requestCode == RC_CAMERA_PERM) {

                int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    callCamera();
                } else {
                    requestWriteExternalStoragePermission(RC_CAMERA_PERM);
                }
            }
        }
    }

    private void loadAuthors(final Book book) {
        if (book.getVolumeInfo() != null &&
                book.getVolumeInfo().getAuthors() != null &&
                !book.getVolumeInfo().getAuthors().isEmpty()) {

            List<String> authors = book.getVolumeInfo().getAuthors();

            String authorsString = "";

            for (String author : authors) {
                authorsString = authorsString.concat(author + "\n");
            }

            mAuthor.setText(authorsString);
        }
    }

    private void loadBookActions(final Book book) {
        if (book.getLend() != null) {

            DateTime lendDate = new DateTime(book.getLend().getLendDate());

            Days days = Days.daysBetween(lendDate, DateTime.now());

            mTextReceiverEmail.setText(String.format(getString(R.string.lent_to_email), book.getLend().getReceiverEmail()));
            mTextReceiverName.setText(String.format(getString(R.string.lent_to), book.getLend().getReceiverName()));
            mTextLentDate.setText(String.format(getString(R.string.lent_day_ago), days.getDays()));
            mBtnLendBook.setImageResource(R.drawable.ic_assignment_return);
            mBtnLendBook.setContentDescription(getString(R.string.btn_return_book_cd));
            mBtnLendBook.setVisibility(View.VISIBLE);
            mCardViewBookBorrowed.setVisibility(View.VISIBLE);

            mBtnLendBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReturnBook(book);
                }
            });

        } else {

            mBtnLendBook.setImageResource(R.drawable.ic_card_giftcard);
            mBtnLendBook.setContentDescription(getString(R.string.btn_lend_book_cd));
            mBtnLendBook.setVisibility(View.VISIBLE);
            mCardViewBookBorrowed.setVisibility(View.GONE);

            mBtnLendBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onLendBook(book);
                }
            });

        }
    }

    public static void expand(final View v) {
        v.measure(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ActionBar.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onClick(View v) {
        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String unFormatted = mFolderListComma.split(",")[which];
        String id = unFormatted.split("=")[1];

        FirebaseDatabaseHelper.getInstance().insertBookFolder(mUserId, id, mBookSelected, (DetailActivity) getActivity());
        dialog.dismiss();
    }

    private Intent createShareBookIntent(Book bookApi) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, MOVIE_SHARE_HASHTAG + getString(R.string.checkbook) + " " + bookApi.getVolumeInfo().getTitle());
        return shareIntent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetailInteractionListener) {
            mListener = (OnDetailInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailInteractionListener");
        }

    }

    public void launchResultActivity() {
        Intent it = new Intent(mContext, AnnotationActivity.class);

        it.putExtra(AnnotationActivity.ARG_BOOK_ID, mBookSelected.getId());
        it.putExtra(AnnotationActivity.ARG_FOLDER_ID, mFolderId);
        if (mTextViewAnnotation.getText().toString().equals(getString(R.string.you_have_no_annotations))) {
            it.putExtra(AnnotationActivity.ARG_CONTENT, "");
        } else {
            it.putExtra(AnnotationActivity.ARG_CONTENT, mTextViewAnnotation.getText().toString());
        }
        it.putExtra(AnnotationActivity.ARG_BOOK_TITLE, mBookSelected.getVolumeInfo().getTitle());

        startActivityForResult(it, RC_ANNOTATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (RC_ANNOTATION == requestCode) {

                String content = data.getStringExtra(AnnotationActivity.ARG_CONTENT);
                setAnnotation(content);
                mBookSelected.setAnnotation(content);

            } else if (requestCode == RC_IMAGE_PICK || requestCode == RC_CAMERA) {

                Object img = null;

                if (data.getData() != null) {
                    img = data.getData();
                } else {
                    img = mSavedImageUri;
                }
                renderImage(img);
            }
        }
    }

    private void renderImage(Object image){
        GlideApp.with(mContext)
                .asBitmap()
                .load(image)
                .into(simpleTarget);
    }

    SimpleTarget simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(final Bitmap resource, Transition<? super Bitmap> transition) {
            try {

                final ViewTreeObserver observer = getView().getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {

                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, mPhotoContainer.getWidth(), mPhotoContainer.getHeight(), true);
                        mPhoto.setImageBitmap(scaledBitmap);
                        mPhoto.invalidate();

                        mPhoto.setVisibility(View.VISIBLE);
                        mPhotoNoImage.setVisibility(View.GONE);

                        return true;
                    }
                });

            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(),ex.getMessage());
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }

public interface OnDetailInteractionListener {

    void onLendBook(Book bookApi);

    void onReturnBook(Book bookApi);

}
}
