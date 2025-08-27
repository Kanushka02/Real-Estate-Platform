import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { propertiesAPI, handleAPIError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Button, Badge, LoadingSpinner, Card } from '../components/common';

const PropertyDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const [property, setProperty] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [contactSeller, setContactSeller] = useState(false);

  useEffect(() => {
    loadProperty();
  }, [id]);

  const loadProperty = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await propertiesAPI.getById(id);
      setProperty(response.data);
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    if (price >= 10000000) {
      return `Rs. ${(price / 10000000).toFixed(1)} Crore`;
    } else if (price >= 100000) {
      return `Rs. ${(price / 100000).toFixed(1)} Lakh`;
    } else {
      return `Rs. ${price.toLocaleString()}`;
    }
  };

  const handleContactSeller = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/properties/${id}` } });
      return;
    }
    setContactSeller(true);
  };

  const handleSaveProperty = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/properties/${id}` } });
      return;
    }
    
    // TODO: Implement save property functionality
    alert('Save property functionality coming soon!');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="xl" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Card className="p-8 max-w-md text-center">
          <div className="text-red-500 text-6xl mb-4">❌</div>
          <h2 className="text-xl font-semibold mb-2">Property Not Found</h2>
          <p className="text-gray-600 mb-4">{error}</p>
          <Link to="/properties" className="text-blue-600 hover:underline">
            ← Back to Properties
          </Link>
        </Card>
      </div>
    );
  }

  if (!property) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Card className="p-8 max-w-md text-center">
          <h2 className="text-xl font-semibold mb-2">Property Not Found</h2>
          <Link to="/properties" className="text-blue-600 hover:underline">
            ← Back to Properties
          </Link>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Breadcrumb */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-4">
          <nav className="flex items-center space-x-2 text-sm text-gray-600">
            <Link to="/" className="hover:text-blue-600">Home</Link>
            <span>›</span>
            <Link to="/properties" className="hover:text-blue-600">Properties</Link>
            <span>›</span>
            <span className="text-gray-900">{property.title}</span>
          </nav>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Property Images */}
            <Card className="mb-6 overflow-hidden">
              <div className="h-96 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                <div className="text-white text-8xl">🏠</div>
              </div>
              <div className="p-4">
                <p className="text-sm text-gray-600">
                  Photo gallery coming soon - property images will be displayed here
                </p>
              </div>
            </Card>

            {/* Property Info */}
            <Card className="p-6 mb-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <Badge 
                    variant={property.propertyType === 'SALE' ? 'success' : 'info'}
                    className="mb-2"
                  >
                    For {property.propertyType === 'SALE' ? 'Sale' : 'Rent'}
                  </Badge>
                  {property.featured && (
                    <Badge variant="warning" className="ml-2">Featured</Badge>
                  )}
                </div>
                <div className="text-right">
                  <p className="text-3xl font-bold text-blue-600">
                    {formatPrice(property.price)}
                    {property.propertyType === 'RENT' && (
                      <span className="text-lg text-gray-500">/month</span>
                    )}
                  </p>
                </div>
              </div>

              <h1 className="text-3xl font-bold text-gray-900 mb-4">
                {property.title}
              </h1>

              <div className="flex items-center text-gray-600 mb-6">
                <span className="mr-2">📍</span>
                <span className="text-lg">
                  {property.address}, {property.location.city}, {property.location.district}
                </span>
              </div>

              {/* Property Features */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
                {property.bedrooms && (
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl mb-2">🛏️</div>
                    <p className="text-lg font-semibold">{property.bedrooms}</p>
                    <p className="text-sm text-gray-600">Bedrooms</p>
                  </div>
                )}
                {property.bathrooms && (
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl mb-2">🚿</div>
                    <p className="text-lg font-semibold">{property.bathrooms}</p>
                    <p className="text-sm text-gray-600">Bathrooms</p>
                  </div>
                )}
                {property.areaSqft && (
                  <div className="text-center p-4 bg-gray-50 rounded-lg">
                    <div className="text-2xl mb-2">📐</div>
                    <p className="text-lg font-semibold">{property.areaSqft.toLocaleString()}</p>
                    <p className="text-sm text-gray-600">Sq Ft</p>
                  </div>
                )}
                <div className="text-center p-4 bg-gray-50 rounded-lg">
                  <div className="text-2xl mb-2">🏷️</div>
                  <p className="text-lg font-semibold">{property.category.categoryName}</p>
                  <p className="text-sm text-gray-600">Type</p>
                </div>
              </div>

              {/* Description */}
              <div>
                <h2 className="text-xl font-semibold mb-3">Description</h2>
                <p className="text-gray-700 leading-relaxed">
                  {property.description}
                </p>
              </div>
            </Card>

            {/* Location Map Placeholder */}
            <Card className="p-6">
              <h2 className="text-xl font-semibold mb-4">Location</h2>
              <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
                <div className="text-center text-gray-500">
                  <div className="text-4xl mb-2">🗺️</div>
                  <p>Interactive map coming soon</p>
                  <p className="text-sm">{property.location.city}, {property.location.district}</p>
                </div>
              </div>
            </Card>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            {/* Action Buttons */}
            <Card className="p-6 mb-6">
              <div className="space-y-3">
                <Button 
                  className="w-full text-lg py-3"
                  onClick={handleContactSeller}
                >
                  📞 Contact Seller
                </Button>
                <Button 
                  variant="outline" 
                  className="w-full"
                  onClick={handleSaveProperty}
                >
                  ❤️ Save Property
                </Button>
                <Button variant="outline" className="w-full">
                  📤 Share Property
                </Button>
              </div>
            </Card>

            {/* Seller Info */}
            <Card className="p-6 mb-6">
              <h3 className="text-lg font-semibold mb-4">Seller Information</h3>
              <div className="flex items-center mb-4">
                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mr-4">
                  <span className="text-blue-600 text-xl">👤</span>
                </div>
                <div>
                  <p className="font-semibold">
                    {property.seller.firstName} {property.seller.lastName}
                  </p>
                  <p className="text-sm text-gray-500 capitalize">{property.seller.role}</p>
                </div>
              </div>
              {property.seller.phone && (
                <p className="text-sm text-gray-600 mb-2">
                  📱 {property.seller.phone}
                </p>
              )}
              <p className="text-sm text-gray-600">
                ✉️ {property.seller.email}
              </p>
            </Card>

            {/* Property Details */}
            <Card className="p-6">
              <h3 className="text-lg font-semibold mb-4">Property Details</h3>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Property ID:</span>
                  <span className="font-medium">PL-{property.propertyId}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Listed:</span>
                  <span className="font-medium">
                    {new Date(property.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Status:</span>
                  <span className="font-medium capitalize">{property.status}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Category:</span>
                  <span className="font-medium">{property.category.categoryName}</span>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </div>

      {/* Contact Modal */}
      {contactSeller && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="p-6 max-w-md w-full mx-4">
            <h3 className="text-xl font-semibold mb-4">Contact Seller</h3>
            <div className="mb-4">
              <p className="text-gray-600 mb-2">
                Get in touch with {property.seller.firstName} {property.seller.lastName}
              </p>
              <div className="bg-gray-50 p-4 rounded-lg">
                <p className="mb-2">📱 {property.seller.phone}</p>
                <p>✉️ {property.seller.email}</p>
              </div>
            </div>
            <div className="flex gap-2">
              <Button 
                onClick={() => window.open(`tel:${property.seller.phone}`)}
                className="flex-1"
              >
                Call Now
              </Button>
              <Button 
                variant="outline"
                onClick={() => setContactSeller(false)}
              >
                Close
              </Button>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
};

export default PropertyDetail;