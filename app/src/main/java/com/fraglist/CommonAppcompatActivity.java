package com.fraglist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Android on 2/24/2016.
 */
public class CommonAppcompatActivity extends AppCompatActivity {

    public Animation animation_slide_in_right, animation_top_to_bottom,animation_down_from_top,animation_up_from_bottom;
    public Typeface typeface;
    public ConnectionDetector connectionDetector;
    public boolean isInternetAvailable;
    public ProgressDialog progressDialog;
    private LinearLayout layout;
    private static final String PREF_NAME = "preference";
    private Locale locale;
    protected int screenHeight, screenWidth;
    public Typeface tfDroidSans;
    private Dialog customLoading;
    public long MIN_TIME_INTERVAL = 60 * 1000L;
    private SharedPreferences preferences;
    private Button send_button;
    private int year;
    private int month;
    private int day, pickerId, pos;
    private EditText editText,write_comment;
    Context mContext;
    DatePickerDialog datePickerDialog;


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        typeface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.ttf");
        isInternetAvailable = getNetworkState().isConnectingToInternet();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    layout.setVisibility(View.GONE);
                    break;

                case 2:
                    layout.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public void hideKeyBoard(Context context) {
        if (getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        /*inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);*/
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public void showUserAlert(LinearLayout linearLayout, TextView txt_title, TextView txt_close, String title, String close) {
        this.layout = linearLayout;
        layout.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        txt_close.setText(close);
        handler.sendEmptyMessageDelayed(1, 2000);

    }


    public boolean isMobileNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            return true;
        } else {
            return false;
        }
    }


    public void showInternetAlert(LinearLayout linearLayout, TextView txt_title, TextView txt_close) {
        this.layout = linearLayout;
        layout.setVisibility(View.VISIBLE);
        txt_title.setText("Kindly check your internet connection and try again...");
        txt_close.setText("Close");
        handler.sendEmptyMessageDelayed(2, 3000);

    }


    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String getLanguage() {
        String language = null;
        SharedPreferences preferences;
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        language = preferences.getString("Language", "en");
        return language;
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
        //restartActivity();
    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        locale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }


    public boolean Communication() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        switch (manager.getSimState()) {
            case TelephonyManager.SIM_STATE_ABSENT:
                return false;
            case TelephonyManager.SIM_STATE_READY:
                return true;
            default:
                break;

        }
        return false;
    }

    public ConnectionDetector getNetworkState() {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        return connectionDetector;
    }

    public DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[]
                    datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }


    public void addSnackBar(CoordinatorLayout coordinatorLayout, String Title, String action) {
        Snackbar
                .make(coordinatorLayout, Title, Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }

    public void showInternetAlert(CoordinatorLayout coordinatorLayout) {
        Snackbar
                .make(coordinatorLayout, "Kindly Check your Internet Connection and try again...", Snackbar.LENGTH_LONG)
                .setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();
    }

    public void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("MyAPP");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private void getDisplayDetails() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screenHeight = outMetrics.heightPixels;
        screenWidth = outMetrics.widthPixels;
    }

    public int getScreenHeight() {

        return screenHeight;
    }

    public int getScreenWidth() {

        return screenWidth;
    }

    public int getWidthByPercentage(double i) {

        int result = (int) ((i * screenWidth) / 100);
        return result;
    }

    public int getHeightByPercentage(double d) {

        int result = (int) ((d * screenHeight) / 100);
        return result;
    }

    public void showDatePickerDialog(int datePickerId, EditText text, Context context, int position) {
        this.pickerId = datePickerId;
        this.editText = text;
        this.mContext = context;
        this.pos = position;
        showDialog(1);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                String[] split = date.split("\\-");
                return new DatePickerDialog(mContext, mDateSetListener, Integer.valueOf(split[2]), Integer.valueOf(split[1]), Integer.valueOf(split[0]));
            default:
                break;
        }
        return datePickerDialog;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            getDate(pickerId, String.valueOf(selectedYear) + "-" + String.valueOf(selectedMonth + 1) + "-" + String.valueOf(selectedDay), editText, pos);
        }
    };

    public void getDate(int pickerId2, String string, EditText editText2, int pos2) {

    }
}