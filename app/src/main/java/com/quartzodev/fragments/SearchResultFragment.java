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
import com.quartzodev.task.SearchTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

/**
 * Created by victoraldir on 24/03/2017.
 */

public class SearchResultFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BookApi>>{

    private static final int LOADER_ID_SEARCH = 2;

    private static final String ARG_FOLDER_ID = "mFolderId";
    private static final String ARG_USER_ID = "mUserId";
    private static final String ARG_ISBN = "mIsbn";

    private static final String ARG_QUERY = "query";
    private static final String ARG_MAX_RESULT = "maxResult";

    //private SearchTask mSearchTask;
    private BookGridAdapter mAdapter;
    private String mFolderId;
    private String mISBN;

    private BookGridFragment.OnGridFragmentInteractionListener mListener;

//    @BindView(R.id.grid_book_progress_bar)
//    ProgressBar mProgressBar;

    @BindView(R.id.recycler_view_books)
    RecyclerView mRecyclerView;

    private LoaderManager mLoadManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_grid_book, container, false);

        ButterKnife.bind(this, rootView);

        //mProgressBar = (ProgressBar) rootView.findViewById(R.id.grid_book_progress_bar);

        mAdapter = new BookGridAdapter(getActivity(),new ArrayList<BookApi>(),mFolderId,mListener);

        mRecyclerView.setAdapter(mAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);

        if(getArguments().containsKey(ARG_ISBN)){
            mISBN = getArguments().getString(ARG_ISBN);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadManager = getLoaderManager();
        mLoadManager.initLoader(LOADER_ID_SEARCH,null,this);

//        if(mISBN != null){
//
//            executeSearch(mISBN,1);
//
//        }else{
//            //setLoading(false);
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadManager = getLoaderManager();
    }

    public void executeSearch(String query, Integer maxResult){

//        if( mSearchTask != null ) {
//            mSearchTask.cancel();
//        }
//
//        mSearchTask = new SearchTask();
//
//        if(maxResult == null){
//            mSearchTask.execute(query,null);
//        }else{
//            mSearchTask.execute(query,maxResult.toString());
//        }

        Bundle bundle = new Bundle();
        bundle.putString(ARG_QUERY,query);
        if(maxResult != null) {
            bundle.putInt(ARG_MAX_RESULT, maxResult);
        }

        if(mLoadManager.hasRunningLoaders()) {

            if(mLoadManager.getLoader(LOADER_ID_SEARCH) != null)
                mLoadManager.getLoader(LOADER_ID_SEARCH).cancelLoad();

            mLoadManager.restartLoader(LOADER_ID_SEARCH, bundle, this);

        }else{
            Loader l = mLoadManager.getLoader(LOADER_ID_SEARCH);
            if(l == null){
                mLoadManager.initLoader(LOADER_ID_SEARCH, bundle, this);
            }else{
                mLoadManager.restartLoader(LOADER_ID_SEARCH, bundle, this);
            }

        }

        //setLoading(true);

    }

    //TODO Stop request when rotating
    public static SearchResultFragment newInstance(String userId, String folderId, String isbn) {

        Bundle arguments = new Bundle();
        arguments.putString(ARG_USER_ID, userId);
        arguments.putString(ARG_FOLDER_ID, folderId);
        arguments.putString(ARG_ISBN, isbn);
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


    public void setLoading(boolean flag){
        ViewGroup container = (ViewGroup)this.getView();

        if (container != null) {
            if(flag) {
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.VISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.INVISIBLE);
            }else{
                container.findViewById(R.id.grid_book_progress_bar).setVisibility(View.INVISIBLE);
                container.findViewById(R.id.recycler_view_books).setVisibility(View.VISIBLE);
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
    public Loader<List<BookApi>> onCreateLoader(int id, Bundle args) {
        setLoading(true);

        if(args != null && args.containsKey(ARG_QUERY)){

            if(args.containsKey(ARG_MAX_RESULT)){

                return new SearchTask(getContext(),
                        "",
                        null);

            }else{
                return new SearchTask(getContext(),
                        args.getString(ARG_QUERY),
                        args.getInt(ARG_MAX_RESULT));
            }
        }

        return new SearchTask(getContext(),
                "",
                null);
    }

    @Override
    public void onLoadFinished(Loader<List<BookApi>> loader, List<BookApi> data) {
        setLoading(false);
        mAdapter.swap(data);
    }

    @Override
    public void onLoaderReset(Loader<List<BookApi>> loader) {

    }

//    public class SearchTask extends AsyncTask<String,Integer,List<BookApi>> {
//
//        private final String LOG = SearchTask.class.getSimpleName();
//        private boolean mCanceled = false;
////        private RecyclerView recyclerView;
////        private ProgressBar progressBar;
//
//        public SearchTask(){
////            this.recyclerView = fragment.mRecyclerView;
////            this.progressBar = fragment.mProgressBar;
//        }
//
//        @Override
//        protected void onPreExecute() {
////            mRecyclerView.setVisibility(View.INVISIBLE);
////            progressBar.setVisibility(View.VISIBLE);
//
//        }
//
//        @Override
//        protected void onCancelled() {
//
//            if(!isCancelled()) {
//                Log.d(LOG, "Thread ID: " + getId() + " cancelled");
//                //setLoading(false);
//                super.onCancelled();
//            }
//        }
//
//        @Override
//        protected List<BookApi> doInBackground(String... params) {
//
//            if(!isCancelled()) {
//                Log.d(LOG, "Thread ID: " + getId() + ". Running search query for text: " + params[0]);
//
//                try {
//
//                    Response<BookResponse> bookResponseResponse;
//
//                    //Has max results limit
//                    if(params[1] != null && !params[1].isEmpty()){
//                        bookResponseResponse = APIService.getInstance()
//                                .getBooksMaxResult(params[0],Integer.parseInt(params[1]))
//                                .execute();
//                    }else{
//                        bookResponseResponse = APIService.getInstance().getBooks(params[0]).execute();
//                    }
//
//
//                    if (bookResponseResponse.body() != null && !mCanceled) {
//                        return bookResponseResponse.body().getItems();
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<BookApi> bookApis) {
//
//            Log.d(LOG,"Thread ID: " + getId() + ". Completed");
//
//            if(!isCancelled()){
//                //setLoading(false);
//                mAdapter.swap(bookApis);
//            }
//
//        }
//
//        public void cancel() {
//            mCanceled = true;
//        }
//    }

}
