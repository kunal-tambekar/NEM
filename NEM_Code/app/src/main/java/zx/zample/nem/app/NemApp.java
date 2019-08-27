package zx.zample.nem.app;

import android.app.Application;

import zx.zample.nem.util.ConnectionDetector;
import zx.zample.nem.util.PrefConstants;
import zx.zample.nem.util.PreferenceUtil;

/**
 * Created by kunal on 4/16/17.
 */

public class NemApp extends Application {
    public static NemApp mAppInstance;
    public static final String TAG = NemApp.class.getSimpleName();
    private ConnectionDetector mConnectionDetector;
    private String username,photoPathUrl;

    public static NemApp getInstance(){
        return mAppInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppInstance = this;

        PreferenceUtil.setString(this, PrefConstants.PREF_APP_NAME,PrefConstants.PREF_USERNAME,"admin");
        PreferenceUtil.setString(this, PrefConstants.PREF_APP_NAME,PrefConstants.PREF_PASSWORD,"password");
    }

    public ConnectionDetector getConnectionDetector(){
        if(mConnectionDetector== null){
            mConnectionDetector = new ConnectionDetector(this);
        }
        return mConnectionDetector;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoPathUrl() {
        return photoPathUrl;
    }

    public void setPhotoPathUrl(String photoPathUrl) {
        this.photoPathUrl = photoPathUrl;
    }
}
