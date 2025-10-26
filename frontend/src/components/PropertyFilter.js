import React, { useState } from 'react';
import { DISTRICTS, PROPERTY_TYPES, LISTING_TYPES } from '../utils/constants';

const PropertyFilter = ({ onFilter }) => {
  const [filters, setFilters] = useState({
    type: '',
    listingType: '',
    district: '',
    city: '',
    minPrice: '',
    maxPrice: '',
    minBedrooms: '',
  });

  const handleChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Remove empty filters
    const activeFilters = {};
    Object.keys(filters).forEach(key => {
      if (filters[key] !== '') {
        activeFilters[key] = filters[key];
      }
    });
    
    onFilter(activeFilters);
  };

  const handleReset = () => {
    setFilters({
      type: '',
      listingType: '',
      district: '',
      city: '',
      minPrice: '',
      maxPrice: '',
      minBedrooms: '',
    });
    onFilter({});
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md mb-6">
      <h2 className="text-xl font-bold mb-4">Filter Properties</h2>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Property Type
            </label>
            <select
              name="type"
              value={filters.type}
              onChange={handleChange}
              className="input-field"
            >
              <option value="">All Types</option>
              {PROPERTY_TYPES.map(type => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Listing Type
            </label>
            <select
              name="listingType"
              value={filters.listingType}
              onChange={handleChange}
              className="input-field"
            >
              <option value="">All Listings</option>
              {LISTING_TYPES.map(type => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              District
            </label>
            <select
              name="district"
              value={filters.district}
              onChange={handleChange}
              className="input-field"
            >
              <option value="">All Districts</option>
              {DISTRICTS.map(district => (
                <option key={district} value={district}>{district}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              City
            </label>
            <input
              type="text"
              name="city"
              value={filters.city}
              onChange={handleChange}
              placeholder="Enter city"
              className="input-field"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Min Price (Rs.)
            </label>
            <input
              type="number"
              name="minPrice"
              value={filters.minPrice}
              onChange={handleChange}
              placeholder="Min price"
              className="input-field"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Max Price (Rs.)
            </label>
            <input
              type="number"
              name="maxPrice"
              value={filters.maxPrice}
              onChange={handleChange}
              placeholder="Max price"
              className="input-field"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Min Bedrooms
            </label>
            <input
              type="number"
              name="minBedrooms"
              value={filters.minBedrooms}
              onChange={handleChange}
              placeholder="Min bedrooms"
              className="input-field"
            />
          </div>
        </div>

        <div className="flex space-x-4">
          <button type="submit" className="btn-primary">
            Apply Filters
          </button>
          <button type="button" onClick={handleReset} className="btn-secondary">
            Reset
          </button>
        </div>
      </form>
    </div>
  );
};

export default PropertyFilter;

