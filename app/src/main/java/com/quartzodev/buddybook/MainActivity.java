package com.quartzodev.buddybook;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.test.espresso.IdlingResource;
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
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.quartzodev.IdlingResource.SimpleIdlingResource;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.data.User;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.fragments.DetailActivityFragment;
import com.quartzodev.fragments.FolderListFragment;
import com.quartzodev.fragments.SearchResultFragment;
import com.quartzodev.fragments.ViewPagerFragment;
import com.quartzodev.provider.SuggestionProvider;
import com.quartzodev.ui.BarcodeCaptureActivity;
import com.quartzodev.utils.ConnectionUtils;
import com.quartzodev.utils.Constants;
import com.quartzodev.utils.DialogUtils;
import com.quartzodev.utils.PathUtils;
import com.quartzodev.views.DynamicImageView;
import com.quartzodev.widgets.BuddyBookWidgetProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FolderListFragment.OnListFragmentInteractionListener,
        BookGridFragment.OnGridFragmentInteractionListener,
        SearchView.OnQueryTextListener,
        FirebaseDatabaseHelper.OnPaidOperationListener,
        DetailActivityFragment.OnDetailInteractionListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_BARCODE_CAPTURE = 2;
    private static final int RC_SIGN_IN = 5;
    private static final int RC_PICKFILE = 6;
    private static final int RC_INSERT_BOOK = 7;

    public static final String EXTRA_USER_ID = "userId";
    private static final String KEY_PARCELABLE_USER = "userKey";
    private static final String KEY_FOLDER_ID = "folderIdKey";
    private static final String KEY_CURRENT_QUERY = "queryKey";
    private static final String KEY_FLAG_SEARCH_OPEN = "flagSearchOpenKey";
    private static final int TOTAL_SEARCH_RESULT = 40;

    private final int NO_INTERNET = 1;
    private final int LOADING = 2;
    private final int READY = 3;

    @BindView(R.id.main_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar2)
    @Nullable
    Toolbar mToolbar2;
    @BindView(R.id.main_message)
    TextView mTextViewMessage;
    @BindView(R.id.fragment_main_container)
    FrameLayout mFrameLayoutContainer;
    @BindView(R.id.loading_container)
    RelativeLayout mLoadingContainer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.main_progress_bar)
    ProgressBar mProgressBar;

    private User mUser;
    private Context mContext;

    //Authentication entities
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    private String mFolderId;
    private String mFolderName;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private String mCurrQuery;
    private boolean mFlagSeachBarOpen;
    private List<Folder> mFolderList;
    private String mFolderListComma;
    private SearchRecentSuggestions mSuggestions;
    private boolean mTwoPane;
    private Snackbar mSnackbarNoInternet;
    private SharedPreferences mPrefs;
    private LinearLayout mTabLinearLayoutLeft;
    private LinearLayout mTabLinearLayoutRight;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if(findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }

        // Get the IdlingResource instance
        getIdlingResource();

        mContext = this;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
        mNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mSuggestions = new SearchRecentSuggestions(this,
                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);

        if(savedInstanceState != null){
            mUser = (User) savedInstanceState.get(KEY_PARCELABLE_USER);
            mFolderId = savedInstanceState.getString(KEY_FOLDER_ID);
        }

        setupFab();

        if(BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE)) initAdView();

        checkUserIsLoged();
    }

    private void checkUserIsLoged(){

        if (mFirebaseAuth.getCurrentUser() != null) {
            onSignedIn(mFirebaseAuth.getCurrentUser());
        } else {
            if (ConnectionUtils.isNetworkConnected(getApplication())) {
                launchLoginActivityResult();
            }else{
                showStatus(NO_INTERNET);
            }
        }
    }

    public void setupFab(){
        if (ConnectionUtils.isNetworkConnected(getApplication()) || FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFab.setVisibility(View.VISIBLE);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // launch barcode activity.
                    Intent intent = new Intent(mContext, BarcodeCaptureActivity.class);
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true); //Automatic focus
                    intent.putExtra(BarcodeCaptureActivity.UseFlash, false); //Flash false

                    startActivityForResult(intent, RC_BARCODE_CAPTURE);

                }
            });
        }
    }

    public FloatingActionButton getFab(){
        return mFab;
    }

    private void initAdView(){
        //Ad main
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onSignedIn(final FirebaseUser firebaseUser) {

        if (mUser == null || mUser.getUid().equals(firebaseUser.getUid())) {

            mUser = User.setupUserFirstTime(firebaseUser, mContext);

            mFirebaseDatabaseHelper.fetchUserById(mUser.getUid(), new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    updateUser(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUser(DataSnapshot dataSnapshot) {

        if(mUser != null) {

            if (dataSnapshot.getValue() != null) {

                mFirebaseDatabaseHelper.updateUserLastActivity(mUser.getUid());
                loadFolderListFragment();
                Snackbar.make(mCoordinatorLayout, getText(R.string.success_sign_in), Snackbar.LENGTH_SHORT).show();

            } else {

                mFirebaseDatabaseHelper.insertUser(mUser, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError == null) {
                            mFirebaseDatabaseHelper.insertDefaulFolder(
                                    mUser.getUid(),
                                    mContext.getResources().getString(R.string.tab_my_books), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            Snackbar.make(mCoordinatorLayout, getText(R.string.success_sign_in), Snackbar.LENGTH_SHORT).show();
                                            loadFolderListFragment();
                                        }
                                    });
                        }

                    }
                });

            }
        }
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

    public void doNothing(View view) {

    }

    private void loadProfileOnDrawer() {

        if(mUser != null) {

            LinearLayout linearLayout = (LinearLayout) mNavigationView.getHeaderView(0); //LinearLayout Index
            ImageView mImageViewProfile = linearLayout.findViewById(R.id.main_imageview_user_photo);
            TextView mTextViewUsername = linearLayout.findViewById(R.id.main_textview_username);
            TextView mTextViewTextEmail = linearLayout.findViewById(R.id.main_textview_user_email);

            mTextViewTextEmail.setText(mUser.getEmail());
            mTextViewUsername.setText(mUser.getUsername());

            GlideApp.with(this)
                    .load(mUser.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageViewProfile);

            //Test
            if (mIdlingResource != null)
                mIdlingResource.setIdleState(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == ErrorCodes.UNKNOWN_ERROR) {
                AuthUI.getInstance().signOut(this);
            }

            if (resultCode == ErrorCodes.NO_NETWORK) {

                showStatus(NO_INTERNET);

            } else {
                checkUserIsLoged();
            }

        } else if (requestCode == RC_BARCODE_CAPTURE && resultCode == CommonStatusCodes.SUCCESS) {

            if (data != null) {

                Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                Fragment searchFragment = SearchResultFragment.newInstance(mFolderId, barcode.displayValue, R.menu.menu_search_result);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_main_container, searchFragment).commitAllowingStateLoss();

            }
        }else if (requestCode == RC_PICKFILE && resultCode == CommonStatusCodes.SUCCESS_CACHE){
            Log.d(TAG,data.getDataString());

            try {

                Uri uri = data.getData();
                String mimeType = getContentResolver().getType(uri);

                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                Log.d(TAG,"mimeType: " + mimeType);
                Log.d(TAG,"nameView: " + returnCursor.getString(nameIndex));
                Log.d(TAG,"sizeView: " + returnCursor.getLong(sizeIndex));

                File file = new File(PathUtils.getPath(mContext,uri));

                Log.d(TAG,"File path: " + PathUtils.getPath(mContext,uri));
                Log.d(TAG,"File absolute path: " + PathUtils.getPath(mContext,uri));
                Log.d(TAG,"File exists? : " + file.exists());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }

        }
    }

    public void showStatus(int statusCode){

        switch (statusCode){
            case NO_INTERNET:

                mTextViewMessage.setVisibility(View.VISIBLE);
                mFrameLayoutContainer.setVisibility(View.GONE);
                mTextViewMessage.setText(getString(R.string.no_internet));

                mLoadingContainer.setVisibility(View.GONE);
                mFab.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mToolbar.setVisibility(View.GONE);
                //mTabLayout.setVisibility(View.GONE);

                final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            connectedRef.removeEventListener(this);
                            checkUserIsLoged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });

                break;
            case LOADING:

                mFrameLayoutContainer.setVisibility(View.GONE);

                mLoadingContainer.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mTextViewMessage.setVisibility(View.VISIBLE);
                mTextViewMessage.setText("Loading");

                mFab.setVisibility(View.GONE);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mToolbar.setVisibility(View.GONE);

                break;
            case READY:

                mTextViewMessage.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mFrameLayoutContainer.setVisibility(View.VISIBLE);
                mLoadingContainer.setVisibility(View.GONE);

                mFab.setVisibility(View.VISIBLE);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mToolbar.setVisibility(View.VISIBLE);

                break;
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_PARCELABLE_USER)) {
            mUser = (User) savedInstanceState.get(KEY_PARCELABLE_USER);
        }

        if (savedInstanceState.containsKey(KEY_FLAG_SEARCH_OPEN)) {
            mCurrQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mFlagSeachBarOpen = savedInstanceState.getBoolean(KEY_FLAG_SEARCH_OPEN);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARCELABLE_USER, mUser);
        outState.putString(KEY_FOLDER_ID,mFolderId);

        if (mSearchView != null && mSearchView.isEnabled()) {
            outState.putCharSequence(KEY_CURRENT_QUERY, mSearchView.getQuery().toString());
            outState.putBoolean(KEY_FLAG_SEARCH_OPEN, mSearchView.isShown());

        }
    }

    private void launchLoginActivityResult() {

        //Test
        if(mIdlingResource != null)
            mIdlingResource.setIdleState(false);

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);

        overridePendingTransition(R.anim.fui_slide_in_right, R.anim.fui_slide_in_right);


    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void handleIntent(Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            mSuggestions.saveRecentQuery(query, null);
            mSearchView.clearFocus();
            mSearchView.setQuery(query, true);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void setIntentShareMenu(Intent intent){

        if(mToolbar2 != null) {
            MenuItem menushareItem = mToolbar2.getMenu().findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menushareItem);
            menushareItem.setVisible(true);
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mToolbar2 == null) {

            if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE)) {
                getMenuInflater().inflate(R.menu.main, menu);
            } else {
                getMenuInflater().inflate(R.menu.main_paid, menu);
            }

        } else {
            if (!mToolbar.getMenu().hasVisibleItems())
                mToolbar.inflateMenu(R.menu.main_toolbar1);

            if (!mToolbar2.getMenu().hasVisibleItems()) {
                if (BuildConfig.FLAVOR.equals(Constants.FLAVOR_FREE)) {
                    mToolbar2.inflateMenu(R.menu.main_toolbar2);
                } else {
                    mToolbar2.inflateMenu(R.menu.main_toolbar2_paid);
                }
            }
        }


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

        if (mFlagSeachBarOpen) {
            mSearchItem.expandActionView();
            mSearchView.setQuery(mCurrQuery, true);
            mSearchView.clearFocus();
        }

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        final String search_tag = "SEARCH_TAG";

        mSearchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {

                if (!ConnectionUtils.isNetworkConnected(mContext)) {
                    mSnackbarNoInternet = Snackbar.make(mCoordinatorLayout, R.string.no_internet_search, Snackbar.LENGTH_INDEFINITE);
                    mSnackbarNoInternet.show();
                }

                ViewPagerFragment fragment = ViewPagerFragment.newInstance(ViewPagerFragment.SEARCH_VIEW_PAGER, mFolderId, null, null);
                loadFragment(fragment,search_tag);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                removeFragment(search_tag);

                if (mSnackbarNoInternet != null && mSnackbarNoInternet.isShown()) {
                    mSnackbarNoInternet.dismiss();
                }

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

            case R.id.action_sign_out:
                mUser = null;
                AuthUI.getInstance().signOut(this);
                clearFragmentBackStack();
                launchLoginActivityResult();

                return true;

            case R.id.action_clear_search:
                mSuggestions.clearHistory();
                Snackbar.make(mCoordinatorLayout, getString(R.string.search_clear), Snackbar.LENGTH_LONG).show();
                return true;

            case R.id.action_add_book:

                lauchInsertEditActivity(null);

                break;

            case R.id.action_about:
                new LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .start(this);
                break;
            case R.id.action_to_premium:

                DialogUtils.alertDialogUpgradePro(this);

                break;
            case R.id.action_sort:

                DialogUtils.alertDialogSortList(mContext,mCoordinatorLayout);

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void lauchInsertEditActivity(String bookId){

        Intent it = new Intent(mContext, InsertEditBookActivity.class);

        if(bookId != null)
            it.putExtra(InsertEditBookActivity.ARG_BOOK_ID,bookId);

        it.putExtra(InsertEditBookActivity.ARG_FOLDER_NAME, mFolderName);
        it.putExtra(InsertEditBookActivity.ARG_FOLDER_ID, mFolderId);
        it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(it, RC_INSERT_BOOK);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLongClickListenerFolderListInteraction(final Folder folder) {

        DialogUtils.alertDialogDeleteFolder(mContext, folder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFirebaseDatabaseHelper.deleteFolder(mUser.getUid(), folder.getId());
                onClickListenerFolderListInteraction(null);
                Snackbar.make(mCoordinatorLayout,getText(R.string.folder_deleted),Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClickListenerFolderListInteraction(Folder folder) {

        mDrawer.closeDrawer(GravityCompat.START);
        mFolderId = folder != null ? folder.getId() : null;
        if(mFolderId == null){

            mToolbar.findViewById(R.id.toolbar_container).setVisibility(View.VISIBLE);
            mFolderName = getString(R.string.tab_my_books);
            loadMainViewPagerFragment();
            loadFragment(ViewPagerFragment.newInstance(ViewPagerFragment.MAIN_VIEW_PAGER,
                    mFolderId,null,null), null);

        }else{

            mToolbar.findViewById(R.id.toolbar_container).setVisibility(View.GONE);
            mFolderName = folder.getDescription();
            mToolbar.setTitle(mFolderName);

            if(folder.getBooks() != null){
                mToolbar.setSubtitle( String.format(getString(R.string.number_of_books),folder.getBooks().size()));
            }else{
                mToolbar.setSubtitle( String.format(getString(R.string.number_of_books),0));
            }

            loadFragment(BookGridFragment.newInstance(mFolderId,R.menu.menu_my_books), null);
        }
    }

    @Override
    public void onClickAddFolderListInteraction() {
        DialogUtils.alertDialogAddFolder(this,
                mFolderList,
                FirebaseDatabaseHelper.getInstance(),
                mUser.getUid());
    }

    private void clearFragmentBackStack(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    //Comes first
    private void loadFolderListFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nav_header, new FolderListFragment());
        transaction.commitAllowingStateLoss();
    }

    //Second step
    private void loadApplication() {
        loadProfileOnDrawer();
        updateWidget();
        loadMainViewPagerFragment();
        setupRateApp();
        showStatus(READY);
    }

    public void setupRateApp(){
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(3) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void loadMainViewPagerFragment(){
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if (frag == null) {
            loadFragment(ViewPagerFragment.newInstance(ViewPagerFragment.MAIN_VIEW_PAGER,
                    mFolderId,null,null), null);
        }
    }

    private void loadFragment(Fragment fragment, String tag){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(tag != null) {
            transaction.add(R.id.fragment_main_container, fragment, tag);
        }else{
            transaction.replace(R.id.fragment_main_container, fragment);
        }

        transaction.commitNow();
        checkTabLayout();
    }

    private void removeFragment(String tag){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
        }

        checkTabLayout();
    }

    public void checkTabLayout(){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if (currentFragment instanceof ViewPagerFragment) {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setupWithViewPager(((ViewPagerFragment) currentFragment).getViewPager());
            setupTabIcons(((ViewPagerFragment) currentFragment).getTypeFragment());
        }else{
            mTabLayout.setVisibility(View.GONE);
        }
    }

    //Gets fired when FolderListFragment is done
    @Override
    public void onFolderListIsAvailable(List<Folder> folderList, String folderListComma) {
        mFolderList = folderList;
        mFolderListComma = folderListComma;

        loadApplication();
    }

    @Override
    public void onClickListenerBookGridInteraction(String folderId, Book book, DynamicImageView imageView) {

        if (mTwoPane) {

            Gson gson = new Gson();
            DetailActivityFragment newFragment = DetailActivityFragment.newInstance(mUser.getUid(), book.getId(), folderId, mFolderListComma, gson.toJson(book));

            getSupportFragmentManager().popBackStackImmediate();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detail_container, newFragment).commitAllowingStateLoss();

        }else {

            Intent it = new Intent(this, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(DetailActivity.ARG_BOOK_ID, book.getId());
            bundle.putString(DetailActivity.ARG_FOLDER_ID, folderId);
            bundle.putString(DetailActivity.ARG_USER_ID, mUser.getUid());
            bundle.putString(DetailActivity.ARG_FOLDER_LIST_ID, mFolderListComma);

            Gson gson = new Gson();
            bundle.putString(DetailActivity.ARG_BOOK_JSON, gson.toJson(book));

            it.putExtras(bundle);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(it, ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, imageView.getTransitionName()).toBundle());
            } else {
                startActivity(it);
            }

        }
    }

    private void showToolbar(){
        if (mToolbar.getParent() instanceof AppBarLayout){
            ((AppBarLayout)mToolbar.getParent()).setExpanded(true,true);
        }
    }

    @Override
    public void onDeleteBookClickListener(final String mFolderId, final Book book) {

        mFirebaseDatabaseHelper.deleteBookFolder(mUser.getUid(), mFolderId, book);

        showToolbar();

        Snackbar.make(mCoordinatorLayout, getString(R.string.book_deleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.redo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), mFolderId, book, (MainActivity) mContext);
                    }
                }).show();
    }

    @Override
    public void onAddBookToFolderClickListener(final String folderId, final Book book) {

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
                                    mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), folderId, book, (MainActivity) mContext);
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
    public void onCopyBookToFolderClickListener(final String folderId, final Book book) {


        DialogUtils.alertDialogListFolder(mContext, mFolderListComma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String unFormatted = mFolderListComma.split(",")[which];
                final String id = unFormatted.split("=")[1];
                String name = unFormatted.split("=")[0];

                Snackbar.make(mCoordinatorLayout, String.format(getString(R.string.copied_to_folder), name), Snackbar.LENGTH_LONG)
                        .show();

                mFirebaseDatabaseHelper.insertBookFolder(mUser.getUid(), id, book, (MainActivity) mContext);

            }
        });

    }

    @Override
    public void onLendBookClickListener(Book book, MenuItem menuItem) {
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUser.getUid(), mFolderId, book, menuItem);
    }

    @Override
    public void onReturnBookClickListener(Book book) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUser.getUid(), mFolderId, book);
    }

    @Override
    public void onEditListener(Book book) {
        lauchInsertEditActivity(book.getId());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (query != null && !query.isEmpty()) {

            mSuggestions.saveRecentQuery(query, null);

            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

            if(frag instanceof ViewPagerFragment){
                ((ViewPagerFragment) frag).executeSearch(query, TOTAL_SEARCH_RESULT);
            }


            mSearchView.clearFocus();
        }

        return true;

    }


    public void refreshCurrentFragment(){
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if(frag instanceof BookGridFragment){
            ((BookGridFragment) frag).refresh();
        }else if(frag instanceof ViewPagerFragment){
            ((ViewPagerFragment) frag).refresh();
        }

        Snackbar.make(mCoordinatorLayout,"We should refresh sort here",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText != null && !newText.isEmpty()) {
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);
            if(frag instanceof ViewPagerFragment){
                ((ViewPagerFragment) frag).executeSearch(newText, TOTAL_SEARCH_RESULT);
            }

        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * Makes sure to unregister the BroadcastReceiver when this activity is destroyed
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(key != null && !key.equals("com.facebook.appevents.SessionInfo.sessionEndTime")){
                refreshCurrentFragment();
            }

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onInsertBook(boolean success) {
        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        }
    }

    @Override
    public void onInsertFolder(boolean success) {

        if (!success) {
            DialogUtils.alertDialogUpgradePro(this);
        }
    }

    public TabLayout getTabLayout(){
        return mTabLayout;
    }

    public void setupTabIcons(String typeFragment) {

        TabLayout.Tab tab1 =  mTabLayout.getTabAt(0);
        TabLayout.Tab tab2 =  mTabLayout.getTabAt(1);

        if(tab1 == null || mTabLinearLayoutLeft == null) {
            mTabLinearLayoutLeft = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        }

        if(tab2 == null || mTabLinearLayoutRight == null){
            mTabLinearLayoutRight = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        }

        if(typeFragment.equals(ViewPagerFragment.MAIN_VIEW_PAGER)) {
            ((ImageView) mTabLinearLayoutLeft.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_favorite_border);
            ((TextView) mTabLinearLayoutLeft.findViewById(R.id.tab_title)).setText(getString(R.string.tab_my_books));
        }else{
            ((ImageView) mTabLinearLayoutLeft.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_action_globe);
            ((TextView) mTabLinearLayoutLeft.findViewById(R.id.tab_title)).setText(getString(R.string.tab_search_online));
        }

        mTabLayout.getTabAt(0).setCustomView(mTabLinearLayoutLeft);

        if(typeFragment.equals(ViewPagerFragment.MAIN_VIEW_PAGER)) {
            ((ImageView) mTabLinearLayoutRight.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_library_books);
            ((TextView) mTabLinearLayoutRight.findViewById(R.id.tab_title)).setText(getString(R.string.tab_top_books));
        }else{
            ((ImageView) mTabLinearLayoutRight.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_action_folder_closed);
            if(mFolderName != null) {
                ((TextView) mTabLinearLayoutRight.findViewById(R.id.tab_title)).setText(mFolderName);
            }else{
                ((TextView) mTabLinearLayoutRight.findViewById(R.id.tab_title)).setText(getString(R.string.tab_my_books));
            }
        }

        mTabLayout.getTabAt(1).setCustomView(mTabLinearLayoutRight);
    }

    @Override
    public void onLendBook(Book bookApi) {
        DialogUtils.alertDialogLendBook(this, mCoordinatorLayout, mFirebaseDatabaseHelper, mUser.getUid(),mFolderId, bookApi,null);
    }

    @Override
    public void onReturnBook(Book bookApi) {
        DialogUtils.alertDialogReturnBook(this, mFirebaseDatabaseHelper, mUser.getUid(),mFolderId, bookApi);
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

}
