package and.com.moviemanic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 11-03-2017.
 */

public class Favourite implements Parcelable {

    private String thumbnail;
    private String title;
    private String overview;
    private String release;
    private double vote;
    private Integer id;

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setVote(double vote) {
        this.vote = vote;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThumbnail() {

        return this.thumbnail;
    }

    public String getTitle() {
        return this.title;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getRelease() {
        return this.release;
    }

    public double getVote() {
        return this.vote;
    }

    public Integer getId() {
        return this.id;
    }

    public Favourite(String thumbnail, String title, String overview, String release, double vote, Integer id) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.overview = overview;
        this.release = release;
        this.vote = vote;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbnail);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.release);
        dest.writeDouble(this.vote);
        dest.writeValue(this.id);
    }

    public Favourite() {
    }

    protected Favourite(Parcel in) {
        this.thumbnail = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.release = in.readString();
        this.vote = in.readDouble();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Favourite> CREATOR = new Parcelable.Creator<Favourite>() {
        @Override
        public Favourite createFromParcel(Parcel source) {
            return new Favourite(source);
        }

        @Override
        public Favourite[] newArray(int size) {
            return new Favourite[size];
        }
    };
}
