package com.quartzodev.buddybook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;
import com.quartzodev.api.APIService;
import com.quartzodev.api.BookApi;
import com.quartzodev.api.BookResponse;
import com.quartzodev.data.FirebaseDatabaseHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.quartzodev.buddybook", appContext.getPackageName());

        FirebaseDatabase.getInstance();

        Callback<BookResponse> callback = new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {

                List<BookApi> bookList = response.body().getItems();

                //Folder folder = new Folder("Popular Books");

                Map<String, BookApi> bookApiMap = new HashMap<>();

                for (BookApi bookApi : bookList) {
                    bookApiMap.put(bookApi.getId(), bookApi);
                }

                //folder.setBooks(bookApiMap);

                FirebaseDatabaseHelper.getInstance().insertPopularBooks(bookApiMap);

//                    Log.d(TAG,call.toString());
                synchronized (this) {
                    this.notify();
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
//                    Log.e(TAG,call.toString());
                this.notify();
            }
        };

        APIService.getInstance().getBooks("flowers+inauthor:keyes", callback);

        synchronized (callback) {
            callback.wait();
        }

    }
}
