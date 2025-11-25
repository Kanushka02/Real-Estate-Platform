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
    <div className="bg-white">
      {/* Hero Section */}
      <div className="bg-gradient-to-br from-blue-600 via-blue-700 to-indigo-800 text-white py-24 md:py-40 relative overflow-hidden">
        {/* Background decoration */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 -mr-48 -mt-48"></div>
        <div className="absolute bottom-0 left-0 w-96 h-96 bg-indigo-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 -ml-48 -mb-48"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center">
            <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight tracking-tight">
              Find Your Dream Property
            </h1>
            <p className="text-lg md:text-2xl mb-12 text-blue-100 max-w-3xl mx-auto leading-relaxed">
              Discover thousands of verified properties for sale and rent across Sri Lanka
            </p>

            <form onSubmit={handleSearch} className="max-w-3xl mx-auto">
              <div className="flex gap-2 md:gap-0 shadow-2xl rounded-lg md:rounded-l-lg overflow-hidden">
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  placeholder="Search by location, property type..."
                  className="flex-1 px-6 py-4 text-gray-900 focus:outline-none placeholder-gray-500 text-lg"
                />
                <button type="submit" className="bg-white text-blue-600 px-8 py-4 rounded-lg md:rounded-r-lg font-bold hover:bg-blue-50 transition duration-300 whitespace-nowrap flex items-center gap-2 md:gap-3">
                  <span>🔍</span>
                  <span className="hidden sm:inline">Search</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-12 mb-20 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="card p-8 text-center hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 border-l-4 border-blue-600">
            <div className="text-6xl mb-4">🏠</div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">Browse Properties</h3>
            <p className="text-gray-600 text-sm leading-relaxed">Find your perfect home from thousands of verified listings</p>
          </div>
          <div className="card p-8 text-center hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 border-l-4 border-green-600">
            <div className="text-6xl mb-4">📝</div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">List Property</h3>
            <p className="text-gray-600 text-sm leading-relaxed">Post your property and reach thousands of buyers instantly</p>
          </div>
          <div className="card p-8 text-center hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 border-l-4 border-yellow-600">
            <div className="text-6xl mb-4">⭐</div>
            <h3 className="text-2xl font-bold text-gray-900 mb-3">Save Favorites</h3>
            <p className="text-gray-600 text-sm leading-relaxed">Keep track of properties you like and compare easily</p>
          </div>
        </div>
      </div>

      {/* Featured Properties */}
      {featuredProperties.length > 0 && (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-24">
          <div className="flex justify-between items-start md:items-center mb-10 flex-col md:flex-row gap-4">
            <div>
              <div className="inline-block px-4 py-1.5 bg-blue-100 text-blue-700 rounded-full text-sm font-semibold mb-3">⭐ Featured</div>
              <h2 className="text-4xl md:text-5xl font-bold text-gray-900">Premium Properties</h2>
              <p className="text-gray-600 mt-3 text-lg">Handpicked properties curated just for you</p>
            </div>
            <Link to="/properties" className="btn-primary hidden md:inline-flex">
              View All →
            </Link>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
            {featuredProperties.slice(0, 6).map(property => (
              <PropertyCard key={property.id} property={property} />
            ))}
          </div>

          <div className="text-center md:hidden">
            <Link to="/properties" className="btn-primary">
              View All Properties
            </Link>
          </div>
        </div>
      )}

      {/* Latest Properties */}
      <div className="bg-gray-50 py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-8">
            <div>
              <div className="inline-block px-4 py-1.5 bg-green-100 text-green-700 rounded-full text-sm font-semibold mb-3">🆕 New</div>
              <h2 className="text-4xl md:text-5xl font-bold text-gray-900">Latest Listings</h2>
              <p className="text-gray-600 mt-3 text-lg">Recently added properties in your area</p>
            </div>
            <Link to="/properties" className="btn-ghost font-semibold hidden md:inline-flex">
              View All →
            </Link>
          </div>

          {loading ? (
            <div className="text-center py-16">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
              <p className="text-gray-600 mt-4">Loading properties...</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {latestProperties.map(property => (
                  <PropertyCard key={property.id} property={property} />
                ))}
              </div>

              <div className="text-center md:hidden">
                <Link to="/properties" className="btn-primary">
                  View All Properties
                </Link>
              </div>
            </>
          )}
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-blue-600 via-blue-700 to-indigo-800 text-white py-20 relative overflow-hidden">
        {/* Decorative blur circles */}
        <div className="absolute top-0 left-0 w-96 h-96 bg-blue-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 -ml-48 -mt-48"></div>
        <div className="absolute bottom-0 right-0 w-96 h-96 bg-indigo-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20 -mr-48 -mb-48"></div>

        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">
            Ready to Find Your Perfect Property?
          </h2>
          <p className="text-xl text-blue-100 mb-10">
            Join thousands of users buying, selling, and renting properties in Sri Lanka
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4">
            <Link to="/properties" className="inline-flex items-center justify-center gap-2 bg-white text-blue-600 font-bold py-3 px-8 rounded-lg hover:bg-blue-50 transition-all duration-300 hover:scale-105">
              🔍 Browse Properties
            </Link>
            <Link to="/signup" className="inline-flex items-center justify-center gap-2 bg-blue-500 text-white font-bold py-3 px-8 rounded-lg hover:bg-blue-400 transition-all duration-300 hover:scale-105 border border-blue-400">
              ➕ Sign Up Free
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;