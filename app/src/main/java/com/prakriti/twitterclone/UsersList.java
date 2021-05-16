package com.prakriti.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listview_users;
    private ArrayList<String> twitUsersList;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        listview_users = findViewById(R.id.listview_users);
        // populate listview with users from server using query // using array adapter
        twitUsersList = new ArrayList(); // holds usernames
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, twitUsersList);
        // passing a predefined layout

        // specify choice mode for list -> checked & unchecked
        listview_users.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listview_users.setOnItemClickListener(this);

        getUsersFromServer(); // get all users
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

    private void getUsersFromServer() {
        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            // don't show current user on list
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getString("username"));

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseUser user : objects) {
                                twitUsersList.add(user.getUsername());
                            }
                            listview_users.setAdapter(arrayAdapter);

                            // also check for followed users
                            for(String twitterUser : twitUsersList) {
                                if (ParseUser.getCurrentUser().getList("following") != null) {
                                    // * calling contains() on null value will crash the app
                                    if (ParseUser.getCurrentUser().getList("following").contains(twitterUser)) {
                                        listview_users.setItemChecked(twitUsersList.indexOf(twitterUser), true);
                                    }
                                }
                            }
                        }
                    } else {
                        MYToast.makeToast(UsersList.this, "Unable to retrieve users\n" + e.getMessage(), Toast.LENGTH_SHORT,
                                MYToast.CUSTOM_TYPE_ERROR).show();
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            MYToast.makeToast(this, e.getMessage(), Toast.LENGTH_SHORT, MYToast.CUSTOM_TYPE_ERROR).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // here, we use checked textview
        CheckedTextView checkedTextView = (CheckedTextView) view;
        if (checkedTextView.isChecked()) {
            MYToast.makeToast(this, "You are following " + twitUsersList.get(position), MYToast.LENGTH_SHORT,
                    MYToast.CUSTOM_TYPE_INFO, MYToast.CUSTOM_GRAVITY_CENTER).show();

            // add user to 'following' list on server
            ParseUser.getCurrentUser().add("following", twitUsersList.get(position));
        }
        else {
            MYToast.makeToast(this, "You have unfollowed " + twitUsersList.get(position), MYToast.LENGTH_SHORT,
                    MYToast.CUSTOM_TYPE_INFO, MYToast.CUSTOM_GRAVITY_CENTER).show();

            // get list & remove unchecked user
            ParseUser.getCurrentUser().getList("following").remove(twitUsersList.get(position));

            // remove the list from server & add it again to refresh it
            List currentUserIsFollowing = ParseUser.getCurrentUser().getList("following");
            ParseUser.getCurrentUser().remove("following");
            ParseUser.getCurrentUser().put("following", currentUserIsFollowing); // pass updated list

        }
        // save changes
        ParseUser.getCurrentUser().saveInBackground();
    }
}