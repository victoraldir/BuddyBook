package com.quartzodev.buddybook;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.quartzodev.api.BookApi;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.data.User;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.fragments.FolderListFragment;
import com.quartzodev.fragments.SearchResultFragment;
import com.quartzodev.fragments.ViewPagerFragment;
import com.quartzodev.provider.SuggestionProvider;
import com.quartzodev.ui.BarcodeCaptureActivity;
import com.quartzodev.utils.DialogUtils;
import com.quartzodev.views.DynamicImageView;
import com.quartzodev.widgets.CircleTransform;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FolderListFragment.OnListFragmentInteractionListener,
        FirebaseAuth.AuthStateListener,
        FirebaseDatabaseHelper.OnDataSnapshotListener,
        BookGridFragment.OnGridFragmentInteractionListener,
        SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 1;
    private static final int RC_BARCODE_CAPTURE = 2;
    private static final String KEY_PARCELABLE_USER = "userKey";
    private static final String KEY_CURRENT_QUERY = "queryKey";

    @BindView(R.id.main_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ViewPagerFragment mRetainedViewPagerFragment;
    private ImageView mImageViewProfile;
    private TextView mTextViewUsername;
    private TextView mTextViewTextEmail;
    private User mUser;
    private Context mContext;

    //Authentication entities
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    private String mFolderId;
    private SearchResultFragment mSearchResultFragment;
    private SearchView mSearchView;
    private FolderListFragment mRetainedFolderFragment;
    private String mCurrQuery;
    private List<Folder> mFolderList;
    private String mFolderListComma;
    private FrameLayout mFolderListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mContext = this;

        LinearLayout linearLayout = (LinearLayout) mNavigationView.getHeaderView(0); //LinearLayout Index
        mImageViewProfile = (ImageView) linearLayout.findViewById(R.id.main_imageview_user_photo);
        mTextViewUsername = (TextView) linearLayout.findViewById(R.id.main_textview_username);
        mTextViewTextEmail = (TextView) linearLayout.findViewById(R.id.main_textview_user_email);
        mFolderListContainer = (FrameLayout) linearLayout.findViewById(R.id.container_nav_header);

        //Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // launch barcode activity.
                Intent intent = new Intent(mContext, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //Automatic focus
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //Flash false

                startActivityForResult(intent, RC_BARCODE_CAPTURE);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                    }
                });

    }

    public void onSignedIn(final FirebaseUser firebaseUser) {

        if (mUser == null) {
            mUser = User.setupUserFirstTime(firebaseUser, mContext);
            mFirebaseDatabaseHelper.fetchUserById(mUser.getUid(), this);
            loadProfileOnDrawer();
        } else {
            loadProfileOnDrawer();
        }

        updateFolderList();
    }

    private void loadProfileOnDrawer() {

        mTextViewTextEmail.setText(mUser.getEmail());
        mTextViewUsername.setText(mUser.getUsername());

        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .centerCrop()
                .placeholder(android.R.drawable.sym_def_app_icon)
                .transform(new CircleTransform(mContext))
                .into(mImageViewProfile);
    }

    private void updateFolderList() {

        if (mRetainedFolderFragment == null) {

            mRetainedFolderFragment = FolderListFragment.newInstance(mUser.getUid());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(mFolderListContainer.getId(), mRetainedFolderFragment);
            transaction.commit();

        }

    }


    private void loadBooksPageView() {

        mRetainedViewPagerFragment = ViewPagerFragment.newInstance(mUser.getUid());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main_container, mRetainedViewPagerFragment);
        transaction.commitAllowingStateLoss();

    }

    private void loadCustomFolder(Folder folder) {

        if (folder == null) { //Load My Books folder
            loadBooksPageView();
            return;
        }

        Fragment mCurrentGridFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if (mCurrentGridFragment instanceof BookGridFragment) {

            ((BookGridFragment) mCurrentGridFragment).updateContent(mUser.getUid(), folder.getId(),folder.getDescription(), BookGridFragment.FLAG_CUSTOM_FOLDER);

        } else {

            Fragment newFragment = BookGridFragment.newInstanceCustomFolder(mUser.getUid(), folder.getId(),folder.getDescription(), BookGridFragment.FLAG_CUSTOM_FOLDER);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main_container, newFragment);
            transaction.addToBackStack(folder.getId());
            transaction.commit();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) finish();
        } else if (requestCode == RC_BARCODE_CAPTURE) {
            if (requestCode == RC_BARCODE_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        Log.d(TAG, "Barcode read: " + barcode.displayValue);

                        Fragment searchFragment = SearchResultFragment.newInstance(mUser.getUid(), mFolderId, barcode.displayValue);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_main_container, searchFragment).commit();

                    } else {
                        Log.d(TAG, "No barcode captured, intent data is null");
                    }
                }

            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_PARCELABLE_USER)) {
            mUser = (User) savedInstanceState.get(KEY_PARCELABLE_USER);
        }

        if (savedInstanceState.containsKey(KEY_CURRENT_QUERY)) {
            mCurrQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_PARCELABLE_USER, mUser);

        if (mSearchView != null && mSearchView.getQuery() != null && mSearchView.isEnabled()) {
            outState.putCharSequence(KEY_CURRENT_QUERY, mSearchView.getQuery().toString());
        }

        super.onSaveInstanceState(outState);
    }

    private void launchLoginActivityResult() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.LoginTheme)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void handleIntent(Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //TODO do my query
            Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            mSearchResultFragment.executeSearch(query, null);
            mSearchView.clearFocus();

            mSearchView.setQuery(query, true);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Retrieve the SearchView and plug it into SearchManager
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                return false;
            }
        });

        if (mCurrQuery != null) {
            //TODO open searchView programaticaly
            mSearchView.requestFocus();
            mSearchView.setQuery(mCurrQuery, true);
        }

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        mSearchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Toast.makeText(mContext, "Search view's been clicked", Toast.LENGTH_SHORT).show();

                mSearchResultFragment = SearchResultFragment.newInstance(mUser.getUid(), mFolderId, null);


                mSearchResultFragment.setRetainInstance(true);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_main_container, mSearchResultFragment).commit();

                mSearchView.requestFocus();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Toast.makeText(mContext, "Search's been closed", Toast.LENGTH_SHORT).show();

                FragmentManager fm = getSupportFragmentManager();

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.remove(mSearchResultFragment);
                transaction.replace(R.id.fragment_main_container, ViewPagerFragment.newInstance(mUser.getUid())); //TODO check the best way to get it done.
                transaction.commit();

                return true;
            }
        });

        ComponentName componentName = new ComponentName(mContext, MainActivity.class);

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:

                return true;

            case R.id.action_sign_out:

                mUser = null;
                AuthUI.getInstance().signOut(this);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_books) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLongClickListenerFolderListInteraction(final Folder folder) {
        Toast.makeText(mContext, folder.toString(), Toast.LENGTH_SHORT).show();

        DialogUtils.alertDialogDeleteFolder(mContext, folder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFirebaseDatabaseHelper.deleteFolder(mUser.getUid(), folder.getId());
            }
        });

    }

    @Override
    public void onClickListenerFolderListInteraction(Folder folder) {

        drawer.closeDrawer(GravityCompat.START);
        mFolderId = folder != null ? folder.getId() : null;
        loadCustomFolder(folder);
    }

    @Override
    public void onClickAddFolderListInteraction() {
        DialogUtils.alertDialogAddFolder(this,
                mFolderList,
                FirebaseDatabaseHelper.getInstance(),
                mUser.getUid());
    }

    @Override
    public void onFolderListIsAvailable(List<Folder> folderList, String folderListComma) {
        mFolderList = folderList;
        mFolderListComma = folderListComma;
        loadBooksPageView();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            launchLoginActivityResult();
        } else {
            onSignedIn(firebaseUser);

        }
    }

    @Override
    public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() != null) {
            Log.d(TAG, "User is already in database");
            mFirebaseDatabaseHelper.updateUserLastActivity(mUser.getUid());
        } else {
            mFirebaseDatabaseHelper.insertUser(mUser);
        }

        Snackbar.make(mCoordinatorLayout, getText(R.string.success_sign_in), Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onClickListenerBookGridInteraction(String folderId, BookApi book, DynamicImageView imageView) {
        Toast.makeText(mContext, "Should open details for book id: " + book.getVolumeInfo().getTitle() + " Folder id: " + folderId, Toast.LENGTH_SHORT).show();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        mFirebaseDatabaseHelper.insertBookSearchHistory(mUser.getUid(), book); //Insert book

        Intent it = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailActivity.ARG_BOOK_ID, book.id);
        bundle.putString(DetailActivity.ARG_FOLDER_ID, folderId);
        bundle.putString(DetailActivity.ARG_USER_ID, mUser.getUid());
        bundle.putString(DetailActivity.ARG_FOLDER_LIST_ID, mFolderListComma);

        //if(currentFragment instanceof  SearchResultFragment){
        Gson gson = new Gson();
        bundle.putString(DetailActivity.ARG_BOOK_JSON, gson.toJson(book));
        //}

        it.putExtras(bundle);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(it, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, imageView.getTransitionName()).toBundle());
        } else {
            startActivity(it);
        }


    }

    @Override
    public void onDeleteBookClickListener(final String mFolderId, final BookApi book) {
        mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(),mFolderId,book);

        Snackbar.make(mCoordinatorLayout, getString(R.string.deleted_folder), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.redo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), mFolderId, book);

                    }
                }).show();
    }

    @Override
    public void onAddBookToFolderClickListener(final String folderId, final BookApi book) {

        if(folderId.equals(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER)){ //I have this operation

            Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.added_to_folder),
                    getString(R.string.tab_my_books)), Snackbar.LENGTH_SHORT).show();

            mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(),folderId,book);
            return;
        }

        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String unFormatted = mFolderListComma.split(",")[which];
                final String id = unFormatted.split("=")[1];
                String name = unFormatted.split("=")[0];

                Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.added_to_folder),name), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.redo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!id.equals(folderId)) {
                                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), folderId, book);
                                    mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), id, book);
                                }
                            }
                        }).show();

                if(!id.equals(folderId)) {
                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), id, book);
                    mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), folderId, book);
                }

            }
        });
    }

    @Override
    public void  onCopyBookToFolderClickListener(final String folderId, final BookApi book){


        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String unFormatted = mFolderListComma.split(",")[which];
                final String id = unFormatted.split("=")[1];
                String name = unFormatted.split("=")[0];

                Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.copied_to_folder),name), Snackbar.LENGTH_LONG)
                        .show();

                if(!id.equals(folderId)) {
                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), id, book);
                }

            }
        });

    }

    @Override
    public void onLendBookClickListener(BookApi book) {
        DialogUtils.alertDialogLendBook(this, mFirebaseDatabaseHelper,mUser.getUid(),book);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (query != null && !query.isEmpty()) {
//            setProgressBar(true);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            mSearchResultFragment.executeSearch(query, null);
            mSearchView.clearFocus();
        }

        return true;

    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText != null && !newText.isEmpty()) {
            mSearchResultFragment.executeSearch(newText, null);
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(this);
    }

    /**
     * Makes sure to unregister the BroadcastReceiver when this activity is destroyed
     */
    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
    }
}
