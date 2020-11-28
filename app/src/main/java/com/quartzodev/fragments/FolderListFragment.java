package com.quartzodev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.quartzodev.adapters.FolderListAdapter;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FolderListFragment extends Fragment {


    // TODO: Customize parameter argument names
    private static final String ARG_USER_ID = "user-id";
    private final String KEY_USER_ID = "userId";

    @BindView(R.id.btn_add_new_folder)
    Button mBtnAddFolder;

    private OnListFragmentInteractionListener mListener;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;
    private FirebaseAuth mFirebaseAuth;
    private FolderListAdapter mAdapter;
//    private String mUserId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FolderListFragment() {
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

//        outState.putString(KEY_USER_ID, mUserId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (mFirebaseAuth.getCurrentUser() != null) {

            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_USER_ID)) {
//                mUserId = savedInstanceState.getString(KEY_USER_ID);
            } else {
//                mUserId = mFirebaseAuth.getCurrentUser().getUid();
            }

            if (mFirebaseDatabaseHelper == null)
                mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

            if (getArguments() != null) {
//                mUserId = getArguments().getString(ARG_USER_ID);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_folder_list, container, false);

        ButterKnife.bind(this, root);

        FirebaseRecyclerOptions<Folder> options =
                new FirebaseRecyclerOptions.Builder<Folder>()
                        .setQuery(mFirebaseDatabaseHelper.fetchFolders()
                                        .orderByChild("description"),
                                Folder.class)
                        .build();

        mAdapter = new FolderListAdapter(options, mListener, getContext());

        RecyclerView recyclerView = root.findViewById(R.id.folder_list_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mBtnAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickAddFolderListInteraction();
            }
        });

        return root;
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
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
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
