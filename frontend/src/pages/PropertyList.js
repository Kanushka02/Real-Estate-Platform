import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { propertiesAPI, handleAPIError } from '../services/api';
import { Card, Button, Badge, LoadingSpinner, Input, Select } from '../components/common';

const PropertyList = () => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchParams, setSearchParams] = useSearchParams();

  // Filter states
  const [filters, setFilters] = useState({
    keyword: searchParams.get('keyword') || '',
    district: searchParams.get('district') || '',
    propertyType: searchParams.get('propertyType') || '',
    bedrooms: searchParams.get('bedrooms') || '',
    minPrice: searchParams.get('minPrice') || '',
    maxPrice: searchParams.get('maxPrice') || ''
  });

  // Sri Lankan districts for filter dropdown
  const sriLankanDistricts = [
    'Colombo', 'Gampaha', 'Kandy', 'Galle', 'Kalutara', 'Kurunegala', 
    'Anuradhapura', 'Ratnapura', 'Kegalle', 'Nuwara Eliya', 'Matara',
    'Hambantota', 'Badulla', 'Moneragala', 'Trincomalee', 'Batticaloa',
    'Ampara', 'Polonnaruwa', 'Jaffna', 'Kilinochchi', 'Mannar',
    'Mullaitivu', 'Vavuniya', 'Puttalam', 'Matale'
  ];

  // Load properties on component mount and when filters change
  useEffect(() => {
    loadProperties();
  }, []);

  const loadProperties = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Determine which API call to make based on active filters
      let response;
      
      if (filters.keyword) {
        response = await propertiesAPI.search(filters.keyword);
      } else if (filters.district) {
        response = await propertiesAPI.getByDistrict(filters.district);
      } else if (filters.propertyType === 'SALE') {
        response = await propertiesAPI.getForSale();
      } else if (filters.propertyType === 'RENT') {
        response = await propertiesAPI.getForRent();
      } else if (filters.bedrooms) {
        response = await propertiesAPI.getByBedrooms(parseInt(filters.bedrooms));
      } else {
        response = await propertiesAPI.getAll();
      }
      
      setProperties(response.data);
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key, value) => {
    const newFilters = { ...filters, [key]: value };
    setFilters(newFilters);
    
    // Update URL params
    const newParams = new URLSearchParams();
    Object.entries(newFilters).forEach(([k, v]) => {
      if (v) newParams.set(k, v);
    });
    setSearchParams(newParams);
  };

  const applyFilters = () => {
    loadProperties();
  };

  const clearFilters = () => {
    const clearedFilters = {
      keyword: '', district: '', propertyType: '', 
      bedrooms: '', minPrice: '', maxPrice: ''
    };
    setFilters(clearedFilters);
    setSearchParams(new URLSearchParams());
    loadProperties();
  };

  const formatPrice = (price) => {
    if (price >= 10000000) {
      return `Rs. ${(price / 10000000).toFixed(1)}Cr`;
    } else if (price >= 100000) {
      return `Rs. ${(price / 100000).toFixed(1)}L`;
    } else {
      return `Rs. ${price.toLocaleString()}`;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="xl" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="container mx-auto px-4 py-12">
          <h1 className="text-4xl font-bold mb-4">Find Your Dream Property in Sri Lanka</h1>
          <p className="text-xl opacity-90">Discover the perfect home, apartment, or land in beautiful locations across the island</p>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Filters Sidebar */}
          <div className="lg:w-1/4">
            <Card className="p-6 sticky top-4">
              <h2 className="text-xl font-semibold mb-4">Filter Properties</h2>
              
              {/* Keyword Search */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Search Keywords
                </label>
                <Input
                  type="text"
                  placeholder="House, apartment, land..."
                  value={filters.keyword}
                  onChange={(e) => handleFilterChange('keyword', e.target.value)}
                />
              </div>

              {/* District Filter */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  District
                </label>
                <Select
                  value={filters.district}
                  onChange={(e) => handleFilterChange('district', e.target.value)}
                >
                  <option value="">All Districts</option>
                  {sriLankanDistricts.map(district => (
                    <option key={district} value={district}>{district}</option>
                  ))}
                </Select>
              </div>

              {/* Property Type Filter */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Property Type
                </label>
                <Select
                  value={filters.propertyType}
                  onChange={(e) => handleFilterChange('propertyType', e.target.value)}
                >
                  <option value="">All Types</option>
                  <option value="SALE">For Sale</option>
                  <option value="RENT">For Rent</option>
                </Select>
              </div>

              {/* Bedrooms Filter */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Bedrooms
                </label>
                <Select
                  value={filters.bedrooms}
                  onChange={(e) => handleFilterChange('bedrooms', e.target.value)}
                >
                  <option value="">Any</option>
                  <option value="1">1 Bedroom</option>
                  <option value="2">2 Bedrooms</option>
                  <option value="3">3 Bedrooms</option>
                  <option value="4">4+ Bedrooms</option>
                </Select>
              </div>

              {/* Price Range */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Price Range (Rs.)
                </label>
                <div className="flex gap-2">
                  <Input
                    type="number"
                    placeholder="Min"
                    value={filters.minPrice}
                    onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                  />
                  <Input
                    type="number"
                    placeholder="Max"
                    value={filters.maxPrice}
                    onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                  />
                </div>
              </div>

              {/* Filter Actions */}
              <div className="flex gap-2">
                <Button onClick={applyFilters} className="flex-1">
                  Apply Filters
                </Button>
                <Button variant="outline" onClick={clearFilters}>
                  Clear
                </Button>
              </div>
            </Card>
          </div>

          {/* Properties List */}
          <div className="lg:w-3/4">
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-6">
                {error}
              </div>
            )}

            {/* Results Header */}
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-semibold">
                {properties.length} Properties Found
              </h2>
              <div className="flex gap-2">
                <Button variant="outline" size="sm">
                  Sort by Price
                </Button>
                <Button variant="outline" size="sm">
                  Sort by Date
                </Button>
              </div>
            </div>

            {/* Property Cards Grid */}
            {properties.length === 0 ? (
              <div className="text-center py-12">
                <div className="text-gray-400 text-6xl mb-4">🏠</div>
                <h3 className="text-xl font-medium text-gray-600 mb-2">No Properties Found</h3>
                <p className="text-gray-500">Try adjusting your search filters or browse all properties</p>
                <Button className="mt-4" onClick={clearFilters}>
                  View All Properties
                </Button>
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {properties.map((property) => (
                  <PropertyCard key={property.propertyId} property={property} />
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Property Card Component
const PropertyCard = ({ property }) => {
  const formatPrice = (price) => {
    if (price >= 10000000) {
      return `Rs. ${(price / 10000000).toFixed(1)}Cr`;
    } else if (price >= 100000) {
      return `Rs. ${(price / 100000).toFixed(1)}L`;
    } else {
      return `Rs. ${price.toLocaleString()}`;
    }
  };

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow duration-200">
      {/* Property Image Placeholder */}
      <div className="h-48 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
        <div className="text-white text-4xl">🏠</div>
      </div>
      
      <div className="p-6">
        {/* Property Type Badge */}
        <div className="flex justify-between items-start mb-3">
          <Badge 
            variant={property.propertyType === 'SALE' ? 'success' : 'info'}
            className="mb-2"
          >
            For {property.propertyType === 'SALE' ? 'Sale' : 'Rent'}
          </Badge>
          {property.featured && (
            <Badge variant="warning">Featured</Badge>
          )}
        </div>

        {/* Title */}
        <h3 className="text-xl font-semibold text-gray-900 mb-2 line-clamp-2">
          {property.title}
        </h3>

        {/* Location */}
        <p className="text-gray-600 mb-2 flex items-center">
          <span className="mr-1">📍</span>
          {property.location.city}, {property.location.district}
        </p>

        {/* Price */}
        <p className="text-2xl font-bold text-blue-600 mb-3">
          {formatPrice(property.price)}
          {property.propertyType === 'RENT' && <span className="text-sm text-gray-500">/month</span>}
        </p>

        {/* Property Details */}
        <div className="flex items-center text-sm text-gray-600 mb-4">
          {property.bedrooms && (
            <span className="flex items-center mr-4">
              🛏️ {property.bedrooms} beds
            </span>
          )}
          {property.bathrooms && (
            <span className="flex items-center mr-4">
              🚿 {property.bathrooms} baths
            </span>
          )}
          {property.areaSqft && (
            <span className="flex items-center">
              📐 {property.areaSqft.toLocaleString()} sqft
            </span>
          )}
        </div>

        {/* Description */}
        <p className="text-gray-600 text-sm mb-4 line-clamp-2">
          {property.description}
        </p>

        {/* Seller Info */}
        <div className="flex items-center justify-between pt-4 border-t">
          <div className="flex items-center">
            <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-2">
              <span className="text-blue-600 text-sm">👤</span>
            </div>
            <div>
              <p className="text-sm font-medium">
                {property.seller.firstName} {property.seller.lastName}
              </p>
              <p className="text-xs text-gray-500">{property.seller.role}</p>
            </div>
          </div>
          
          <Link 
            to={`/properties/${property.propertyId}`}
            className="text-blue-600 hover:text-blue-800 font-medium text-sm"
          >
            View Details →
          </Link>
        </div>
      </div>
    </Card>
  );
};

export default PropertyList;