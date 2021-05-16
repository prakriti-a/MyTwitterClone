package com.prakriti.twitterclone;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class AllAccessCodes {

        // check for empty fields submitted
        public static boolean isFieldNull(EditText field) {
            if (field.getText().toString().trim().equalsIgnoreCase("")) {
                field.setError("This field cannot be blank");
                field.requestFocus();
                return true;
            }
            return false;
            // equals() compares contents, == compares objects
        }

//        public void hideKeyboard(View view) {
//            try {
//                InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                view.clearFocus();
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

}
