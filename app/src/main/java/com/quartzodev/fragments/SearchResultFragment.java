package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.adapters.BookGridAdapter;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.task.SearchTask;
import com.quartzodev.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class SearchResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int LOADER_ID_SEARCH = 3;
    private static final String ARG_FOLDER_ID = "mFolderId";
    private static final String ARG_ISBN = "mIsbn";
    private static final String ARG_QUERY = "query";
    private static final String ARG_MAX_RESULT = "maxResult";
    private static final String ARG_MENU_ID = "mMenuId";

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    private BookGridAdapter mAdapter;
    private String mFolderId;
    private String mISBN;
    private BookGridFragment.OnGridFragmentInteractionListener mListener;
    private LoaderManager mLoadManager;
    private Context mContext;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private String mQuery;
    private Integer mMenuId;
    private FirebaseAuth mFirebaseAuth;

    public SearchResultFragment() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static SearchResultFragment newInstance(String folderId, String isbn, Integer menuId) {

        Bundle arguments = new Bundle();
        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_ISBN, isbn);
        arguments.putInt(ARG_MENU_ID, menuId);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(arguments);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_FOLDER_ID)) {
            mFolderId = getArguments().getString(ARG_FOLDER_ID);
        }

        if (getArguments().containsKey(ARG_MENU_ID)) {
            mMenuId = getArguments().getInt(ARG_MENU_ID);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);

        ButterKnife.bind(this, rootView);

        mAdapter = new BookGridAdapter(getActivity(),
                new ArrayList<Book>(),
                mListener,
                mMenuId);

        mAdapter.setFolderId(mFolderId);
        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager sglm =
                new GridLayoutManager(getContext(), columnCount);
        mRecyclerView.setLayoutManager(sglm);

        if (getArguments().containsKey(ARG_ISBN)) {
            mISBN = getArguments().getString(ARG_ISBN);
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
            ((MainActivity) getActivity()).getTabLayout().setVisibility(View.GONE);
            executeSearchSearchFragment(mISBN, 1);
        } else {
            mLoadManager.initLoader(LOADER_ID_SEARCH, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadManager = getLoaderManager();
    }


    public void executeSearchSearchFragment(String query, Integer maxResult) {

        if (mAdapter != null) {

            mQuery = query;

//            mAdapter.clearList();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_QUERY, query);
            if (maxResult != null) {
                bundle.putInt(ARG_MAX_RESULT, maxResult);
            }

            if (mFolderId == null && ConnectionUtils.isNetworkConnected(mContext)
                    && FirebaseAuth.getInstance().getCurrentUser() != null) {

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

            } else {

                setLoading(true);

                mFirebaseDatabaseHelper.findBookSearch(mFolderId, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Book> bookApis = new ArrayList<>();

                        if (mQuery != null) {

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Book book = child.getValue(Book.class);

                                if (book.getVolumeInfo().getSearchField().contains(mQuery.toLowerCase())) {
                                    bookApis.add(book);
                                }

                            }
                            mAdapter.swap(bookApis);
                        }

                        setLoading(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

            setLoading(true);

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
        setLoading(false);

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
    }

}