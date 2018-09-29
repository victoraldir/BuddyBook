package com.quartzodev.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.Book;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.FileProvider;

/**
 * Created by victoraldir on 10/03/2018.
 */

public class ExportCSVUtil {

    private static final String EXTENSION_CSV = ".csv";

    public static void writeWithCsvMapWriter(String folderTitle, List<Book> bookList, Activity activity) throws Exception {

        final String[] header = new String[]{
                "Book Title",
                "Author",
                "Publisher",
                "Publishing Date",
                "ISBN (10 - 13)",
                "Print type",
                "Language",
                "Number of pages",
                "Annotations",
                "Description"};

        // create the customer Maps (using the header elements for the column keys)

        List<Map<String, Object>> mapList = new ArrayList<>();

        for (Book book : bookList) {

            final Map<String, Object> row = new HashMap<>();
            row.put(header[0], book.getVolumeInfo().title);
            row.put(header[1], formatStringList(book.getVolumeInfo().getAuthors()));
            row.put(header[2], book.getVolumeInfo().getPublisher());
            //row.put(header[3], new GregorianCalendar(1945, Calendar.JUNE, 13).getTime());
            //TODO set as Calendar so that user can order
            row.put(header[3], book.getVolumeInfo().getPublishedDate());
            row.put(header[4], formatStringList(Arrays.asList(book.getVolumeInfo().getIsbn10(), book.getVolumeInfo().getIsbn13())));
            row.put(header[5], book.getVolumeInfo().getPrintType());
            row.put(header[6], book.getVolumeInfo().language);
            row.put(header[7], book.getVolumeInfo().pageCount);
            row.put(header[8], book.getAnnotation());
            row.put(header[9], book.getVolumeInfo().getDescription());

            mapList.add(row);

        }

        ICsvMapWriter mapWriter = null;
        try {

            ContentValues values = new ContentValues();
            Uri reportPathUri;
            File reportPath = new File(activity.getFilesDir(), "csvreports");
            if (!reportPath.exists()) reportPath.mkdir();
            File newFile = new File(reportPath, folderTitle + EXTENSION_CSV);

            mapWriter = new CsvMapWriter(new FileWriter(newFile),
                    CsvPreference.STANDARD_PREFERENCE);

            if (Build.VERSION.SDK_INT > 21) { //use this if Lollipop_Mr1 (API 22) or above
                reportPathUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", newFile);
            } else {
                reportPathUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }

            final CellProcessor[] processors = getProcessors();

            // write the header
            mapWriter.writeHeader(header);

            // write the customer maps
            for (Map<String, Object> row : mapList) {
                mapWriter.write(row, header, processors);
            }

            Intent intent = new Intent(Intent.ACTION_SEND).setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, reportPathUri);
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.send_to)));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
        }
    }

    public static String formatStringList(List<String> strings) {

        String result = "";

        if (strings == null)
            return result;

        if (strings.isEmpty()) {
            return result;
        } else {
            for (int x = 0; x < strings.size(); x++) {
                if (strings.get(x) != null) {
                    if (result.isEmpty()) {
                        result = strings.get(x);
                    } else {
                        result = result + " - " + strings.get(x);
                    }
                }
            }
        }

        return result;

    }

    public static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{
                new ConvertNullTo(""), // firstName
                new ConvertNullTo(""), // lastName
                new ConvertNullTo(""), // birthDate
                new ConvertNullTo(""), // mailingAddress
                new ConvertNullTo(""), // married
                new ConvertNullTo(""), // numberOfKids
                new ConvertNullTo(""), // favouriteQuote
                new ConvertNullTo(""), // email
                new ConvertNullTo(""), // loyaltyPoints
                new ConvertNullTo("")
        };

        return processors;
    }

}
