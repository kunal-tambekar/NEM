package zx.zample.nem.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zx.zample.nem.R;
import zx.zample.nem.adapter.NewsAdapter;
import zx.zample.nem.app.NemApp;
import zx.zample.nem.model.NewsModel;
import zx.zample.nem.model.NewsResponse;
import zx.zample.nem.model.SunRiseSunSetModel;
import zx.zample.nem.model.SunRiseSunSetResponse;
import zx.zample.nem.network.ApiClient;
import zx.zample.nem.network.ApiInterface;
import zx.zample.nem.util.Constants;
import zx.zample.nem.widget.RecyclerviewTouchListner;

public class NewsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, RecyclerviewTouchListner.ClickListener {

    private final int PERMISSION_ACCESS_FINE_LOCATION = 111;
    private LocationManager locationManager;
    private double latitudeNetwork = 0.0;
    private double longitudeNetwork = 0.0;
    private GoogleApiClient googleApiClient;
    private NewsAdapter newsAdapter;
    private RecyclerView newsList;
    private ProgressBar pbCityName;
    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeNetwork = location.getLongitude();
            latitudeNetwork = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fetchSunriseSunsetData();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
    private ProgressBar pbNews;
    private TextView tvNewsError;
    private ArrayList<NewsModel> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        newsList = (RecyclerView) findViewById(R.id.rv_news_list);
        pbCityName = (ProgressBar) findViewById(R.id.progress_city);
        pbNews = (ProgressBar) findViewById(R.id.progress_news);
        tvNewsError = (TextView) findViewById(R.id.tv_news_error);
        pbCityName.setVisibility(View.VISIBLE);
        pbCityName.setVisibility(View.VISIBLE);
        tvNewsError.setVisibility(View.GONE);

        newsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newsList.addOnItemTouchListener(new RecyclerviewTouchListner(this, newsList, this));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);

        tvNewsError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchTopNews();
                fetchSunriseSunsetData();
            }
        });
        if (NemApp.getInstance().getConnectionDetector().isConnectedToInternet()) {
            fetchTopNews();
        } else {
            Toast.makeText(this, "Check network connection and try again", Toast.LENGTH_SHORT).show();
            tvNewsError.setVisibility(View.VISIBLE);
            pbNews.setVisibility(View.GONE);
        }

        if (provider != null) {
            startLocationUpdates();
        }

    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(NewsActivity.this, "Need Location details to fetch News", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
        dialog.show();
    }

    public void fetchSunriseSunsetData() {
        ApiInterface apiService = ApiClient.getSunInstance().create(ApiInterface.class);
        Call<SunRiseSunSetResponse> getSunInfo = apiService.getSunRiseSunSetDetails(String.valueOf(latitudeNetwork),
                String.valueOf(longitudeNetwork), "today", 0);
        getSunInfo.enqueue(new Callback<SunRiseSunSetResponse>() {
            @Override
            public void onResponse(Call<SunRiseSunSetResponse> call, Response<SunRiseSunSetResponse> response) {
                fetchAddress();
                SunRiseSunSetModel sunData = response.body().getResults();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss+00:00");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = null;
                try {
                    date = df.parse(sunData.getSunrise());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat df2 = new SimpleDateFormat("h:mm:ss a");
                df2.setTimeZone(TimeZone.getDefault());
                String formattedDate = df2.format(date);
                ((TextView) findViewById(R.id.txt_sunrise)).setText("DAWN: " + formattedDate);

                try {
                    date = df.parse(sunData.getSunset());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                formattedDate = df2.format(date);
                ((TextView) findViewById(R.id.txt_sunset)).setText("DUSK: " + formattedDate);
            }

            @Override
            public void onFailure(Call<SunRiseSunSetResponse> call, Throwable t) {
                Toast.makeText(NewsActivity.this, "Failed to fetch Sunrise Sunset details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchTopNews() {
        pbNews.setVisibility(View.VISIBLE);
        tvNewsError.setVisibility(View.GONE);
        ApiInterface apiService = ApiClient.getNInstance().create(ApiInterface.class);
        Call<NewsResponse> getTopNews = apiService.getTopNews(Constants.NEWS_SRC_NEW_YOR_TIMES, "en", Constants.NEWS_API_KEY);
        getTopNews.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                articles = new ArrayList<>(response.body().getArticles());
                newsAdapter = new NewsAdapter(NewsActivity.this, articles);
                newsList.setAdapter(newsAdapter);
                pbNews.setVisibility(View.GONE);
                tvNewsError.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                t.printStackTrace();
                pbNews.setVisibility(View.GONE);
                tvNewsError.setVisibility(View.VISIBLE);
                Toast.makeText(NewsActivity.this, "Failed to fetch News", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 1000, 10, locationListenerGPS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        if (latitudeNetwork == longitudeNetwork)
            startLocationUpdates();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        locationManager.removeUpdates(locationListenerGPS);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                fetchSunriseSunsetData();
            } else {
                Toast.makeText(this, "Need your location to fetch Sunset and Sunrise timing", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void fetchAddress() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitudeNetwork, longitudeNetwork, 1);
            if (addresses.size() > 0) {
                // Just some handy code to get complete address
                StringBuilder strAddress = new StringBuilder();
                String address = addresses.get(0).getAddressLine(0) + "\n";
                strAddress.append(address);
                String city = addresses.get(0).getLocality() + "\n";
                strAddress.append(city);
                String state = addresses.get(0).getAdminArea() + "\n";
                strAddress.append(state);
                String country = addresses.get(0).getCountryName() + "\n";
                strAddress.append(country);
                String postalCode = addresses.get(0).getPostalCode() + "\n";
                strAddress.append(postalCode);

                ((TextView) findViewById(R.id.txt_city)).setText(addresses.get(0).getLocality());

                Toast.makeText(NewsActivity.this, "Location : " + addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
            }
            pbCityName.setVisibility(View.GONE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                latitudeNetwork = lastLocation.getLatitude();
                longitudeNetwork = lastLocation.getLongitude();
                fetchSunriseSunsetData();

            } else {
                if (!isLocationEnabled() && latitudeNetwork == longitudeNetwork) {
                    showAlert();
                    Toast.makeText(NewsActivity.this, "Last know location not found", Toast.LENGTH_SHORT).show();
                    return;
                }

            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View view, int position) {
        String newsUrl = view.findViewById(R.id.cv_news_card).getTag().toString();
        if (newsUrl.trim().length() > 5)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl)));
//        Toast.makeText(this, "Redirecting to: " + view.findViewById(R.id.cv_news_card).getTag(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View view, int position) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("NEWS_URL", view.findViewById(R.id.cv_news_card).getTag().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "COPIED URL: " + view.findViewById(R.id.cv_news_card).getTag(), Toast.LENGTH_SHORT).show();
    }
}
