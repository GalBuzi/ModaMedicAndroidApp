package View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.modamedicandroidapplication.R;

import java.util.Calendar;
import java.util.TimeZone;

import Controller.AppController;
import View.ViewUtils.DateUtils;
import View.ViewUtils.InputFilterMinMax;

public class EditPersonalInfoActivity extends AbstractActivity {

    private static final String TAG = "EditPersonalInfoActivity";
    private static AppController appController;
    private Calendar BirthDayDate = null;
    private RadioGroup gender = null;
    private EditText weight = null;
    private EditText height = null;
    private RadioGroup smoker = null;
    private Spinner education = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_update);
        appController = AppController.getController(this);
        initializeAllFields();
        limitFields();
    }


    private void initializeAllFields() {
        gender = findViewById(R.id.radioGender);
        smoker = findViewById(R.id.radioSmoke);
        education = findViewById(R.id.education_spinner);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
    }

    private void limitFields() {
        weight.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "200")});
    }

    public void updateInfo(View view) {
        boolean flag = verifyInputs();
        if (flag) {
            String gender = getRadioButtonResult(this.gender);
            String isSmoker = getRadioButtonResult(smoker);
            String education = this.education.getSelectedItem().toString();
            int weight = Integer.parseInt(this.weight.getText().toString());
            int height = Integer.parseInt(this.height.getText().toString());
            long birthday = DateUtils.changeDateTo00AM(this.BirthDayDate.getTimeInMillis());
            boolean success = appController.updateInfo(gender,isSmoker,education,weight,height,birthday);
            if(success)
                success();
        }
    }

    private String getRadioButtonResult(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedButton = findViewById(selectedId);
        return selectedButton.getText().toString();
    }

    @SuppressLint("ResourceType")
    private boolean verifyInputs() {
        if (gender.getCheckedRadioButtonId() == -1) {
            showAlert(R.string.fill_gender);
            return false;
        }
        if (smoker.getCheckedRadioButtonId() == -1) {
            showAlert(R.string.fill_smoke);
            return false;
        }
        if (education.getSelectedItem() == null) {
            showAlert(R.string.fill_education);
            return false;
        }
        if (weight.length() == 0) {
            showAlert(R.string.fill_weight);
            return false;
        }
        if (height.length() == 0) {
            showAlert(R.string.fill_height);
            return false;
        }
        if (BirthDayDate == null) {
            showAlert(R.string.fill_date);
            return false;
        }
        return true;
    }

    private void showAlert(int msg) {
        new AlertDialog.Builder(EditPersonalInfoActivity.this)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setNegativeButton(R.string.tryAgain, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showInfo(int msg) {
        new AlertDialog.Builder(EditPersonalInfoActivity.this)
                .setTitle(R.string.succes)
                .setMessage(msg)
                .setNegativeButton(R.string.succes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void showInfo(String msg) {
        new AlertDialog.Builder(EditPersonalInfoActivity.this)
                .setTitle(R.string.succes)
                .setMessage(msg)
                .setNegativeButton(R.string.succes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    public void goHomePage(View view) {
        finish();
    }

    public void chooseDate(View view) {
        Button mDisplayDate = findViewById(R.id.chooseDateRegister);
        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
//                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
                BirthDayDate = Calendar.getInstance();
                BirthDayDate.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
                BirthDayDate.set(Calendar.YEAR,year);
                BirthDayDate.set(Calendar.MONTH,month-1);
                BirthDayDate.set(Calendar.DAY_OF_MONTH,day);
                BirthDayDate.set(Calendar.HOUR_OF_DAY,0);
                BirthDayDate.set(Calendar.MINUTE,0);
                BirthDayDate.set(Calendar.SECOND,0);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                EditPersonalInfoActivity.this,
                android.R.style.Widget_DeviceDefault,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        dialog.getWindow().setNavigationBarColor((getColor(R.color.colorAccent)));
        dialog.getWindow().setLayout((int) (getWidthOfScreen()*0.9),4*getHeightOfScreen()/5);
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundColor(getColor(R.color.white));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getColor(R.color.white));            }
        });
        dialog.show();
    }

    private int getWidthOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getHeightOfScreen() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void success() {
        new AlertDialog.Builder(EditPersonalInfoActivity.this)
                .setTitle(R.string.succes)
                .setMessage(R.string.updateSuccess)
                .setNegativeButton(R.string.movehome, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openHomePage();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
    private void openHomePage() {
        Intent intent = new Intent(this, HomePageBodyActivity.class);
        finish();
        startActivity(intent);
    }

}
