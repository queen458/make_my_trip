package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "travel_packages")
public class TravelPackage {
    @Id
    private String _id;
    private String packageName;
    private String description;
    private String destination;
    private int duration; // in days
    private double originalPrice;
    private double discountedPrice;
    private double discountPercentage;
    private List<String> flightIds;
    private List<String> hotelIds;
    private List<String> tourActivities;
    private String packageType; // PRE_BUILT, CUSTOMIZABLE
    private boolean isActive;
    private String imageUrl;
    private List<String> highlights;
    private int minGroupSize;
    private double groupDiscountPercentage;

    // Constructors
    public TravelPackage() {}

    public TravelPackage(String packageName, String destination, String packageType) {
        this.packageName = packageName;
        this.destination = destination;
        this.packageType = packageType;
        this.isActive = true;
        this.discountPercentage = 10.0; // Default 10% discount
        this.minGroupSize = 5;
        this.groupDiscountPercentage = 5.0; // Additional 5% for groups
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public List<String> getFlightIds() {
        return flightIds;
    }

    public void setFlightIds(List<String> flightIds) {
        this.flightIds = flightIds;
    }

    public List<String> getHotelIds() {
        return hotelIds;
    }

    public void setHotelIds(List<String> hotelIds) {
        this.hotelIds = hotelIds;
    }

    public List<String> getTourActivities() {
        return tourActivities;
    }

    public void setTourActivities(List<String> tourActivities) {
        this.tourActivities = tourActivities;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public int getMinGroupSize() {
        return minGroupSize;
    }

    public void setMinGroupSize(int minGroupSize) {
        this.minGroupSize = minGroupSize;
    }

    public double getGroupDiscountPercentage() {
        return groupDiscountPercentage;
    }

    public void setGroupDiscountPercentage(double groupDiscountPercentage) {
        this.groupDiscountPercentage = groupDiscountPercentage;
    }
}

