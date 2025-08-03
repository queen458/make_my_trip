import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Plane, Clock, AlertCircle, CheckCircle, XCircle, Search } from 'lucide-react';
import axios from 'axios';

interface FlightStatus {
  _id: string;
  flightNumber: string;
  airline: string;
  origin: string;
  destination: string;
  scheduledDeparture: string;
  actualDeparture?: string;
  scheduledArrival: string;
  estimatedArrival?: string;
  status: string;
  delayReason?: string;
  delayMinutes: number;
  gate?: string;
  terminal?: string;
}

export default function FlightStatusPage() {
  const [flightStatuses, setFlightStatuses] = useState<FlightStatus[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState<Date>(new Date());

  useEffect(() => {
    fetchFlightStatuses();
    initializeMockData();
    
    // Set up auto-refresh every 30 seconds
    const interval = setInterval(() => {
      simulateStatusUpdates();
    }, 30000);

    return () => clearInterval(interval);
  }, []);

  const fetchFlightStatuses = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/flight-status');
      setFlightStatuses(response.data);
      setLastUpdated(new Date());
    } catch (error) {
      console.error('Error fetching flight statuses:', error);
    } finally {
      setLoading(false);
    }
  };

  const initializeMockData = async () => {
    try {
      await axios.post('http://localhost:8080/api/flight-status/initialize-mock-data');
    } catch (error) {
      console.error('Error initializing mock data:', error);
    }
  };

  const simulateStatusUpdates = async () => {
    try {
      await axios.post('http://localhost:8080/api/flight-status/simulate-all');
      fetchFlightStatuses();
    } catch (error) {
      console.error('Error simulating status updates:', error);
    }
  };

  const searchFlights = async () => {
    if (!searchQuery.trim()) {
      fetchFlightStatuses();
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8080/api/flight-status/search?query=${searchQuery}`);
      setFlightStatuses(response.data);
    } catch (error) {
      console.error('Error searching flights:', error);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ON_TIME':
        return <CheckCircle className="w-5 h-5 text-green-500" />;
      case 'DELAYED':
        return <Clock className="w-5 h-5 text-yellow-500" />;
      case 'CANCELLED':
        return <XCircle className="w-5 h-5 text-red-500" />;
      case 'DEPARTED':
        return <Plane className="w-5 h-5 text-blue-500" />;
      case 'ARRIVED':
        return <CheckCircle className="w-5 h-5 text-green-600" />;
      default:
        return <AlertCircle className="w-5 h-5 text-gray-500" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ON_TIME':
        return 'text-green-600 bg-green-100';
      case 'DELAYED':
        return 'text-yellow-600 bg-yellow-100';
      case 'CANCELLED':
        return 'text-red-600 bg-red-100';
      case 'DEPARTED':
        return 'text-blue-600 bg-blue-100';
      case 'ARRIVED':
        return 'text-green-700 bg-green-200';
      default:
        return 'text-gray-600 bg-gray-100';
    }
  };

  const formatTime = (dateString: string) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading flight statuses...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Live Flight Status</h1>
          <p className="text-gray-600">Real-time flight information and updates</p>
          <p className="text-sm text-gray-500 mt-2">
            Last updated: {lastUpdated.toLocaleTimeString()}
          </p>
        </div>

        {/* Search Bar */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex gap-4">
            <div className="flex-1">
              <input
                type="text"
                placeholder="Search by flight number, airline, or city..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                onKeyPress={(e) => e.key === 'Enter' && searchFlights()}
              />
            </div>
            <Button onClick={searchFlights} className="flex items-center gap-2">
              <Search className="w-4 h-4" />
              Search
            </Button>
            <Button 
              onClick={fetchFlightStatuses} 
              variant="outline"
              className="flex items-center gap-2"
            >
              Refresh
            </Button>
          </div>
        </div>

        {/* Flight Status Cards */}
        <div className="grid gap-6">
          {flightStatuses.length === 0 ? (
            <div className="bg-white rounded-lg shadow-md p-8 text-center">
              <Plane className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">No flight statuses found</p>
            </div>
          ) : (
            flightStatuses.map((flight) => (
              <div key={flight._id} className="bg-white rounded-lg shadow-md p-6">
                <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
                  {/* Flight Info */}
                  <div className="flex-1 mb-4 lg:mb-0">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="text-xl font-bold text-gray-900">
                        {flight.flightNumber}
                      </h3>
                      <span className="text-gray-600">•</span>
                      <span className="text-gray-600">{flight.airline}</span>
                    </div>
                    
                    <div className="flex items-center gap-4 text-lg">
                      <div className="text-center">
                        <div className="font-semibold">{flight.origin}</div>
                        <div className="text-sm text-gray-500">
                          {formatTime(flight.scheduledDeparture)}
                        </div>
                        <div className="text-xs text-gray-400">
                          {formatDate(flight.scheduledDeparture)}
                        </div>
                      </div>
                      
                      <div className="flex-1 flex items-center justify-center">
                        <div className="w-full max-w-24 h-px bg-gray-300 relative">
                          <Plane className="w-4 h-4 text-blue-600 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white" />
                        </div>
                      </div>
                      
                      <div className="text-center">
                        <div className="font-semibold">{flight.destination}</div>
                        <div className="text-sm text-gray-500">
                          {formatTime(flight.estimatedArrival || flight.scheduledArrival)}
                        </div>
                        <div className="text-xs text-gray-400">
                          {formatDate(flight.scheduledArrival)}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Status Info */}
                  <div className="lg:ml-8">
                    <div className="flex items-center gap-2 mb-2">
                      {getStatusIcon(flight.status)}
                      <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(flight.status)}`}>
                        {flight.status.replace('_', ' ')}
                        {flight.delayMinutes > 0 && ` (${flight.delayMinutes}m)`}
                      </span>
                    </div>
                    
                    {flight.delayReason && (
                      <p className="text-sm text-gray-600 mb-2">
                        Reason: {flight.delayReason}
                      </p>
                    )}
                    
                    <div className="text-sm text-gray-500">
                      {flight.gate && <span>Gate: {flight.gate}</span>}
                      {flight.terminal && flight.gate && <span> • </span>}
                      {flight.terminal && <span>Terminal: {flight.terminal}</span>}
                    </div>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* Auto-refresh notification */}
        <div className="mt-8 text-center">
          <p className="text-sm text-gray-500">
            Flight statuses are automatically updated every 30 seconds
          </p>
        </div>
      </div>
    </div>
  );
}

