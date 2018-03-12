package com.quartzodev.inserteditbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.quartzodev.buddybook.R;
import com.quartzodev.utils.AnimationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.google.gson.internal.$Gson$Preconditions.*;

public class InsertEditBookActivity extends AppCompatActivity implements InsertEditBookContract.View {

    @BindView(R.id.container_more_fields)
    public LinearLayout mMoreFieldsContainer;
    @BindView(R.id.btn_more_fields)
    public TextView btnMoreFields;
    @BindView(R.id.insert_container)
    ScrollView mInsertContainer;
    @BindView(R.id.insert_progressbar)
    ProgressBar mProgressBar;

    private InsertEditBookContract.Presenter mPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new Answers());
        setContentView(R.layout.activity_insert_book);
        ButterKnife.bind(this);

        mPresenter = new InsertEditBookPresenter(this, null);
        mPresenter.loadForm();

        btnMoreFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.clickMoreFields();
            }
        });
    }

    @Override
    public void expandMoreFields() {

        if (mMoreFieldsContainer.getVisibility() == View.VISIBLE) {
            AnimationUtils.collapse(mMoreFieldsContainer);
        } else {
            btnMoreFields.setVisibility(View.GONE);
            AnimationUtils.expand(mMoreFieldsContainer);
        }
    }

    @Override
    public void setLoading(boolean flag) {
        if(!flag){
            mInsertContainer.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }else{
            mInsertContainer.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPresenter(InsertEditBookContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
