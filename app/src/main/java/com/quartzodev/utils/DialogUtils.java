package com.quartzodev.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quartzodev.buddybook.R;
import com.quartzodev.data.FirebaseDatabaseHelper;
import com.quartzodev.data.Folder;
import com.quartzodev.fragments.FolderListFragment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public static void alertDialogListFolder(final Context context, String foldersCommaSep, DialogInterface.OnClickListener onClickListener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setTitle(R.string.pick_folder)
                .setNegativeButton("Cancel", null)
                .setItems(formatFolderList(foldersCommaSep), onClickListener);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private static String[] formatFolderList(String folderList){

        String[] unFormatedList = folderList.split(",");

        String[] newList = new String[unFormatedList.length];

        for(int x=0; x<unFormatedList.length; x++){
            newList[x] = unFormatedList[x].split("=")[0];
        }

        return newList;
    }

    public static void alertDialogAddFolder(final Activity activity, FragmentManager fragmentManager,
                                            final FirebaseDatabaseHelper mFirebaseDatabaseHelper,
                                            final String userId){

        LayoutInflater inflater = activity.getLayoutInflater();

        View view =  inflater.inflate(R.layout.dialog_add_folder,null);

        final EditText urlEditText = (EditText) view.findViewById(R.id.edittext_add_folder_description);
        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.signup_input_layout_name);

        FolderListFragment folderFragment = (FolderListFragment)
                fragmentManager.findFragmentById(R.id.container_nav_header);

        final List<Folder> folders = folderFragment.getmFolderList();

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.item_add_folder)
                .setPositiveButton("Ok",null)
                .setNegativeButton("Cancel", null)
                .setView(view)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface arg0) {

                Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        if(urlEditText.getText().toString().isEmpty()){

                            textInputLayout.setError(activity.getString(R.string.folder_desc_empty));

                        }else if(folders != null && folders.contains(new Folder(urlEditText.getText().toString()))){

                            textInputLayout.setError(activity.getString(R.string.folder_already_exits));

                        }else{

                            Folder newFolder = new Folder(urlEditText.getText().toString());

                            mFirebaseDatabaseHelper.insertFolder(userId,newFolder);

                            dialog.dismiss();
                        }

                    }
                });
            }
        });

        dialog.show();

    }

}
