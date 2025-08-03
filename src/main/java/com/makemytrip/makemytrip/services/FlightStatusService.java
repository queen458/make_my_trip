package com.makemytrip.makemytrip.services;

import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.repositories.FlightStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class FlightStatusService {

    @Autowired
    private FlightStatusRepository flightStatusRepository;

    private final Random random = new Random();
    private final String[] delayReasons = {
        "Weather conditions",
        "Air traffic control",
        "Mechanical issue",
        "Crew scheduling",
        "Airport congestion",
        "Security check",
        "Fuel delay",
        "Previous flight delay"
    };

    private final String[] statuses = {
        "ON_TIME", "DELAYED", "DEPARTED", "ARRIVED", "CANCELLED"
    };

    public List<FlightStatus> getAllFlightStatuses() {
        return flightStatusRepository.findAll();
    }

    public Optional<FlightStatus> getFlightStatusByNumber(String flightNumber) {
        return flightStatusRepository.findByFlightNumber(flightNumber);
    }

    public List<FlightStatus> getFlightStatusesByAirline(String airline) {
        return flightStatusRepository.findByAirline(airline);
    }

    public List<FlightStatus> getFlightStatusesByRoute(String origin, String destination) {
        return flightStatusRepository.findByOriginAndDestination(origin, destination);
    }

    public List<FlightStatus> searchFlightStatuses(String query) {
        // Try to find by flight number first
        List<FlightStatus> results = flightStatusRepository.findByFlightNumberContaining(query);
        
        // If no results, search by location
        if (results.isEmpty()) {
            results = flightStatusRepository.findByOriginOrDestinationContaining(query);
        }
        
        return results;
    }

    public FlightStatus createFlightStatus(FlightStatus flightStatus) {
        flightStatus.setStatus("ON_TIME");
        flightStatus.setDelayMinutes(0);
        return flightStatusRepository.save(flightStatus);
    }

    public FlightStatus updateFlightStatus(String id, FlightStatus flightStatus) {
        flightStatus.setId(id);
        return flightStatusRepository.save(flightStatus);
    }

    public void deleteFlightStatus(String id) {
        flightStatusRepository.deleteById(id);
    }

    // Mock API methods for simulating real-time updates
    public FlightStatus simulateStatusUpdate(String flightNumber) {
        Optional<FlightStatus> optionalStatus = flightStatusRepository.findByFlightNumber(flightNumber);
        
        if (optionalStatus.isPresent()) {
            FlightStatus status = optionalStatus.get();
            
            // Randomly update status
            String newStatus = statuses[random.nextInt(statuses.length)];
            status.setStatus(newStatus);
            
            // If delayed, add delay information
            if ("DELAYED".equals(newStatus)) {
                int delayMinutes = 15 + random.nextInt(180); // 15-195 minutes delay
                status.setDelayMinutes(delayMinutes);
                status.setDelayReason(delayReasons[random.nextInt(delayReasons.length)]);
                
                // Update estimated arrival
                if (status.getScheduledArrival() != null) {
                    status.setEstimatedArrival(status.getScheduledArrival().plusMinutes(delayMinutes));
                }
            } else if ("ON_TIME".equals(newStatus)) {
                status.setDelayMinutes(0);
                status.setDelayReason(null);
                status.setEstimatedArrival(status.getScheduledArrival());
            }
            
            return flightStatusRepository.save(status);
        }
        
        return null;
    }

    public List<FlightStatus> simulateAllStatusUpdates() {
        List<FlightStatus> allStatuses = flightStatusRepository.findAll();
        
        for (FlightStatus status : allStatuses) {
            // 30% chance of status change
            if (random.nextDouble() < 0.3) {
                simulateStatusUpdate(status.getFlightNumber());
            }
        }
        
        return flightStatusRepository.findAll();
    }

    public void initializeMockData() {
        if (flightStatusRepository.count() == 0) {
            // Create some mock flight statuses
            createMockFlightStatus("AI101", "Air India", "DEL", "BOM", 
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4));
            
            createMockFlightStatus("6E202", "IndiGo", "BOM", "BLR", 
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3));
            
            createMockFlightStatus("SG303", "SpiceJet", "BLR", "CCU", 
                LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(3));
            
            createMockFlightStatus("UK404", "Vistara", "CCU", "DEL", 
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(5));
            
            createMockFlightStatus("G8505", "GoAir", "DEL", "GOI", 
                LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(6));
        }
    }

    private void createMockFlightStatus(String flightNumber, String airline, String origin, 
                                      String destination, LocalDateTime departure, LocalDateTime arrival) {
        FlightStatus status = new FlightStatus(flightNumber, airline, origin, destination);
        status.setScheduledDeparture(departure);
        status.setScheduledArrival(arrival);
        status.setEstimatedArrival(arrival);
        status.setGate("A" + (random.nextInt(20) + 1));
        status.setTerminal("T" + (random.nextInt(3) + 1));
        
        flightStatusRepository.save(status);
    }
}

