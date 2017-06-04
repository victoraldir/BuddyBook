package com.quartzodev.buddybook;

import android.app.Activity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
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
import android.support.v7.app.AlertDialog;
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
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.quartzodev.data.BookApi;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.data.User;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.fragments.FolderListFragment;
import com.quartzodev.fragments.SearchResultFragment;
import com.quartzodev.fragments.ViewPagerFragment;
import com.quartzodev.provider.SuggestionProvider;
import com.quartzodev.ui.BarcodeCaptureActivity;
import com.quartzodev.utils.ConnectionUtils;
import com.quartzodev.utils.DialogUtils;
import com.quartzodev.views.DynamicImageView;
import com.quartzodev.widgets.BuddyBookWidgetProvider;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FolderListFragment.OnListFragmentInteractionListener,
        FirebaseAuth.AuthStateListener,
        BookGridFragment.OnGridFragmentInteractionListener,
        SearchView.OnQueryTextListener,
        FirebaseDatabaseHelper.OnPaidOperationListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_USER_ID = "userId";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_BARCODE_CAPTURE = 2;
    private static final String KEY_PARCELABLE_USER = "userKey";
    private static final String KEY_CURRENT_QUERY = "queryKey";

    @BindView(R.id.main_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_message)
    TextView mTextViewMessage;
    @BindView(R.id.fragment_main_container)
    FrameLayout mFrameLayoutContainer;
    @BindView(R.id.fab)
    FloatingActionButton fab;

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
    private MenuItem mSearchItem;
    private FolderListFragment mRetainedFolderFragment;
    private String mCurrQuery;
    private List<Folder> mFolderList;
    private String mFolderListComma;
    private FrameLayout mFolderListContainer;
    private SearchRecentSuggestions mSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mSuggestions = new SearchRecentSuggestions(this,
                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);

        mContext = this;

        LinearLayout linearLayout = (LinearLayout) mNavigationView.getHeaderView(0); //LinearLayout Index
        mImageViewProfile = (ImageView) linearLayout.findViewById(R.id.main_imageview_user_photo);
        mTextViewUsername = (TextView) linearLayout.findViewById(R.id.main_textview_username);
        mTextViewTextEmail = (TextView) linearLayout.findViewById(R.id.main_textview_user_email);
        mFolderListContainer = (FrameLayout) linearLayout.findViewById(R.id.container_nav_header);

        //Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        mNavigationView.setNavigationItemSelectedListener(this);

        if(ConnectionUtils.isNetworkConnected(getApplication()) || FirebaseAuth.getInstance().getCurrentUser() != null) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // launch barcode activity.
                    Intent intent = new Intent(mContext, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //Automatic focus
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //Flash false

                    startActivityForResult(intent, RC_BARCODE_CAPTURE);

                }
            });

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawer.setDrawerListener(toggle);
            toggle.syncState();
        }

        if(mFirebaseAuth.getCurrentUser() != null){
            onSignedIn(mFirebaseAuth.getCurrentUser());
        }else{
            mFirebaseAuth.addAuthStateListener(this);
            launchLoginActivityResult();
        }
    }




    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void onSignedIn(final FirebaseUser firebaseUser) {

        if (mUser == null ||
                mUser.getUid().equals(firebaseUser.getUid())) {
            mUser = User.setupUserFirstTime(firebaseUser, mContext);

            mFirebaseDatabaseHelper.fetchUserById(mUser.getUid(), new FirebaseDatabaseHelper.OnDataSnapshotListener() {
                @Override
                public void onDataSnapshotListenerAvailable(DataSnapshot dataSnapshot) {
                    updateUser(dataSnapshot);
                }
            });
        }
    }

    private void updateUser(DataSnapshot dataSnapshot){

        if (dataSnapshot.getValue() != null) {

            Log.d(TAG, "User is already in database");
            mFirebaseDatabaseHelper.updateUserLastActivity(mUser.getUid());
            loadApplication();
        } else {
            mFirebaseDatabaseHelper.insertUser(mUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError == null){
                        mFirebaseDatabaseHelper.insertDefaulFolder(
                                mUser.getUid(),
                                mContext.getResources().getString(R.string.tab_my_books), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                relaunchActivity();
                            }
                        });
                    }

                }
            });
        }

        Snackbar.make(mCoordinatorLayout, getText(R.string.success_sign_in), Snackbar.LENGTH_SHORT).show();
    }

    public void updateWidget() {
        Intent intent = new Intent(this, BuddyBookWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int appWidgetIds[] = appWidgetManager
                .getAppWidgetIds(new ComponentName(mContext, BuddyBookWidgetProvider.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        sendBroadcast(intent);
    }

    private void loadProfileOnDrawer() {

        mTextViewTextEmail.setText(mUser.getEmail());
        mTextViewUsername.setText(mUser.getUsername());

        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(mImageViewProfile);
    }

    private void updateFolderList() {

        mRetainedFolderFragment = FolderListFragment.newInstance(mUser.getUid());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mFolderListContainer.getId(), mRetainedFolderFragment);
        transaction.commit();

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

            ((BookGridFragment) mCurrentGridFragment).updateContent(mUser.getUid(), folder.getId(), folder.getDescription(), BookGridFragment.FLAG_CUSTOM_FOLDER);

        } else {

            Fragment newFragment = BookGridFragment.newInstanceCustomFolder(mUser.getUid(), folder.getId(), folder.getDescription(), BookGridFragment.FLAG_CUSTOM_FOLDER);

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

            if (resultCode == ErrorCodes.UNKNOWN_ERROR){
                AuthUI.getInstance().signOut(this);
                finish();
            }

            if (resultCode == ErrorCodes.NO_NETWORK){
                mTextViewMessage.setVisibility(View.VISIBLE);
                mFrameLayoutContainer.setVisibility(View.GONE);
                mTextViewMessage.setText(getString(R.string.no_internet));

                final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            connectedRef.removeEventListener(this);
                            relaunchActivity();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });


            }else{
                mTextViewMessage.setVisibility(View.GONE);
                mFrameLayoutContainer.setVisibility(View.VISIBLE);
            }

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
        ((Activity) mContext).startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);




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

            mSuggestions.saveRecentQuery(query, null);

            mSearchResultFragment.executeSearch(query, null);
            mSearchView.clearFocus();

            mSearchView.setQuery(query, true);
        }

    }

//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        if (intent == null) {
//            intent = new Intent();
//        }
//
//        super.startActivityForResult(intent, requestCode);
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(ConnectionUtils.isNetworkConnected(getApplication()) || FirebaseAuth.getInstance().getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.main, menu);

            // Retrieve the SearchView and plug it into SearchManager
            //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            mSearchItem = menu.findItem(R.id.action_search);
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);

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

                    mSearchResultFragment = SearchResultFragment.newInstance(mUser.getUid(), mFolderId, null);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_main_container, mSearchResultFragment);
                    transaction.commitNow();

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {

                    FragmentManager fm = getSupportFragmentManager();

                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.remove(mSearchResultFragment);
                    transaction.commitNow();

                    return true;
                }
            });

            ComponentName componentName = new ComponentName(mContext, MainActivity.class);

            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_sign_out:

                mUser = null;
                AuthUI.getInstance().signOut(this);
                if(ConnectionUtils.isNetworkConnected(getApplication())) {
                    mFirebaseAuth.addAuthStateListener(this);
                    launchLoginActivityResult();
                }else{
                    relaunchActivity();
                }

                return true;

            case  R.id.action_clear_search:
                mSuggestions.clearHistory();
                Snackbar.make(mCoordinatorLayout,getString(R.string.search_clear),Snackbar.LENGTH_LONG).show();

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

        DialogUtils.alertDialogDeleteFolder(mContext, folder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFirebaseDatabaseHelper.deleteFolder(mUser.getUid(), folder.getId());
            }
        });

    }

    @Override
    public void onClickListenerFolderListInteraction(Folder folder) {

        mDrawer.closeDrawer(GravityCompat.START);
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

        if (firebaseUser != null) {
            mFirebaseAuth.removeAuthStateListener(this);
            relaunchActivity();
        }
    }

    private void loadApplication() {
        loadProfileOnDrawer();
        updateFolderList();
        updateWidget();
    }

    public void relaunchActivity() {
        Intent it = new Intent(mContext, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
        finish();
    }

    @Override
    public void onClickListenerBookGridInteraction(String folderId, BookApi book, DynamicImageView imageView) {

        mFirebaseDatabaseHelper.insertBookSearchHistory(mUser.getUid(), book); //Insert book

        Intent it = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailActivity.ARG_BOOK_ID, book.id);
        bundle.putString(DetailActivity.ARG_FOLDER_ID, folderId);
        bundle.putString(DetailActivity.ARG_USER_ID, mUser.getUid());
        bundle.putString(DetailActivity.ARG_FOLDER_LIST_ID, mFolderListComma);

        if (folderId == FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER) {
            bundle.putBoolean(DetailActivity.ARG_FLAG_IS_LENT_BOOK, true);
        } else {
            bundle.putBoolean(DetailActivity.ARG_FLAG_IS_LENT_BOOK, false);
        }

        Gson gson = new Gson();
        bundle.putString(DetailActivity.ARG_BOOK_JSON, gson.toJson(book));

        it.putExtras(bundle);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(it, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, imageView.getTransitionName()).toBundle());
        } else {
            startActivity(it);
        }


    }

    @Override
    public void onDeleteBookClickListener(final String mFolderId, final BookApi book) {
        mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), mFolderId, book);

        Snackbar.make(mCoordinatorLayout, getString(R.string.deleted_folder), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.redo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), mFolderId, book, (MainActivity) mContext);

                    }
                }).show();
    }

    @Override
    public void onAddBookToFolderClickListener(final String folderId, final BookApi book) {

        if (folderId.equals(FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER)) { //I have this operation

            Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.added_to_folder),
                    getString(R.string.tab_my_books)), Snackbar.LENGTH_SHORT).show();

            mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), folderId, book, this);
            return;
        }

        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String unFormatted = mFolderListComma.split(",")[which];
                final String id = unFormatted.split("=")[1];
                String name = unFormatted.split("=")[0];

                Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.added_to_folder), name), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.redo), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!id.equals(folderId)) {
                                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), folderId, book,(MainActivity) mContext);
                                    mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), id, book);
                                }
                            }
                        }).show();

                if (!id.equals(folderId)) {
                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), id, book, (MainActivity) mContext);
                    mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), folderId, book);
                }

            }
        });
    }

    @Override
    public void onCopyBookToFolderClickListener(final String folderId, final BookApi book) {


        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String unFormatted = mFolderListComma.split(",")[which];
                final String id = unFormatted.split("=")[1];
                String name = unFormatted.split("=")[0];

                Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.copied_to_folder), name), Snackbar.LENGTH_LONG)
                        .show();

                if (!id.equals(folderId)) {
                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), id, book, (MainActivity) mContext);
                }

            }
        });

    }

    @Override
    public void onLendBookClickListener(BookApi book) {
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUser.getUid(), book);
    }

    @Override
    public void onReturnBookClickListener(BookApi book) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUser.getUid(), book);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (query != null && !query.isEmpty()) {

            mSuggestions.saveRecentQuery(query, null);

            mSearchResultFragment.executeSearch(query, null);
            mSearchView.clearFocus();
        }

        return true;

    }

    public void searchHint(View view){
        mSearchItem.expandActionView();
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
    }

    /**
     * Makes sure to unregister the BroadcastReceiver when this activity is destroyed
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onInsertBook(boolean success) {
        if(!success){
            DialogUtils.alertDialogUpgradePro(this);
        }
    }

    @Override
    public void onInsertFolder(boolean success) {

        if(!success){
            DialogUtils.alertDialogUpgradePro(this);
        }
    }
}
