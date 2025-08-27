import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsProfileMenuOpen(false);
    setIsMobileMenuOpen(false);
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  const NavLink = ({ to, children, className = "", onClick }) => (
    <Link
      to={to}
      onClick={onClick}
      className={`px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200 ${
        isActive(to)
          ? 'bg-primary-100 text-primary-700'
          : 'text-gray-700 hover:text-primary-600 hover:bg-gray-100'
      } ${className}`}
    >
      {children}
    </Link>
  );

  return (
    <nav className="bg-white shadow-sm sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
                <span className="text-white font-bold text-sm">P</span>
              </div>
              <span className="text-xl font-bold text-gray-900">PropertyLK</span>
            </Link>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-1">
            <NavLink to="/">Home</NavLink>
            <NavLink to="/properties">Properties</NavLink>
            <NavLink to="/search">Search</NavLink>
            
            {isAuthenticated && (
              <>
                {(user?.role === 'SELLER' || user?.role === 'ADMIN') && (
                  <>
                    <NavLink to="/seller/properties">My Properties</NavLink>
                    <NavLink to="/seller/properties/create">Add Property</NavLink>
                  </>
                )}
                
                {user?.role === 'ADMIN' && (
                  <NavLink to="/admin/dashboard">Admin</NavLink>
                )}
              </>
            )}
          </div>

          {/* Auth Section */}
          <div className="hidden md:flex items-center space-x-4">
            {isAuthenticated ? (
              <div className="relative">
                <button
                  onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                  className="flex items-center space-x-2 text-gray-700 hover:text-primary-600 focus:outline-none"
                >
                  <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                    <span className="text-primary-700 font-medium text-sm">
                      {user?.firstName?.charAt(0) || user?.email?.charAt(0) || 'U'}
                    </span>
                  </div>
                  <span className="text-sm font-medium">
                    {user?.firstName || user?.email?.split('@')[0] || 'User'}
                  </span>
                  <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </button>

                {/* Profile Dropdown */}
                {isProfileMenuOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50">
                    <div className="py-1">
                      <Link
                        to="/profile"
                        onClick={() => setIsProfileMenuOpen(false)}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Profile
                      </Link>
                      <Link
                        to="/dashboard"
                        onClick={() => setIsProfileMenuOpen(false)}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Dashboard
                      </Link>
                      <hr className="my-1" />
                      <button
                        onClick={handleLogout}
                        className="w-full text-left block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      >
                        Logout
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex items-center space-x-3">
                <NavLink to="/login">Login</NavLink>
                <Link
                  to="/register"
                  className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors duration-200"
                >
                  Sign Up
                </Link>
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
              className="text-gray-700 hover:text-primary-600 focus:outline-none focus:ring-2 focus:ring-primary-500 p-2"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {isMobileMenuOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Navigation */}
        {isMobileMenuOpen && (
          <div className="md:hidden border-t border-gray-200">
            <div className="px-2 pt-2 pb-3 space-y-1">
              <NavLink to="/" onClick={() => setIsMobileMenuOpen(false)} className="block">
                Home
              </NavLink>
              <NavLink to="/properties" onClick={() => setIsMobileMenuOpen(false)} className="block">
                Properties
              </NavLink>
              <NavLink to="/search" onClick={() => setIsMobileMenuOpen(false)} className="block">
                Search
              </NavLink>
              
              {isAuthenticated && (
                <>
                  {(user?.role === 'SELLER' || user?.role === 'ADMIN') && (
                    <>
                      <NavLink to="/seller/properties" onClick={() => setIsMobileMenuOpen(false)} className="block">
                        My Properties
                      </NavLink>
                      <NavLink to="/seller/properties/create" onClick={() => setIsMobileMenuOpen(false)} className="block">
                        Add Property
                      </NavLink>
                    </>
                  )}
                  
                  {user?.role === 'ADMIN' && (
                    <NavLink to="/admin/dashboard" onClick={() => setIsMobileMenuOpen(false)} className="block">
                      Admin Dashboard
                    </NavLink>
                  )}
                  
                  <NavLink to="/profile" onClick={() => setIsMobileMenuOpen(false)} className="block">
                    Profile
                  </NavLink>
                  <NavLink to="/dashboard" onClick={() => setIsMobileMenuOpen(false)} className="block">
                    Dashboard
                  </NavLink>
                  
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-3 py-2 rounded-md text-sm font-medium text-gray-700 hover:text-primary-600 hover:bg-gray-100"
                  >
                    Logout
                  </button>
                </>
              )}
              
              {!isAuthenticated && (
                <>
                  <NavLink to="/login" onClick={() => setIsMobileMenuOpen(false)} className="block">
                    Login
                  </NavLink>
                  <NavLink to="/register" onClick={() => setIsMobileMenuOpen(false)} className="block">
                    Sign Up
                  </NavLink>
                </>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Backdrop for dropdowns */}
      {(isProfileMenuOpen || isMobileMenuOpen) && (
        <div 
          className="fixed inset-0 z-40" 
          onClick={() => {
            setIsProfileMenuOpen(false);
            setIsMobileMenuOpen(false);
          }}
        />
      )}
    </nav>
  );
};

export default Navbar;