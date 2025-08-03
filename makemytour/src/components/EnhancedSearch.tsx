import { useState, useEffect, useRef } from 'react';
import { Button } from '@/components/ui/button';
import { 
  Search, 
  MapPin, 
  Calendar, 
  Users, 
  Filter,
  X,
  Clock,
  Star,
  Plane,
  Hotel as HotelIcon
} from 'lucide-react';
import axios from 'axios';

interface SearchFilters {
  searchType: 'flights' | 'hotels';
  from?: string;
  to?: string;
  location?: string;
  airline?: string;
  minPrice?: number;
  maxPrice?: number;
  minSeats?: number;
  minRooms?: number;
  amenities?: string;
  checkIn?: string;
  checkOut?: string;
  passengers: number;
}

interface SearchSuggestion {
  value: string;
  label: string;
  type: 'location' | 'airline';
}

interface SearchHistory {
  _id: string;
  searchType: string;
  origin?: string;
  destination?: string;
  searchQuery: string;
  searchDateTime: string;
}

interface EnhancedSearchProps {
  onSearchResults: (results: any[], type: string) => void;
  initialType?: 'flights' | 'hotels';
}

export default function EnhancedSearch({ onSearchResults, initialType = 'flights' }: EnhancedSearchProps) {
  const [filters, setFilters] = useState<SearchFilters>({
    searchType: initialType,
    passengers: 1
  });
  
  const [showFilters, setShowFilters] = useState(false);
  const [suggestions, setSuggestions] = useState<SearchSuggestion[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [activeSuggestionField, setActiveSuggestionField] = useState<string>('');
  const [searchHistory, setSearchHistory] = useState<SearchHistory[]>([]);
  const [showHistory, setShowHistory] = useState(false);
  const [loading, setLoading] = useState(false);
  
  const suggestionRef = useRef<HTMLDivElement>(null);
  const historyRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    loadSearchHistory();
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (suggestionRef.current && !suggestionRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
      if (historyRef.current && !historyRef.current.contains(event.target as Node)) {
        setShowHistory(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const loadSearchHistory = async () => {
    try {
      // Using a default user ID for demo purposes
      const response = await axios.get('http://localhost:8080/api/search/history/user123');
      setSearchHistory(response.data);
    } catch (error) {
      console.error('Error loading search history:', error);
    }
  };

  const getSuggestions = async (query: string, type: 'location' | 'airline') => {
    if (query.length < 2) {
      setSuggestions([]);
      return;
    }

    try {
      const endpoint = type === 'location' 
        ? `http://localhost:8080/api/search/suggestions/locations?query=${query}`
        : `http://localhost:8080/api/search/suggestions/airlines?query=${query}`;
      
      const response = await axios.get(endpoint);
      const suggestionList = response.data.map((item: string) => ({
        value: item,
        label: item,
        type
      }));
      
      setSuggestions(suggestionList);
      setShowSuggestions(true);
    } catch (error) {
      console.error('Error fetching suggestions:', error);
    }
  };

  const handleInputChange = (field: string, value: string) => {
    setFilters(prev => ({ ...prev, [field]: value }));
    
    // Trigger suggestions for location and airline fields
    if ((field === 'from' || field === 'to' || field === 'location') && value.length >= 2) {
      setActiveSuggestionField(field);
      getSuggestions(value, 'location');
    } else if (field === 'airline' && value.length >= 2) {
      setActiveSuggestionField(field);
      getSuggestions(value, 'airline');
    } else {
      setShowSuggestions(false);
    }
  };

  const selectSuggestion = (suggestion: SearchSuggestion) => {
    setFilters(prev => ({ ...prev, [activeSuggestionField]: suggestion.value }));
    setShowSuggestions(false);
    setSuggestions([]);
  };

  const selectFromHistory = (historyItem: SearchHistory) => {
    if (historyItem.searchType === 'FLIGHT') {
      setFilters(prev => ({
        ...prev,
        searchType: 'flights',
        from: historyItem.origin || '',
        to: historyItem.destination || ''
      }));
    } else if (historyItem.searchType === 'HOTEL') {
      setFilters(prev => ({
        ...prev,
        searchType: 'hotels',
        location: historyItem.destination || ''
      }));
    }
    setShowHistory(false);
  };

  const saveSearchHistory = async () => {
    try {
      const params = new URLSearchParams({
        userId: 'user123',
        searchType: filters.searchType.toUpperCase(),
        ...(filters.from && { from: filters.from }),
        ...(filters.to && { to: filters.to }),
        ...(filters.location && { to: filters.location }),
        passengers: filters.passengers.toString()
      });

      await axios.post(`http://localhost:8080/api/search/history?${params}`);
      loadSearchHistory();
    } catch (error) {
      console.error('Error saving search history:', error);
    }
  };

  const performSearch = async () => {
    setLoading(true);
    
    try {
      let response;
      
      if (filters.searchType === 'flights') {
        const params = new URLSearchParams();
        if (filters.from) params.append('from', filters.from);
        if (filters.to) params.append('to', filters.to);
        if (filters.airline) params.append('airline', filters.airline);
        if (filters.minPrice) params.append('minPrice', filters.minPrice.toString());
        if (filters.maxPrice) params.append('maxPrice', filters.maxPrice.toString());
        if (filters.minSeats) params.append('minSeats', filters.minSeats.toString());
        
        response = await axios.get(`http://localhost:8080/api/search/flights?${params}`);
      } else {
        const params = new URLSearchParams();
        if (filters.location) params.append('location', filters.location);
        if (filters.minPrice) params.append('minPrice', filters.minPrice.toString());
        if (filters.maxPrice) params.append('maxPrice', filters.maxPrice.toString());
        if (filters.amenities) params.append('amenities', filters.amenities);
        if (filters.minRooms) params.append('minRooms', filters.minRooms.toString());
        
        response = await axios.get(`http://localhost:8080/api/search/hotels?${params}`);
      }
      
      onSearchResults(response.data, filters.searchType);
      await saveSearchHistory();
    } catch (error) {
      console.error('Error performing search:', error);
      onSearchResults([], filters.searchType);
    } finally {
      setLoading(false);
    }
  };

  const clearFilters = () => {
    setFilters({
      searchType: filters.searchType,
      passengers: 1
    });
  };

  return (
    <div className="bg-white rounded-xl shadow-lg p-6 relative">
      {/* Search Type Toggle */}
      <div className="flex mb-6 bg-gray-100 rounded-lg p-1">
        <button
          onClick={() => setFilters(prev => ({ ...prev, searchType: 'flights' }))}
          className={`flex-1 flex items-center justify-center gap-2 py-2 px-4 rounded-md transition-colors ${
            filters.searchType === 'flights'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          <Plane className="w-4 h-4" />
          Flights
        </button>
        <button
          onClick={() => setFilters(prev => ({ ...prev, searchType: 'hotels' }))}
          className={`flex-1 flex items-center justify-center gap-2 py-2 px-4 rounded-md transition-colors ${
            filters.searchType === 'hotels'
              ? 'bg-white text-blue-600 shadow-sm'
              : 'text-gray-600 hover:text-gray-800'
          }`}
        >
          <HotelIcon className="w-4 h-4" />
          Hotels
        </button>
      </div>

      {/* Main Search Fields */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
        {filters.searchType === 'flights' ? (
          <>
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-1">From</label>
              <div className="relative">
                <MapPin className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Origin city"
                  value={filters.from || ''}
                  onChange={(e) => handleInputChange('from', e.target.value)}
                  onFocus={() => setShowHistory(true)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>
            
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-1">To</label>
              <div className="relative">
                <MapPin className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Destination city"
                  value={filters.to || ''}
                  onChange={(e) => handleInputChange('to', e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>
          </>
        ) : (
          <div className="relative">
            <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
            <div className="relative">
              <MapPin className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
              <input
                type="text"
                placeholder="City or area"
                value={filters.location || ''}
                onChange={(e) => handleInputChange('location', e.target.value)}
                onFocus={() => setShowHistory(true)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {filters.searchType === 'flights' ? 'Departure' : 'Check-in'}
          </label>
          <div className="relative">
            <Calendar className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
            <input
              type="date"
              value={filters.checkIn || ''}
              onChange={(e) => setFilters(prev => ({ ...prev, checkIn: e.target.value }))}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            {filters.searchType === 'flights' ? 'Passengers' : 'Guests'}
          </label>
          <div className="relative">
            <Users className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
            <input
              type="number"
              min="1"
              max="10"
              value={filters.passengers}
              onChange={(e) => setFilters(prev => ({ ...prev, passengers: parseInt(e.target.value) || 1 }))}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>
      </div>

      {/* Advanced Filters Toggle */}
      <div className="flex items-center justify-between mb-4">
        <Button
          onClick={() => setShowFilters(!showFilters)}
          variant="outline"
          className="flex items-center gap-2"
        >
          <Filter className="w-4 h-4" />
          Advanced Filters
        </Button>
        
        {Object.keys(filters).some(key => 
          key !== 'searchType' && key !== 'passengers' && filters[key as keyof SearchFilters]
        ) && (
          <Button onClick={clearFilters} variant="ghost" className="text-red-600">
            <X className="w-4 h-4 mr-1" />
            Clear Filters
          </Button>
        )}
      </div>

      {/* Advanced Filters */}
      {showFilters && (
        <div className="border-t pt-4 mb-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filters.searchType === 'flights' && (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Airline</label>
                <input
                  type="text"
                  placeholder="Preferred airline"
                  value={filters.airline || ''}
                  onChange={(e) => handleInputChange('airline', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Min Price</label>
              <input
                type="number"
                placeholder="Minimum price"
                value={filters.minPrice || ''}
                onChange={(e) => setFilters(prev => ({ ...prev, minPrice: parseInt(e.target.value) || undefined }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Max Price</label>
              <input
                type="number"
                placeholder="Maximum price"
                value={filters.maxPrice || ''}
                onChange={(e) => setFilters(prev => ({ ...prev, maxPrice: parseInt(e.target.value) || undefined }))}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {filters.searchType === 'flights' ? (
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Min Seats</label>
                <input
                  type="number"
                  placeholder="Minimum available seats"
                  value={filters.minSeats || ''}
                  onChange={(e) => setFilters(prev => ({ ...prev, minSeats: parseInt(e.target.value) || undefined }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            ) : (
              <>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Amenities</label>
                  <input
                    type="text"
                    placeholder="WiFi, Pool, Gym..."
                    value={filters.amenities || ''}
                    onChange={(e) => setFilters(prev => ({ ...prev, amenities: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Min Rooms</label>
                  <input
                    type="number"
                    placeholder="Minimum available rooms"
                    value={filters.minRooms || ''}
                    onChange={(e) => setFilters(prev => ({ ...prev, minRooms: parseInt(e.target.value) || undefined }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* Search Button */}
      <Button 
        onClick={performSearch} 
        className="w-full flex items-center justify-center gap-2"
        disabled={loading}
      >
        {loading ? (
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
        ) : (
          <Search className="w-4 h-4" />
        )}
        Search {filters.searchType === 'flights' ? 'Flights' : 'Hotels'}
      </Button>

      {/* Suggestions Dropdown */}
      {showSuggestions && suggestions.length > 0 && (
        <div ref={suggestionRef} className="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
          {suggestions.map((suggestion, index) => (
            <button
              key={index}
              onClick={() => selectSuggestion(suggestion)}
              className="w-full px-4 py-2 text-left hover:bg-gray-100 flex items-center gap-2"
            >
              <MapPin className="w-4 h-4 text-gray-400" />
              {suggestion.label}
            </button>
          ))}
        </div>
      )}

      {/* Search History Dropdown */}
      {showHistory && searchHistory.length > 0 && (
        <div ref={historyRef} className="absolute z-10 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto">
          <div className="px-4 py-2 bg-gray-50 border-b">
            <div className="flex items-center gap-2 text-sm text-gray-600">
              <Clock className="w-4 h-4" />
              Recent Searches
            </div>
          </div>
          {searchHistory.slice(0, 5).map((item) => (
            <button
              key={item._id}
              onClick={() => selectFromHistory(item)}
              className="w-full px-4 py-2 text-left hover:bg-gray-100"
            >
              <div className="text-sm font-medium">{item.searchQuery}</div>
              <div className="text-xs text-gray-500">
                {new Date(item.searchDateTime).toLocaleDateString()}
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

