import React from 'react';
import { Link } from 'react-router-dom';
import { formatPrice, STATUS_COLORS } from '../utils/constants';
import { getImageSource } from '../utils/imageUtils';

const PropertyCard = ({ property, showStatus = false }) => {
  // Use utility function to get image source
  const mainImage = getImageSource(property);
  const defaultImage = '/no-image.png';

  return (
    <div className="card hover:shadow-xl transition-shadow duration-300">
      <div className="relative">
        <img
          src={mainImage}
          alt={property.title}
          className="w-full h-48 object-cover"
          onError={(e) => {
            console.warn('Image failed to load:', property.id);
            e.target.src = defaultImage;
            e.target.onerror = null; // Prevent infinite loop
          }}
          loading="lazy"
        />
        {showStatus && property.status && (
          <span className={`badge absolute top-2 right-2 ${STATUS_COLORS[property.status]}`}>
            {property.status}
          </span>
        )}
        <span className="badge absolute top-2 left-2 bg-primary-600 text-white">
          {property.listingType}
        </span>
      </div>
      
      <div className="p-4">
        <h3 className="text-xl font-semibold text-gray-900 mb-2 truncate">
          {property.title}
        </h3>
        
        <p className="text-gray-600 text-sm mb-2">
          📍 {property.city}, {property.district}
        </p>
        
        <div className="flex items-center justify-between mb-3">
          <span className="text-2xl font-bold text-primary-600">
            {formatPrice(property.price)}
          </span>
          <span className="badge bg-gray-100 text-gray-800">
            {property.type}
          </span>
        </div>
        
        <div className="flex items-center space-x-4 text-sm text-gray-600 mb-4">
          {property.bedrooms && (
            <span>🛏️ {property.bedrooms} Beds</span>
          )}
          {property.bathrooms && (
            <span>🚿 {property.bathrooms} Baths</span>
          )}
          {property.landSize && (
            <span>📏 {property.landSize} perches</span>
          )}
        </div>
        
        <Link
          to={`/properties/${property.id}`}
          className="block w-full text-center btn-primary"
        >
          View Details
        </Link>
      </div>
    </div>
  );
};

export default PropertyCard;

