package and.com.moviemanic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 07-03-2017.
 */

public class Movie extends Favourite implements Parcelable {

    private String thumbnail;
    private String title;
    private String overview;
    private String release;
    private double vote;
    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public Movie(String thumbnail, String title, String overview, String release, double vote, int id) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.overview = overview;
        this.release = release;
        this.vote = vote;
        this.id = id;
    }

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

    public Movie() {
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
        dest.writeInt(this.id);
    }

    protected Movie(Parcel in) {
        this.thumbnail = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.release = in.readString();
        this.vote = in.readDouble();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
