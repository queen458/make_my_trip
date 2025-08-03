package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.models.SearchHistory;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import com.makemytrip.makemytrip.repositories.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class EnhancedSearchService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    // Flight search with enhanced filters
    public List<Flight> searchFlights(String from, String to, String airline, 
                                    Double minPrice, Double maxPrice, Integer minSeats) {
        List<Flight> results = new ArrayList<>();
        
        if (from != null && to != null) {
            results = flightRepository.findByFromAndTo(from, to);
        } else if (from != null || to != null) {
            String location = from != null ? from : to;
            results = flightRepository.findByFromOrToContaining(location);
        } else {
            results = flightRepository.findAll();
        }
        
        // Apply filters
        if (airline != null && !airline.isEmpty()) {
            results = results.stream()
                .filter(flight -> flight.getFlightName().toLowerCase().contains(airline.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (minPrice != null) {
            results = results.stream()
                .filter(flight -> flight.getPrice() >= minPrice)
                .collect(Collectors.toList());
        }
        
        if (maxPrice != null) {
            results = results.stream()
                .filter(flight -> flight.getPrice() <= maxPrice)
                .collect(Collectors.toList());
        }
        
        if (minSeats != null) {
            results = results.stream()
                .filter(flight -> flight.getAvailableSeats() >= minSeats)
                .collect(Collectors.toList());
        }
        
        return results;
    }

    // Hotel search with enhanced filters
    public List<Hotel> searchHotels(String location, Double minPrice, Double maxPrice, 
                                  String amenities, Integer minRooms) {
        List<Hotel> results = new ArrayList<>();
        
        if (location != null && !location.isEmpty()) {
            results = hotelRepository.findByLocationContaining(location);
        } else {
            results = hotelRepository.findAll();
        }
        
        // Apply filters
        if (minPrice != null && maxPrice != null) {
            results = results.stream()
                .filter(hotel -> hotel.getPricePerNight() >= minPrice && hotel.getPricePerNight() <= maxPrice)
                .collect(Collectors.toList());
        } else if (minPrice != null) {
            results = results.stream()
                .filter(hotel -> hotel.getPricePerNight() >= minPrice)
                .collect(Collectors.toList());
        } else if (maxPrice != null) {
            results = results.stream()
                .filter(hotel -> hotel.getPricePerNight() <= maxPrice)
                .collect(Collectors.toList());
        }
        
        if (amenities != null && !amenities.isEmpty()) {
            results = results.stream()
                .filter(hotel -> hotel.getamenities() != null && 
                    hotel.getamenities().toLowerCase().contains(amenities.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (minRooms != null) {
            results = results.stream()
                .filter(hotel -> hotel.getAvailableRooms() >= minRooms)
                .collect(Collectors.toList());
        }
        
        return results;
    }

    // Autocomplete for locations
    public List<String> getLocationSuggestions(String query) {
        Set<String> suggestions = new HashSet<>();
        
        // Get flight locations
        List<Flight> flights = flightRepository.findByFromOrToContaining(query);
        for (Flight flight : flights) {
            if (flight.getFrom().toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(flight.getFrom());
            }
            if (flight.getTo().toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(flight.getTo());
            }
        }
        
        // Get hotel locations
        List<Hotel> hotels = hotelRepository.findByLocationContaining(query);
        for (Hotel hotel : hotels) {
            suggestions.add(hotel.getLocation());
        }
        
        return suggestions.stream()
            .limit(10)
            .collect(Collectors.toList());
    }

    // Get airline suggestions
    public List<String> getAirlineSuggestions(String query) {
        List<Flight> flights = flightRepository.findByFlightNameContaining(query);
        
        return flights.stream()
            .map(Flight::getFlightName)
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }

    // Save search history
    public void saveSearchHistory(String userId, String searchType, String from, String to, 
                                String checkIn, String checkOut, int passengers) {
        SearchHistory history = new SearchHistory(userId, searchType);
        history.setOrigin(from);
        history.setDestination(to);
        history.setCheckInDate(checkIn);
        history.setCheckOutDate(checkOut);
        history.setPassengers(passengers);
        history.setSearchQuery(from + " to " + to);
        
        searchHistoryRepository.save(history);
        
        // Keep only last 20 searches per user
        List<SearchHistory> userHistory = searchHistoryRepository.findByUserIdOrderBySearchDateTimeDesc(userId);
        if (userHistory.size() > 20) {
            for (int i = 20; i < userHistory.size(); i++) {
                searchHistoryRepository.delete(userHistory.get(i));
            }
        }
    }

    // Get recent searches
    public List<SearchHistory> getRecentSearches(String userId) {
        return searchHistoryRepository.findTop10ByUserIdOrderBySearchDateTimeDesc(userId);
    }

    // Get recent searches by type
    public List<SearchHistory> getRecentSearchesByType(String userId, String searchType) {
        List<SearchHistory> allHistory = searchHistoryRepository.findByUserIdAndSearchTypeOrderBySearchDateTimeDesc(userId, searchType);
        return allHistory.stream().limit(10).collect(Collectors.toList());
    }

    // Clear search history
    public void clearSearchHistory(String userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }

    // Get popular destinations
    public List<String> getPopularDestinations() {
        List<SearchHistory> recentSearches = searchHistoryRepository.findAll()
            .stream()
            .filter(search -> search.getSearchDateTime().isAfter(LocalDateTime.now().minusDays(30)))
            .collect(Collectors.toList());
        
        return recentSearches.stream()
            .map(SearchHistory::getDestination)
            .filter(dest -> dest != null && !dest.isEmpty())
            .collect(Collectors.groupingBy(dest -> dest, Collectors.counting()))
            .entrySet()
            .stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .map(entry -> entry.getKey())
            .collect(Collectors.toList());
    }
}

