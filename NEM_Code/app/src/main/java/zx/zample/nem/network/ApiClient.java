package zx.zample.nem.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zx.zample.nem.util.Constants;

/**
 * Created by kunal on 4/12/17.
 */

public class ApiClient {

    private static Retrofit retrofitN = null;
    private static Retrofit retrofitE = null;
    private static Retrofit retrofitM = null;
    private static Retrofit retrofitSun = null;

    public static Retrofit getNInstance() {
        if (retrofitN == null) {
            retrofitN = new Retrofit.Builder()
                    .baseUrl(Constants.URL_NEWS_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitN;
    }

    public static Retrofit getSunInstance() {
        if (retrofitSun == null) {
            retrofitSun = new Retrofit.Builder()
                    .baseUrl(Constants.URL_SUNRISE_SUNSET_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitSun;
    }

    public static Retrofit getEInstance() {
        if (retrofitE == null) {
            retrofitE = new Retrofit.Builder()
                    .baseUrl(Constants.URL_MOVIE_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitE;
    }

    public static Retrofit getMInstance() {
        if (retrofitM == null) {
            retrofitM = new Retrofit.Builder()
                    .baseUrl(Constants.URL_QUOTES_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitM;
    }
}
