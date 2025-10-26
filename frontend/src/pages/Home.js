import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { propertyAPI } from '../services/api';
import PropertyCard from '../components/PropertyCard';

const Home = () => {
  const [latestProperties, setLatestProperties] = useState([]);
  const [featuredProperties, setFeaturedProperties] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProperties();
  }, []);

  const fetchProperties = async () => {
    try {
      const [latestRes, featuredRes] = await Promise.all([
        propertyAPI.getLatest(),
        propertyAPI.getFeatured(),
      ]);
      
      setLatestProperties(latestRes.data);
      setFeaturedProperties(featuredRes.data);
    } catch (error) {
      console.error('Error fetching properties:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/properties?search=${searchKeyword}`);
    }
  };

  return (
    <div className="min-h-screen">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 text-white py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl md:text-5xl font-bold mb-4">
              Find Your Dream Property in Sri Lanka
            </h1>
            <p className="text-xl mb-8">
              Browse thousands of properties for sale and rent
            </p>
            
            <form onSubmit={handleSearch} className="max-w-2xl mx-auto">
              <div className="flex">
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  placeholder="Search by location, property type..."
                  className="flex-1 px-6 py-4 rounded-l-lg text-gray-900 focus:outline-none"
                />
                <button type="submit" className="bg-white text-primary-600 px-8 py-4 rounded-r-lg font-semibold hover:bg-gray-100">
                  Search
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-10">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-lg shadow-lg text-center">
            <div className="text-4xl mb-2">🏠</div>
            <h3 className="text-2xl font-bold text-gray-900">Browse Properties</h3>
            <p className="text-gray-600">Find your perfect home</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-lg text-center">
            <div className="text-4xl mb-2">📝</div>
            <h3 className="text-2xl font-bold text-gray-900">List Property</h3>
            <p className="text-gray-600">Post your property for free</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-lg text-center">
            <div className="text-4xl mb-2">⭐</div>
            <h3 className="text-2xl font-bold text-gray-900">Save Favorites</h3>
            <p className="text-gray-600">Keep track of properties you like</p>
          </div>
        </div>
      </div>

      {/* Featured Properties */}
      {featuredProperties.length > 0 && (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-16">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-3xl font-bold text-gray-900">Featured Properties</h2>
            <Link to="/properties" className="text-primary-600 hover:text-primary-700 font-semibold">
              View All →
            </Link>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {featuredProperties.slice(0, 6).map(property => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>
        </div>
      )}

      {/* Latest Properties */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-16">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-3xl font-bold text-gray-900">Latest Properties</h2>
          <Link to="/properties" className="text-primary-600 hover:text-primary-700 font-semibold">
            View All →
          </Link>
        </div>
        
        {loading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {latestProperties.map(property => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>
        )}
      </div>

      {/* CTA Section */}
      <div className="bg-gray-100 mt-16 py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            Ready to Find Your Dream Property?
          </h2>
          <p className="text-xl text-gray-600 mb-8">
            Join thousands of users buying, selling, and renting properties in Sri Lanka
          </p>
          <div className="flex justify-center space-x-4">
            <Link to="/properties" className="btn-primary text-lg px-8 py-3">
              Browse Properties
            </Link>
            <Link to="/signup" className="btn-secondary text-lg px-8 py-3">
              Sign Up Free
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;

