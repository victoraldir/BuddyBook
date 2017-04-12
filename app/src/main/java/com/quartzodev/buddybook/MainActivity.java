package com.quartzodev.buddybook;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.quartzodev.api.BookApi;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.data.User;
import com.quartzodev.fragments.BookGridFragment;
import com.quartzodev.fragments.FolderListFragment;
import com.quartzodev.fragments.ViewPagerFragment;
import com.quartzodev.provider.SuggestionProvider;
import com.quartzodev.utils.DialogUtils;
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
        BookGridFragment.OnGridFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int NUM_PAGES = 2;
    private static final int RC_SIGN_IN = 1;
    private static final String KEY_PARCELABLE_USER = "userKey";

    @BindView(R.id.main_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;


    private ImageView mImageViewProfile;
    private TextView mTextViewUsername;
    private TextView mTextViewTextEmail;

    private User mUser;
    private Context mContext;

    //Authentication entities
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;

        LinearLayout linearLayout = (LinearLayout) mNavigationView.getHeaderView(0); //LinearLayout Index
        mImageViewProfile = (ImageView) linearLayout.findViewById(R.id.main_imageview_user_photo);
        mTextViewUsername = (TextView) linearLayout.findViewById(R.id.main_textview_username);
        mTextViewTextEmail = (TextView) linearLayout.findViewById(R.id.main_textview_user_email);

        //Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

    }

    public void onSignedIn(final FirebaseUser firebaseUser) {

        if (mUser == null) {
            mUser = User.setupUserFirstTime(firebaseUser, mContext);
            firebaseDatabaseHelper.fetchUserById(mUser.getUid(), this);
        }

        loadProfileOnDrawer();
        loadBooksPageView();
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

    private void updateFolderList(){
        FolderListFragment folderFragment = (FolderListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (folderFragment != null) {
            folderFragment.updateFolderListByUserId(mUser.getUid());
        }
    }

    private void loadBooksPageView() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if(fragment instanceof ViewPagerFragment){

        }else{
            ViewPagerFragment newFragment = ViewPagerFragment.newInstance(mUser.getUid());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main_container, newFragment);
            transaction.addToBackStack(null);


            transaction.commit();
        }
    }

    private void loadCustomFolder(String folderId) {

        if(folderId == null){ //Load My Books folder
            loadBooksPageView();
            return;
        }

        Fragment newFragment = BookGridFragment.newInstanceCustomFolder(mUser.getUid(),folderId, BookGridFragment.FLAG_CUSTOM_FOLDER);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) finish();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(KEY_PARCELABLE_USER)) {
            mUser = (User) savedInstanceState.get(KEY_PARCELABLE_USER);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_PARCELABLE_USER, mUser);

        super.onSaveInstanceState(outState);
    }

    private void launchLoginActivityResult() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
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

        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //TODO do my query
            Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        ComponentName componentName = new ComponentName(mContext, MainActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

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
                firebaseDatabaseHelper.deleteFolder(mUser.getUid(),folder.getId());
            }
        });

    }

    @Override
    public void onClickListenerFolderListInteraction(Folder folder) {

        drawer.closeDrawer(GravityCompat.START);

        loadCustomFolder(folder != null ? folder.getDescription() : null);
    }

    @Override
    public void onClickAddFolderListInteraction() {
        DialogUtils.alertDialogAddFolder(this,
                getSupportFragmentManager(),
                FirebaseDatabaseHelper.getInstance(),
                mUser.getUid());
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
            firebaseDatabaseHelper.updateUserLastActivity(mUser.getUid());
        } else {
            firebaseDatabaseHelper.insertUser(mUser);
        }

        Snackbar.make(mCoordinatorLayout, getText(R.string.success_sign_in), Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void onClickListenerBookGridInteraction(String folderId, BookApi book) {
        Toast.makeText(mContext,"Should open details for book id: " + book.getVolumeInfo().getTitle() + " Folder id: " + folderId,Toast.LENGTH_SHORT).show();

        FolderListFragment folderFragment = (FolderListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);

        Intent it = new Intent(this,DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DetailActivity.ARG_BOOK_ID, book.id);
        bundle.putString(DetailActivity.ARG_FOLDER_ID, folderId);
        bundle.putString(DetailActivity.ARG_USER_ID, mUser.getUid());
        bundle.putString(DetailActivity.ARG_FOLDER_LIST_ID, folderFragment.getmFolderListCommaSeparated());

        it.putExtras(bundle);
        startActivity(it);

    }
}
