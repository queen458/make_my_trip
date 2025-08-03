package com.makemytrip.makemytrip.repositories;

import com.makemytrip.makemytrip.models.SearchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends MongoRepository<SearchHistory, String> {
    
    List<SearchHistory> findByUserIdOrderBySearchDateTimeDesc(String userId);
    
    List<SearchHistory> findByUserIdAndSearchTypeOrderBySearchDateTimeDesc(String userId, String searchType);
    
    @Query("{'userId': ?0}")
    List<SearchHistory> findTop10ByUserIdOrderBySearchDateTimeDesc(String userId);
    
    void deleteByUserId(String userId);
}

