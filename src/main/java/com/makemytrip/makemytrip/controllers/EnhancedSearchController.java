package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.SearchHistory;
import com.makemytrip.makemytrip.services.EnhancedSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class EnhancedSearchController {

    @Autowired
    private EnhancedSearchService enhancedSearchService;

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minSeats) {
        
        List<Flight> flights = enhancedSearchService.searchFlights(from, to, airline, minPrice, maxPrice, minSeats);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/hotels")
    public ResponseEntity<List<Hotel>> searchHotels(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String amenities,
            @RequestParam(required = false) Integer minRooms) {
        
        List<Hotel> hotels = enhancedSearchService.searchHotels(location, minPrice, maxPrice, amenities, minRooms);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/suggestions/locations")
    public ResponseEntity<List<String>> getLocationSuggestions(@RequestParam String query) {
        List<String> suggestions = enhancedSearchService.getLocationSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/suggestions/airlines")
    public ResponseEntity<List<String>> getAirlineSuggestions(@RequestParam String query) {
        List<String> suggestions = enhancedSearchService.getAirlineSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/history")
    public ResponseEntity<String> saveSearchHistory(
            @RequestParam String userId,
            @RequestParam String searchType,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(defaultValue = "1") int passengers) {
        
        enhancedSearchService.saveSearchHistory(userId, searchType, from, to, checkIn, checkOut, passengers);
        return ResponseEntity.ok("Search history saved successfully");
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<SearchHistory>> getRecentSearches(@PathVariable String userId) {
        List<SearchHistory> history = enhancedSearchService.getRecentSearches(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{userId}/{searchType}")
    public ResponseEntity<List<SearchHistory>> getRecentSearchesByType(
            @PathVariable String userId,
            @PathVariable String searchType) {
        List<SearchHistory> history = enhancedSearchService.getRecentSearchesByType(userId, searchType);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history/{userId}")
    public ResponseEntity<String> clearSearchHistory(@PathVariable String userId) {
        enhancedSearchService.clearSearchHistory(userId);
        return ResponseEntity.ok("Search history cleared successfully");
    }

    @GetMapping("/popular-destinations")
    public ResponseEntity<List<String>> getPopularDestinations() {
        List<String> destinations = enhancedSearchService.getPopularDestinations();
        return ResponseEntity.ok(destinations);
    }
}

