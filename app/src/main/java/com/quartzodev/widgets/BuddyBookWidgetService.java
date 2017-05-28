package com.quartzodev.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.data.BookApi;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victoraldir on 21/05/2017.
 */

public class BuddyBookWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewFactory(getApplicationContext());
    }

    public class ListRemoteViewFactory implements RemoteViewsFactory,
            ValueEventListener,
            FirebaseAuth.AuthStateListener {

        private final String TAG = ListRemoteViewFactory.class.getSimpleName();
        List<BookApi> mData = new ArrayList<>();
        FirebaseDatabaseHelper mFirebaseDatabaseHelper;
        FirebaseAuth mFirebaseAuth;
        private String mUserId;
        private Context mContext;

        public ListRemoteViewFactory(Context context) {
            mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
            mContext = context;
            //mUserId = userId;

            mFirebaseAuth = FirebaseAuth.getInstance();

            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

            if (firebaseUser != null) {
                mUserId = firebaseUser.getUid();
                loadBookList();
            }
        }

        @Override
        public void onCreate() {
//            loadBookList();
            mFirebaseAuth.addAuthStateListener(this);
        }

        public void loadBookList() {
            if (mUserId != null) {
                mFirebaseDatabaseHelper.fetchLentBooks(mUserId, this);
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mData = new ArrayList<>();
            mFirebaseAuth.removeAuthStateListener(this);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (mData.isEmpty()) {
                return null;
            }

            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.list_item_widget_lend);

            BookApi bookApi = mData.get(position);

            DateTime lendDate = new DateTime(bookApi.getLend().getLendDate());

            Days days = Days.daysBetween(lendDate, DateTime.now());

            remoteViews.setTextViewText(R.id.book_title, bookApi.getVolumeInfo().getTitle());
            remoteViews.setTextViewText(R.id.receiver_name, String.format(getString(R.string.lent_to),bookApi.getLend().getReceiverName()));
            remoteViews.setTextViewText(R.id.lend_days, String.format(getString(R.string.day_ago),days.getDays()));

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.getValue() != null) {

                Folder folder = dataSnapshot.getValue(Folder.class);

                List<BookApi> listLend = new ArrayList<>();

                if (folder.getBooks() != null) {

                    for (BookApi bookApi : folder.getBooks().values()) {
                        if (bookApi.getLend() != null) {
                            listLend.add(bookApi);
                        }
                    }

                    mData = new ArrayList<>(listLend);

                    updateWidget();
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "onDataChange fired");
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser == null) {
                mData = new ArrayList<>();
                updateWidget();
            } else {
                loadBookList();
            }

        }

        private void updateWidget() {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            int appWidgetIds[] = appWidgetManager
                    .getAppWidgetIds(new ComponentName(mContext, BuddyBookWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

        }
    }
}
