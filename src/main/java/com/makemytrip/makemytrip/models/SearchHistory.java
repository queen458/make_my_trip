package com.makemytrip.makemytrip.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "search_history")
public class SearchHistory {
    @Id
    private String _id;
    private String userId;
    private String searchType; // FLIGHT, HOTEL, PACKAGE
    private String origin;
    private String destination;
    private String checkInDate;
    private String checkOutDate;
    private int passengers;
    private String searchQuery;
    private LocalDateTime searchDateTime;

    // Constructors
    public SearchHistory() {}

    public SearchHistory(String userId, String searchType) {
        this.userId = userId;
        this.searchType = searchType;
        this.searchDateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public LocalDateTime getSearchDateTime() {
        return searchDateTime;
    }

    public void setSearchDateTime(LocalDateTime searchDateTime) {
        this.searchDateTime = searchDateTime;
    }
}

