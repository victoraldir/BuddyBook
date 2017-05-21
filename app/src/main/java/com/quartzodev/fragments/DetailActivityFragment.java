package com.quartzodev.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.quartzodev.api.BookApi;
import com.quartzodev.api.VolumeInfo;
import com.quartzodev.buddybook.DetailActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.task.FetchBookTask;
import com.quartzodev.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener,
        FirebaseDatabaseHelper.OnDataSnapshotListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String MOVIE_SHARE_HASHTAG = "#BuddyBook ";

    private static final int LOADER_ID_BOOK = 1;
    @BindView(R.id.detail_imageview_thumb)
    ImageView mPhoto;
    @BindView(R.id.detail_textview_title)
    TextView mTitle;
    @BindView(R.id.detail_textview_author)
    TextView mAuthor;
    @BindView(R.id.detail_textview_published_date)
    TextView mPublishedDate;
    @BindView(R.id.detail_textview_description)
    TextView mDescription;
    @BindView(R.id.detail_imageView_bookmark)
    ImageView btnBookMark;

    @BindView(R.id.detail_imageView_lend_book)
    @Nullable
    ImageView btnLendBook;

    @BindView(R.id.detail_textview_receiver_name)
    @Nullable
    TextView textReceiverName;

    @BindView(R.id.detail_textview_receiver_email)
    @Nullable
    TextView textReceiverEmail;

    @BindView(R.id.detail_textview_receiver_date)
    @Nullable
    TextView textLentDate;
    //    public static final String ARG_BOOK_ID = "bookId";
//    public static final String ARG_FOLDER_ID = "folderId";
//    public static final String ARG_USER_ID = "userId";
//    public static final String ARG_FOLDER_LIST_ID = "folderListId";
//    public static final String ARG_BOOK_JSON = "bookJson";
    private String mBookId;
    private String mBookJson;
    private String mFolderId;
    private String mUserId;
    private String mFolderListComma;
    private Context mContext;
    private BookApi mBookSelected;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private MenuItem menushareItem;
    private ShareActionProvider mShareActionProvider;
    private Boolean isLentBook;

    public DetailActivityFragment() {
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        setHasOptionsMenu(true);
    }

    public static DetailActivityFragment newInstance(String userId, String bookId, String folderId, String folderListId, String bookJson, boolean flagIsLent) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putString(DetailActivity.ARG_BOOK_ID, bookId);
        args.putString(DetailActivity.ARG_FOLDER_ID, folderId);
        args.putString(DetailActivity.ARG_USER_ID, userId);
        args.putString(DetailActivity.ARG_FOLDER_LIST_ID, folderListId);
        args.putString(DetailActivity.ARG_BOOK_JSON, bookJson);
        args.putBoolean(DetailActivity.ARG_FLAG_IS_LENT_BOOK, flagIsLent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookId = getArguments().getString(DetailActivity.ARG_BOOK_ID);
        mFolderId = getArguments().getString(DetailActivity.ARG_FOLDER_ID);
        mUserId = getArguments().getString(DetailActivity.ARG_USER_ID);
        mFolderListComma = getArguments().getString(DetailActivity.ARG_FOLDER_LIST_ID);
        mBookJson = getArguments().getString(DetailActivity.ARG_BOOK_JSON);
        isLentBook = getArguments().getBoolean(DetailActivity.ARG_FLAG_IS_LENT_BOOK);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;

        if(isLentBook){
            view = inflater.inflate(R.layout.fragment_detail_lent_book, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_detail, container, false);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_detail, menu);
        menushareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menushareItem);
        if(mBookSelected != null) {
            menushareItem.setVisible(true);
            mShareActionProvider.setShareIntent(createShareBookIntent(mBookSelected));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(mContext, "Folder list is: " + mFolderListComma, Toast.LENGTH_SHORT).show();
        //getLoaderManager().initLoader(LOADER_ID_BOOK,null,this).forceLoad();
        mFirebaseDatabaseHelper.findBook(mUserId, mFolderId, mBookId, this);
    }

    private void loadBookDetails(BookApi bookApi) {

        Log.d(TAG, "Should detail book: " + bookApi.getVolumeInfo().getDescription());

        if (bookApi != null) {

            VolumeInfo volumeInfo = bookApi.getVolumeInfo();

            if (volumeInfo != null && bookApi.getVolumeInfo().getImageLink() != null) {
                Glide.with(mContext)
                        .load(volumeInfo.getImageLink().getThumbnail())
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(mPhoto);
            }

            mTitle.setText(bookApi.getVolumeInfo().getTitle());
            mAuthor.setText(bookApi.getVolumeInfo().getAuthors() != null ? bookApi.getVolumeInfo().getAuthors().get(0) : "");
            mDescription.setText(bookApi.getVolumeInfo().getDescription());
            mPublishedDate.setText(bookApi.getVolumeInfo().getPublishedDate());

            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

            actionBar.setTitle(bookApi.getVolumeInfo().getTitle());
            actionBar.setSubtitle(bookApi.getVolumeInfo().getAuthors() != null ? bookApi.getVolumeInfo().getAuthors().get(0) : "");

            btnBookMark.setOnClickListener(this);

            if(bookApi.getLend() != null){
                textReceiverEmail.setText(bookApi.getLend().getReceiverEmail());
                textReceiverName.setText(bookApi.getLend().getReceiverName());
                textLentDate.setText(bookApi.getLend().getLendDate().toString());
                btnLendBook.setImageResource(R.drawable.ic_assignment_return_black_24dp);
            }

        }
    }

//    @Override
//    public Loader<BookApi> onCreateLoader(int id, Bundle args) {
//        return new FetchBookTask(mContext, mUserId, mFolderId, mBookId);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<BookApi> loader, BookApi data) {
//        mBookSelected = data;
//        loadBookDetails(mBookSelected);
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<BookApi> loader) {
//    }


    @Override
    public void onClick(View v) {
        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String unFormatted = mFolderListComma.split(",")[which];
        String id = unFormatted.split("=")[1];

        Toast.makeText(mContext, "Position: " + which, Toast.LENGTH_SHORT).show();
        FirebaseDatabaseHelper.getInstance().insertBookFolder(mUserId, id, mBookSelected);
        dialog.dismiss();
    }

    private Intent createShareBookIntent(BookApi bookApi) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, MOVIE_SHARE_HASHTAG + getString(R.string.checkbook) + " " + bookApi.getVolumeInfo().getTitle());
        return shareIntent;
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
        Log.d(TAG, "Data received: " + dataSnapshot.toString());
        mBookSelected = dataSnapshot.getValue(BookApi.class);

        //TODO maybe it's not needed. Leaving as it's for now.
        if (mBookSelected == null) {
            Gson gson = new Gson();
            mBookSelected = gson.fromJson(mBookJson, BookApi.class);
        }

        loadBookDetails(mBookSelected);

    }
}
