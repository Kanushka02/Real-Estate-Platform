import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { propertyAPI } from '../services/api';
import PropertyCard from '../components/PropertyCard';
import PropertyFilter from '../components/PropertyFilter';

const Properties = () => {
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const search = searchParams.get('search');
    if (search) {
      handleSearch(search);
    } else {
      fetchProperties();
    }
  }, [currentPage, searchParams]);

  const fetchProperties = async (filters = {}) => {
    setLoading(true);
    try {
      let response;
      if (Object.keys(filters).length > 0) {
        response = await propertyAPI.filter(filters, currentPage, 12);
      } else {
        response = await propertyAPI.getAll(currentPage, 12);
      }
      
      setProperties(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching properties:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (keyword) => {
    setLoading(true);
    try {
      const response = await propertyAPI.search(keyword, currentPage, 12);
      setProperties(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error searching properties:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = (filters) => {
    setCurrentPage(0);
    fetchProperties(filters);
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    window.scrollTo(0, 0);
  };

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900">Browse Properties</h1>
          <p className="text-gray-600 mt-2">Find your ideal property from our extensive collection</p>
        </div>
        
        {/* Filter */}
        <PropertyFilter onFilter={handleFilter} />

        {/* Results */}
        {loading ? (
          <div className="text-center py-16">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="text-gray-600 mt-4">Loading properties...</p>
          </div>
        ) : (
          <>
            {properties.length === 0 ? (
              <div className="card p-12 text-center">
                <div className="text-5xl mb-4">🔍</div>
                <h3 className="text-2xl font-bold text-gray-900 mb-2">No properties found</h3>
                <p className="text-gray-600">Try adjusting your filters or search terms</p>
              </div>
            ) : (
              <>
                <div className="mb-6">
                  <p className="text-gray-600 font-medium">
                    Showing <span className="font-bold text-gray-900">{properties.length}</span> properties
                  </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
                  {properties.map(property => (
                    <PropertyCard key={property.id} property={property} />
                  ))}
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="flex flex-col items-center gap-6">
                    <div className="flex gap-2 flex-wrap justify-center">
                      <button
                        onClick={() => handlePageChange(currentPage - 1)}
                        disabled={currentPage === 0}
                        className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        ← Previous
                      </button>
                      
                      <div className="flex items-center gap-2">
                        {Array.from({ length: totalPages }, (_, i) => (
                          <button
                            key={i}
                            onClick={() => handlePageChange(i)}
                            className={`w-10 h-10 rounded-lg font-medium transition duration-200 ${
                              currentPage === i
                                ? 'bg-blue-600 text-white'
                                : 'bg-white text-gray-700 hover:bg-gray-100 border border-gray-200'
                            }`}
                          >
                            {i + 1}
                          </button>
                        ))}
                      </div>
                      
                      <button
                        onClick={() => handlePageChange(currentPage + 1)}
                        disabled={currentPage === totalPages - 1}
                        className="btn-secondary disabled:opacity-50 disabled:cursor-not-allowed"
                      >
                        Next →
                      </button>
                    </div>
                    
                    <p className="text-gray-600 text-sm">
                      Page <span className="font-bold">{currentPage + 1}</span> of <span className="font-bold">{totalPages}</span>
                    </p>
                  </div>
                )}
              </>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default Properties;