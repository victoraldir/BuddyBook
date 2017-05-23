package com.quartzodev.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.quartzodev.api.BookApi;
import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;
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

        String userId = intent.getExtras().getString(MainActivity.EXTRA_USER_ID);

        return new ListRemoteViewFactory(getApplicationContext(), userId);
    }

    public class ListRemoteViewFactory implements RemoteViewsFactory, ValueEventListener {

        private final String TAG = ListRemoteViewFactory.class.getSimpleName();
        private String mUserId;

        List<BookApi> mData = new ArrayList<>();
        FirebaseDatabaseHelper mFirebaseDatabaseHelper;

        private Context mContext;

        public ListRemoteViewFactory(Context context, String userId){
            mFirebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();
            mContext = context;
            mUserId = userId;
            loadBookList();
            FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        @Override
        public void onCreate() {
//            loadBookList();
        }

        public void loadBookList(){
            mFirebaseDatabaseHelper.fetchLentBooks(mUserId,this);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mData = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if(mData.isEmpty()){
                return null;
            }

            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.list_item_widget_lend);

            BookApi bookApi = mData.get(position);

            DateTime lendDate = new DateTime(bookApi.getLend().getLendDate());

            Days days = Days.daysBetween(lendDate, DateTime.now());

            remoteViews.setTextViewText(R.id.book_title, bookApi.getVolumeInfo().getTitle());
            remoteViews.setTextViewText(R.id.receiver_name, bookApi.getLend().getReceiverName());
            remoteViews.setTextViewText(R.id.lend_days, days.getDays() + " Days \n ago");

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

            if(dataSnapshot.getValue() != null){

                Folder folder = dataSnapshot.getValue(Folder.class);

                List<BookApi> listLend = new ArrayList<>();

                if(folder.getBooks() != null) {

                    for (BookApi bookApi : folder.getBooks().values()) {
                        if (bookApi.getLend() != null) {
                            listLend.add(bookApi);
                        }
                    }

                    mData = new ArrayList<>(listLend);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                    int appWidgetIds[] = appWidgetManager
                            .getAppWidgetIds(new ComponentName(mContext, BuddyBookWidgetProvider.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);

                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG,"onDataChange fired");
        }
    }
}
