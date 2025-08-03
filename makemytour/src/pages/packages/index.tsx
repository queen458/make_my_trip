import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { 
  MapPin, 
  Calendar, 
  Users, 
  Star, 
  Heart, 
  Share2, 
  Filter,
  Search,
  Package,
  Plane,
  Hotel,
  Camera
} from 'lucide-react';
import axios from 'axios';

interface TravelPackage {
  _id: string;
  packageName: string;
  description: string;
  destination: string;
  duration: number;
  originalPrice: number;
  discountedPrice: number;
  discountPercentage: number;
  flightIds: string[];
  hotelIds: string[];
  tourActivities: string[];
  packageType: string;
  isActive: boolean;
  imageUrl: string;
  highlights: string[];
  minGroupSize: number;
  groupDiscountPercentage: number;
}

export default function PackagesPage() {
  const [packages, setPackages] = useState<TravelPackage[]>([]);
  const [filteredPackages, setFilteredPackages] = useState<TravelPackage[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState('ALL');
  const [priceRange, setPriceRange] = useState({ min: 0, max: 50000 });
  const [durationRange, setDurationRange] = useState({ min: 1, max: 15 });
  const [groupSize, setGroupSize] = useState(1);
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    fetchPackages();
    initializeMockData();
  }, []);

  useEffect(() => {
    filterPackages();
  }, [packages, searchQuery, selectedType, priceRange, durationRange]);

  const fetchPackages = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/packages');
      setPackages(response.data);
    } catch (error) {
      console.error('Error fetching packages:', error);
    } finally {
      setLoading(false);
    }
  };

  const initializeMockData = async () => {
    try {
      await axios.post('http://localhost:8080/api/packages/initialize-mock-data');
    } catch (error) {
      console.error('Error initializing mock data:', error);
    }
  };

  const filterPackages = () => {
    let filtered = packages;

    // Search filter
    if (searchQuery.trim()) {
      filtered = filtered.filter(pkg =>
        pkg.packageName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        pkg.destination.toLowerCase().includes(searchQuery.toLowerCase()) ||
        pkg.description.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Type filter
    if (selectedType !== 'ALL') {
      filtered = filtered.filter(pkg => pkg.packageType === selectedType);
    }

    // Price filter
    filtered = filtered.filter(pkg =>
      pkg.discountedPrice >= priceRange.min && pkg.discountedPrice <= priceRange.max
    );

    // Duration filter
    filtered = filtered.filter(pkg =>
      pkg.duration >= durationRange.min && pkg.duration <= durationRange.max
    );

    setFilteredPackages(filtered);
  };

  const calculateGroupDiscount = async (packageId: string) => {
    if (groupSize <= 1) return null;
    
    try {
      const response = await axios.get(
        `http://localhost:8080/api/packages/${packageId}/group-discount?groupSize=${groupSize}`
      );
      return response.data;
    } catch (error) {
      console.error('Error calculating group discount:', error);
      return null;
    }
  };

  const sharePackage = (pkg: TravelPackage) => {
    const shareData = {
      title: pkg.packageName,
      text: `Check out this amazing ${pkg.destination} package for ${pkg.duration} days!`,
      url: window.location.href
    };

    if (navigator.share) {
      navigator.share(shareData);
    } else {
      // Fallback for browsers that don't support Web Share API
      const shareText = `${shareData.title} - ${shareData.text} ${shareData.url}`;
      navigator.clipboard.writeText(shareText);
      alert('Package details copied to clipboard!');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading travel packages...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Travel Package Bundles</h1>
          <p className="text-gray-600">Discover amazing destinations with our curated travel packages</p>
        </div>

        {/* Search and Filters */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <div className="flex flex-col lg:flex-row gap-4 mb-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" />
                <input
                  type="text"
                  placeholder="Search packages, destinations..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            </div>
            <Button
              onClick={() => setShowFilters(!showFilters)}
              variant="outline"
              className="flex items-center gap-2"
            >
              <Filter className="w-4 h-4" />
              Filters
            </Button>
          </div>

          {/* Filters */}
          {showFilters && (
            <div className="border-t pt-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Package Type</label>
                <select
                  value={selectedType}
                  onChange={(e) => setSelectedType(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="ALL">All Types</option>
                  <option value="PRE_BUILT">Pre-built</option>
                  <option value="CUSTOMIZABLE">Customizable</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Price Range: ₹{priceRange.min} - ₹{priceRange.max}
                </label>
                <div className="flex gap-2">
                  <input
                    type="range"
                    min="0"
                    max="50000"
                    step="1000"
                    value={priceRange.min}
                    onChange={(e) => setPriceRange({...priceRange, min: parseInt(e.target.value)})}
                    className="flex-1"
                  />
                  <input
                    type="range"
                    min="0"
                    max="50000"
                    step="1000"
                    value={priceRange.max}
                    onChange={(e) => setPriceRange({...priceRange, max: parseInt(e.target.value)})}
                    className="flex-1"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Duration: {durationRange.min} - {durationRange.max} days
                </label>
                <div className="flex gap-2">
                  <input
                    type="range"
                    min="1"
                    max="15"
                    value={durationRange.min}
                    onChange={(e) => setDurationRange({...durationRange, min: parseInt(e.target.value)})}
                    className="flex-1"
                  />
                  <input
                    type="range"
                    min="1"
                    max="15"
                    value={durationRange.max}
                    onChange={(e) => setDurationRange({...durationRange, max: parseInt(e.target.value)})}
                    className="flex-1"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Group Size</label>
                <input
                  type="number"
                  min="1"
                  max="20"
                  value={groupSize}
                  onChange={(e) => setGroupSize(parseInt(e.target.value) || 1)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          )}
        </div>

        {/* Package Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredPackages.length === 0 ? (
            <div className="col-span-full bg-white rounded-lg shadow-md p-8 text-center">
              <Package className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">No packages found matching your criteria</p>
            </div>
          ) : (
            filteredPackages.map((pkg) => (
              <PackageCard 
                key={pkg._id} 
                package={pkg} 
                groupSize={groupSize}
                onShare={() => sharePackage(pkg)}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
}

interface PackageCardProps {
  package: TravelPackage;
  groupSize: number;
  onShare: () => void;
}

const PackageCard = ({ package: pkg, groupSize, onShare }: PackageCardProps) => {
  const [isLiked, setIsLiked] = useState(false);
  const [groupDiscountPrice, setGroupDiscountPrice] = useState<number | null>(null);

  useEffect(() => {
    if (groupSize >= pkg.minGroupSize) {
      calculateGroupPrice();
    } else {
      setGroupDiscountPrice(null);
    }
  }, [groupSize, pkg]);

  const calculateGroupPrice = () => {
    const additionalDiscount = pkg.groupDiscountPercentage / 100;
    const discountedPrice = pkg.discountedPrice * (1 - additionalDiscount);
    setGroupDiscountPrice(discountedPrice);
  };

  const finalPrice = groupDiscountPrice || pkg.discountedPrice;
  const totalDiscount = groupDiscountPrice 
    ? pkg.discountPercentage + pkg.groupDiscountPercentage 
    : pkg.discountPercentage;

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
      {/* Image */}
      <div className="relative h-48">
        <img
          src={pkg.imageUrl || 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800'}
          alt={pkg.packageName}
          className="w-full h-full object-cover"
        />
        <div className="absolute top-4 left-4">
          <span className={`px-2 py-1 rounded text-xs font-medium ${
            pkg.packageType === 'PRE_BUILT' 
              ? 'bg-blue-100 text-blue-800' 
              : 'bg-green-100 text-green-800'
          }`}>
            {pkg.packageType.replace('_', ' ')}
          </span>
        </div>
        <div className="absolute top-4 right-4 flex gap-2">
          <button
            onClick={() => setIsLiked(!isLiked)}
            className={`p-2 rounded-full ${
              isLiked ? 'bg-red-500 text-white' : 'bg-white text-gray-600'
            } hover:bg-red-500 hover:text-white transition-colors`}
          >
            <Heart className="w-4 h-4" />
          </button>
          <button
            onClick={onShare}
            className="p-2 rounded-full bg-white text-gray-600 hover:bg-blue-500 hover:text-white transition-colors"
          >
            <Share2 className="w-4 h-4" />
          </button>
        </div>
        {pkg.discountPercentage > 0 && (
          <div className="absolute bottom-4 left-4">
            <span className="bg-red-500 text-white px-2 py-1 rounded text-sm font-medium">
              {Math.round(totalDiscount)}% OFF
            </span>
          </div>
        )}
      </div>

      {/* Content */}
      <div className="p-6">
        <div className="flex items-start justify-between mb-2">
          <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
            {pkg.packageName}
          </h3>
          <div className="flex items-center text-yellow-500">
            <Star className="w-4 h-4 fill-current" />
            <span className="text-sm text-gray-600 ml-1">4.5</span>
          </div>
        </div>

        <div className="flex items-center text-gray-600 mb-2">
          <MapPin className="w-4 h-4 mr-1" />
          <span className="text-sm">{pkg.destination}</span>
        </div>

        <div className="flex items-center text-gray-600 mb-4">
          <Calendar className="w-4 h-4 mr-1" />
          <span className="text-sm">{pkg.duration} days</span>
        </div>

        <p className="text-gray-600 text-sm mb-4 line-clamp-2">
          {pkg.description}
        </p>

        {/* Highlights */}
        <div className="mb-4">
          <div className="flex flex-wrap gap-2">
            {pkg.highlights?.slice(0, 3).map((highlight, index) => (
              <span key={index} className="text-xs bg-gray-100 text-gray-700 px-2 py-1 rounded">
                {highlight}
              </span>
            ))}
          </div>
        </div>

        {/* Inclusions */}
        <div className="flex items-center gap-4 mb-4 text-sm text-gray-600">
          <div className="flex items-center">
            <Plane className="w-4 h-4 mr-1" />
            <span>Flights</span>
          </div>
          <div className="flex items-center">
            <Hotel className="w-4 h-4 mr-1" />
            <span>Hotels</span>
          </div>
          <div className="flex items-center">
            <Camera className="w-4 h-4 mr-1" />
            <span>Tours</span>
          </div>
        </div>

        {/* Pricing */}
        <div className="border-t pt-4">
          <div className="flex items-center justify-between mb-2">
            <div>
              {pkg.originalPrice > finalPrice && (
                <span className="text-sm text-gray-500 line-through">
                  ₹{pkg.originalPrice.toLocaleString()}
                </span>
              )}
              <div className="text-xl font-bold text-gray-900">
                ₹{finalPrice.toLocaleString()}
              </div>
              {groupSize >= pkg.minGroupSize && groupDiscountPrice && (
                <div className="text-xs text-green-600">
                  Additional {pkg.groupDiscountPercentage}% group discount applied!
                </div>
              )}
            </div>
            <div className="text-right">
              <div className="text-sm text-gray-600">per person</div>
              {groupSize >= pkg.minGroupSize && (
                <div className="text-xs text-blue-600">
                  Group of {groupSize}
                </div>
              )}
            </div>
          </div>

          <Button className="w-full">
            Book Now
          </Button>

          {groupSize < pkg.minGroupSize && (
            <p className="text-xs text-gray-500 mt-2 text-center">
              Book for {pkg.minGroupSize}+ people to get {pkg.groupDiscountPercentage}% additional discount
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

