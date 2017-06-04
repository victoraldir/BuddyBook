package com.quartzodev.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.quartzodev.buddybook.MainActivity;
import com.quartzodev.data.BookApi;
import com.quartzodev.data.Lend;
import com.quartzodev.buddybook.DetailActivity;
import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;

import java.util.Date;
import java.util.List;

/**
 * Created by victoraldir on 28/03/2017.
 */

public class DialogUtils {

    public static void alertDialogDeleteFolder(final Context context, Folder folder, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage(R.string.dialog_message)
                .setTitle(String.format(context.getString(R.string.dialog_title), folder.getDescription()))
                .setPositiveButton(R.string.dialog_btn_positive, onClickListener)
                .setNegativeButton(R.string.dialog_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static void alertDialogListFolder(final Context context, String foldersCommaSep, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setTitle(R.string.pick_folder)
                .setNegativeButton(context.getString(R.string.dialog_btn_cancel), null)
                .setItems(formatFolderList(foldersCommaSep), onClickListener);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private static String[] formatFolderList(String folderList) {

        String[] newList = new String[0];

        if(folderList != null) {
            String[] unFormatedList = folderList.split(",");

            newList = new String[unFormatedList.length];

            for (int x = 0; x < unFormatedList.length; x++) {
                newList[x] = unFormatedList[x].split("=")[0];
            }

        }
        
        return newList;
    }

    public static void alertDialogAddFolder(final Activity activity,
                                            final List<Folder> folders,
                                            final FirebaseDatabaseHelper mFirebaseDatabaseHelper,
                                            final String userId) {

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_folder, null);

        final EditText urlEditText = (EditText) view.findViewById(R.id.edittext_add_folder_description);
        urlEditText.setSingleLine(true);
        urlEditText.setContentDescription(activity.getString(R.string.receiver_name_cd));
        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.signup_input_layout_name);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.item_add_folder)
                .setPositiveButton(activity.getString(R.string.ok), null)
                .setNegativeButton(activity.getString(R.string.dialog_btn_cancel), null)
                .setView(view)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if (urlEditText.getText().toString().isEmpty()) {

                            textInputLayout.setError(activity.getString(R.string.folder_desc_empty));

                        } else if (folders != null && folders.contains(new Folder(urlEditText.getText().toString()))) {

                            textInputLayout.setError(activity.getString(R.string.folder_already_exits));

                        } else {

                            Folder newFolder = new Folder(urlEditText.getText().toString());

                            if(activity instanceof DetailActivity){
                                mFirebaseDatabaseHelper.insertFolder(userId, newFolder,(DetailActivity) activity);
                            }else{
                                mFirebaseDatabaseHelper.insertFolder(userId, newFolder,(MainActivity) activity);
                            }

                            dialog.dismiss();
                        }

                    }
                });
            }
        });

        dialog.show();

    }

    public static void alertDialogLendBook(final Activity activity,
                                           final CoordinatorLayout coordinatorLayout,
                                           final FirebaseDatabaseHelper mFirebaseDatabaseHelper,
                                           final String userId,
                                           final BookApi book) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_lend_book, null);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_title_lend)
                .setPositiveButton(activity.getString(R.string.ok), null)
                .setNegativeButton(activity.getString(R.string.dialog_btn_cancel), null)
                .setView(view)
                .create();

        final EditText nameEdtText = (EditText) view.findViewById(R.id.edittext_receiver_name);
        nameEdtText.setSingleLine(true);
        nameEdtText.setContentDescription(activity.getString(R.string.receiver_name_cd));

        final TextInputLayout nameInputLayout = (TextInputLayout) view.findViewById(R.id.dialog_input_layout_name);

        final EditText emailEdtText = (EditText) view.findViewById(R.id.edittext_receiver_email);
        emailEdtText.setSingleLine(true);
        emailEdtText.setContentDescription(activity.getString(R.string.receiver_email_cd));

        final TextInputLayout emailInputLayout = (TextInputLayout) view.findViewById(R.id.dialog_input_layout_email);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if (nameEdtText.getText().toString().isEmpty()) {

                            nameInputLayout.setError(activity.getString(R.string.lend_name_empty));

                        } else if (emailEdtText.getText().toString().isEmpty()) {

                            emailInputLayout.setError(activity.getString(R.string.lend_email_empty));

                        }else if(!isValidEmail(emailEdtText.getText().toString())) {
                            emailInputLayout.setError(activity.getString(R.string.lend_email_invalid));
                        }else {

                            Lend lend = new Lend(nameEdtText.getText().toString(),
                                    emailEdtText.getText().toString(),
                                    new Date());

                            book.setLend(lend);

                            mFirebaseDatabaseHelper.updateBook(userId, FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER, book);

                            try {
                                DetailActivity detailActivity = ((DetailActivity) activity);

                                if (detailActivity != null) {
                                    detailActivity.loadBook();
                                }
                            } catch (Exception ex) {
                            }

                            dialog.dismiss();

                            Snackbar.make(coordinatorLayout, activity.getText(R.string.success_lend_book), Snackbar.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        dialog.show();

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void alertDialogReturnBook(final Activity activity,
                                             final FirebaseDatabaseHelper mFirebaseDatabaseHelper,
                                             final String userId,
                                             final BookApi book) {

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.dialog_return_book_title))
                .setMessage(String.format(activity.getString(R.string.dialog_return_book_body), book.getLend().getReceiverName()))
                .setPositiveButton(activity.getString(R.string.dialog_btn_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        BookApi updatedBook = book;
                        updatedBook.setLend(null);
                        mFirebaseDatabaseHelper.updateBook(userId, FirebaseDatabaseHelper.REF_MY_BOOKS_FOLDER, updatedBook);

                        try {
                            ((DetailActivity) activity).loadBook();
                        } catch (Exception ex) {
                        }
                    }
                })
                .setNegativeButton(activity.getString(R.string.dialog_btn_cancel), null)
                .setCancelable(true)
                .create();

        dialog.show();

    }

    public static void alertDialogUpgradePro(final Activity activity){

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_upgrade)
                .setMessage(R.string.dialog_upgrade_message)
                .setPositiveButton(R.string.dialog_btn_updgate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(activity.getString(R.string.dialog_btn_cancel),null)
                .setCancelable(true)
                .create();

        dialog.show();
    }
}
