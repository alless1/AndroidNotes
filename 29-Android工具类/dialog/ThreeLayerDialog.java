package com.gsl.speed.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gsl.speed.R;

/**
 * 通用 三个按钮 对话框
 */

public class ThreeLayerDialog extends Dialog implements View.OnClickListener {

    private String title;
    private String description;
    private String btn_text_one;
    private String btn_text_two;
    private String btn_text_three;
    private OnCommonClickListener mListener;

    private TextView mTv_btn_one;
    private TextView mTv_btn_two;
    private View mView_two_divider;
    private TextView mTv_cancel;
    private TextView mTv_title;
    private TextView mTv_description;

    public static Builder builder(Context context) {

        return new Builder(context);
    }

    public ThreeLayerDialog(@NonNull Context context) {
        this(context, R.style.BaseMeasureDialogStyle);
    }

    private ThreeLayerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_three_layer);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mTv_title = (TextView) findViewById(R.id.tv_title);
        mTv_description = (TextView) findViewById(R.id.tv_description);

        mTv_btn_one = (TextView) findViewById(R.id.tv_btn_one);
        mTv_btn_two = (TextView) findViewById(R.id.tv_btn_two);
        mTv_cancel = (TextView) findViewById(R.id.tv_cancel);
        mView_two_divider = findViewById(R.id.view_two_bottom);

        mTv_btn_one.setOnClickListener(this);
        mTv_btn_two.setOnClickListener(this);
        mTv_cancel.setOnClickListener(this);

        mTv_title.setText(title);
        mTv_description.setText(description);
        if (!TextUtils.isEmpty(btn_text_one)) {
            mTv_btn_one.setText(btn_text_one);
            mTv_btn_one.setVisibility(View.VISIBLE);
        } else {
            mTv_btn_one.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(btn_text_two)) {
            mTv_btn_two.setText(btn_text_two);
            mView_two_divider.setVisibility(View.VISIBLE);
            mTv_btn_two.setVisibility(View.VISIBLE);
        } else {
            mView_two_divider.setVisibility(View.GONE);
            mTv_btn_two.setVisibility(View.GONE);
        }
        mTv_cancel.setText(btn_text_three);
    }

    public static class Builder {

        private String title;
        private String description;

        private String btn_text_one;
        private String btn_text_two;
        private String btn_text_three;
        private Context context;

        private OnCommonClickListener listener;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setBtnOneText(String btn_text_one) {
            this.btn_text_one = btn_text_one;
            return this;
        }

        public Builder setBtnTwoText(String btn_text_two) {
            this.btn_text_two = btn_text_two;
            return this;
        }

        public Builder setBtnThreeText(String btn_text_three) {
            this.btn_text_three = btn_text_three;
            return this;
        }

        public Builder setListener(OnCommonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public void show() {
            create().show();
        }

        public ThreeLayerDialog create() {
            ThreeLayerDialog threeLayerDialog = new ThreeLayerDialog(context);
            threeLayerDialog.title = this.title;
            threeLayerDialog.description = this.description;
            threeLayerDialog.btn_text_one = this.btn_text_one;
            threeLayerDialog.btn_text_two = this.btn_text_two;
            threeLayerDialog.btn_text_three = this.btn_text_three;
            if (TextUtils.isEmpty(threeLayerDialog.btn_text_three))
                threeLayerDialog.btn_text_three = context.getString(R.string.cancel);
            threeLayerDialog.mListener = this.listener;
            return threeLayerDialog;
        }
    }


    @Override
    public void onClick(View v) {
        dismiss();
        if (mListener == null)
            return;
        switch (v.getId()) {
            case R.id.tv_btn_one:
                mListener.onClickOne();
                break;
            case R.id.tv_btn_two:
                mListener.onClickTwo();
                break;
            case R.id.tv_cancel:
                break;
        }
    }

    public interface OnCommonClickListener {
        void onClickOne();

        void onClickTwo();

    }
}
