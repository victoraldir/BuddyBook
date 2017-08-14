package com.quartzodev.buddybook;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;
import com.quartzodev.api.APIService;
import com.quartzodev.api.interfaces.IQuery;
import com.quartzodev.data.Book;
import com.quartzodev.data.FirebaseDatabaseHelper;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    String[] isbnList = {"1250069793"};

    private static final String TAG = ExampleInstrumentedTest.class.getSimpleName();

    @Test
    @Ignore
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.quartzodev.buddybook", appContext.getPackageName());

        FirebaseDatabase.getInstance();

    }


    @Test
    @Ignore
    public void shouldGetBookByISBNGoodreads() throws IOException, InterruptedException {


        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        IQuery query = APIService.getInstance().getService(APIService.GOODREADS);

        FirebaseDatabaseHelper db = FirebaseDatabaseHelper.getInstance();


        for (int x = 0; x < isbnList.length; x++) {

            Book book = query.getBookByISBN(isbnList[x]);

            db.insertBookPopularFolder(book);

        }
    }
}
