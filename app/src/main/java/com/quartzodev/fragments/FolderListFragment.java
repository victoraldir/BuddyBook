package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.quartzodev.adapters.FolderListAdapter;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FolderListFragment extends Fragment implements FirebaseDatabaseHelper.OnDataSnapshotListener,
        ChildEventListener {

    private static final String TAG = FolderListFragment.class.getSimpleName();
    // TODO: Customize parameter argument names
    private static final String ARG_USER_ID = "user-id";
    private final String KEY_USER_ID = "userId";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private FolderListAdapter myFolderRecyclerViewAdapter;
    private List<Folder> mFolderList;
    private String mUserId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FolderListFragment() {
        mFolderList = new ArrayList<>();
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
    }

//    public void updateFolderListByUserId(String userId){
//        mUserId = userId;
//        mFirebaseDatabaseHelper.fetchFolders(userId,this,this);
//    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FolderListFragment newInstance(String userId) {
        FolderListFragment fragment = new FolderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateFolderListByUserId() {
        mFirebaseDatabaseHelper.fetchFolders(mUserId, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString(KEY_USER_ID, mUserId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USER_ID)) {
            mUserId = savedInstanceState.getString(KEY_USER_ID);
//            updateFolderListByUserId(mUserId);
        }

        if (mFirebaseDatabaseHelper == null)
            mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirebaseDatabaseHelper.fetchFolders(mUserId, this);
        mFirebaseDatabaseHelper.attachFetchFolders(mUserId, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);

        myFolderRecyclerViewAdapter = new FolderListAdapter(new ArrayList<Folder>(), mListener);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(myFolderRecyclerViewAdapter);
        }
        return view;
    }

    public List<Folder> getmFolderList() {
        return mFolderList;
    }

    public String getmFolderListCommaSeparated() {

        List<String> stringList = new ArrayList<>();

        for (Folder folder : mFolderList) {
            stringList.add(folder.getDescription() + "=" + folder.getId());
        }

        //Default folder My Books
        stringList.add(getString(R.string.tab_my_books) + "=" + "myBooksFolder");

        return android.text.TextUtils.join(",", stringList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFirebaseDatabaseHelper.detachFetchFolders(mUserId, this);
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
        Log.d(TAG, "DataSnapshot of folders Query: " + dataSnapshot != null ? dataSnapshot.toString() : "EMPTY");

        List<Folder> folderList = new ArrayList<>();

        if (dataSnapshot.getValue() != null) {

            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Folder folder = postSnapshot.getValue(Folder.class);
                /**
                 * Just to don't list My Books on the RecycleView
                 */
                if (folder.getId() != null && folder.getDescription() != null &&
                        !folder.getDescription().equals(getString(R.string.tab_my_books))) {

                    folderList.add(folder);
                }

            }

            mFolderList = folderList;
            myFolderRecyclerViewAdapter.swap(mFolderList);

        }

        if (mListener != null)
            mListener.onFolderListIsAvailable(folderList, getmFolderListCommaSeparated());
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        if (isAdded()) {
            if (mFolderList != null && dataSnapshot.getValue() != null) {
                Folder folder = dataSnapshot.getValue(Folder.class);

                if (!mFolderList.contains(folder) && !folder.getDescription().equals(getString(R.string.tab_my_books))) {
                    mFolderList.add(folder);
                    myFolderRecyclerViewAdapter.swap(mFolderList);

                    mListener.onFolderListIsAvailable(mFolderList, getmFolderListCommaSeparated());
                }

            }
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            Folder folder = dataSnapshot.getValue(Folder.class);

            mFolderList.remove(folder);
            myFolderRecyclerViewAdapter.swap(mFolderList);

            mListener.onFolderListIsAvailable(mFolderList, getmFolderListCommaSeparated());

        }

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLongClickListenerFolderListInteraction(Folder folder);

        void onClickListenerFolderListInteraction(Folder folder);

        void onClickAddFolderListInteraction();

        void onFolderListIsAvailable(List<Folder> folderList, String folderListComma);
    }

    /**
     * These two methods have to be implemented by the adapter.
     *
     */


}
