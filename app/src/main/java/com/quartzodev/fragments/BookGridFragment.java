package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.quartzodev.adapters.BookGridAdapter;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.views.DynamicImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class BookGridFragment extends Fragment implements
        ChildEventListener {

    private static final String TAG = BookGridFragment.class.getSimpleName();

    public static final int FLAG_MY_BOOKS_FOLDER = 0;
    public static final int FLAG_TOP_BOOKS_FOLDER = 1;
    public static final int FLAG_CUSTOM_FOLDER = 2;
    public static final int FLAG_SEARCH = 3;

    private static final String ARG_POSITION_ID = "mFlag";
    private static final String ARG_USER_ID = "mUserId";
    private static final String ARG_FOLDER_ID = "mFolderId";
    private static final String ARG_FOLDER_NAME = "mFolderName";

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    private BookGridAdapter mAdapter;
    private String mUserId;
    private String mFolderId;
    private String mFolderName;
    private int mFlag;
    private OnGridFragmentInteractionListener mListener;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    public static BookGridFragment newInstanceCustomFolder(String userId, String folderId, String folderName, int flag) {
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_POSITION_ID, flag);
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_FOLDER_NAME, folderName);
        BookGridFragment fragment = new BookGridFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    public void updateContent(String userId, String folderId, String folderName, int flag) {

        detachFirebaseListener();

        mUserId = userId;
        mFolderId = folderId;
        mAdapter.setFolderId(mFolderId);
        mFlag = flag;
        mFolderName = folderName;

        attachFirebaseListener();

        loadPopularBooks();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_USER_ID)) {
            mUserId = getArguments().getString(ARG_USER_ID);
        }

        if (getArguments().containsKey(ARG_POSITION_ID)) {
            mFlag = getArguments().getInt(ARG_POSITION_ID);
        }

        if (getArguments().containsKey(ARG_FOLDER_ID)) {
            mFolderId = getArguments().getString(ARG_FOLDER_ID);
        }

        if (getArguments().containsKey(ARG_FOLDER_NAME)) {
            mFolderName = getArguments().getString(ARG_FOLDER_NAME);
        }

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachFirebaseListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateToolbarTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new BookGridAdapter(getActivity(),
                new HashSet<Book>(),
                mListener,
                mFlag);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setFolderId(mFolderId);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), columnCount);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        if (mFolderName != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFolderName);
        }

        updateToolbarTitle();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHideFloatButtonOnScroll();
        loadPopularBooks();
    }

    private void loadPopularBooks(){

        setLoading(true);

        if (mFlag == FLAG_TOP_BOOKS_FOLDER) {

            mFirebaseDatabaseHelper.fetchPopularBooks(new FirebaseDatabaseHelper.OnDataSnapshotListener() {
                @Override
                public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {

                    List<Book> bookList = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Book book = child.getValue(Book.class);
                        bookList.add(book);
                    }

                    mAdapter.swap(bookList);
                    setLoading(false);
                }
            });

        }else if (mFlag == FLAG_MY_BOOKS_FOLDER) {

            mFirebaseDatabaseHelper.fetchMyBooksFolder(mUserId, new FirebaseDatabaseHelper.OnDataSnapshotListener() {
                @Override
                public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if(folder.getBooks() != null && folder.getBooks().values() != null) {
                        mAdapter.swap(new ArrayList<>(folder.getBooks().values()));
                    }else{
                        mAdapter.swap(new ArrayList<Book>());
                    }

                    setLoading(false);
                }
            });

        }else if (mFlag == FLAG_CUSTOM_FOLDER) {
            mFirebaseDatabaseHelper.fetchBooksFromFolder(mUserId, mFolderId, new FirebaseDatabaseHelper.OnDataSnapshotListener() {
                @Override
                public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if(folder.getBooks() != null && folder.getBooks().values() != null) {
                        mAdapter.swap(new ArrayList<>(folder.getBooks().values()));
                    }else{
                        mAdapter.swap(new ArrayList<Book>());
                    }

                    setLoading(false);
                }
            });
        }


    }

    private void setupHideFloatButtonOnScroll(){

        if((getActivity()) != null) {

            final FloatingActionButton fab = ((MainActivity) getActivity()).getFab();

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fab.isShown())
                        fab.hide();
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

    }

    private void updateToolbarTitle() {
        if (getActivity() != null) {
            if (mFolderName != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mFolderName);
            } else {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            }
        }
    }

    public void setLoading(boolean flag) {
        ViewGroup container = (ViewGroup) this.getView();

        if (container != null) {
            if (flag) {
                container.findViewById(R.id.fragment_grid_message).setVisibility(View.GONE);
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.VISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.GONE);
            } else {
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.GONE);
                if (mAdapter.getItemCount() == 0) {
                    container.findViewById(R.id.fragment_grid_message).setVisibility(View.VISIBLE);
                    container.findViewById(R.id.recycler_view_books).setVisibility(View.GONE);
                } else {
                    container.findViewById(R.id.fragment_grid_message).setVisibility(View.GONE);
                    container.findViewById(R.id.recycler_view_books).setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGridFragmentInteractionListener) {
            mListener = (OnGridFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGridFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void detachFirebaseListener() {
        if (mFlag == FLAG_MY_BOOKS_FOLDER) {
            mFirebaseDatabaseHelper.detachBookFolderChildEventListener(mUserId, FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER, this);
        } else if (mFlag == FLAG_CUSTOM_FOLDER) {
            mFirebaseDatabaseHelper.detachBookFolderChildEventListener(mUserId, mFolderId, this);
        }
    }

    public void attachFirebaseListener() {
        if (mFlag == FLAG_MY_BOOKS_FOLDER) {
            mFirebaseDatabaseHelper.attachBookFolderChildEventListener(mUserId, FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER, this);
        } else if (mFlag == FLAG_CUSTOM_FOLDER) {
            mFirebaseDatabaseHelper.attachBookFolderChildEventListener(mUserId, mFolderId, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        detachFirebaseListener();
    }



    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildAdded FIRED " + dataSnapshot.toString());

        if (dataSnapshot.getValue() != null) {
            Book bookApi = dataSnapshot.getValue(Book.class);
            mAdapter.addItem(bookApi);
            setLoading(false);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        Log.d(TAG, "onChildRemoved FIRED " + dataSnapshot.toString());

        if (dataSnapshot.getValue() != null) {
            Book bookApi = dataSnapshot.getValue(Book.class);
            mAdapter.removeItem(bookApi);
            setLoading(false);
        }

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface OnGridFragmentInteractionListener {

        void onClickListenerBookGridInteraction(String mFolderId, Book book, DynamicImageView imageView);
        void onDeleteBookClickListener(String mFolderId, Book book);
        void onAddBookToFolderClickListener(String mFolderId, Book book);
        void onCopyBookToFolderClickListener(String mFolderId, Book book);
        void onLendBookClickListener(Book book);
        void onReturnBookClickListener(Book book);
    }
}
