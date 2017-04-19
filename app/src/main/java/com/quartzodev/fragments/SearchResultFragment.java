package com.quartzodev.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quartzodev.adapters.BookGridAdapter;
import com.quartzodev.api.APIService;
import com.quartzodev.api.BookApi;
import com.quartzodev.api.BookResponse;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;
import com.quartzodev.task.FetchFolderTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class SearchResultFragment extends Fragment {

    private static final String ARG_FOLDER_ID = "mFolderId";
    private static final String ARG_USER_ID = "mUserId";

    private SearchTask mSearchTask;
    private BookGridAdapter mAdapter;
    private String mFolderId;
    private BookGridFragment.OnGridFragmentInteractionListener mListener;

    @BindView(R.id.grid_book_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);

        ButterKnife.bind(this, rootView);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.grid_book_progress_bar);

        mAdapter = new BookGridAdapter(getActivity(),new ArrayList<BookApi>(),mFolderId,mListener);

        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        return rootView;
    }


    public void executeSearch(String query){

        if( mSearchTask != null ) {
            mSearchTask.cancel();
        }

        mSearchTask = new SearchTask(this);

        mSearchTask.execute(query);

    }

    public static SearchResultFragment newInstance(String userId, String folderId) {

        Bundle arguments = new Bundle();
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(arguments);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(ARG_FOLDER_ID)){
            mFolderId = getArguments().getString(ARG_FOLDER_ID);
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

    public class SearchTask extends AsyncTask<String,Integer,List<BookApi>> {

        private final String LOG = SearchTask.class.getSimpleName();
        private boolean mCanceled = false;
        private RecyclerView recyclerView;
        private ProgressBar progressBar;

        public SearchTask(SearchResultFragment fragment){
            this.recyclerView = fragment.mRecyclerView;
            this.progressBar = fragment.mProgressBar;
        }

        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {

            Log.d(LOG,"Thread ID: " + getId() + " cancelled");

            super.onCancelled();
        }

        @Override
        protected List<BookApi> doInBackground(String... params) {

            Log.d(LOG, "Thread ID: " + getId() + ". Running search query for text: " + params[0]);

            try {
                Response<BookResponse> bookResponseResponse = APIService.getInstance().getBooks(params[0]).execute();

                if(bookResponseResponse.body() != null && !mCanceled){
                    return bookResponseResponse.body().getItems();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<BookApi> bookApis) {

            Log.d(LOG,"Thread ID: " + getId() + ". Completed");

            mAdapter.swap(bookApis);

            mProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        public void cancel() {
            mCanceled = true;
        }
    }

}
