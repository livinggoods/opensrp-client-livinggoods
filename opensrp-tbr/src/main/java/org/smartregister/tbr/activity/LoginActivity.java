package org.smartregister.tbr.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.TimeStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.event.Listener;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.tbr.BuildConfig;
import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.LoginConfiguration.Background;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.tbr.util.Constants;
import org.smartregister.util.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import util.ImageLoaderRequest;
import util.TbrConstants;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.smartregister.tbr.util.Constants.CONFIGURATION.LOGIN;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;
import static org.smartregister.util.Log.logVerbose;
import static util.TbrConstants.VIEW_CONFIGURATION_PREFIX;

/**
 * Created on 09/10/2017 by SGithengi
 */
public class LoginActivity extends AppCompatActivity {

    public static final String PREF_TEAM_LOCATIONS = "PREF_TEAM_LOCATIONS";
    public static final ArrayList<String> ALLOWED_LEVELS;

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add("Health Facility");
        ALLOWED_LEVELS.add("Visit Location");
    }

    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    public static final String ENGLISH_LOCALE = "en";
    private static final String URDU_LOCALE = "ur";
    private static final String ENGLISH_LANGUAGE = "English";
    private static final String URDU_LANGUAGE = "Urdu";
    private RemoteLoginTask remoteLoginTask;
    private CheckBox showPasswordCheckBox;
    public static final String TAG = LoginActivity.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logVerbose("Initializing ...");
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = getOpenSRPContext().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            logError("Error onCreate: " + e);

        }

        setContentView(R.layout.login);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        //android.content.Context appContext = this;
        positionViews();
        initializeLoginFields();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        setListenerOnShowPasswordCheckbox();
        initializeProgressDialog();

        setLanguage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeBuildDetails() {
        TextView buildDetailsTextView = (TextView) findViewById(org.smartregister.R.id.login_build);
        try {
            buildDetailsTextView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        processViewCustomizations();
        if (!getOpenSRPContext().IsUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void login(final View view) {
        login(view, !getOpenSRPContext().allSharedPreferences().fetchForceRemoteLogin());
    }

    private void login(final View view, boolean localLogin) {

        android.util.Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        hideKeyboard();
        view.setClickable(false);

        final String userName = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            if (localLogin) {
                localLogin(view, userName, password);
            } else {
                remoteLogin(view, userName, password);
            }
        } else {
            showErrorDialog(getResources().getString(R.string.unauthorized));
            view.setClickable(true);
        }

        android.util.Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void initializeLoginFields() {
        userNameEditText = (EditText) findViewById(org.smartregister.R.id.login_userNameText);
        passwordEditText = (EditText) findViewById(org.smartregister.R.id.login_passwordText);
        showPasswordCheckBox = (CheckBox) findViewById(R.id.show_password);
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(findViewById(org.smartregister.R.id.login_loginButton));
                    return true;
                }
                return false;
            }
        });
    }

    private void setListenerOnShowPasswordCheckbox() {
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void localLogin(View view, String userName, String password) {
        view.setClickable(true);
        if (getOpenSRPContext().userService().isUserInValidGroup(userName, password)
                && (!TbrConstants.TIME_CHECK || TimeStatus.OK.equals(getOpenSRPContext().userService().validateStoredServerTimeZone()))) {
            localLoginWith(userName, password);
        } else {

            login(findViewById(org.smartregister.R.id.login_loginButton), false);
        }
    }


    private void remoteLogin(final View view, final String userName, final String password) {

        if (!getOpenSRPContext().allSharedPreferences().fetchBaseURL("").isEmpty()) {
            tryRemoteLogin(userName, password, new Listener<LoginResponse>() {
                public void onEvent(LoginResponse loginResponse) {
                    view.setClickable(true);
                    if (loginResponse == SUCCESS) {
                        if (getOpenSRPContext().userService().isUserInPioneerGroup(userName) || 1==1) {
                            TimeStatus timeStatus = getOpenSRPContext().userService().validateDeviceTime(
                                    loginResponse.payload(), TbrConstants.MAX_SERVER_TIME_DIFFERENCE);
                            if (!TbrConstants.TIME_CHECK || timeStatus.equals(TimeStatus.OK)) {
                                remoteLoginWith(userName, password, loginResponse.payload());
                                /*Intent intent = new Intent(appContext, PullUniqueIdsIntentService.class);
                                appContext.startService(intent);*/
                            } else {
                                if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                    TimeZone serverTimeZone = getOpenSRPContext().userService()
                                            .getServerTimeZone(loginResponse.payload());
                                    showErrorDialog(getString(timeStatus.getMessage(),
                                            serverTimeZone.getDisplayName()));
                                } else {
                                    showErrorDialog(getString(timeStatus.getMessage()));
                                }
                            }
                        } else { // Valid user from wrong group trying to log in
                            showErrorDialog(getResources().getString(R.string.unauthorized_group));
                        }
                    } else {
                        if (loginResponse == null) {
                            showErrorDialog("Sorry, your login failed. Please try again.");
                        } else {
                            if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                                showErrorDialog(getResources().getString(R.string.no_internet_connectivity));
                            } else if (loginResponse == UNKNOWN_RESPONSE) {
                                showErrorDialog(getResources().getString(R.string.unknown_response));
                            } else if (loginResponse == UNAUTHORIZED) {
                                showErrorDialog(getResources().getString(R.string.unauthorized));
                            }
//                        showErrorDialog(loginResponse.message());
                        }
                    }
                }
            });

        } else {
            view.setClickable(true);
            showErrorDialog("OpenSRP Base URL is missing. Please add it in Settings and try again");

        }
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.smartregister.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();

        if (!LoginActivity.this.isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }

        remoteLoginTask = new RemoteLoginTask(userName, password, afterLoginCheck);
        remoteLoginTask.execute();
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            logError("Error hideKeyboard: " + e);
        }
    }

    private void localLoginWith(String userName, String password) {
        getOpenSRPContext().userService().localLogin(userName, password);
        goToHome(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
                android.util.Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLoginWith(String userName, String password, LoginResponseData userInfo) {
        getOpenSRPContext().userService().remoteLogin(userName, password, userInfo);
        goToHome(true);
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
        finish();
    }

    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    private String getBuildDate() throws PackageManager.NameNotFoundException, IOException {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new java.util.Date(BuildConfig.TIMESTAMP));
    }

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(getOpenSRPContext().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources res = getOpenSRPContext().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);

    }


    public static String switchLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(getOpenSRPContext().applicationContext()));

        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if (URDU_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(URDU_LOCALE);
            Resources res = getOpenSRPContext().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(URDU_LOCALE);
            res.updateConfiguration(conf, dm);
            return URDU_LANGUAGE;
        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
            Resources res = getOpenSRPContext().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(ENGLISH_LOCALE);
            res.updateConfiguration(conf, dm);
            return ENGLISH_LANGUAGE;
        }
    }

    private void positionViews() {
        final ScrollView canvasSV = (ScrollView) findViewById(R.id.canvasSV);
        final RelativeLayout canvasRL = (RelativeLayout) findViewById(R.id.canvasRL);
        final LinearLayout logoCanvasLL = (LinearLayout) findViewById(R.id.logoCanvasLL);
        final LinearLayout credentialsCanvasLL = (LinearLayout) findViewById(R.id.credentialsCanvasLL);

        canvasSV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasSV.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int windowHeight = canvasSV.getHeight();
                int topMargin = (windowHeight / 2)
                        - (credentialsCanvasLL.getHeight() / 2)
                        - logoCanvasLL.getHeight();
                topMargin = topMargin / 2;

                RelativeLayout.LayoutParams logoCanvasLP = (RelativeLayout.LayoutParams) logoCanvasLL.getLayoutParams();
                logoCanvasLP.setMargins(0, topMargin, 0, 0);
                logoCanvasLL.setLayoutParams(logoCanvasLP);

                canvasRL.setMinimumHeight(windowHeight);
            }
        });
    }

    public static Context getOpenSRPContext() {
        return TbrApplication.getInstance().getContext();
    }

    private void extractLocations(ArrayList<String> locationList, JSONObject rawLocationData)
            throws JSONException {
        final String NODE = "node";
        final String CHILDREN = "children";
        String name = rawLocationData.getJSONObject(NODE).getString("locationId");
        String level = rawLocationData.getJSONObject(NODE).getJSONArray("tags").getString(0);

        if (ALLOWED_LEVELS.contains(level)) {
            locationList.add(name);
        }
        if (rawLocationData.has(CHILDREN)) {
            Iterator<String> childIterator = rawLocationData.getJSONObject(CHILDREN).keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                extractLocations(locationList, rawLocationData.getJSONObject(CHILDREN)
                        .getJSONObject(curChildKey));
            }
        }

    }

    private void processViewCustomizations() {
        try {
            String jsonString = Utils.getPreference(this, VIEW_CONFIGURATION_PREFIX + LOGIN, null);
            if (jsonString == null) return;
            ViewConfiguration loginView = ConfigurableViewsLibrary.getJsonSpecHelper().getConfigurableView(jsonString);
            LoginConfiguration metadata = (LoginConfiguration) loginView.getMetadata();
            LoginConfiguration.Background background = metadata.getBackground();
            if (!metadata.getShowPasswordCheckbox()) {
                showPasswordCheckBox.setVisibility(View.GONE);
            } else {
                showPasswordCheckBox.setVisibility(View.VISIBLE);
            }
           /* TODO MAIMOONA if (background.getOrientation() != null && background.getStartColor() != null && background.getEndColor() != null) {
                View canvasRL = findViewById(R.id.canvasRL);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setOrientation(
                        GradientDrawable.Orientation.valueOf(background.getOrientation()));
                gradientDrawable.setColors(new int[]{Color.parseColor(background.getStartColor()),
                        Color.parseColor(background.getEndColor())});
                canvasRL.setBackground(gradientDrawable);
            }*/
            if (metadata.getLogoUrl() != null) {
                ImageView imageView = (ImageView) findViewById(R.id.logoImage);
                ImageLoaderRequest.getInstance(this.getApplicationContext()).getImageLoader()
                        .get(metadata.getLogoUrl(), ImageLoader.getImageListener(imageView,
                                R.drawable.ic_logo, R.drawable.ic_logo)).getBitmap();
                TextView loginBuild = (TextView) findViewById(R.id.login_build);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(loginBuild.getLayoutParams());
                lp.setMargins(0, 0, 0, 0);
                loginBuild.setLayoutParams(lp);
            }

        } catch (Exception e) {
            android.util.Log.d(TAG, e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////
// Inner classes
////////////////////////////////////////////////////////////////
    private class RemoteLoginTask extends AsyncTask<Void, Void, LoginResponse> {
        private final String userName;
        private final String password;
        private final Listener<LoginResponse> afterLoginCheck;

        private RemoteLoginTask(String userName, String password, Listener<LoginResponse> afterLoginCheck) {
            this.userName = userName;
            this.password = password;
            this.afterLoginCheck = afterLoginCheck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            return getOpenSRPContext().userService().isValidRemoteLogin(userName, password);
        }

        @Override
        protected void onPostExecute(LoginResponse loginResponse) {
            super.onPostExecute(loginResponse);
            if (progressDialog != null && progressDialog.isShowing()) {

                if (LoginActivity.this.isDestroyed()) { // Fix not attached to window manager Exception
                    return;
                }
                progressDialog.dismiss();
            }
            afterLoginCheck.onEvent(loginResponse);
        }
    }

    private class SaveTeamLocationsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> locationsCSV = locationsCSV();

            if (locationsCSV.isEmpty()) {
                return null;
            }

            Utils.writePreference(TbrApplication.getInstance().getApplicationContext(), PREF_TEAM_LOCATIONS, StringUtils.join(locationsCSV, ","));
            return null;
        }

        private ArrayList<String> locationsCSV() {
            final String LOCATIONS_HIERARCHY = "locationsHierarchy";
            final String MAP = "map";
            JSONObject locationData;
            ArrayList<String> locations = new ArrayList<>();
            try {
                locationData = new JSONObject(TbrApplication.getInstance().getContext().anmLocationController().get());
                if (locationData.has(LOCATIONS_HIERARCHY) && locationData.getJSONObject(LOCATIONS_HIERARCHY).has(MAP)) {
                    JSONObject map = locationData.getJSONObject(LOCATIONS_HIERARCHY).getJSONObject(MAP);
                    Iterator<String> keys = map.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();
                        extractLocations(locations, map.getJSONObject(curKey));
                    }
                }
            } catch (Exception e) {
                android.util.Log.e(getClass().getCanonicalName(), android.util.Log.getStackTraceString(e));
            }
            return locations;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViews(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            logInfo("Refreshing Login View...");
            processViewCustomizations();
        }
    }

}
