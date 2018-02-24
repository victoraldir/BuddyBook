package com.quartzodev.utils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.quartzodev.data.Book;
import com.quartzodev.data.Folder;

/**
 * Created by victoraldir on 04/01/2018.
 */

public class AnswersUtil {

    private static final String METRIC_ADD_BOOK_FOLDER = "Add book to folder";
    private static final String METRIC_LEND_BOOK = "Lend book";
    private static final String METRIC_ADD_CUSTOM_BOOK = "Add custom book";

    public static void onAddBookToFolder(Book book, Folder folder) {
        try {
            Answers.getInstance().logCustom(new CustomEvent(METRIC_ADD_BOOK_FOLDER)
                    .putCustomAttribute("Book Label", book.getVolumeInfo().getDescription())
                    .putCustomAttribute("Folder label", folder.getDescription()));
        } catch (Exception ex) {
        }
    }

    public static void onLendBook(Book book) {
        try {
            Answers.getInstance().logCustom(new CustomEvent(METRIC_LEND_BOOK)
                    .putCustomAttribute("Book Label", book.getVolumeInfo().getDescription()));
        } catch (Exception ex) {
        }
    }

    public static void onAddCustomBook(Book book, Folder folder) {
        try {
            Answers.getInstance().logCustom(new CustomEvent(METRIC_ADD_CUSTOM_BOOK)
                    .putCustomAttribute("Book Label", book.getVolumeInfo().getDescription())
                    .putCustomAttribute("Folder label", folder.getDescription()));
        } catch (Exception ex) {
        }
    }

}
