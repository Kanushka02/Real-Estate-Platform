import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { adminAPI, handleAPIError } from '../services/api';
import { Card, Button, Badge, LoadingSpinner, Alert, Input } from '../components/common';

const AdminDashboard = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');
  const [dashboardStats, setDashboardStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated || user?.role !== 'ADMIN') {
      navigate('/');
      return;
    }
    loadDashboardData();
  }, [isAuthenticated, user, navigate]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError('');
      
      if (activeTab === 'overview') {
        const statsResponse = await adminAPI.getDashboardStats();
        setDashboardStats(statsResponse.data);
      } else if (activeTab === 'users') {
        const usersResponse = await adminAPI.getAllUsers();
        setUsers(usersResponse.data);
      } else if (activeTab === 'properties') {
        const propertiesResponse = await adminAPI.getAllProperties();
        setProperties(propertiesResponse.data);
      }
      
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const handleToggleUserStatus = async (userId, currentStatus) => {
    try {
      await adminAPI.toggleUserStatus(userId);
      setUsers(prev => 
        prev.map(u => 
          u.userId === userId 
            ? { ...u, isActive: !currentStatus }
            : u
        )
      );
      alert(`User ${!currentStatus ? 'activated' : 'deactivated'} successfully`);
    } catch (error) {
      alert('Failed to update user status: ' + handleAPIError(error));
    }
  };

  const handleChangePropertyStatus = async (propertyId, newStatus) => {
    try {
      await adminAPI.changePropertyStatus(propertyId, newStatus);
      setProperties(prev => 
        prev.map(p => 
          p.propertyId === propertyId 
            ? { ...p, status: newStatus }
            : p
        )
      );
      alert(`Property status changed to ${newStatus}`);
    } catch (error) {
      alert('Failed to update property status: ' + handleAPIError(error));
    }
  };

  const handleDeleteProperty = async (propertyId) => {
    if (!window.confirm('Are you sure you want to delete this property? This action cannot be undone.')) {
      return;
    }

    try {
      await adminAPI.deleteProperty(propertyId);
      setProperties(prev => prev.filter(p => p.propertyId !== propertyId));
      alert('Property deleted successfully');
    } catch (error) {
      alert('Failed to delete property: ' + handleAPIError(error));
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-6">
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-600 mt-1">Manage users, properties, and platform analytics</p>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Navigation */}
          <div className="lg:w-1/4">
            <Card className="p-6">
              <nav className="space-y-2">
                <button
                  onClick={() => {
                    setActiveTab('overview');
                    setLoading(true);
                    loadDashboardData();
                  }}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'overview'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Overview & Stats
                </button>

                <button
                  onClick={() => {
                    setActiveTab('users');
                    setLoading(true);
                    loadDashboardData();
                  }}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'users'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  User Management
                </button>

                <button
                  onClick={() => {
                    setActiveTab('properties');
                    setLoading(true);
                    loadDashboardData();
                  }}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'properties'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Property Management
                </button>

                <button
                  onClick={() => setActiveTab('settings')}
                  className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                    activeTab === 'settings'
                      ? 'bg-blue-50 text-blue-600 border border-blue-200'
                      : 'hover:bg-gray-50'
                  }`}
                >
                  Platform Settings
                </button>
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

            {loading ? (
              <div className="flex justify-center py-12">
                <LoadingSpinner size="xl" />
              </div>
            ) : (
              <>
                {/* Overview Tab */}
                {activeTab === 'overview' && dashboardStats && (
                  <div className="space-y-6">
                    {/* Stats Grid */}
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                      <Card className="p-6 text-center">
                        <div className="text-3xl text-blue-600 mb-2">👥</div>
                        <p className="text-3xl font-bold text-gray-900">
                          {dashboardStats.users.total}
                        </p>
                        <p className="text-gray-600">Total Users</p>
                        <p className="text-sm text-green-600 mt-1">
                          {dashboardStats.users.active} active
                        </p>
                      </Card>
                      
                      <Card className="p-6 text-center">
                        <div className="text-3xl text-green-600 mb-2">🏠</div>
                        <p className="text-3xl font-bold text-gray-900">
                          {dashboardStats.properties.total}
                        </p>
                        <p className="text-gray-600">Total Properties</p>
                        <p className="text-sm text-green-600 mt-1">
                          {dashboardStats.properties.active} active
                        </p>
                      </Card>

                      <Card className="p-6 text-center">
                        <div className="text-3xl text-orange-600 mb-2">🏪</div>
                        <p className="text-3xl font-bold text-gray-900">
                          {dashboardStats.users.sellers}
                        </p>
                        <p className="text-gray-600">Sellers</p>
                      </Card>

                      <Card className="p-6 text-center">
                        <div className="text-3xl text-purple-600 mb-2">👨‍💼</div>
                        <p className="text-3xl font-bold text-gray-900">
                          {dashboardStats.users.buyers}
                        </p>
                        <p className="text-gray-600">Buyers</p>
                      </Card>
                    </div>

                    {/* Property Type Breakdown */}
                    <Card className="p-6">
                      <h2 className="text-xl font-semibold mb-4">Property Statistics</h2>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="text-center">
                          <div className="text-4xl text-green-600 mb-2">💰</div>
                          <p className="text-2xl font-bold">{dashboardStats.properties.forSale}</p>
                          <p className="text-gray-600">For Sale</p>
                        </div>
                        <div className="text-center">
                          <div className="text-4xl text-blue-600 mb-2">🏠</div>
                          <p className="text-2xl font-bold">{dashboardStats.properties.forRent}</p>
                          <p className="text-gray-600">For Rent</p>
                        </div>
                      </div>
                    </Card>

                    {/* Quick Actions */}
                    <Card className="p-6">
                      <h2 className="text-xl font-semibold mb-4">Quick Actions</h2>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <Button 
                          onClick={() => {
                            setActiveTab('users');
                            loadDashboardData();
                          }}
                          variant="outline"
                          className="w-full"
                        >
                          Manage Users
                        </Button>
                        <Button 
                          onClick={() => {
                            setActiveTab('properties');
                            loadDashboardData();
                          }}
                          variant="outline"
                          className="w-full"
                        >
                          Manage Properties
                        </Button>
                      </div>
                    </Card>
                  </div>
                )}

                {/* Users Management Tab */}
                {activeTab === 'users' && (
                  <div className="space-y-6">
                    <div className="flex justify-between items-center">
                      <h2 className="text-2xl font-semibold">User Management</h2>
                      <div className="flex gap-2">
                        <Input
                          type="text"
                          placeholder="Search users..."
                          className="w-64"
                        />
                        <Button variant="outline">Search</Button>
                      </div>
                    </div>

                    <Card className="overflow-hidden">
                      <div className="overflow-x-auto">
                        <table className="w-full">
                          <thead className="bg-gray-50">
                            <tr>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                User
                              </th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Role
                              </th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Status
                              </th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Joined
                              </th>
                              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Actions
                              </th>
                            </tr>
                          </thead>
                          <tbody className="bg-white divide-y divide-gray-200">
                            {users.map(user => (
                              <tr key={user.userId} className="hover:bg-gray-50">
                                <td className="px-6 py-4 whitespace-nowrap">
                                  <div className="flex items-center">
                                    <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                      <span className="text-blue-600">👤</span>
                                    </div>
                                    <div>
                                      <p className="font-medium text-gray-900">
                                        {user.firstName} {user.lastName}
                                      </p>
                                      <p className="text-sm text-gray-500">
                                        {user.email}
                                      </p>
                                    </div>
                                  </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                  <Badge 
                                    variant={
                                      user.role === 'ADMIN' ? 'warning' :
                                      user.role === 'SELLER' ? 'info' : 'secondary'
                                    }
                                  >
                                    {user.role}
                                  </Badge>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                  <Badge 
                                    variant={user.isActive ? 'success' : 'danger'}
                                  >
                                    {user.isActive ? 'Active' : 'Inactive'}
                                  </Badge>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                  {new Date(user.createdAt).toLocaleDateString()}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm">
                                  {user.role !== 'ADMIN' && (
                                    <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => handleToggleUserStatus(user.userId, user.isActive)}
                                    >
                                      {user.isActive ? 'Deactivate' : 'Activate'}
                                    </Button>
                                  )}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </Card>
                  </div>
                )}

                {/* Properties Management Tab */}
                {activeTab === 'properties' && (
                  <div className="space-y-6">
                    <div className="flex justify-between items-center">
                      <h2 className="text-2xl font-semibold">Property Management</h2>
                      <div className="flex gap-2">
                        <select 
                          className="input-field w-40"
                          onChange={(e) => {
                            if (e.target.value === 'all') {
                              loadDashboardData();
                            }
                          }}
                        >
                          <option value="all">All Properties</option>
                          <option value="ACTIVE">Active Only</option>
                          <option value="INACTIVE">Inactive Only</option>
                        </select>
                      </div>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                      {properties.map(property => (
                        <PropertyAdminCard
                          key={property.propertyId}
                          property={property}
                          onChangeStatus={handleChangePropertyStatus}
                          onDelete={handleDeleteProperty}
                        />
                      ))}
                    </div>

                    {properties.length === 0 && (
                      <Card className="p-12 text-center">
                        <div className="text-gray-400 text-6xl mb-4">🏠</div>
                        <h3 className="text-xl font-medium text-gray-600">
                          No Properties Found
                        </h3>
                      </Card>
                    )}
                  </div>
                )}

                {/* Settings Tab */}
                {activeTab === 'settings' && (
                  <div className="space-y-6">
                    <h2 className="text-2xl font-semibold">Platform Settings</h2>
                    
                    <Card className="p-6">
                      <h3 className="text-lg font-semibold mb-4">System Information</h3>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="bg-gray-50 p-4 rounded-lg">
                          <p className="text-sm text-gray-600">Platform Version</p>
                          <p className="font-semibold">PropertyLK v1.0</p>
                        </div>
                        <div className="bg-gray-50 p-4 rounded-lg">
                          <p className="text-sm text-gray-600">Database</p>
                          <p className="font-semibold">PostgreSQL</p>
                        </div>
                        <div className="bg-gray-50 p-4 rounded-lg">
                          <p className="text-sm text-gray-600">Backend</p>
                          <p className="font-semibold">Spring Boot</p>
                        </div>
                        <div className="bg-gray-50 p-4 rounded-lg">
                          <p className="text-sm text-gray-600">Frontend</p>
                          <p className="font-semibold">React.js</p>
                        </div>
                      </div>
                    </Card>

                    <Card className="p-6">
                      <h3 className="text-lg font-semibold mb-4">Admin Actions</h3>
                      <div className="space-y-4">
                        <Button variant="outline" className="w-full justify-start">
                          Export User Data
                        </Button>
                        <Button variant="outline" className="w-full justify-start">
                          Export Property Data
                        </Button>
                        <Button variant="outline" className="w-full justify-start">
                          System Backup
                        </Button>
                      </div>
                    </Card>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Property Card Component for Admin
const PropertyAdminCard = ({ property, onChangeStatus, onDelete }) => {
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
      <div className="h-32 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
        <div className="text-white text-2xl">🏠</div>
      </div>
      
      <div className="p-4">
        <div className="flex justify-between items-start mb-2">
          <Badge 
            variant={property.propertyType === 'SALE' ? 'success' : 'info'}
          >
            {property.propertyType}
          </Badge>
          <Badge 
            variant={property.status === 'ACTIVE' ? 'success' : 'secondary'}
          >
            {property.status}
          </Badge>
        </div>

        <h3 className="font-semibold text-gray-900 mb-2 line-clamp-2">
          {property.title}
        </h3>

        <p className="text-blue-600 font-semibold mb-2">
          {formatPrice(property.price)}
        </p>

        <p className="text-sm text-gray-600 mb-2">
          📍 {property.location.city}, {property.location.district}
        </p>

        <p className="text-sm text-gray-600 mb-3">
          👤 {property.seller.firstName} {property.seller.lastName}
        </p>

        {/* Admin Actions */}
        <div className="space-y-2">
          <div className="flex gap-2">
            <select
              className="input-field text-sm flex-1"
              value={property.status}
              onChange={(e) => onChangeStatus(property.propertyId, e.target.value)}
            >
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="SOLD">Sold</option>
              <option value="RENTED">Rented</option>
            </select>
          </div>
          
          <div className="flex gap-2">
            <Button 
              size="sm" 
              variant="outline" 
              className="flex-1"
              onClick={() => window.open(`/properties/${property.propertyId}`, '_blank')}
            >
              View
            </Button>
            <Button 
              size="sm" 
              variant="outline" 
              onClick={() => onDelete(property.propertyId)}
              className="text-red-600 hover:text-red-700"
            >
              Delete
            </Button>
          </div>
        </div>
      </div>
    </Card>
  );
};

export default AdminDashboard;