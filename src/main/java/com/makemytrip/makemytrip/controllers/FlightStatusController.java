package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.FlightStatus;
import com.makemytrip.makemytrip.services.FlightStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight-status")
@CrossOrigin(origins = "*")
public class FlightStatusController {

    @Autowired
    private FlightStatusService flightStatusService;

    @GetMapping
    public ResponseEntity<List<FlightStatus>> getAllFlightStatuses() {
        List<FlightStatus> statuses = flightStatusService.getAllFlightStatuses();
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<FlightStatus> getFlightStatus(@PathVariable String flightNumber) {
        Optional<FlightStatus> status = flightStatusService.getFlightStatusByNumber(flightNumber);
        return status.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/airline/{airline}")
    public ResponseEntity<List<FlightStatus>> getFlightStatusesByAirline(@PathVariable String airline) {
        List<FlightStatus> statuses = flightStatusService.getFlightStatusesByAirline(airline);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/route")
    public ResponseEntity<List<FlightStatus>> getFlightStatusesByRoute(
            @RequestParam String origin,
            @RequestParam String destination) {
        List<FlightStatus> statuses = flightStatusService.getFlightStatusesByRoute(origin, destination);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightStatus>> searchFlightStatuses(@RequestParam String query) {
        List<FlightStatus> statuses = flightStatusService.searchFlightStatuses(query);
        return ResponseEntity.ok(statuses);
    }

    @PostMapping
    public ResponseEntity<FlightStatus> createFlightStatus(@RequestBody FlightStatus flightStatus) {
        FlightStatus createdStatus = flightStatusService.createFlightStatus(flightStatus);
        return ResponseEntity.ok(createdStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightStatus> updateFlightStatus(
            @PathVariable String id,
            @RequestBody FlightStatus flightStatus) {
        FlightStatus updatedStatus = flightStatusService.updateFlightStatus(id, flightStatus);
        return ResponseEntity.ok(updatedStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightStatus(@PathVariable String id) {
        flightStatusService.deleteFlightStatus(id);
        return ResponseEntity.noContent().build();
    }

    // Mock API endpoints for real-time simulation
    @PostMapping("/simulate/{flightNumber}")
    public ResponseEntity<FlightStatus> simulateStatusUpdate(@PathVariable String flightNumber) {
        FlightStatus updatedStatus = flightStatusService.simulateStatusUpdate(flightNumber);
        if (updatedStatus != null) {
            return ResponseEntity.ok(updatedStatus);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/simulate-all")
    public ResponseEntity<List<FlightStatus>> simulateAllStatusUpdates() {
        List<FlightStatus> updatedStatuses = flightStatusService.simulateAllStatusUpdates();
        return ResponseEntity.ok(updatedStatuses);
    }

    @PostMapping("/initialize-mock-data")
    public ResponseEntity<String> initializeMockData() {
        flightStatusService.initializeMockData();
        return ResponseEntity.ok("Mock flight status data initialized successfully");
    }
}

