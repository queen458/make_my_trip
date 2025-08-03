package com.makemytrip.makemytrip.controllers;

import com.makemytrip.makemytrip.models.TravelPackage;
import com.makemytrip.makemytrip.services.TravelPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "*")
public class TravelPackageController {

    @Autowired
    private TravelPackageService travelPackageService;

    @GetMapping
    public ResponseEntity<List<TravelPackage>> getAllPackages() {
        List<TravelPackage> packages = travelPackageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPackage> getPackageById(@PathVariable String id) {
        Optional<TravelPackage> travelPackage = travelPackageService.getPackageById(id);
        return travelPackage.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<TravelPackage>> getPackagesByDestination(@PathVariable String destination) {
        List<TravelPackage> packages = travelPackageService.getPackagesByDestination(destination);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TravelPackage>> getPackagesByType(@PathVariable String type) {
        List<TravelPackage> packages = travelPackageService.getPackagesByType(type);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<TravelPackage>> getPackagesByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<TravelPackage> packages = travelPackageService.getPackagesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/duration")
    public ResponseEntity<List<TravelPackage>> getPackagesByDuration(
            @RequestParam int minDuration,
            @RequestParam int maxDuration) {
        List<TravelPackage> packages = travelPackageService.getPackagesByDuration(minDuration, maxDuration);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TravelPackage>> searchPackages(@RequestParam String query) {
        List<TravelPackage> packages = travelPackageService.searchPackages(query);
        return ResponseEntity.ok(packages);
    }

    @PostMapping
    public ResponseEntity<TravelPackage> createPackage(@RequestBody TravelPackage travelPackage) {
        TravelPackage createdPackage = travelPackageService.createPackage(travelPackage);
        return ResponseEntity.ok(createdPackage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelPackage> updatePackage(
            @PathVariable String id,
            @RequestBody TravelPackage travelPackage) {
        TravelPackage updatedPackage = travelPackageService.updatePackage(id, travelPackage);
        return ResponseEntity.ok(updatedPackage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable String id) {
        travelPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/group-discount")
    public ResponseEntity<TravelPackage> calculateGroupDiscount(
            @PathVariable String id,
            @RequestParam int groupSize) {
        TravelPackage discountedPackage = travelPackageService.calculateGroupDiscount(id, groupSize);
        if (discountedPackage != null) {
            return ResponseEntity.ok(discountedPackage);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/initialize-mock-data")
    public ResponseEntity<String> initializeMockData() {
        travelPackageService.initializeMockData();
        return ResponseEntity.ok("Mock travel package data initialized successfully");
    }
}

