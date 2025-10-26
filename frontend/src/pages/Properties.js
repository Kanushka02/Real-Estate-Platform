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
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Browse Properties</h1>
      
      <PropertyFilter onFilter={handleFilter} />

      {loading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      ) : (
        <>
          {properties.length === 0 ? (
            <div className="text-center py-12">
              <p className="text-xl text-gray-600">No properties found</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                {properties.map(property => (
                  <PropertyCard key={property.id} property={property} />
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex justify-center space-x-2">
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="btn-secondary disabled:opacity-50"
                  >
                    Previous
                  </button>
                  
                  <span className="px-4 py-2 text-gray-700">
                    Page {currentPage + 1} of {totalPages}
                  </span>
                  
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                    className="btn-secondary disabled:opacity-50"
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </>
      )}
    </div>
  );
};

export default Properties;

