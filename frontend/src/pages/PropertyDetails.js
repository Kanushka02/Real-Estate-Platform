import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { propertyAPI, favoriteAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { formatPrice, STATUS_COLORS } from '../utils/constants';
import { getImageSource } from '../utils/imageUtils';

const PropertyDetails = () => {
  const { id } = useParams();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    fetchProperty();
    if (isAuthenticated()) {
      checkFavorite();
    }
  }, [id]);

  const fetchProperty = async () => {
    try {
      const [propertyResponse, imageResponse] = await Promise.all([
        propertyAPI.getById(id),
        propertyAPI.getPropertyImage(id)
      ]);
      
      const propertyData = propertyResponse.data;
      
      // Add image data to property
      if (imageResponse.data) {
        propertyData.imageData = Array.from(new Uint8Array(imageResponse.data));
        propertyData.imageType = imageResponse.headers['content-type'];
      }
      
      setProperty(propertyData);
    } catch (error) {
      console.error('Error fetching property:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkFavorite = async () => {
    try {
      const response = await favoriteAPI.check(id);
      setIsFavorite(response.data);
    } catch (error) {
      console.error('Error checking favorite:', error);
    }
  };

  const handleFavoriteToggle = async () => {
    if (!isAuthenticated()) {
      navigate('/login');
      return;
    }

    try {
      if (isFavorite) {
        await favoriteAPI.remove(id);
        setIsFavorite(false);
      } else {
        await favoriteAPI.add(id);
        setIsFavorite(true);
      }
    } catch (error) {
      console.error('Error toggling favorite:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!property) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 text-center">
        <h2 className="text-2xl font-bold text-gray-900">Property not found</h2>
      </div>
    );
  }

  // Use utility function to get image source
  const mainImage = getImageSource(property);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Image Display */}
      <div className="mb-8">
        <img
          src={mainImage}
          alt={property.title}
          className="w-full h-96 object-cover rounded-lg mb-4"
          onError={(e) => { e.target.src = 'https://via.placeholder.com/800x400?text=No+Image+Available'; }}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2">
          <div className="flex items-start justify-between mb-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{property.title}</h1>
              <p className="text-gray-600">📍 {property.address}, {property.city}, {property.district}</p>
            </div>
            <button
              onClick={handleFavoriteToggle}
              className={`text-2xl ${isFavorite ? 'text-red-500' : 'text-gray-400'} hover:scale-110 transition`}
            >
              {isFavorite ? '❤️' : '🤍'}
            </button>
          </div>

          <div className="flex items-center space-x-4 mb-6">
            <span className="text-3xl font-bold text-primary-600">
              {formatPrice(property.price)}
            </span>
            <span className="badge bg-primary-100 text-primary-800">
              {property.listingType}
            </span>
            <span className="badge bg-gray-100 text-gray-800">
              {property.type}
            </span>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {property.bedrooms && (
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <div className="text-2xl mb-2">🛏️</div>
                <div className="font-semibold">{property.bedrooms}</div>
                <div className="text-sm text-gray-600">Bedrooms</div>
              </div>
            )}
            {property.bathrooms && (
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <div className="text-2xl mb-2">🚿</div>
                <div className="font-semibold">{property.bathrooms}</div>
                <div className="text-sm text-gray-600">Bathrooms</div>
              </div>
            )}
            {property.landSize && (
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <div className="text-2xl mb-2">📏</div>
                <div className="font-semibold">{property.landSize}</div>
                <div className="text-sm text-gray-600">Perches</div>
              </div>
            )}
            {property.parkingSpaces && (
              <div className="bg-gray-50 p-4 rounded-lg text-center">
                <div className="text-2xl mb-2">🚗</div>
                <div className="font-semibold">{property.parkingSpaces}</div>
                <div className="text-sm text-gray-600">Parking</div>
              </div>
            )}
          </div>

          <div className="mb-6">
            <h2 className="text-xl font-bold text-gray-900 mb-3">Description</h2>
            <p className="text-gray-700 whitespace-pre-line">{property.description}</p>
          </div>

          {/* REMOVE: Features Section */}
        </div>

        {/* Sidebar */}
        <div className="lg:col-span-1">
          <div className="card p-6 sticky top-4">
            <h3 className="text-xl font-bold text-gray-900 mb-4">Contact Owner</h3>
            
            <div className="mb-4">
              <p className="text-gray-600 mb-2">Listed by:</p>
              <p className="font-semibold">{property.ownerName}</p>
            </div>

            <button className="w-full btn-primary mb-3">
              📞 Call Owner
            </button>
            <button className="w-full btn-secondary mb-3">
              ✉️ Send Message
            </button>

            <div className="border-t pt-4 mt-4">
              <h4 className="font-semibold mb-2">Property Details</h4>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Property ID:</span>
                  <span className="font-semibold">#{property.id}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Status:</span>
                  <span className={`badge ${STATUS_COLORS[property.status]}`}>
                    {property.status}
                  </span>
                </div>
                {property.floorSize && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">Floor Size:</span>
                    <span className="font-semibold">{property.floorSize} sq ft</span>
                  </div>
                )}
                <div className="flex justify-between">
                  <span className="text-gray-600">Posted:</span>
                  <span className="font-semibold">
                    {new Date(property.createdAt).toLocaleDateString()}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PropertyDetails;

