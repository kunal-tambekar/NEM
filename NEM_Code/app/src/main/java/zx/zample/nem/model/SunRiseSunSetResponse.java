package zx.zample.nem.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kunal on 4/12/17.
 */

public class SunRiseSunSetResponse {

    @SerializedName("results")
    private SunRiseSunSetModel results;
    @SerializedName("status")
    private String status;

    public SunRiseSunSetResponse(SunRiseSunSetModel results, String status) {
        this.results = results;
        this.status = status;
    }

    public SunRiseSunSetModel getResults() {
        return results;
    }

    public void setResults(SunRiseSunSetModel results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
