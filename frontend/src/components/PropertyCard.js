import React from 'react';
import { Link } from 'react-router-dom';
import { formatPrice, STATUS_COLORS } from '../utils/constants';
import { getImageSource } from '../utils/imageUtils';

const PropertyCard = ({ property, showStatus = false, onDelete, onToggleStatus, showActions = false }) => {
  
  const mainImage = getImageSource(property);

  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300 h-full flex flex-col">
      <div className="relative h-48 w-full bg-gray-200">
        <img
          src={mainImage}
          alt={property.title}
          className="w-full h-full object-cover"
          onError={(e) => {
            // CHANGED: Use placehold.co instead of via.placeholder.com
            e.target.onerror = null; 
            e.target.src = 'https://placehold.co/400x300?text=Image+Error';
          }}
        />
        
        {/* ... rest of the component remains the same ... */}
        <div className="absolute top-2 left-2 flex flex-col gap-1">
            <span className="bg-blue-600 text-white text-xs font-bold px-2 py-1 rounded shadow">
                {property.listingType}
            </span>
        </div>

        {showStatus && property.status && (
          <span className={`absolute top-2 right-2 text-xs font-bold px-2 py-1 rounded shadow ${STATUS_COLORS[property.status]}`}>
            {property.status}
          </span>
        )}
      </div>
      
      {/* Content Section */}
      <div className="p-4 flex flex-col flex-grow">
        <h3 className="text-lg font-bold text-gray-900 mb-1 truncate">
          {property.title}
        </h3>
        
        <p className="text-gray-500 text-sm mb-3 flex items-center truncate">
          📍 {property.city}, {property.district}
        </p>
        
        <div className="flex items-center justify-between mb-3 mt-auto">
          <span className="text-xl font-bold text-blue-600">
            {formatPrice(property.price)}
          </span>
          <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded uppercase font-medium">
            {property.type}
          </span>
        </div>
        
        <div className="flex items-center justify-between text-xs text-gray-500 border-t pt-3 mb-3">
          {property.bedrooms && <span>🛏️ {property.bedrooms} Beds</span>}
          {property.bathrooms && <span>🚿 {property.bathrooms} Baths</span>}
          {property.landSize && <span>📏 {property.landSize} perches</span>}
        </div>
        
        <div className="flex gap-2 mt-2">
            {!showActions ? (
                <Link
                    to={`/properties/${property.id}`}
                    className="w-full bg-blue-600 text-white text-center py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition"
                >
                    View Details
                </Link>
            ) : (
                <>
                    <Link 
                        to={`/properties/edit/${property.id || property.propertyId}`} 
                        className="flex-1 bg-gray-200 text-gray-700 text-center py-2 rounded-lg text-sm font-medium hover:bg-gray-300"
                    >
                        Edit
                    </Link>
                    {onDelete && (
                        <button 
                            onClick={() => onDelete(property.id || property.propertyId)}
                            className="flex-1 bg-red-600 text-white text-center py-2 rounded-lg text-sm font-medium hover:bg-red-700"
                        >
                            Delete
                        </button>
                    )}
                </>
            )}
        </div>
      </div>
    </div>
  );
};

export default PropertyCard;