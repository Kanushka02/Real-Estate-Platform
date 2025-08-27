import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { propertiesAPI, locationsAPI, categoriesAPI, handleAPIError } from '../services/api';
import { Card, Button, Input, Select, Alert, LoadingSpinner } from '../components/common';

const PropertyForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const isEdit = !!id;

  // Form state
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    price: '',
    propertyType: 'SALE',
    bedrooms: '',
    bathrooms: '',
    areaSqft: '',
    address: '',
    locationId: '',
    categoryId: ''
  });

  // Form options
  const [locations, setLocations] = useState([]);
  const [categories, setCategories] = useState([]);
  
  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (user?.role !== 'SELLER' && user?.role !== 'ADMIN') {
      navigate('/dashboard');
      return;
    }

    loadFormData();
  }, [isAuthenticated, user, id, navigate]);

  const loadFormData = async () => {
    try {
      setLoading(true);
      
      // Load locations and categories
      const [locationsRes, categoriesRes] = await Promise.all([
        propertiesAPI.getLocations(),
        propertiesAPI.getCategories()
      ]);
      
      setLocations(locationsRes.data);
      setCategories(categoriesRes.data);

      // If editing, load property data
      if (isEdit) {
        const propertyRes = await propertiesAPI.getById(id);
        const property = propertyRes.data;
        
        setFormData({
          title: property.title,
          description: property.description,
          price: property.price.toString(),
          propertyType: property.propertyType,
          bedrooms: property.bedrooms?.toString() || '',
          bathrooms: property.bathrooms?.toString() || '',
          areaSqft: property.areaSqft?.toString() || '',
          address: property.address,
          locationId: property.location.locationId.toString(),
          categoryId: property.category.categoryId.toString()
        });
      }
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (name, value) => {
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear errors when user starts typing
    if (error) setError('');
  };

  const validateForm = () => {
    if (!formData.title.trim()) {
      setError('Property title is required');
      return false;
    }
    if (!formData.description.trim()) {
      setError('Property description is required');
      return false;
    }
    if (!formData.price || formData.price <= 0) {
      setError('Valid price is required');
      return false;
    }
    if (!formData.address.trim()) {
      setError('Property address is required');
      return false;
    }
    if (!formData.locationId) {
      setError('Location is required');
      return false;
    }
    if (!formData.categoryId) {
      setError('Property category is required');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      setError('');
      
      const propertyData = {
        ...formData,
        price: parseFloat(formData.price),
        bedrooms: formData.bedrooms ? parseInt(formData.bedrooms) : null,
        bathrooms: formData.bathrooms ? parseInt(formData.bathrooms) : null,
        areaSqft: formData.areaSqft ? parseInt(formData.areaSqft) : null,
        locationId: parseInt(formData.locationId),
        categoryId: parseInt(formData.categoryId)
      };

      if (isEdit) {
        await propertiesAPI.updateProperty(id, propertyData);
        setSuccess('Property updated successfully!');
      } else {
        await propertiesAPI.createProperty(propertyData);
        setSuccess('Property created successfully!');
      }

      // Redirect after success
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  if (loading && !locations.length) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="xl" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="container mx-auto px-4 max-w-2xl">
        <Card className="p-8">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              {isEdit ? 'Edit Property' : 'Add New Property'}
            </h1>
            <p className="text-gray-600 mt-2">
              {isEdit ? 'Update your property details' : 'List your property for sale or rent'}
            </p>
          </div>

          {error && (
            <Alert type="error" className="mb-6">
              {error}
            </Alert>
          )}

          {success && (
            <Alert type="success" className="mb-6">
              {success}
            </Alert>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Basic Information */}
            <div className="space-y-4">
              <h2 className="text-xl font-semibold text-gray-900">Basic Information</h2>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Property Title *
                </label>
                <Input
                  type="text"
                  placeholder="e.g., Beautiful 3BR House in Colombo"
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description *
                </label>
                <textarea
                  className="input-field min-h-[120px] resize-y"
                  placeholder="Describe your property, its features, amenities, and surroundings..."
                  value={formData.description}
                  onChange={(e) => handleInputChange('description', e.target.value)}
                  required
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Property Type *
                  </label>
                  <Select
                    value={formData.propertyType}
                    onChange={(e) => handleInputChange('propertyType', e.target.value)}
                    required
                  >
                    <option value="SALE">For Sale</option>
                    <option value="RENT">For Rent</option>
                  </Select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Price (Rs.) *
                  </label>
                  <Input
                    type="number"
                    placeholder={formData.propertyType === 'RENT' ? 'Monthly rent' : 'Sale price'}
                    value={formData.price}
                    onChange={(e) => handleInputChange('price', e.target.value)}
                    required
                  />
                </div>
              </div>
            </div>

            {/* Property Details */}
            <div className="space-y-4">
              <h2 className="text-xl font-semibold text-gray-900">Property Details</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bedrooms
                  </label>
                  <Select
                    value={formData.bedrooms}
                    onChange={(e) => handleInputChange('bedrooms', e.target.value)}
                  >
                    <option value="">Select</option>
                    <option value="1">1 Bedroom</option>
                    <option value="2">2 Bedrooms</option>
                    <option value="3">3 Bedrooms</option>
                    <option value="4">4 Bedrooms</option>
                    <option value="5">5+ Bedrooms</option>
                  </Select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bathrooms
                  </label>
                  <Select
                    value={formData.bathrooms}
                    onChange={(e) => handleInputChange('bathrooms', e.target.value)}
                  >
                    <option value="">Select</option>
                    <option value="1">1 Bathroom</option>
                    <option value="2">2 Bathrooms</option>
                    <option value="3">3 Bathrooms</option>
                    <option value="4">4+ Bathrooms</option>
                  </Select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Area (Sq Ft)
                  </label>
                  <Input
                    type="number"
                    placeholder="e.g., 1200"
                    value={formData.areaSqft}
                    onChange={(e) => handleInputChange('areaSqft', e.target.value)}
                  />
                </div>
              </div>
            </div>

            {/* Location */}
            <div className="space-y-4">
              <h2 className="text-xl font-semibold text-gray-900">Location</h2>
              
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Full Address *
                </label>
                <Input
                  type="text"
                  placeholder="e.g., 123 Main Street, Nugegoda"
                  value={formData.address}
                  onChange={(e) => handleInputChange('address', e.target.value)}
                  required
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    District/City *
                  </label>
                  <Select
                    value={formData.locationId}
                    onChange={(e) => handleInputChange('locationId', e.target.value)}
                    required
                  >
                    <option value="">Select Location</option>
                    {locations.map(location => (
                      <option key={location.locationId} value={location.locationId}>
                        {location.city}, {location.district}
                      </option>
                    ))}
                  </Select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Property Category *
                  </label>
                  <Select
                    value={formData.categoryId}
                    onChange={(e) => handleInputChange('categoryId', e.target.value)}
                    required
                  >
                    <option value="">Select Category</option>
                    {categories.map(category => (
                      <option key={category.categoryId} value={category.categoryId}>
                        {category.categoryName}
                      </option>
                    ))}
                  </Select>
                </div>
              </div>
            </div>

            {/* Image Upload Placeholder */}
            <div className="space-y-4">
              <h2 className="text-xl font-semibold text-gray-900">Property Images</h2>
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
                <div className="text-gray-400 text-4xl mb-4">📷</div>
                <p className="text-gray-600 mb-2">Image upload coming soon</p>
                <p className="text-sm text-gray-500">
                  For now, properties will display with a placeholder image
                </p>
              </div>
            </div>

            {/* Form Actions */}
            <div className="flex gap-4 pt-6">
              <Button 
                type="button" 
                variant="outline" 
                onClick={() => navigate(-1)}
                disabled={loading}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button 
                type="submit" 
                disabled={loading}
                className="flex-1"
              >
                {loading ? (
                  <LoadingSpinner size="sm" />
                ) : (
                  isEdit ? 'Update Property' : 'Create Property'
                )}
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  );
};

export default PropertyForm;