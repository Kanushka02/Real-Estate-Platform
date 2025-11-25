import React from 'react';
import { Home, Mail, Phone, MapPin, Facebook, Twitter, Instagram, Linkedin } from 'lucide-react';

const Footer = () => {
  return (
    <footer className="bg-gray-900 text-white mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 lg:gap-12">
          {/* Brand Section */}
          <div className="lg:col-span-1">
            <a href="/" className="flex items-center gap-2.5 mb-4 group">
              <div className="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center group-hover:bg-blue-700 transition-colors">
                <Home className="w-5 h-5 text-white" />
              </div>
              <span className="text-xl font-semibold">RealEstate</span>
            </a>
            <p className="text-gray-400 text-sm leading-relaxed mb-6">
              Your trusted platform for finding and listing properties in Sri Lanka. Making real estate transactions simple and secure.
            </p>

            {/* Social Links */}
            <div className="flex items-center gap-3">
              <a href="#" className="w-9 h-9 flex items-center justify-center bg-gray-800 hover:bg-blue-600 rounded-lg transition-colors" aria-label="Facebook">
                <Facebook className="w-4 h-4" />
              </a>
              <a href="#" className="w-9 h-9 flex items-center justify-center bg-gray-800 hover:bg-blue-600 rounded-lg transition-colors" aria-label="Twitter">
                <Twitter className="w-4 h-4" />
              </a>
              <a href="#" className="w-9 h-9 flex items-center justify-center bg-gray-800 hover:bg-blue-600 rounded-lg transition-colors" aria-label="Instagram">
                <Instagram className="w-4 h-4" />
              </a>
              <a href="#" className="w-9 h-9 flex items-center justify-center bg-gray-800 hover:bg-blue-600 rounded-lg transition-colors" aria-label="LinkedIn">
                <Linkedin className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-base font-semibold mb-4 text-white">Quick Links</h4>
            <ul className="space-y-3">
              <li>
                <a href="/" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Home</span>
                </a>
              </li>
              <li>
                <a href="/properties" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Properties</span>
                </a>
              </li>
              <li>
                <a href="/about" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">About Us</span>
                </a>
              </li>
              <li>
                <a href="/contact" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Contact</span>
                </a>
              </li>
            </ul>
          </div>

          {/* Property Types */}
          <div>
            <h4 className="text-base font-semibold mb-4 text-white">Property Types</h4>
            <ul className="space-y-3">
              <li>
                <a href="/properties?type=house" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Houses</span>
                </a>
              </li>
              <li>
                <a href="/properties?type=apartment" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Apartments</span>
                </a>
              </li>
              <li>
                <a href="/properties?type=land" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Land</span>
                </a>
              </li>
              <li>
                <a href="/properties?type=commercial" className="text-gray-400 hover:text-white transition-colors text-sm inline-flex items-center group">
                  <span className="group-hover:translate-x-1 transition-transform">Commercial</span>
                </a>
              </li>
            </ul>
          </div>

          {/* Contact Info */}
          <div>
            <h4 className="text-base font-semibold mb-4 text-white">Contact Us</h4>
            <div className="space-y-3">
              <a href="mailto:info@realestate.lk" className="flex items-start gap-3 text-gray-400 hover:text-white transition-colors text-sm group">
                <Mail className="w-5 h-5 mt-0.5 flex-shrink-0 text-blue-500" />
                <span className="group-hover:translate-x-1 transition-transform">info@realestate.lk</span>
              </a>
              <a href="tel:+94112345678" className="flex items-start gap-3 text-gray-400 hover:text-white transition-colors text-sm group">
                <Phone className="w-5 h-5 mt-0.5 flex-shrink-0 text-blue-500" />
                <span className="group-hover:translate-x-1 transition-transform">+94 11 234 5678</span>
              </a>
              <div className="flex items-start gap-3 text-gray-400 text-sm">
                <MapPin className="w-5 h-5 mt-0.5 flex-shrink-0 text-blue-500" />
                <span>Colombo, Sri Lanka</span>
              </div>
            </div>
          </div>
        </div>

        {/* Bottom Section */}
        <div className="border-t border-gray-800 mt-12 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-gray-400 text-sm text-center md:text-left">
              © 2025 RealEstate Platform. All rights reserved.
            </p>
            <div className="flex flex-wrap justify-center gap-6">
              <a href="/privacy" className="text-gray-400 hover:text-white text-sm transition-colors">
                Privacy Policy
              </a>
              <a href="/terms" className="text-gray-400 hover:text-white text-sm transition-colors">
                Terms of Service
              </a>
              <a href="/sitemap" className="text-gray-400 hover:text-white text-sm transition-colors">
                Sitemap
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;