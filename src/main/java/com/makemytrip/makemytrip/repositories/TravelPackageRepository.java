package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.TravelPackage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TravelPackageRepository extends MongoRepository<TravelPackage, String> {
    
    List<TravelPackage> findByDestination(String destination);
    
    List<TravelPackage> findByPackageType(String packageType);
    
    List<TravelPackage> findByIsActiveTrue();
    
    @Query("{'destination': {$regex: ?0, $options: 'i'}}")
    List<TravelPackage> findByDestinationContaining(String destination);
    
    @Query("{'discountedPrice': {$gte: ?0, $lte: ?1}}")
    List<TravelPackage> findByPriceRange(double minPrice, double maxPrice);
    
    @Query("{'duration': {$gte: ?0, $lte: ?1}}")
    List<TravelPackage> findByDurationRange(int minDuration, int maxDuration);
    
    @Query("{'packageName': {$regex: ?0, $options: 'i'}}")
    List<TravelPackage> findByPackageNameContaining(String packageName);
}

