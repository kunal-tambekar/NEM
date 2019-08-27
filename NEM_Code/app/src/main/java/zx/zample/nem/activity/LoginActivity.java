package zx.zample.nem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import zx.zample.nem.R;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.util.PrefConstants;
import zx.zample.nem.util.PreferenceUtil;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 999;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputUsername;
    private EditText inputPassword;
    private Button btnLogin, btnGoogleSignIn;
    private TextInputLayout tilUsername, tilPassword;
    private CheckBox cbRememberMe;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputUsername = (EditText) findViewById(R.id.input_username);
        inputPassword = (EditText) findViewById(R.id.input_password);
        tilUsername = (TextInputLayout) findViewById(R.id.input_layout_username);
        tilPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnGoogleSignIn = (Button) findViewById(R.id.btn_google_signin);
        cbRememberMe = (CheckBox) findViewById(R.id.cb_login_remember_me);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            NemApp.getInstance().setUsername(mUser.getUid());
            if (mUser.getPhotoUrl() != null) {
                NemApp.getInstance().setPhotoPathUrl(mUser.getPhotoUrl().toString());
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        cbRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtil.setBoolean(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_REMEMBER_ME, b);
            }
        });

        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uname = inputUsername.getText().toString();
                String pwd = inputPassword.getText().toString();
                if (uname.trim().equalsIgnoreCase("")) {
                    tilUsername.setError("Username cant be empty");
//                    Toast.makeText(LoginActivity.this, "Username cant be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    tilUsername.setErrorEnabled(false);
                }

                if (pwd.trim().equalsIgnoreCase("")) {
//                    Toast.makeText(LoginActivity.this, "Password cant be empty", Toast.LENGTH_SHORT).show();
                    tilPassword.setError("Password cant be empty");
                    return;
                } else {
                    tilPassword.setErrorEnabled(false);
                }

                if ((PreferenceUtil.getString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_USERNAME).equals(uname)) &&
                        (PreferenceUtil.getString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_PASSWORD).equals(pwd))) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    PreferenceUtil.setString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_CURRENT_USER, "ANONYMOUS");
                    if (PreferenceUtil.getString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_PREV_USER).equalsIgnoreCase("ANONYMOUS")) {
                        PreferenceUtil.setBoolean(LoginActivity.this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_IS_FIRST_TIME, false);
                        PreferenceUtil.setBoolean(LoginActivity.this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_SHOW_TUTORIAL, false);
                    }
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials\nHint - admin:password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle the result of the sign-in activity
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mUser = mAuth.getCurrentUser();
                            NemApp.getInstance().setUsername(mUser.getUid());
                            if (mUser.getPhotoUrl() != null) {
                                NemApp.getInstance().setPhotoPathUrl(mUser.getPhotoUrl().toString());
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            PreferenceUtil.setString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_CURRENT_USER, mUser.getEmail());
                            if (PreferenceUtil.getString(getApplicationContext(), PrefConstants.PREF_APP_NAME, PrefConstants.PREF_PREV_USER).equalsIgnoreCase(mUser.getEmail())) {
                                PreferenceUtil.setBoolean(LoginActivity.this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_IS_FIRST_TIME, false);
                                PreferenceUtil.setBoolean(LoginActivity.this, PrefConstants.PREF_APP_NAME, PrefConstants.PREF_SHOW_TUTORIAL, false);
                            }
                            finish();
                        }
                    }
                });
    }
}
