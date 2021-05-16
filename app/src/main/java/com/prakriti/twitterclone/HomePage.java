package com.prakriti.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private ListView listview_userTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        MYToast.makeToast(this, "Welcome " + ParseUser.getCurrentUser().getUsername() + "!", MYToast.LENGTH_SHORT,
                MYToast.CUSTOM_TYPE_SUCCESS).show();

        listview_userTweets = findViewById(R.id.listview_userTweets);

        getTweetsOfFollowedUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemLogout:
                logOutCurrentUser();
                break;

            case R.id.itemSendTweet:
                startActivity(new Intent(this, SharePost.class));
                break;

            case R.id.itemUsersList:
                startActivity(new Intent(this, UsersList.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutCurrentUser() {
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    MYToast.makeToast(HomePage.this, "You have logged out", MYToast.LENGTH_SHORT,
                            MYToast.CUSTOM_TYPE_SUCCESS).show();
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                    finish();
                }
                else {
                    MYToast.makeToast(HomePage.this, "Unable to log out. Please try again", MYToast.LENGTH_SHORT,
                            MYToast.CUSTOM_TYPE_ERROR).show();
                }
            }
        });
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

    private void getTweetsOfFollowedUsers() {
        // use list of key-value pairs
        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        // Simple adapter takes arraylist of maps
        // simple list item 2 -> holds title & subtitle
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, tweetList, android.R.layout.simple_list_item_2,
                new String[] {"tweetUserName", "tweetedContent"}, new int[] {android.R.id.text1, android.R.id.text2});
            // pass context, list used to populate listview, type of listview layout,
                    // string array of keys in map, order of keys to be displayed in listview
        try {
            // to handle users will null tweets as well
            ParseQuery<ParseObject> tweetsQuery = ParseQuery.getQuery("UserTweets");
                // to get tweets of users in current user's following
            tweetsQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("following"));
            tweetsQuery.orderByDescending("createdAt");

            tweetsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects.size() != 0) {
                        for(ParseObject obj : objects) {
                            // create new hash map & add each tweets user & content with same keys as specified above
                            HashMap<String, String> userTweet = new HashMap<>();

                            String tweetUserName = obj.getString("user");
                            String tweetedContent = obj.getString("tweet") + "\n" + obj.getCreatedAt() + "\n";
                                    // extract req part of date & convert to current user's timezone

                            userTweet.put("tweetUserName", tweetUserName);
                            userTweet.put("tweetedContent", tweetedContent); // write code to add date tweet was created
                            // add each map to arraylist
                            tweetList.add(userTweet);
                        }
                        listview_userTweets.setAdapter(simpleAdapter);
                    }
                    else  {
                        MYToast.makeToast(HomePage.this, "Unable to retrieve User Activity. Please try again",
                                Toast.LENGTH_SHORT, MYToast.CUSTOM_TYPE_ERROR).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            MYToast.makeToast(HomePage.this, e.getMessage(), Toast.LENGTH_SHORT, MYToast.CUSTOM_TYPE_ERROR).show();
            e.printStackTrace();
        }
    }

}