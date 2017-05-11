package com.quartzodev.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.quartzodev.api.BookApi;
import com.quartzodev.api.VolumeInfo;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.task.FetchBookTask;
import com.quartzodev.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<BookApi>, View.OnClickListener, DialogInterface.OnClickListener {

    private static final int LOADER_ID_BOOK = 1;

    public static final String ARG_BOOK_ID = "bookId";
    public static final String ARG_FOLDER_ID = "folderId";
    public static final String ARG_USER_ID = "userId";
    public static final String ARG_FOLDER_LIST_ID = "folderListId";
    private String mBookId;
    private String mFolderId;
    private String mUserId;
    private String mFolderListComma;
    private Context mContext;
    private BookApi mBookSelected;

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

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookId = getArguments().getString(ARG_BOOK_ID);
        mFolderId = getArguments().getString(ARG_FOLDER_ID);
        mUserId = getArguments().getString(ARG_USER_ID);
        mFolderListComma = getArguments().getString(ARG_FOLDER_LIST_ID);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(mContext,"Folder list is: " + mFolderListComma,Toast.LENGTH_SHORT).show();
        getLoaderManager().initLoader(LOADER_ID_BOOK,null,this).forceLoad();
    }

    public static DetailActivityFragment newInstance(String userId ,String bookId, String folderId, String folderListId) {
        DetailActivityFragment fragment = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        args.putString(ARG_FOLDER_ID, folderId);
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_FOLDER_LIST_ID, folderListId);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadBookDetails(BookApi bookApi){

        if(bookApi != null) {

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
            mAuthor.setText(bookApi.getVolumeInfo().getAuthors().get(0));
            mDescription.setText(bookApi.getVolumeInfo().getDescription());
            mPublishedDate.setText(bookApi.getVolumeInfo().getPublishedDate());

            btnBookMark.setOnClickListener(this);

        }
    }

    @Override
    public Loader<BookApi> onCreateLoader(int id, Bundle args) {
        return new FetchBookTask(mContext,mUserId,mFolderId,mBookId);
    }

    @Override
    public void onLoadFinished(Loader<BookApi> loader, BookApi data) {
        mBookSelected = data;
        loadBookDetails(mBookSelected);
    }

    @Override
    public void onLoaderReset(Loader<BookApi> loader) {
    }


    @Override
    public void onClick(View v) {
        DialogUtils.alertDialogListFolder(mContext, mFolderListComma,this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        String unFormatted = mFolderListComma.split(",")[which];
        String id = unFormatted.split("=")[1];

        Toast.makeText(mContext,"Position: " + which, Toast.LENGTH_SHORT).show();
        FirebaseDatabaseHelper.getInstance().insertBookFolder(mUserId,id,mBookSelected);
        dialog.dismiss();
    }
}
