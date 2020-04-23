package View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.modamedicandroidapplication.R;

import Controller.AppController;
import Model.Users.Permissions;
import Model.Utils.Constants;
import View.ViewUtils.BindingValues;

/*
Home page screen
 */
public class MainActivity extends AbstractActivity {

    private String username;
    private String password;
    SharedPreferences sharedPref;

    public Activity getContext() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE);
        if (loggedUser()) {
            openHomePage();
        }
        else {
            sharedPref.edit().putBoolean(Constants.LOGGED_USER, false).apply();
            setContentView(R.layout.activity_main);
            setHideKeyBoard();
            askForPermissions();
            EditText username_textfield = findViewById(R.id.username_textfield);
            String username = getUserName();
            if (!username.equals("not exists"))
                username_textfield.setText(username);
        }
    }

    private boolean loggedUser() {
        return sharedPref.getBoolean(Constants.LOGGED_USER,false);
    }

    private String getUserName() {
        String not_exists = "not exists";
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
        String name = sharedPref.getString("username",not_exists);
        return name;
    }


    private void askForPermissions() {
        /**
         * PERMMISIONS REQUEST
         * ALL YOU NEED TO DO IS TO INSERT THE PERMISSION NAME TO THE MANIFEST FILE
         */
        Permissions permissions = new Permissions(this);
        try {
            permissions.requestPermissions();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //end permissions requests
    }

    private void setHideKeyBoard() {
        EditText password_textfield = findViewById(R.id.password_textfield);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        password_textfield.setOnFocusChangeListener(ofcListener);
    }

    public void registerFunction(View view) {
        Intent intent = new Intent(this, RegisterNewUserActivity.class);
        startActivity(intent);
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {

            if ((v.getId() == R.id.password_textfield) && !hasFocus) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }


    public void loginFunction(View view) {
        Log.i("Main Page", "Login button clicked");
        EditText username_textfield = findViewById(R.id.username_textfield);
        EditText password_textfield = findViewById(R.id.password_textfield);

        this.username = username_textfield.getText().toString();
        this.password = password_textfield.getText().toString();

        Log.i("Main Page", "User " + username + " with password " + password + " logged in");
        AppController controller = AppController.getController(this);
        boolean logged = controller.login(username, password, this);
        if (logged) {
            openHomePage();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.sharedPreferencesName,Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(Constants.LOGGED_USER,true).apply();
        } else {
            Log.i("Main Page", "wrong password or user name for username: " + username);
            WrongDetailsMessage();
        }
    }

    private void openHomePage() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.putExtra(BindingValues.LOGGED_USERNAME, username);
        startActivity(intent);
    }

    public void forgetPasswordFunction(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        EditText username_textfield = findViewById(R.id.username_textfield);
        this.username = username_textfield.getText().toString();
        intent.putExtra(BindingValues.TRIED_TO_LOG_USERNAME, username);
        startActivity(intent);
    }

    private void WrongDetailsMessage() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage(R.string.wrongDetails)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNeutralButton(R.string.forgot_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        forgetPasswordFunction(getContext().findViewById(R.id.forgot_password_button));
                    }
                })
                .show();
    }
}
