package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoTextWatcher implements TextWatcher {

    private final String phoneNumberPattern = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{10,20}$";
    private final String vkAddressPattern = "^vk\\.com/[\\w-]{3,}$";
    private final String gitAddressPattern = "^github\\.com/[\\w-]{3,}/?[\\w-]*";

    private Context mContext;
    private EditText mEditText;
    private TextInputLayout mTextInputLayout;
    private ImageView mImageView;

    private String mErrorMessage;

    public InfoTextWatcher(Context context, EditText editText, TextInputLayout textInputLayout, ImageView imageView) {
        this.mContext = context;
        this.mEditText = editText;
        this.mTextInputLayout = textInputLayout;
        this.mImageView = imageView;
    }

    private void validate(String text, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        if (!matcher.matches()) {
            mImageView.setEnabled(false);
            mTextInputLayout.setError(mErrorMessage);
        } else {
            mImageView.setEnabled(true);
            mTextInputLayout.setErrorEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().toLowerCase().contains("https://")) {
            charSequence = charSequence.toString().replaceAll("https://", "");
            mEditText.setText(charSequence);
        }
        if (charSequence.toString().toLowerCase().contains("http://")) {
            charSequence = charSequence.toString().replaceAll("http://", "");
            mEditText.setText(charSequence);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        switch (mEditText.getId()) {
            case R.id.phone_edit:
                mErrorMessage = mContext.getString(R.string.user_profile_phone_error_message);
                validate(mEditText.getText().toString().trim(), phoneNumberPattern);
//            String output = String.format("%s (%s) %s-%s", charSequence.subSequence(0,1) ,charSequence.subSequence(1,4), charSequence.subSequence(4,7), charSequence.subSequence(7,11));
//                mEditText.setText(PhoneNumberUtils.formatNumber(editable.toString()));
                break;
            case R.id.mail_edit:
                mErrorMessage = mContext.getString(R.string.user_profile_mail_error_message);
                validate(mEditText.getText().toString().trim(), Patterns.EMAIL_ADDRESS.pattern());
                break;
            case R.id.vk_edit:
                mErrorMessage = mContext.getString(R.string.user_profile_vk_error_message);
                validate(mEditText.getText().toString().trim(), vkAddressPattern);
                break;
            case R.id.git_edit:
                mErrorMessage = mContext.getString(R.string.user_profile_github_error_message);
                validate(mEditText.getText().toString().trim(), gitAddressPattern);
                break;
        }
    }
}