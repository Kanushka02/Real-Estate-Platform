import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { propertiesAPI, handleAPIError } from '../services/api';
import { Button, Card, Badge, LoadingSpinner } from '../components/common';

const Home = () => {
  const navigate = useNavigate();
  const [featuredProperties, setFeaturedProperties] = useState([]);
  const [stats, setStats] = useState({ forSale: 0, forRent: 0, total: 0 });
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch featured properties (first 6 properties)
        const properties = await propertiesAPI.getAll();
        setFeaturedProperties(properties.slice(0, 6));

        // Calculate stats
        const forSale = await propertiesAPI.getForSale();
        const forRent = await propertiesAPI.getForRent();
        
        setStats({
          forSale: forSale.length,
          forRent: forRent.length,
          total: properties.length
        });
      } catch (error) {
        console.error('Error fetching data:', handleAPIError(error));
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const formatPrice = (price) => {
    if (price >= 1000000) {
      return `Rs. ${(price / 1000000).toFixed(1)}M`;
    } else if (price >= 1000) {
      return `Rs. ${(price / 1000).toFixed(0)}K`;
    }
    return `Rs. ${price.toLocaleString()}`;
  };

  const PropertyCard = ({ property }) => (
    <Card hover className="h-full">
      <div className="relative">
        {/* Property Image Placeholder */}
        <div className="w-full h-48 bg-gray-200 rounded-lg mb-4 flex items-center justify-center">
          <svg className="w-12 h-12 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
          </svg>
        </div>
        
        {/* Property Type Badge */}
        <div className="absolute top-2 left-2">
          <Badge variant={property.propertyType === 'SALE' ? 'success' : 'primary'}>
            For {property.propertyType}
          </Badge>
        </div>
      </div>

      <div className="space-y-2">
        <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
          {property.title}
        </h3>
        
        <p className="text-2xl font-bold text-primary-600">
          {formatPrice(property.price)}
          {property.propertyType === 'RENT' && <span className="text-sm font-normal">/month</span>}
        </p>

        <div className="flex items-center text-sm text-gray-600 space-x-4">
          {property.bedrooms && (
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838L7.667 9.088l1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3zM3.31 9.397L5 10.12v4.102a8.969 8.969 0 00-1.05-.174 1 1 0 01-.89-.89 11.115 11.115 0 01.25-3.762zM9.3 16.573A9.026 9.026 0 007 14.935v-3.957l1.818.78a3 3 0 002.364 0l5.508-2.361a11.026 11.026 0 01.25 3.762 1 1 0 01-.89.89 8.968 8.968 0 00-5.35 2.524 1 1 0 01-1.4 0zM6 18a1 1 0 001-1v-2.065a8.935 8.935 0 00-2-.712V17a1 1 0 001 1z"/>
              </svg>
              {property.bedrooms} beds
            </span>
          )}
          {property.bathrooms && (
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M5 2a1 1 0 011 1v1h1a1 1 0 010 2H6v1a1 1 0 01-2 0V6H3a1 1 0 010-2h1V3a1 1 0 011-1zm0 10a1 1 0 011 1v1h1a1 1 0 110 2H6v1a1 1 0 11-2 0v-1H3a1 1 0 110-2h1v-1a1 1 0 011-1zM12 2a1 1 0 01.967.744L14.146 7.2 17.5 9.134a1 1 0 010 1.732L14.146 12.8l-1.179 4.456a1 1 0 01-1.856.048L9.932 13.9 6.5 12.134a1 1 0 010-1.732L9.932 8.1l1.179-4.456A1 1 0 0112 2z" clipRule="evenodd" />
              </svg>
              {property.bathrooms} baths
            </span>
          )}
          {property.areaSqft && (
            <span className="flex items-center">
              <svg className="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M3 4a1 1 0 011-1h4a1 1 0 010 2H6.414l2.293 2.293a1 1 0 01-1.414 1.414L5 6.414V8a1 1 0 01-2 0V4zm9 1a1 1 0 010-2h4a1 1 0 011 1v4a1 1 0 01-2 0V6.414l-2.293 2.293a1 1 0 11-1.414-1.414L13.586 5H12zm-9 7a1 1 0 012 0v1.586l2.293-2.293a1 1 0 111.414 1.414L6.414 15H8a1 1 0 010 2H4a1 1 0 01-1-1v-4zm13-1a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 010-2h1.586l-2.293-2.293a1 1 0 111.414-1.414L15.586 13H14a1 1 0 01-1-1z" clipRule="evenodd" />
              </svg>
              {property.areaSqft} sq ft
            </span>
          )}
        </div>

        <p className="text-sm text-gray-600">
          📍 {property.location.city}, {property.location.district}
        </p>

        <div className="pt-3">
          <Link
            to={`/properties/${property.propertyId}`}
            className="text-primary-600 hover:text-primary-700 font-medium text-sm"
          >
            View Details →
          </Link>
        </div>
      </div>
    </Card>
  );

  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-r from-primary-600 to-primary-800 overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold text-white mb-6">
              Find Your Dream Property in{' '}
              <span className="text-primary-200">Sri Lanka</span>
            </h1>
            <p className="text-xl text-primary-100 mb-8 max-w-2xl mx-auto">
              Discover amazing properties for sale and rent across Sri Lanka. 
              From luxury villas in Colombo to beachside homes in Galle.
            </p>

            {/* Search Bar */}
            <div className="max-w-2xl mx-auto">
              <form onSubmit={handleSearch} className="flex">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search by location, property type, or keywords..."
                  className="flex-1 px-6 py-4 text-lg rounded-l-lg border-0 focus:ring-2 focus:ring-primary-300 focus:outline-none"
                />
                <Button
                  type="submit"
                  size="lg"
                  className="rounded-l-none px-8"
                >
                  Search
                </Button>
              </form>
            </div>

            {/* Quick Stats */}
            <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8 max-w-3xl mx-auto">
              <div className="text-center">
                <div className="text-3xl font-bold text-white">{stats.total}</div>
                <div className="text-primary-200">Total Properties</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-white">{stats.forSale}</div>
                <div className="text-primary-200">For Sale</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold text-white">{stats.forRent}</div>
                <div className="text-primary-200">For Rent</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Featured Properties */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Featured Properties
            </h2>
            <p className="text-lg text-gray-600">
              Discover our handpicked selection of premium properties
            </p>
          </div>

          {loading ? (
            <div className="flex justify-center py-12">
              <LoadingSpinner size="lg" />
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {featuredProperties.map((property) => (
                <PropertyCard key={property.propertyId} property={property} />
              ))}
            </div>
          )}

          <div className="text-center mt-12">
            <Link to="/properties">
              <Button size="lg" variant="secondary">
                View All Properties
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Quick Actions */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-3xl font-bold text-gray-900 mb-6">
                Ready to Find Your Perfect Home?
              </h2>
              <p className="text-lg text-gray-600 mb-8">
                Browse through thousands of verified properties across Sri Lanka. 
                Our platform makes it easy to find exactly what you're looking for.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Link to="/properties?type=SALE">
                  <Button size="lg" className="w-full sm:w-auto">
                    Buy Property
                  </Button>
                </Link>
                <Link to="/properties?type=RENT">
                  <Button size="lg" variant="secondary" className="w-full sm:w-auto">
                    Rent Property
                  </Button>
                </Link>
              </div>
            </div>

            <div>
              <h2 className="text-3xl font-bold text-gray-900 mb-6">
                Want to List Your Property?
              </h2>
              <p className="text-lg text-gray-600 mb-8">
                Join thousands of property owners who trust PropertyLK to 
                showcase their properties to serious buyers and tenants.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Link to="/register">
                  <Button size="lg" className="w-full sm:w-auto">
                    Join as Seller
                  </Button>
                </Link>
                <Link to="/properties">
                  <Button size="lg" variant="secondary" className="w-full sm:w-auto">
                    Learn More
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Popular Locations */}
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Popular Locations
            </h2>
            <p className="text-lg text-gray-600">
              Explore properties in Sri Lanka's most sought-after areas
            </p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {[
              { name: 'Colombo', count: '1,200+' },
              { name: 'Kandy', count: '800+' },
              { name: 'Galle', count: '500+' },
              { name: 'Negombo', count: '400+' },
              { name: 'Nugegoda', count: '600+' },
              { name: 'Maharagama', count: '300+' },
              { name: 'Dehiwala', count: '250+' },
              { name: 'Kotte', count: '350+' }
            ].map((location) => (
              <Link
                key={location.name}
                to={`/properties?location=${location.name}`}
                className="bg-white p-6 rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200 text-center"
              >
                <h3 className="font-semibold text-gray-900 mb-2">{location.name}</h3>
                <p className="text-sm text-gray-600">{location.count} properties</p>
              </Link>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;