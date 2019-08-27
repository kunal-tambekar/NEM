package zx.zample.nem.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kunal on 4/17/17.
 */

public class ForismaticQuoteModel {

    @SerializedName("quoteText")
    private String quoteText;
    @SerializedName("quoteAuthor")
    private String quoteAuthor;
    @SerializedName("senderName")
    private String senderName;
    @SerializedName("senderLink")
    private String senderLink;
    @SerializedName("quoteLink")
    private String quoteLink;

    public ForismaticQuoteModel(String quoteText, String quoteAuthor, String senderName, String senderLink, String quoteLink) {
        this.quoteText = quoteText;
        this.quoteAuthor = quoteAuthor;
        this.senderName = senderName;
        this.senderLink = senderLink;
        this.quoteLink = quoteLink;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getQuoteAuthor() {
        return quoteAuthor;
    }

    public void setQuoteAuthor(String quoteAuthor) {
        this.quoteAuthor = quoteAuthor;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLink() {
        return senderLink;
    }

    public void setSenderLink(String senderLink) {
        this.senderLink = senderLink;
    }

    public String getQuoteLink() {
        return quoteLink;
    }

    public void setQuoteLink(String quoteLink) {
        this.quoteLink = quoteLink;
    }
}
