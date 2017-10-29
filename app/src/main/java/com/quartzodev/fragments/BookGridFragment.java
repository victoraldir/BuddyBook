package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.quartzodev.adapters.BookGridAdapterFirebase;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.utils.PrefUtils;
import com.quartzodev.views.DynamicImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class BookGridFragment extends Fragment implements BookGridAdapterFirebase.ILoading{

    private final String KEY_USER_ID = "userId";
    private final String KEY_FOLDER_ID = "mFolderId";
    private final String KEY_MENU_ID = "mMenuId";
    private static final String ARG_FOLDER_ID = "mFolderId";
    private static final String ARG_MENU_ID = "mMenuId";

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    private BookGridAdapterFirebase mAdapter;
    private String mUserId;
    private String mFolderId;
    private Integer mMenuId;
    private OnGridFragmentInteractionListener mListener;
    private FirebaseAuth mFirebaseAuth;

    public static BookGridFragment newInstance(String folderId, Integer menuId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putInt(ARG_MENU_ID, menuId);
        BookGridFragment fragment = new BookGridFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_USER_ID, mUserId);
        outState.putString(KEY_FOLDER_ID, mFolderId);
        outState.putInt(KEY_MENU_ID,mMenuId);
        super.onSaveInstanceState(outState);
    }

    public String getFolderId(){
        return mFolderId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            mUserId = savedInstanceState.getString(KEY_USER_ID);
            mFolderId = savedInstanceState.getString(KEY_FOLDER_ID);
            mMenuId = savedInstanceState.getInt(KEY_MENU_ID);

        }else{

            if (getArguments().containsKey(ARG_FOLDER_ID)) {
                mFolderId = getArguments().getString(ARG_FOLDER_ID);
            }

            if (getArguments().containsKey(ARG_MENU_ID)) {
                mMenuId = getArguments().getInt(ARG_MENU_ID);
            }

        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        if(mFirebaseAuth.getCurrentUser() != null) {

            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USER_ID)) {
                mUserId = savedInstanceState.getString(KEY_USER_ID);
            } else {
                mUserId = mFirebaseAuth.getCurrentUser().getUid();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);
        ButterKnife.bind(this, rootView);

        if (mFolderId != null && mFolderId.equals(FirebaseDatabaseHelper.REF_POPULAR_FOLDER)) {

            FirebaseRecyclerOptions<Book> options =
                    new FirebaseRecyclerOptions.Builder<Book>()
                            .setQuery(FirebaseDatabaseHelper.getInstance().fetchPopularBooks(),
                                    Book.class)
                            .build();

            setAdapterWithSort(options);

        }else{

            String sort = getResources()
                    .getStringArray(R.array.default_sorts_codes)[PrefUtils.getSortMode(getContext())];

            FirebaseRecyclerOptions<Book> options =
                    new FirebaseRecyclerOptions.Builder<Book>()
                            .setQuery(FirebaseDatabaseHelper.getInstance()
                                            .fetchBooksFromFolder(mUserId,mFolderId,sort),
                                    Book.class)
                            .build();

            setAdapterWithSort(options);
        }

        return rootView;
    }

    private void setAdapterWithSort(FirebaseRecyclerOptions<Book> options){


        mAdapter = new BookGridAdapterFirebase(options,this,mListener,mFolderId,mMenuId,getContext());
        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), columnCount);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter.startListening();
    }

    public void refresh(){

        String sort = getResources()
                .getStringArray(R.array.default_sorts_codes)[PrefUtils.getSortMode(getContext())];

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(FirebaseDatabaseHelper.getInstance()
                                        .fetchBooksFromFolder(mUserId,mFolderId,sort),
                                Book.class)
                        .build();

        setAdapterWithSort(options);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupHideFloatButtonOnScroll();
        setLoading(true);
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

    public interface OnGridFragmentInteractionListener {

        void onClickListenerBookGridInteraction(String mFolderId, Book book, DynamicImageView imageView);
        void onDeleteBookClickListener(String mFolderId, Book book);
        void onAddBookToFolderClickListener(String mFolderId, Book book);
        void onCopyBookToFolderClickListener(String mFolderId, Book book);
        void onLendBookClickListener(Book book, MenuItem menuItem);
        void onReturnBookClickListener(Book book);
    }
}
