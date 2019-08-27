package zx.zample.nem.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import zx.zample.nem.model.ForismaticQuoteModel;
import zx.zample.nem.model.MovieResponse;
import zx.zample.nem.model.NewsResponse;
import zx.zample.nem.model.SunRiseSunSetResponse;
import zx.zample.nem.util.Constants;

/**
 * Created by kunal on 4/12/17.
 */

public interface ApiInterface {

    //method=getQuote&format=json&json=parseQuote&lang=en
    @GET(Constants.URL_QUOTE)
    Call<ForismaticQuoteModel> getForismaticQuote(@Query("method") String apiKey,
                                                  @Query("format") String format,
                                                  @Query("json") String json,
                                                  @Query("lang") String lang);

    @GET(Constants.URL_MOVIES_TOP_RATED)
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey,@Query("page") int page);

    @GET("/movie/{id}")
    Call<MovieResponse> getMovieDetails(@Path("id") String movieId,
                                        @Query("api_key") String apiKey);


    @GET("json")
    Call<SunRiseSunSetResponse> getSunRiseSunSetDetails(@Query("lat") String lat,
                                                        @Query("lng") String lng,
                                                        @Query("date") String date,
                                                        @Query("formatted") int val);

    //articles?source=the-new-york-times&language=en&apiKey=
    @GET(Constants.URL_NEWS_ARTICLE)
    Call<NewsResponse> getTopNews(@Query("source") String source,
                                  @Query("language") String lang,
                                  @Query("apiKey") String apiKey);

}
