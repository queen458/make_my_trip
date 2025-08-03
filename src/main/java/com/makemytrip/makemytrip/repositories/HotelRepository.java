package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends MongoRepository<Hotel, String> {
    
    List<Hotel> findByLocation(String location);
    
    @Query("{'pricePerNight': {$gte: ?0, $lte: ?1}}")
    List<Hotel> findByPriceRange(double minPrice, double maxPrice);
    
    @Query("{'location': {$regex: ?0, $options: 'i'}}")
    List<Hotel> findByLocationContaining(String location);
    
    @Query("{'hotelName': {$regex: ?0, $options: 'i'}}")
    List<Hotel> findByHotelNameContaining(String hotelName);
    
    @Query("{'amenities': {$regex: ?0, $options: 'i'}}")
    List<Hotel> findByAmenitiesContaining(String amenity);
    
    List<Hotel> findByAvailableRoomsGreaterThan(int rooms);
    
    @Query("{'$and': [{'location': {$regex: ?0, $options: 'i'}}, {'pricePerNight': {$gte: ?1, $lte: ?2}}]}")
    List<Hotel> findByLocationAndPriceRange(String location, double minPrice, double maxPrice);
}