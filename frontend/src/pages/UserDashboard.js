import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { propertiesAPI, bookingsAPI, userAPI, reviewsAPI, handleAPIError } from '../services/api';
import { Card, Button, Badge, LoadingSpinner, Alert } from '../components/common';

const UserDashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');
  const [myProperties, setMyProperties] = useState([]);
  const [savedProperties, setSavedProperties] = useState([]);
  const [myBookings, setMyBookings] = useState([]);
  const [listingBookings, setListingBookings] = useState([]);
  const [myReviews, setMyReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [profileForm, setProfileForm] = useState({ firstName: '', lastName: '', phone: '' });
  const [pwdForm, setPwdForm] = useState({ currentPassword: '', newPassword: '' });
  const [profileMsg, setProfileMsg] = useState('');
  const [pwdMsg, setPwdMsg] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    loadDashboardData();
  }, [isAuthenticated, navigate]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError('');
      
      // Load user's properties if they're a seller
      if (user?.role === 'SELLER' || user?.role === 'ADMIN') {
        const propertiesResponse = await propertiesAPI.getMyProperties();
        setMyProperties(propertiesResponse.data);
      }
      
      // Load saved properties (favorites) - TODO: implement when backend ready
      // const savedResponse = await propertiesAPI.getSavedProperties();
      // setSavedProperties(savedResponse.data);

      // Load bookings
      if (user?.role === 'BUYER' || user?.role === 'ADMIN') {
        try {
          const b = await bookingsAPI.myBookings();
          setMyBookings(b);
        } catch (e) { /* ignore partial failure */ }
      }
      if (user?.role === 'SELLER' || user?.role === 'ADMIN') {
        try {
          const s = await bookingsAPI.bookingsForMyListings();
          setListingBookings(s);
        } catch (e) { /* ignore partial failure */ }
      }

      // Load user's reviews
      try {
        const reviewsResponse = await reviewsAPI.getMyReviews();
        setMyReviews(reviewsResponse.data || []);
      } catch (e) { /* ignore partial failure */ }

      // Seed profile form
      setProfileForm({
        firstName: user?.firstName || '',
        lastName: user?.lastName || '',
        phone: user?.phone || ''
      });
      
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteProperty = async (propertyId) => {
    if (!window.confirm('Are you sure you want to delete this property? This action cannot be undone.')) {
      return;
    }

    try {
      await propertiesAPI.deleteProperty(propertyId);
      setMyProperties(prev => prev.filter(p => p.propertyId !== propertyId));
      alert('Property deleted successfully');
    } catch (error) {
      alert('Failed to delete property: ' + handleAPIError(error));
    }
  };

  const handleToggleStatus = async (propertyId, currentStatus) => {
    try {
      const newStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
      await propertiesAPI.updatePropertyStatus(propertyId, newStatus);
      setMyProperties(prev => 
        prev.map(p => 
          p.propertyId === propertyId 
            ? { ...p, status: newStatus }
            : p
        )
      );
      alert(`Property status changed to ${newStatus}`);
    } catch (error) {
      alert('Failed to update status: ' + handleAPIError(error));
    }
  };

  const formatPrice = (price) => {
    if (price >= 10000000) {
      return `Rs. ${(price / 10000000).toFixed(1)}Cr`;
    } else if (price >= 100000) {
      return `Rs. ${(price / 100000).toFixed(1)}L`;
    } else {
      return `Rs. ${price.toLocaleString()}`;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="xl" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                Welcome back, {user?.firstName}!
              </h1>
              <p className="text-gray-600 mt-1">
                Manage your properties and account settings
              </p>
            </div>
            {(user?.role === 'SELLER' || user?.role === 'ADMIN') && (
              <Link to="/properties/create">
                <Button>+ Add New Property</Button>
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Navigation */}
          <div className="lg:w-1/4">
            <Card className="p-6">
              <div className="flex items-center mb-6">
                <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mr-4">
                  <span className="text-blue-600 text-2xl">👤</span>
                </div>
                <div>
                  <p className="font-semibold text-lg">
                    {user?.firstName} {user?.lastName}
                  </p>
                  <p className="text-gray-500 capitalize">{user?.role}</p>
                </div>
              </div>

              <nav className="space-y-2">
                <button
                  onClick={() => setActiveTab('overview')}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'overview'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Overview
                </button>

                {(user?.role === 'SELLER' || user?.role === 'ADMIN') && (
                  <button
                    onClick={() => setActiveTab('my-properties')}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      activeTab === 'my-properties'
                        ? 'bg-blue-50 text-blue-600 border border-blue-200'
                        : 'hover:bg-gray-50'
                    }`}
                  >
                    My Properties ({myProperties.length})
                  </button>
                )}

                <button
                  onClick={() => setActiveTab('saved-properties')}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'saved-properties'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Saved Properties ({savedProperties.length})
                </button>

                <button
                  onClick={() => setActiveTab('my-reviews')}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'my-reviews'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  My Reviews ({myReviews.length})
                </button>

                <button
                  onClick={() => setActiveTab('profile')}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'profile'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Profile Settings
                </button>

                {(user?.role === 'BUYER' || user?.role === 'ADMIN') && (
                  <button
                    onClick={() => setActiveTab('my-bookings')}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      activeTab === 'my-bookings'
                        ? 'bg-blue-50 text-blue-600 border border-blue-200'
                        : 'hover:bg-gray-50'
                    }`}
                  >
                    My Bookings ({myBookings.length})
                  </button>
                )}

                {(user?.role === 'SELLER' || user?.role === 'ADMIN') && (
                  <button
                    onClick={() => setActiveTab('listing-bookings')}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      activeTab === 'listing-bookings'
                        ? 'bg-blue-50 text-blue-600 border border-blue-200'
                        : 'hover:bg-gray-50'
                    }`}
                  >
                    Bookings for My Listings ({listingBookings.length})
                  </button>
                )}
              </nav>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:w-3/4">
            {error && (
              <Alert type="error" className="mb-6">
                {error}
              </Alert>
            )}

            {/* Overview Tab */}
            {activeTab === 'overview' && (
              <div className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <Card className="p-6 text-center">
                    <div className="text-3xl text-blue-600 mb-2">🏠</div>
                    <p className="text-2xl font-bold text-gray-900">
                      {myProperties.length}
                    </p>
                    <p className="text-gray-600">My Properties</p>
                  </Card>
                  
                  <Card className="p-6 text-center">
                    <div className="text-3xl text-red-500 mb-2">❤️</div>
                    <p className="text-2xl font-bold text-gray-900">
                      {savedProperties.length}
                    </p>
                    <p className="text-gray-600">Saved Properties</p>
                  </Card>
                  
                  <Card className="p-6 text-center">
                    <div className="text-3xl text-green-600 mb-2">✅</div>
                    <p className="text-2xl font-bold text-gray-900">
                      {myProperties.filter(p => p.status === 'ACTIVE').length}
                    </p>
                    <p className="text-gray-600">Active Listings</p>
                  </Card>
                </div>

                {/* Recent Activity */}
                <Card className="p-6">
                  <h2 className="text-xl font-semibold mb-4">Recent Activity</h2>
                  <div className="space-y-4">
                    {myProperties.slice(0, 3).map(property => (
                      <div key={property.propertyId} className="flex items-center justify-between py-3 border-b last:border-b-0">
                        <div>
                          <p className="font-medium">{property.title}</p>
                          <p className="text-sm text-gray-600">
                            Listed on {new Date(property.createdAt).toLocaleDateString()}
                          </p>
                        </div>
                        <Badge 
                          variant={property.status === 'ACTIVE' ? 'success' : 'secondary'}
                        >
                          {property.status}
                        </Badge>
                      </div>
                    ))}
                    {myProperties.length === 0 && (
                      <p className="text-gray-500 text-center py-4">
                        No properties yet. Start by adding your first property!
                      </p>
                    )}
                  </div>
                </Card>
              </div>
            )}

            {/* My Properties Tab */}
            {activeTab === 'my-properties' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <h2 className="text-2xl font-semibold">My Properties</h2>
                  <Link to="/properties/create">
                    <Button>+ Add New Property</Button>
                  </Link>
                </div>

                {myProperties.length === 0 ? (
                  <Card className="p-12 text-center">
                    <div className="text-gray-400 text-6xl mb-4">🏠</div>
                    <h3 className="text-xl font-medium text-gray-600 mb-2">
                      No Properties Yet
                    </h3>
                    <p className="text-gray-500 mb-6">
                      Start building your property portfolio by adding your first listing
                    </p>
                    <Link to="/properties/create">
                      <Button>Add Your First Property</Button>
                    </Link>
                  </Card>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {myProperties.map(property => (
                      <PropertyCard 
                        key={property.propertyId} 
                        property={property}
                        onDelete={handleDeleteProperty}
                        onToggleStatus={handleToggleStatus}
                        showActions={true}
                      />
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Saved Properties Tab */}
            {activeTab === 'saved-properties' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-semibold">Saved Properties</h2>

                <Card className="p-12 text-center">
                  <div className="text-gray-400 text-6xl mb-4">❤️</div>
                  <h3 className="text-xl font-medium text-gray-600 mb-2">
                    Saved Properties Coming Soon
                  </h3>
                  <p className="text-gray-500 mb-6">
                    We're working on the favorites feature. You'll be able to save and organize properties you're interested in.
                  </p>
                  <Link to="/properties">
                    <Button>Browse Properties</Button>
                  </Link>
                </Card>
              </div>
            )}

            {/* My Reviews Tab */}
            {activeTab === 'my-reviews' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-semibold">My Reviews</h2>

                {myReviews.length === 0 ? (
                  <Card className="p-12 text-center">
                    <div className="text-gray-400 text-6xl mb-4">⭐</div>
                    <h3 className="text-xl font-medium text-gray-600 mb-2">
                      No Reviews Yet
                    </h3>
                    <p className="text-gray-500 mb-6">
                      You haven't reviewed any properties yet. Visit properties you've viewed to leave reviews.
                    </p>
                    <Link to="/properties">
                      <Button>Browse Properties</Button>
                    </Link>
                  </Card>
                ) : (
                  <div className="space-y-4">
                    {myReviews.map(review => (
                      <Card key={review.reviewId} className="p-6">
                        <div className="flex justify-between items-start mb-4">
                          <div>
                            <h3 className="text-lg font-semibold text-gray-900 mb-1">
                              {review.property.title}
                            </h3>
                            <p className="text-sm text-gray-600">
                              Reviewed on {new Date(review.createdAt).toLocaleDateString()}
                            </p>
                          </div>
                          <div className="flex items-center">
                            {[1, 2, 3, 4, 5].map((star) => (
                              <span
                                key={star}
                                className={`text-lg ${star <= review.rating ? 'text-yellow-400' : 'text-gray-300'}`}
                              >
                                ★
                              </span>
                            ))}
                            <span className="ml-2 text-sm text-gray-600">({review.rating}/5)</span>
                          </div>
                        </div>
                        {review.comment && (
                          <p className="text-gray-700 mb-4">{review.comment}</p>
                        )}
                        <div className="flex gap-2">
                          <Link to={`/properties/${review.property.propertyId}`}>
                            <Button variant="outline" size="sm">
                              View Property
                            </Button>
                          </Link>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={async () => {
                              if (window.confirm('Are you sure you want to delete this review?')) {
                                try {
                                  await reviewsAPI.delete(review.reviewId);
                                  setMyReviews(prev => prev.filter(r => r.reviewId !== review.reviewId));
                                  alert('Review deleted successfully');
                                } catch (error) {
                                  alert('Failed to delete review: ' + handleAPIError(error));
                                }
                              }
                            }}
                            className="text-red-600 hover:text-red-700"
                          >
                            Delete
                          </Button>
                        </div>
                      </Card>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Profile Tab */}
            {activeTab === 'profile' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-semibold">Profile Settings</h2>
                
                <Card className="p-6">
                  <h3 className="text-lg font-semibold mb-4">Personal Information</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">First Name</label>
                      <input type="text" className="input-field" value={profileForm.firstName} onChange={(e)=>setProfileForm(p=>({...p, firstName: e.target.value}))} />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Last Name</label>
                      <input type="text" className="input-field" value={profileForm.lastName} onChange={(e)=>setProfileForm(p=>({...p, lastName: e.target.value}))} />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                      <input type="email" className="input-field" value={user?.email || ''} readOnly />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Phone</label>
                      <input type="text" className="input-field" value={profileForm.phone} onChange={(e)=>setProfileForm(p=>({...p, phone: e.target.value}))} />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Role</label>
                      <input type="text" className="input-field capitalize" value={user?.role || ''} readOnly />
                    </div>
                  </div>
                  {profileMsg && <div className="text-green-600 text-sm mt-3">{profileMsg}</div>}
                  <div className="mt-6">
                    <Button variant="outline" onClick={async()=>{
                      try {
                        setProfileMsg('');
                        await userAPI.updateProfile(profileForm);
                        setProfileMsg('Profile updated');
                      } catch (e) {
                        alert(handleAPIError(e));
                      }
                    }}>
                      Save Changes
                    </Button>
                  </div>
                </Card>

                <Card className="p-6">
                  <h3 className="text-lg font-semibold mb-4">Change Password</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Current Password</label>
                      <input type="password" className="input-field" value={pwdForm.currentPassword} onChange={(e)=>setPwdForm(p=>({...p, currentPassword: e.target.value}))} />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">New Password</label>
                      <input type="password" className="input-field" value={pwdForm.newPassword} onChange={(e)=>setPwdForm(p=>({...p, newPassword: e.target.value}))} />
                    </div>
                  </div>
                  {pwdMsg && <div className="text-green-600 text-sm mt-3">{pwdMsg}</div>}
                  <div className="mt-6">
                    <Button variant="outline" onClick={async()=>{
                      try {
                        setPwdMsg('');
                        await userAPI.changePassword(pwdForm);
                        setPwdMsg('Password updated');
                        setPwdForm({ currentPassword: '', newPassword: '' });
                      } catch (e) {
                        alert(handleAPIError(e));
                      }
                    }}>
                      Update Password
                    </Button>
                  </div>
                </Card>

              </div>
            )}

            {/* My Bookings (Buyer) */}
            {activeTab === 'my-bookings' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-semibold">My Bookings</h2>
                {myBookings.length === 0 ? (
                  <Card className="p-12 text-center">
                    <div className="text-gray-400 text-6xl mb-4">📅</div>
                    <p className="text-gray-600">No bookings yet.</p>
                  </Card>
                ) : (
                  <div className="space-y-3">
                    {myBookings.map(b => (
                      <Card key={b.bookingId} className="p-4">
                        <div className="flex justify-between items-center">
                          <div>
                            <p className="font-medium">{b.property?.title || 'Property'}</p>
                            <p className="text-sm text-gray-600">Scheduled: {new Date(b.scheduledAt).toLocaleString()}</p>
                          </div>
                          <Badge variant={b.status === 'CONFIRMED' ? 'success' : b.status === 'CANCELLED' ? 'secondary' : 'warning'}>
                            {b.status}
                          </Badge>
                        </div>
                      </Card>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Bookings for My Listings (Seller) */}
            {activeTab === 'listing-bookings' && (
              <div className="space-y-6">
                <h2 className="text-2xl font-semibold">Bookings for My Listings</h2>
                {listingBookings.length === 0 ? (
                  <Card className="p-12 text-center">
                    <div className="text-gray-400 text-6xl mb-4">📅</div>
                    <p className="text-gray-600">No bookings yet.</p>
                  </Card>
                ) : (
                  <div className="space-y-3">
                    {listingBookings.map(b => (
                      <Card key={b.bookingId} className="p-4">
                        <div className="flex justify-between items-center">
                          <div>
                            <p className="font-medium">{b.property?.title || 'Property'}</p>
                            <p className="text-sm text-gray-600">Buyer: {b.buyer?.email}</p>
                            <p className="text-sm text-gray-600">Scheduled: {new Date(b.scheduledAt).toLocaleString()}</p>
                          </div>
                          <Badge variant={b.status === 'CONFIRMED' ? 'success' : b.status === 'CANCELLED' ? 'secondary' : 'warning'}>
                            {b.status}
                          </Badge>
                        </div>
                      </Card>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Property Card Component for Dashboard
const PropertyCard = ({ property, onDelete, onToggleStatus, showActions = false }) => {
  const formatPrice = (price) => {
    if (price >= 10000000) {
      return `Rs. ${(price / 10000000).toFixed(1)}Cr`;
    } else if (price >= 100000) {
      return `Rs. ${(price / 100000).toFixed(1)}L`;
    } else {
      return `Rs. ${price.toLocaleString()}`;
    }
  };

  return (
    <Card className="overflow-hidden">
      {/* Property Image Placeholder */}
      <div className="h-48 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
        <div className="text-white text-4xl">🏠</div>
      </div>
      
      <div className="p-6">
        {/* Status and Type */}
        <div className="flex justify-between items-start mb-3">
          <Badge 
            variant={property.propertyType === 'SALE' ? 'success' : 'info'}
          >
            For {property.propertyType === 'SALE' ? 'Sale' : 'Rent'}
          </Badge>
          <Badge 
            variant={property.status === 'ACTIVE' ? 'success' : 'secondary'}
          >
            {property.status}
          </Badge>
        </div>

        {/* Title */}
        <h3 className="text-xl font-semibold text-gray-900 mb-2 line-clamp-2">
          {property.title}
        </h3>

        {/* Location */}
        <p className="text-gray-600 mb-2 flex items-center">
          <span className="mr-1">📍</span>
          {property.location.city}, {property.location.district}
        </p>

        {/* Price */}
        <p className="text-2xl font-bold text-blue-600 mb-3">
          {formatPrice(property.price)}
          {property.propertyType === 'RENT' && (
            <span className="text-sm text-gray-500">/month</span>
          )}
        </p>

        {/* Property Details */}
        <div className="flex items-center text-sm text-gray-600 mb-4">
          {property.bedrooms && (
            <span className="flex items-center mr-4">
              🛏️ {property.bedrooms} beds
            </span>
          )}
          {property.bathrooms && (
            <span className="flex items-center mr-4">
              🚿 {property.bathrooms} baths
            </span>
          )}
          {property.areaSqft && (
            <span className="flex items-center">
              📐 {property.areaSqft.toLocaleString()} sqft
            </span>
          )}
        </div>

        {/* Actions */}
        {showActions && (
          <div className="flex gap-2 pt-4 border-t">
            <Link 
              to={`/properties/${property.propertyId}`}
              className="flex-1"
            >
              <Button variant="outline" size="sm" className="w-full">
                View
              </Button>
            </Link>
            <Button 
              variant="outline" 
              size="sm"
              onClick={() => onToggleStatus(property.propertyId, property.status)}
            >
              {property.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
            </Button>
            <Button 
              variant="outline" 
              size="sm"
              onClick={() => onDelete(property.propertyId)}
              className="text-red-600 hover:text-red-700"
            >
              Delete
            </Button>
          </div>
        )}
      </div>
    </Card>
  );
};

export default UserDashboard;