package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
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
public class FolderFragment extends Fragment implements FirebaseDatabaseHelper.OnDataSnapshotListener,
        ChildEventListener{

    private static final String TAG = FolderFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private MyFolderRecyclerViewAdapter myFolderRecyclerViewAdapter;
    private List<Folder> mFolderList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FolderFragment() {
    }

    public void updateFolderListByUserId(String userId){
        mFirebaseDatabaseHelper.fetchFolders(userId,this,this);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FolderFragment newInstance(int columnCount) {
        FolderFragment fragment = new FolderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(mFirebaseDatabaseHelper == null)
            mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);

        myFolderRecyclerViewAdapter = new MyFolderRecyclerViewAdapter(new ArrayList<Folder>(), mListener);

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
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
        Log.d(TAG, "DataSnapshot of folders Query: " + dataSnapshot != null ? dataSnapshot.toString() : "EMPTY");

        if(dataSnapshot.getValue() != null){

            List<Folder> folderList = new ArrayList<>();

            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                Folder folder = postSnapshot.getValue(Folder.class);
                folderList.add(folder);
            }

            mFolderList = folderList;
            myFolderRecyclerViewAdapter.swap(mFolderList);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

        if(dataSnapshot.getValue() != null){
            Folder folder = dataSnapshot.getValue(Folder.class);

            mFolderList.remove(folder);
            myFolderRecyclerViewAdapter.swap(mFolderList);

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
        
    }

    /**
     * These two methods have to be implemented by the adapter.
     *
     */

    public interface OnListFragmentInteractionDefaultButtonsListener {
        void onClickAddFolderListInteraction();
    }
}
