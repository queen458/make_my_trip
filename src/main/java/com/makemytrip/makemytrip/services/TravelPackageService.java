package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.TravelPackage;
import com.makemytrip.makemytrip.models.Flight;
import com.makemytrip.makemytrip.models.Hotel;
import com.makemytrip.makemytrip.repositories.TravelPackageRepository;
import com.makemytrip.makemytrip.repositories.FlightRepository;
import com.makemytrip.makemytrip.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class TravelPackageService {

    @Autowired
    private TravelPackageRepository travelPackageRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public List<TravelPackage> getAllPackages() {
        return travelPackageRepository.findByIsActiveTrue();
    }

    public Optional<TravelPackage> getPackageById(String id) {
        return travelPackageRepository.findById(id);
    }

    public List<TravelPackage> getPackagesByDestination(String destination) {
        return travelPackageRepository.findByDestinationContaining(destination);
    }

    public List<TravelPackage> getPackagesByType(String packageType) {
        return travelPackageRepository.findByPackageType(packageType);
    }

    public List<TravelPackage> getPackagesByPriceRange(double minPrice, double maxPrice) {
        return travelPackageRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<TravelPackage> getPackagesByDuration(int minDuration, int maxDuration) {
        return travelPackageRepository.findByDurationRange(minDuration, maxDuration);
    }

    public List<TravelPackage> searchPackages(String query) {
        List<TravelPackage> results = travelPackageRepository.findByPackageNameContaining(query);
        
        if (results.isEmpty()) {
            results = travelPackageRepository.findByDestinationContaining(query);
        }
        
        return results;
    }

    public TravelPackage createPackage(TravelPackage travelPackage) {
        calculatePackagePrice(travelPackage);
        return travelPackageRepository.save(travelPackage);
    }

    public TravelPackage updatePackage(String id, TravelPackage travelPackage) {
        travelPackage.setId(id);
        calculatePackagePrice(travelPackage);
        return travelPackageRepository.save(travelPackage);
    }

    public void deletePackage(String id) {
        travelPackageRepository.deleteById(id);
    }

    public TravelPackage calculateGroupDiscount(String packageId, int groupSize) {
        Optional<TravelPackage> optionalPackage = travelPackageRepository.findById(packageId);
        
        if (optionalPackage.isPresent()) {
            TravelPackage travelPackage = optionalPackage.get();
            double finalPrice = travelPackage.getDiscountedPrice();
            
            if (groupSize >= travelPackage.getMinGroupSize()) {
                double additionalDiscount = travelPackage.getGroupDiscountPercentage() / 100.0;
                finalPrice = finalPrice * (1 - additionalDiscount);
                
                // Create a copy with updated price for display
                TravelPackage discountedPackage = new TravelPackage();
                copyPackageProperties(travelPackage, discountedPackage);
                discountedPackage.setDiscountedPrice(finalPrice);
                discountedPackage.setDiscountPercentage(
                    travelPackage.getDiscountPercentage() + travelPackage.getGroupDiscountPercentage()
                );
                
                return discountedPackage;
            }
            
            return travelPackage;
        }
        
        return null;
    }

    private void calculatePackagePrice(TravelPackage travelPackage) {
        double totalPrice = 0.0;
        
        // Calculate flight costs
        if (travelPackage.getFlightIds() != null) {
            for (String flightId : travelPackage.getFlightIds()) {
                Optional<Flight> flight = flightRepository.findById(flightId);
                if (flight.isPresent()) {
                    totalPrice += flight.get().getPrice();
                }
            }
        }
        
        // Calculate hotel costs
        if (travelPackage.getHotelIds() != null) {
            for (String hotelId : travelPackage.getHotelIds()) {
                Optional<Hotel> hotel = hotelRepository.findById(hotelId);
                if (hotel.isPresent()) {
                    totalPrice += hotel.get().getPricePerNight() * travelPackage.getDuration();
                }
            }
        }
        
        // Add tour activity costs (mock pricing)
        if (travelPackage.getTourActivities() != null) {
            totalPrice += travelPackage.getTourActivities().size() * 50.0; // $50 per activity
        }
        
        travelPackage.setOriginalPrice(totalPrice);
        
        // Apply discount
        double discountAmount = totalPrice * (travelPackage.getDiscountPercentage() / 100.0);
        travelPackage.setDiscountedPrice(totalPrice - discountAmount);
    }

    private void copyPackageProperties(TravelPackage source, TravelPackage target) {
        target.setId(source.getId());
        target.setPackageName(source.getPackageName());
        target.setDescription(source.getDescription());
        target.setDestination(source.getDestination());
        target.setDuration(source.getDuration());
        target.setOriginalPrice(source.getOriginalPrice());
        target.setFlightIds(source.getFlightIds());
        target.setHotelIds(source.getHotelIds());
        target.setTourActivities(source.getTourActivities());
        target.setPackageType(source.getPackageType());
        target.setActive(source.isActive());
        target.setImageUrl(source.getImageUrl());
        target.setHighlights(source.getHighlights());
        target.setMinGroupSize(source.getMinGroupSize());
        target.setGroupDiscountPercentage(source.getGroupDiscountPercentage());
    }

    public void initializeMockData() {
        if (travelPackageRepository.count() == 0) {
            // Create some mock travel packages
            createMockPackage("Goa Beach Paradise", "Goa", "PRE_BUILT", 5,
                Arrays.asList("Beach resort stay", "Water sports", "Sunset cruise", "Local sightseeing"),
                "https://example.com/goa.jpg");
            
            createMockPackage("Kerala Backwaters", "Kerala", "PRE_BUILT", 7,
                Arrays.asList("Houseboat stay", "Spice plantation tour", "Ayurvedic massage", "Cultural show"),
                "https://example.com/kerala.jpg");
            
            createMockPackage("Rajasthan Royal Tour", "Rajasthan", "PRE_BUILT", 10,
                Arrays.asList("Palace visits", "Desert safari", "Camel ride", "Folk dance show", "Heritage walk"),
                "https://example.com/rajasthan.jpg");
            
            createMockPackage("Himachal Adventure", "Himachal Pradesh", "CUSTOMIZABLE", 6,
                Arrays.asList("Trekking", "River rafting", "Paragliding", "Mountain biking"),
                "https://example.com/himachal.jpg");
        }
    }

    private void createMockPackage(String name, String destination, String type, int duration,
                                 List<String> activities, String imageUrl) {
        TravelPackage travelPackage = new TravelPackage(name, destination, type);
        travelPackage.setDescription("Experience the best of " + destination + " with our carefully curated package.");
        travelPackage.setDuration(duration);
        travelPackage.setTourActivities(activities);
        travelPackage.setImageUrl(imageUrl);
        travelPackage.setHighlights(Arrays.asList(
            "All meals included",
            "Professional guide",
            "Transportation included",
            "24/7 support"
        ));
        
        // Mock pricing
        double basePrice = 200.0 * duration + activities.size() * 50.0;
        travelPackage.setOriginalPrice(basePrice);
        travelPackage.setDiscountedPrice(basePrice * 0.85); // 15% discount
        travelPackage.setDiscountPercentage(15.0);
        
        travelPackageRepository.save(travelPackage);
    }
}

