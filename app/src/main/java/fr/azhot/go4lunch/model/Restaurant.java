package fr.azhot.go4lunch.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

/**
 * Class representing a restaurant in Go4Lunch app.
 */
public class Restaurant {

    private String mName;
    private String mPlaceId;
    private String mVicinity;
    private double mLatitude;
    private double mLongitude;
    @Nullable
    private Boolean mIsOpen;
    @Nullable
    private Long mRating;
    @Nullable
    private String mPhotoReference;
    @Nullable
    private String mPhoneNumber;
    @Nullable
    private String mWebsite;
    @Exclude
    private int mWorkmatesJoining;


    public Restaurant() {
        // public no-arg constructor needed for Firestore
    }

    /**
     * Constructor which returns a {@link Restaurant} new instance based on
     * a {@link NearbySearchPOJO.Result} existing instance.
     *
     * @param result a {@link NearbySearchPOJO.Result} which was returned by
     *               the Google Places Nearby Search API.
     */
    public Restaurant(@NonNull NearbySearchPOJO.Result result) {
        mName = result.getName();
        mPlaceId = result.getPlaceId();
        mVicinity = result.getVicinity();
        mLatitude = result.getGeometry().getLocation().getLat();
        mLongitude = result.getGeometry().getLocation().getLng();
        mIsOpen = result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow() : null;
        mRating = result.getRating() != null ? (Math.round(result.getRating() / 5 * 3)) : null;
        mPhotoReference = result.getPhotos() != null ? result.getPhotos().get(0).getPhotoReference() : null;
        mPhoneNumber = null;
        mWebsite = null;
        mWorkmatesJoining = 0;
    }

    /**
     * Constructor which returns a {@link Restaurant} new instance based on
     * a {@link DetailsPOJO.Result} existing instance.
     *
     * @param result a {@link DetailsPOJO.Result} which was returned by the
     *               Google Places Details API.
     */
    public Restaurant(@NonNull DetailsPOJO.Result result) {
        mName = result.getName();
        mPlaceId = result.getPlaceId();
        mVicinity = result.getVicinity();
        mLatitude = result.getGeometry().getLocation().getLat();
        mLongitude = result.getGeometry().getLocation().getLng();
        mRating = result.getRating() != null ? (Math.round(result.getRating() / 5 * 3)) : null;
        mPhotoReference = result.getPhotos() != null ? result.getPhotos().get(0).getPhotoReference() : null;
        mPhoneNumber = result.getInternationalPhoneNumber();
        mWebsite = result.getWebsite();
        mWorkmatesJoining = 0;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getVicinity() {
        return mVicinity;
    }

    public void setVicinity(String vicinity) {
        mVicinity = vicinity;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @Nullable
    public Boolean getOpen() {
        return mIsOpen;
    }

    public void setOpen(@Nullable Boolean open) {
        mIsOpen = open;
    }

    @Nullable
    public Long getRating() {
        return mRating;
    }

    public void setRating(@Nullable Long rating) {
        mRating = rating;
    }

    @Nullable
    public String getPhotoReference() {
        return mPhotoReference;
    }

    public void setPhotoReference(@Nullable String photoReference) {
        mPhotoReference = photoReference;
    }

    @Nullable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    @Nullable
    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(@Nullable String website) {
        mWebsite = website;
    }

    @Exclude
    public int getWorkmatesJoining() {
        return mWorkmatesJoining;
    }

    @Exclude
    public void setWorkmatesJoining(int workmatesJoining) {
        mWorkmatesJoining = workmatesJoining;
    }
}
