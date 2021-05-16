package com.prakriti.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SharePost extends AppCompatActivity implements View.OnClickListener {

    private EditText edtTweetContent;
    private Button btnShareTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);

        edtTweetContent = findViewById(R.id.edtTweetContent);
        btnShareTweet = findViewById(R.id.btnShareTweet);

        btnShareTweet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareTweet:
                shareUsersTweet();
                break;
        }
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            view.clearFocus();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareUsersTweet() {
        if(AllAccessCodes.isFieldNull(edtTweetContent)) {
            return;
        }
        else {
            String tweetContent = edtTweetContent.getText().toString().trim();
            // creating new class
            ParseObject parseObject = new ParseObject("UserTweets");
            parseObject.put("tweet", tweetContent);
            parseObject.put("user", ParseUser.getCurrentUser().getUsername());

            // progress dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending tweet...");
            progressDialog.show();

            // save to server
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        MYToast.makeToast(SharePost.this, "Tweet sent!", MYToast.LENGTH_SHORT,
                                MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_CENTER).show();
                        edtTweetContent.setText("");
                    }
                    else {
                        MYToast.makeToast(SharePost.this, "Could not send tweet. Please try again", MYToast.LENGTH_SHORT,
                                MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_CENTER).show();
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            });
            hideKeyboard(edtTweetContent);
        }
    }
}