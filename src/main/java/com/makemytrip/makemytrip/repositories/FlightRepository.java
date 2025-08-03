package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlightRepository extends MongoRepository<Flight, String> {
    
    List<Flight> findByFromAndTo(String from, String to);
    
    List<Flight> findByFlightName(String flightName);
    
    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Flight> findByPriceRange(double minPrice, double maxPrice);
    
    @Query("{'from': {$regex: ?0, $options: 'i'}}")
    List<Flight> findByFromContaining(String from);
    
    @Query("{'to': {$regex: ?0, $options: 'i'}}")
    List<Flight> findByToContaining(String to);
    
    @Query("{'$or': [{'from': {$regex: ?0, $options: 'i'}}, {'to': {$regex: ?0, $options: 'i'}}]}")
    List<Flight> findByFromOrToContaining(String location);
    
    @Query("{'flightName': {$regex: ?0, $options: 'i'}}")
    List<Flight> findByFlightNameContaining(String flightName);
    
    List<Flight> findByAvailableSeatsGreaterThan(int seats);
}