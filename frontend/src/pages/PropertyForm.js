import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { propertyAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DISTRICTS, PROPERTY_TYPES, LISTING_TYPES } from '../utils/constants';
// Note: We are removing single-file validators for this step to keep multiple-upload simple
import { getImageSource } from '../utils/imageUtils'; 

const PropertyForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    type: 'HOUSE',
    listingType: 'SALE',
    price: '',
    address: '',
    city: '',
    district: '',
    bedrooms: '',
    bathrooms: '',
    landSize: '',
    floorSize: '',
    parkingSpaces: '',
  });

  // --- CHANGED: State to handle multiple files instead of one ---
  const [selectedFiles, setSelectedFiles] = useState(null);
  const [imagePreview, setImagePreview] = useState(null); // Kept for existing image in Edit mode

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (isEdit) {
      fetchProperty();
    }
  }, [id]);

  const fetchProperty = async () => {
    try {
      const response = await propertyAPI.getById(id);
      const property = response.data;
      setFormData({
        title: property.title,
        description: property.description,
        type: property.type,
        listingType: property.listingType,
        price: property.price,
        address: property.address,
        city: property.city,
        district: property.district,
        bedrooms: property.bedrooms || '',
        bathrooms: property.bathrooms || '',
        landSize: property.landSize || '',
        floorSize: property.floorSize || '',
        parkingSpaces: property.parkingSpaces || '',
      });

      // Handle existing image data for display
      const imageSource = getImageSource(property);
      if (imageSource !== '/no-image.png') {
        setImagePreview(imageSource);
      }
    } catch (error) {
      console.error('Error fetching property:', error);
      setError('Failed to load property');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  // --- CHANGED: Handle Multiple Files ---
  const handleImageChange = (e) => {
    // Capture the entire FileList object
    if (e.target.files && e.target.files.length > 0) {
        setSelectedFiles(e.target.files);
        setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // 1. Submit property data (Text data)
      let propertyResponse;
      if (isEdit) {
        await propertyAPI.update(id, formData);
      } else {
        propertyResponse = await propertyAPI.create(formData);
      }

      // --- CHANGED: Upload Multiple Photos ---
      // We use the new uploadPhotos method created in api.js
      if (selectedFiles && selectedFiles.length > 0) {
        const propertyId = isEdit ? id : propertyResponse.data.id;
        await propertyAPI.uploadPhotos(propertyId, selectedFiles);
      }

      navigate('/my-properties');
    } catch (error) {
      console.error('Error saving property:', error);
      setError(error.response?.data?.message || 'Failed to save property');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-50 min-h-screen py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900">
            {isEdit ? '✏️ Edit Property' : '📝 Post New Property'}
          </h1>
          <p className="text-gray-600 mt-2">
            {isEdit 
              ? 'Update your property information' 
              : 'List your property to reach thousands of buyers and renters'}
          </p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-4 rounded-lg mb-6 flex items-start gap-2">
            <span className="text-lg">⚠️</span>
            <div>
              <p className="font-semibold text-sm">{error}</p>
            </div>
          </div>
        )}

        <form onSubmit={handleSubmit} className="card shadow-md p-8 space-y-8">
        {/* Basic Information */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Basic Information</h2>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Title *
              </label>
              <input
                type="text"
                name="title"
                required
                value={formData.title}
                onChange={handleChange}
                className="input-field"
                placeholder="e.g., Modern 3 Bedroom House in Colombo"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description *
              </label>
              <textarea
                name="description"
                required
                value={formData.description}
                onChange={handleChange}
                rows="5"
                className="input-field"
                placeholder="Describe your property..."
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Property Type *
                </label>
                <select
                  name="type"
                  required
                  value={formData.type}
                  onChange={handleChange}
                  className="input-field"
                >
                  {PROPERTY_TYPES.map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Listing Type *
                </label>
                <select
                  name="listingType"
                  required
                  value={formData.listingType}
                  onChange={handleChange}
                  className="input-field"
                >
                  {LISTING_TYPES.map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Price (Rs.) *
              </label>
              <input
                type="number"
                name="price"
                required
                value={formData.price}
                onChange={handleChange}
                className="input-field"
                placeholder="e.g., 15000000"
              />
            </div>
          </div>
        </div>

        {/* Location */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Location</h2>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Address *
              </label>
              <input
                type="text"
                name="address"
                required
                value={formData.address}
                onChange={handleChange}
                className="input-field"
                placeholder="Street address"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  District *
                </label>
                <select
                  name="district"
                  required
                  value={formData.district}
                  onChange={handleChange}
                  className="input-field"
                >
                  <option value="">Select District</option>
                  {DISTRICTS.map(district => (
                    <option key={district} value={district}>{district}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  City *
                </label>
                <input
                  type="text"
                  name="city"
                  required
                  value={formData.city}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="City"
                />
              </div>
            </div>
          </div>
        </div>

        {/* Property Details */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Property Details</h2>
          
          <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Bedrooms
              </label>
              <input
                type="number"
                name="bedrooms"
                value={formData.bedrooms}
                onChange={handleChange}
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Bathrooms
              </label>
              <input
                type="number"
                name="bathrooms"
                value={formData.bathrooms}
                onChange={handleChange}
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Parking Spaces
              </label>
              <input
                type="number"
                name="parkingSpaces"
                value={formData.parkingSpaces}
                onChange={handleChange}
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Land Size (perches)
              </label>
              <input
                type="number"
                step="0.01"
                name="landSize"
                value={formData.landSize}
                onChange={handleChange}
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Floor Size (sq ft)
              </label>
              <input
                type="number"
                step="0.01"
                name="floorSize"
                value={formData.floorSize}
                onChange={handleChange}
                className="input-field"
              />
            </div>
          </div>
        </div>

        {/* --- CHANGED: Additional Information (Multiple Photos) --- */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Photos</h2>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Add Photos (Hold Ctrl to select multiple)
              </label>
              
              {/* CHANGED: Added 'multiple' attribute */}
              <input 
                type="file" 
                multiple
                name="photo" 
                accept="image/*" 
                onChange={handleImageChange}
                className="input-field" 
              />

              {/* Display selected file count */}
              {selectedFiles && selectedFiles.length > 0 && (
                <div className="mt-2 p-2 bg-green-50 border border-green-200 rounded text-sm text-green-700">
                  ✅ {selectedFiles.length} files selected
                </div>
              )}

              <p className="text-xs text-gray-500 mt-1">
                Upload photos of the property (supports JPEG, PNG, GIF formats).
              </p>
            </div>
          </div>
        </div>
        {/* ------------------------------------------------------- */}

        <div className="flex flex-col sm:flex-row gap-4 pt-4 border-t border-gray-200">
          <button
            type="submit"
            disabled={loading}
            className="btn-primary flex-1 sm:flex-none py-3"
          >
            {loading ? '⏳ Saving...' : (isEdit ? '✏️ Update Property' : '✅ Post Property')}
          </button>
          <button
            type="button"
            onClick={() => navigate('/my-properties')}
            className="btn-secondary flex-1 sm:flex-none py-3"
          >
            ✕ Cancel
          </button>
        </div>
        </form>
      </div>
    </div>
  );
};

export default PropertyForm;