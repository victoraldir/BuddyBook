package com.quartzodev.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.Folder;

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

    public static void alertInfo(final Context context,String tittle, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage(message)
                .setTitle(tittle)
                .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    public static class CustomAlertDialog extends AlertDialog.Builder{

        private long tag; //Tag to reference folder

        public CustomAlertDialog(Context context) {
            super(context);
        }

        public long getTag() {
            return tag;
        }

        public void setTag(long tag) {
            this.tag = tag;
        }
    }
}
