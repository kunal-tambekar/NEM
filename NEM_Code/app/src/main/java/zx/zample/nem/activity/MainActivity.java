package zx.zample.nem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import zx.zample.nem.R;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.util.PrefConstants;
import zx.zample.nem.util.PreferenceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int TIME_INTERVAL = 2000;
    LinearLayout btnNews, btnEntertainment, btnMotivation, btnAbout;
    ShowcaseView showcaseView;
    private int counter = 0;
    private long mBackPressed;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mGoogleApiClient.connect();

        btnNews = (LinearLayout) findViewById(R.id.ll_btn_news);
        btnEntertainment = (LinearLayout) findViewById(R.id.ll_btn_entertainment);
        btnMotivation = (LinearLayout) findViewById(R.id.ll_btn_motivation);
        btnAbout = (LinearLayout) findViewById(R.id.ll_home_fullname);

        if (PreferenceUtil.getBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_IS_FIRST_TIME, true))
            showTutorial();

        btnNews.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "NEWS", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnEntertainment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "ENTERTAINMENT", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnMotivation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "MOTIVATION", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        btnEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EntertainmentActivity.class);
                startActivity(intent);
            }
        });

        btnMotivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MotivationActivity.class);
                startActivity(intent);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });


    }

    private void showTutorial() {

        ShowcaseView.Builder res = new ShowcaseView.Builder(this, true)
                .setTarget(new ViewTarget(btnNews))
                .setContentTitle("NEWS")
                .setContentText("Get daily upadtes about whats happenning around the globe");

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
                showcaseView.setContentTitle("ENTERTAINMENT");
                showcaseView.setContentText("Get the list of top movies and add them to your watchlist");
                showcaseView.setShowcase(new ViewTarget(btnEntertainment), true);
                break;

            case 1:
                showcaseView.setContentTitle("MOTIVATION");
                showcaseView.setContentText("Get your daily dose of motivation and inspiration");
                showcaseView.setShowcase(new ViewTarget(btnMotivation), true);
                showcaseView.setButtonText("GOT IT");
                break;

            case 2:
                showcaseView.hide();
                PreferenceUtil.setBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_IS_FIRST_TIME, false);
                break;
        }
        counter++;

    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Press BACK again to Exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.sign_out_menu:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                PreferenceUtil.setString(getApplicationContext(), PrefConstants.PREF_APP_NAME,
                        PrefConstants.PREF_PREV_USER,
                        (mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "ANONYMOUS"));
                mAuth.signOut();
                PreferenceUtil.setBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_IS_FIRST_TIME, true);
                PreferenceUtil.setBoolean(this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_SHOW_TUTORIAL, true);
                NemApp.getInstance().setUsername("ANONYMOUS");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
