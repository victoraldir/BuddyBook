package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.quartzodev.adapters.BookGridAdapter;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.task.SearchTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class SearchResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Book>>,
        FirebaseDatabaseHelper.OnDataSnapshotListener {

    private static final String TAG = SearchResultFragment.class.getSimpleName();

    private static final int LOADER_ID_SEARCH = 3;

    private static final String ARG_FOLDER_ID = "mFolderId";

    private static final String ARG_USER_ID = "mUserId";

    private static final String ARG_ISBN = "mIsbn";

    private static final String ARG_QUERY = "query";

    private static final String ARG_MAX_RESULT = "maxResult";

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    private BookGridAdapter mAdapter;

    private String mFolderId;

    private String mISBN;

    private BookGridFragment.OnGridFragmentInteractionListener mListener;

    private LoaderManager mLoadManager;

    private Context mContext;

    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    private String mUserId;

    private boolean mFlagLoadingLocal;

    private boolean mFlagLoadingRemote;

    private String mQuery;

    public static SearchResultFragment newInstance(String userId, String folderId, String isbn) {

        Bundle arguments = new Bundle();
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_ISBN, isbn);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(arguments);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_grid_book, container, false);

        ButterKnife.bind(this, rootView);

        mAdapter = new BookGridAdapter(getActivity(),
                new HashSet<Book>(),
                mFolderId,
                mListener,
                BookGridFragment.FLAG_SEARCH,
                null);

        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager sglm =
                new GridLayoutManager(getContext(), columnCount);
        mRecyclerView.setLayoutManager(sglm);

        if (getArguments().containsKey(ARG_ISBN)) {
            mISBN = getArguments().getString(ARG_ISBN);
        }

        if (getArguments().containsKey(ARG_USER_ID)) {
            mUserId = getArguments().getString(ARG_USER_ID);
        }

        mContext = getContext();

        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadManager = getLoaderManager();

        if (mISBN != null) {
            executeSearch(mISBN, 1);
        } else {
            mLoadManager.initLoader(LOADER_ID_SEARCH, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadManager = getLoaderManager();
    }

    public void executeSearch(String query, Integer maxResult) {

        mAdapter.clearList();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY, query);
        if (maxResult != null) {
            bundle.putInt(ARG_MAX_RESULT, maxResult);
        }

        if (mLoadManager.hasRunningLoaders()) {

            if (mLoadManager.getLoader(LOADER_ID_SEARCH) != null)
                mLoadManager.getLoader(LOADER_ID_SEARCH).cancelLoad();

            mLoadManager.restartLoader(LOADER_ID_SEARCH, bundle, this);

        } else {
            Loader l = mLoadManager.getLoader(LOADER_ID_SEARCH);
            if (l == null) {
                mLoadManager.initLoader(LOADER_ID_SEARCH, bundle, this);
            } else {
                mLoadManager.restartLoader(LOADER_ID_SEARCH, bundle, this);
            }

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_FOLDER_ID)) {
            mFolderId = getArguments().getString(ARG_FOLDER_ID);
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
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.INVISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.VISIBLE);
                if (mAdapter.getItemCount() == 0) {
                    container.findViewById(R.id.fragment_grid_message).setVisibility(View.VISIBLE);
                } else {
                    container.findViewById(R.id.fragment_grid_message).setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BookGridFragment.OnGridFragmentInteractionListener) {
            mListener = (BookGridFragment.OnGridFragmentInteractionListener) context;
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

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        if (mContext != null && args != null && args.containsKey(ARG_QUERY)) {

            mQuery = args.getString(ARG_QUERY);

            mFlagLoadingLocal = true;
            mFlagLoadingRemote = true;
            setLoading(true);

            //Local search
            mFirebaseDatabaseHelper.findBookSearch(mUserId, mFolderId, mQuery, this);

            if (args.containsKey(ARG_MAX_RESULT)) {

                return new SearchTask(mContext,
                        mQuery,
                        args.getInt(ARG_MAX_RESULT));

            } else {

                return new SearchTask(mContext,
                        mQuery,
                        null);
            }

        }

        return new SearchTask(mContext);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mAdapter.swap(data);

        mFlagLoadingRemote = false;

        if (!mFlagLoadingLocal) {
            setLoading(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {

        List<Book> bookApis = new ArrayList<>();

        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Book book = child.getValue(Book.class);

            if (book.getVolumeInfo().getSearchField().contains(mQuery.toLowerCase())) {
                bookApis.add(book);
            }

        }

        mAdapter.merge(bookApis);

        mFlagLoadingLocal = false;

        if (!mFlagLoadingRemote) {
            setLoading(false);
        }

        Log.d(TAG, dataSnapshot.toString());

    }
}