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
      {/* Hero Section with Background Image */}
      <div className="relative text-white py-32 md:py-48 overflow-hidden">
        {/* Background Image */}
        <div
          className="absolute inset-0 bg-cover bg-center bg-no-repeat"
          style={{
            backgroundImage: `url('https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&q=80')`
          }}
        ></div>

        {/* Dark Blue Overlay for readability */}
        <div className="absolute inset-0 bg-gradient-to-br from-blue-900/90 via-blue-800/85 to-indigo-900/90"></div>

        {/* Decorative blur circles */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500 rounded-full mix-blend-overlay filter blur-3xl opacity-30 -mr-48 -mt-48"></div>
        <div className="absolute bottom-0 left-0 w-96 h-96 bg-indigo-500 rounded-full mix-blend-overlay filter blur-3xl opacity-30 -ml-48 -mb-48"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="text-center">
            <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight tracking-tight drop-shadow-2xl">
              Find Your Dream Property
            </h1>
            <p className="text-xl md:text-2xl mb-14 text-blue-50 max-w-2xl mx-auto leading-relaxed drop-shadow-lg">
              Discover thousands of verified properties<br className="hidden sm:inline" /> for sale and rent across Sri Lanka
            </p>

            <form onSubmit={handleSearch} className="max-w-4xl mx-auto">
              <div className="flex flex-col sm:flex-row gap-3 bg-white rounded-2xl shadow-2xl p-2">
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  placeholder="Search by location, property type..."
                  className="flex-1 px-8 py-5 text-gray-900 focus:outline-none placeholder-gray-400 text-lg rounded-xl bg-transparent border-none"
                />

                {/* Visual Separator - Desktop only */}
                <div className="hidden sm:block w-px bg-gray-200 my-2"></div>

                <button
                  type="submit"
                  className="bg-gradient-to-r from-orange-500 to-orange-600 text-white px-10 py-5 rounded-xl font-bold hover:from-orange-600 hover:to-orange-700 transition-all duration-300 whitespace-nowrap flex items-center justify-center gap-3 shadow-lg hover:shadow-xl transform hover:scale-105"
                >
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                  </svg>
                  <span>Search Properties</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      {/* Quick Stats - Premium Glassmorphism Cards */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 -mt-16 mb-24 relative z-10">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Browse Properties Card */}
          <div className="group relative bg-gradient-to-br from-blue-50 via-white to-blue-50 p-8 rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-500 hover:-translate-y-3 border border-blue-100 overflow-hidden">
            {/* Animated gradient background */}
            <div className="absolute inset-0 bg-gradient-to-br from-blue-600/5 via-transparent to-blue-600/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

            {/* Glowing orb effect */}
            <div className="absolute -top-12 -right-12 w-32 h-32 bg-blue-400 rounded-full blur-3xl opacity-20 group-hover:opacity-40 transition-opacity duration-500"></div>

            <div className="relative z-10">
              <div className="mb-6 transform group-hover:scale-110 transition-all duration-500">
                <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-2xl group-hover:shadow-blue-500/50 transition-all duration-500">
                  <svg className="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                  </svg>
                </div>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4 group-hover:text-blue-600 transition-colors duration-300">Browse Properties</h3>
              <p className="text-gray-600 text-base leading-relaxed mb-6">Find your perfect home from thousands of verified listings</p>
              <div className="flex items-center text-blue-600 font-semibold text-sm group-hover:translate-x-2 transition-transform duration-300">
                Explore Now
                <svg className="w-4 h-4 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </div>
            </div>
          </div>

          {/* List Property Card */}
          <div className="group relative bg-gradient-to-br from-green-50 via-white to-green-50 p-8 rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-500 hover:-translate-y-3 border border-green-100 overflow-hidden">
            {/* Animated gradient background */}
            <div className="absolute inset-0 bg-gradient-to-br from-green-600/5 via-transparent to-green-600/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

            {/* Glowing orb effect */}
            <div className="absolute -top-12 -right-12 w-32 h-32 bg-green-400 rounded-full blur-3xl opacity-20 group-hover:opacity-40 transition-opacity duration-500"></div>

            <div className="relative z-10">
              <div className="mb-6 transform group-hover:scale-110 transition-all duration-500">
                <div className="w-20 h-20 bg-gradient-to-br from-green-500 to-green-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-2xl group-hover:shadow-green-500/50 transition-all duration-500">
                  <svg className="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4 group-hover:text-green-600 transition-colors duration-300">List Property</h3>
              <p className="text-gray-600 text-base leading-relaxed mb-6">Post your property and reach thousands of buyers instantly</p>
              <div className="flex items-center text-green-600 font-semibold text-sm group-hover:translate-x-2 transition-transform duration-300">
                Start Listing
                <svg className="w-4 h-4 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </div>
            </div>
          </div>

          {/* Save Favorites Card */}
          <div className="group relative bg-gradient-to-br from-yellow-50 via-white to-yellow-50 p-8 rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-500 hover:-translate-y-3 border border-yellow-100 overflow-hidden">
            {/* Animated gradient background */}
            <div className="absolute inset-0 bg-gradient-to-br from-yellow-600/5 via-transparent to-yellow-600/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

            {/* Glowing orb effect */}
            <div className="absolute -top-12 -right-12 w-32 h-32 bg-yellow-400 rounded-full blur-3xl opacity-20 group-hover:opacity-40 transition-opacity duration-500"></div>

            <div className="relative z-10">
              <div className="mb-6 transform group-hover:scale-110 transition-all duration-500">
                <div className="w-20 h-20 bg-gradient-to-br from-amber-500 to-yellow-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-2xl group-hover:shadow-yellow-500/50 transition-all duration-500">
                  <svg className="w-10 h-10 text-white" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                  </svg>
                </div>
              </div>
              <h3 className="text-2xl font-bold text-gray-900 mb-4 group-hover:text-yellow-600 transition-colors duration-300">Save Favorites</h3>
              <p className="text-gray-600 text-base leading-relaxed mb-6">Keep track of properties you like and compare easily</p>
              <div className="flex items-center text-yellow-600 font-semibold text-sm group-hover:translate-x-2 transition-transform duration-300">
                View Saved
                <svg className="w-4 h-4 ml-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Featured Properties */}
      {featuredProperties.length > 0 && (
        <div className="bg-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 md:py-24">
            <div className="flex justify-between items-start md:items-center mb-12 md:mb-14 flex-col md:flex-row gap-4">
              <div>
                <div className="inline-block px-4 py-1.5 bg-blue-100 text-blue-700 rounded-full text-sm font-semibold mb-3">⭐ Featured</div>
                <h2 className="text-4xl md:text-5xl font-bold text-gray-900">Premium Properties</h2>
                <p className="text-gray-600 mt-3 text-lg">Handpicked properties curated just for you</p>
              </div>
              <Link to="/properties" className="btn-primary hidden md:inline-flex">
                View All →
              </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-8">
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
        </div>
      )}

      {/* Latest Properties */}
      <div className="bg-gray-50 py-16 md:py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center mb-12 md:mb-14">
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
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-8">
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

      {/* Project Overview - Portfolio Style */}
      <div className="bg-slate-900 py-24 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="max-w-4xl">
            <h2 className="text-3xl md:text-4xl font-bold mb-6 text-blue-400">
              Project Overview
            </h2>
            <div className="prose prose-lg prose-invert text-slate-300 mb-12">
              <h3 className="text-xl font-semibold text-white mb-4">Real Estate Listing Platform</h3>
              <p className="mb-6 leading-relaxed">
                A full-stack marketplace built for the Sri Lankan property market, enabling users to discover, list, and manage properties with secure role-based access.
              </p>

              <div className="grid md:grid-cols-2 gap-8 mt-8">
                <div>
                  <h4 className="text-lg font-semibold text-white mb-3">Key Features</h4>
                  <ul className="space-y-2">
                    <li className="flex items-start gap-2">
                      <svg className="w-6 h-6 text-green-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                      <span>Advanced search & filtering (districts, types, budgets)</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <svg className="w-6 h-6 text-green-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                      <span>Secure authentication with Spring Security & JWT</span>
                    </li>
                    <li className="flex items-start gap-2">
                      <svg className="w-6 h-6 text-green-400 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                      <span>Admin dashboard for listing moderation & user management</span>
                    </li>
                  </ul>
                </div>

                <div>
                  <h4 className="text-lg font-semibold text-white mb-3">Technologies Used</h4>
                  <div className="flex flex-wrap gap-2">
                    {['Java', 'Spring Boot', 'PostgreSQL', 'React.js', 'Tailwind CSS', 'RESTful APIs'].map((tech) => (
                      <span key={tech} className="px-3 py-1 bg-slate-800 border border-slate-700 rounded-full text-sm font-medium text-blue-300 hover:bg-slate-700 transition-colors">
                        {tech}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
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