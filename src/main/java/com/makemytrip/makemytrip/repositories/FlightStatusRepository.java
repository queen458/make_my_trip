package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.FlightStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightStatusRepository extends MongoRepository<FlightStatus, String> {
    
    Optional<FlightStatus> findByFlightNumber(String flightNumber);
    
    List<FlightStatus> findByAirline(String airline);
    
    List<FlightStatus> findByOriginAndDestination(String origin, String destination);
    
    List<FlightStatus> findByStatus(String status);
    
    @Query("{'flightNumber': {$regex: ?0, $options: 'i'}}")
    List<FlightStatus> findByFlightNumberContaining(String flightNumber);
    
    @Query("{'$or': [{'origin': {$regex: ?0, $options: 'i'}}, {'destination': {$regex: ?0, $options: 'i'}}]}")
    List<FlightStatus> findByOriginOrDestinationContaining(String location);
}

