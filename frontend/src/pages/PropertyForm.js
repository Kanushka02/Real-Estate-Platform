import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { propertyAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { DISTRICTS, PROPERTY_TYPES, LISTING_TYPES } from '../utils/constants';
import { fileToByteArray, getImageSource, isValidImageType, isValidImageSize, getFileSizeMB } from '../utils/imageUtils';

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

  // Image handling state
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated()) {
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

      // Handle existing image data using utility function
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

  // Image handling functions
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validate file type
      if (!isValidImageType(file)) {
        setError('Please select a valid image file (JPEG, PNG, GIF, or WebP)');
        return;
      }
      
      // Validate file size
      if (!isValidImageSize(file)) {
        setError(`File size too large. Maximum size is 5MB. Current size: ${getFileSizeMB(file)}MB`);
        return;
      }
      
      setSelectedImage(file);
      setError(''); // Clear any previous errors
      
      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        setImagePreview(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Submit property data without image
      let propertyResponse;
      if (isEdit) {
        propertyResponse = await propertyAPI.update(id, formData);
      } else {
        propertyResponse = await propertyAPI.create(formData);
      }

      // Upload photo separately if selected
      if (selectedImage) {
        const propertyId = isEdit ? id : propertyResponse.data.id;
        await propertyAPI.uploadPhoto(propertyId, selectedImage, true); // true = isPrimary
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
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">
        {isEdit ? 'Edit Property' : 'Post New Property'}
      </h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-6">
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

        {/* Additional Information */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Additional Information</h2>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Add Photo
              </label>
              <input 
                type="file" 
                name="photo" 
                accept="image/*" 
                onChange={handleImageChange}
                className="input-field" 
              />
              {imagePreview && (
                <div className="mt-2">
                  <img 
                    src={imagePreview} 
                    alt="Preview" 
                    className="w-32 h-32 object-cover rounded-lg border"
                  />
                  <p className="text-xs text-gray-500 mt-1">
                    Selected: {selectedImage?.name}
                  </p>
                </div>
              )}
              <p className="text-xs text-gray-500 mt-1">
                Upload a photo of the property (supports JPEG, PNG, GIF formats).
              </p>
            </div>
          </div>
        </div>

        <div className="flex space-x-4">
          <button
            type="submit"
            disabled={loading}
            className="btn-primary disabled:opacity-50"
          >
            {loading ? 'Saving...' : (isEdit ? 'Update Property' : 'Post Property')}
          </button>
          <button
            type="button"
            onClick={() => navigate('/my-properties')}
            className="btn-secondary"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default PropertyForm;

