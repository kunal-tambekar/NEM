package zx.zample.nem.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zx.zample.nem.R;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.ForismaticQuoteModel;
import zx.zample.nem.network.ApiClient;
import zx.zample.nem.network.ApiInterface;
import zx.zample.nem.util.PrefConstants;
import zx.zample.nem.util.PreferenceUtil;
import zx.zample.nem.util.ShakeDetector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MotivationActivity extends AppCompatActivity implements Callback<ForismaticQuoteModel>,View.OnClickListener{

    private static final int UI_ANIMATION_DELAY = 700;
    private final Handler mHideHandler = new Handler();
    private TextView tvQuote,tvQuotedBy;
    private View bottomLayout;
    private boolean mVisible;

    private ShowcaseView showcaseView;
    int counter = 0;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            tvQuote.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_motivation);

        mVisible = true;
        tvQuote = (TextView) findViewById(R.id.tv_quote);
        tvQuotedBy = (TextView) findViewById(R.id.tv_quoted_by);
        bottomLayout = findViewById(R.id.fullscreen_content_controls);
        tvQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomLayout.getVisibility()==View.GONE)
                    bottomLayout.setVisibility(View.VISIBLE);
                else
                    bottomLayout.setVisibility(View.GONE);
            }
        });

        if(NemApp.getInstance().getConnectionDetector().isConnectedToInternet()) {
            fetchData();
        }else{
            tvQuote.setText("Check network connection and try again");
            tvQuotedBy.setText(" NEM ");
            Toast.makeText(this,"Check network connection and try again",Toast.LENGTH_SHORT).show();
        }

        if (PreferenceUtil.getBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_SHOW_TUTORIAL, true))
            showTutorial();

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if(count>2) {
                    Vibrator v = (Vibrator) MotivationActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 50 milliseconds
                    v.vibrate(50);
                    fetchData();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        show();
        hide();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void fetchData(){
        ApiInterface apiService = ApiClient.getMInstance().create(ApiInterface.class);
        Call<ForismaticQuoteModel> getForismaticQuote = apiService.getForismaticQuote("getQuote","json","parseQuote","en");
        getForismaticQuote.enqueue(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(600);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        tvQuote.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onResponse(Call<ForismaticQuoteModel> call, Response<ForismaticQuoteModel> response) {
        ForismaticQuoteModel quoteModel = response.body();
        String quote = quoteModel.getQuoteText().trim();
        String quoteBy = quoteModel.getQuoteAuthor().trim();
        if(quote.length()<=40){
            tvQuote.setTextSize(50.00f);
        }else if(quote.length()<=90){
            tvQuote.setTextSize(40.00f);
        }else if(quote.length()<=150){
            tvQuote.setTextSize(35.00f);
        }else{
            tvQuote.setTextSize(30.00f);
        }
        tvQuote.setText(quote.equalsIgnoreCase("")?"Try try but don't cry":quote);
        tvQuotedBy.setText((quoteBy.equalsIgnoreCase("")?"Anonymous":quoteBy));
    }

    @Override
    public void onFailure(Call<ForismaticQuoteModel> call, Throwable t) {
        tvQuote.setText("Some error occurred,please try again");
        tvQuotedBy.setText(" NEM ");
    }

    private void showTutorial() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        ShowcaseView.Builder res = new ShowcaseView.Builder(this, true)
                .setTarget(Target.NONE)
                .setContentTitle("Shake Me")
                .setContentText("Shake the phone to get a new quote");

        showcaseView = res.setOnClickListener(this)
                .setStyle(R.style.CustomShowcaseTheme)
                .blockAllTouches()
                .build();

        showcaseView.setButtonText("NEXT");

    }


    @Override
    public void onClick(View view) {
        switch (counter) {
            case 0:
                showcaseView.setContentTitle("Quoted by");
                showcaseView.setContentText("Name of the person who quoted the quote");
                showcaseView.setShowcase(new ViewTarget(tvQuotedBy), true);
                break;

            case 1:
                showcaseView.setContentTitle("Tap to toggle visibility");
                showcaseView.setContentText("Tap to hide/show the Quote by field");
                showcaseView.setShowcase(new ViewTarget(tvQuote), true);
                showcaseView.setButtonText("GOT IT");
                break;

            case 2:
                showcaseView.hide();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                PreferenceUtil.setBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_SHOW_TUTORIAL, false);
                break;
        }
        counter++;
    }
}
