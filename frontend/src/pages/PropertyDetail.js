import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { propertiesAPI, handleAPIError, bookingsAPI, reviewsAPI, propertyImagesAPI } from '../services/api';
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
  const [showBooking, setShowBooking] = useState(false);
  const [bookingDate, setBookingDate] = useState('');
  const [bookingTime, setBookingTime] = useState('');
  const [bookingMessage, setBookingMessage] = useState('');
  const [bookingSubmitting, setBookingSubmitting] = useState(false);
  const [bookingError, setBookingError] = useState('');
  const [bookingSuccess, setBookingSuccess] = useState('');

  // Reviews state
  const [reviews, setReviews] = useState([]);
  const [averageRating, setAverageRating] = useState(0);
  const [reviewCount, setReviewCount] = useState(0);
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitting, setReviewSubmitting] = useState(false);
  const [userReview, setUserReview] = useState(null);
  const [canReview, setCanReview] = useState(false);

  // Images state
  const [propertyImages, setPropertyImages] = useState([]);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    loadProperty();
  }, [id]);

  const loadProperty = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await propertiesAPI.getById(id);
      setProperty(response.data);

      // Load reviews data
      await loadReviews();

      // Load property images
      await loadPropertyImages();
    } catch (error) {
      setError(handleAPIError(error));
    } finally {
      setLoading(false);
    }
  };

  const loadReviews = async () => {
    try {
      const [reviewsResponse, averageRatingResponse, reviewCountResponse] = await Promise.all([
        reviewsAPI.getForProperty(id),
        reviewsAPI.getAverageRating(id),
        reviewsAPI.getReviewCount(id)
      ]);

      setReviews(reviewsResponse.data || []);
      setAverageRating(averageRatingResponse.data || 0);
      setReviewCount(reviewCountResponse.data || 0);

      // Check if user can review and get their existing review
      if (isAuthenticated) {
        try {
          const [canReviewResponse, userReviewResponse] = await Promise.all([
            reviewsAPI.canReview(id),
            reviewsAPI.getMyReviewForProperty(id).catch(() => null)
          ]);

          setCanReview(canReviewResponse.data);
          if (userReviewResponse && userReviewResponse.data) {
            setUserReview(userReviewResponse.data);
            setReviewRating(userReviewResponse.data.rating);
            setReviewComment(userReviewResponse.data.comment || '');
          }
        } catch (error) {
          // User might not be able to review or get review
          setCanReview(false);
          setUserReview(null);
        }
      }
    } catch (error) {
      console.error('Error loading reviews:', error);
    }
  };

  const loadPropertyImages = async () => {
    try {
      const imagesResponse = await propertyImagesAPI.getForProperty(id);
      setPropertyImages(imagesResponse.data || []);
      setCurrentImageIndex(0);
    } catch (error) {
      console.error('Error loading property images:', error);
      setPropertyImages([]);
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

  const handleScheduleVisit = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/properties/${id}` } });
      return;
    }
    setShowBooking(true);
  };

  const submitBooking = async () => {
    try {
      setBookingSubmitting(true);
      setBookingError('');
      setBookingSuccess('');
      if (!bookingDate || !bookingTime) {
        setBookingError('Please select date and time');
        return;
      }
      const scheduledAt = new Date(`${bookingDate}T${bookingTime}:00`);
      const payload = {
        propertyId: Number(id),
        scheduledAt: scheduledAt.toISOString().slice(0,19),
        message: bookingMessage
      };
      const res = await bookingsAPI.create(payload);
      setBookingSuccess('Visit scheduled successfully');
      // reset form
      setBookingDate('');
      setBookingTime('');
      setBookingMessage('');
      // optionally close after short delay
      setTimeout(() => setShowBooking(false), 800);
    } catch (error) {
      setBookingError(handleAPIError(error));
    } finally {
      setBookingSubmitting(false);
    }
  };

  const submitReview = async () => {
    try {
      setReviewSubmitting(true);
      const reviewData = {
        rating: reviewRating,
        comment: reviewComment.trim()
      };

      if (userReview) {
        // Update existing review
        await reviewsAPI.update(userReview.reviewId, reviewData);
      } else {
        // Create new review
        await reviewsAPI.create(id, reviewData);
      }

      // Reload reviews
      await loadReviews();
      setShowReviewForm(false);
      setReviewComment('');
      setReviewRating(5);
    } catch (error) {
      console.error('Error submitting review:', error);
      alert('Failed to submit review. Please try again.');
    } finally {
      setReviewSubmitting(false);
    }
  };

  const renderStars = (rating, interactive = false, onRatingChange = null) => {
    return (
      <div className="flex items-center">
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type="button"
            className={`text-2xl ${interactive ? 'cursor-pointer hover:scale-110 transition-transform' : 'cursor-default'} ${
              star <= rating ? 'text-yellow-400' : 'text-gray-300'
            }`}
            onClick={interactive ? () => onRatingChange && onRatingChange(star) : undefined}
            disabled={!interactive}
          >
            ★
          </button>
        ))}
      </div>
    );
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
              {propertyImages.length > 0 ? (
                <div className="relative">
                  <div className="h-96 bg-gray-100 flex items-center justify-center overflow-hidden">
                    <img
                      src={`http://localhost:8082${propertyImages[currentImageIndex]?.imageUrl}`}
                      alt={`${property?.title} - Image ${currentImageIndex + 1}`}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = 'https://via.placeholder.com/800x400?text=Image+Not+Found';
                      }}
                    />
                  </div>
                  {propertyImages.length > 1 && (
                    <>
                      {/* Navigation arrows */}
                      <button
                        onClick={() => setCurrentImageIndex(prev =>
                          prev > 0 ? prev - 1 : propertyImages.length - 1
                        )}
                        className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all"
                      >
                        ‹
                      </button>
                      <button
                        onClick={() => setCurrentImageIndex(prev =>
                          prev < propertyImages.length - 1 ? prev + 1 : 0
                        )}
                        className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-black bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all"
                      >
                        ›
                      </button>

                      {/* Image indicators */}
                      <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex space-x-2">
                        {propertyImages.map((_, index) => (
                          <button
                            key={index}
                            onClick={() => setCurrentImageIndex(index)}
                            className={`w-2 h-2 rounded-full transition-all ${
                              index === currentImageIndex ? 'bg-white' : 'bg-white bg-opacity-50'
                            }`}
                          />
                        ))}
                      </div>
                    </>
                  )}
                </div>
              ) : (
                <div className="h-96 bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center">
                  <div className="text-white text-8xl">🏠</div>
                </div>
              )}
              <div className="p-4">
                <p className="text-sm text-gray-600">
                  {propertyImages.length > 0
                    ? `${propertyImages.length} photo${propertyImages.length > 1 ? 's' : ''} available`
                    : 'No photos uploaded yet'
                  }
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
            <Card className="p-6 mb-6">
              <h2 className="text-xl font-semibold mb-4">Location</h2>
              <div className="h-64 bg-gray-100 rounded-lg flex items-center justify-center">
                <div className="text-center text-gray-500">
                  <div className="text-4xl mb-2">🗺️</div>
                  <p>Interactive map coming soon</p>
                  <p className="text-sm">{property.location.city}, {property.location.district}</p>
                </div>
              </div>
            </Card>

            {/* Reviews Section */}
            <Card className="p-6">
              <div className="flex justify-between items-center mb-6">
                <div>
                  <h2 className="text-xl font-semibold">Reviews & Ratings</h2>
                  <div className="flex items-center mt-2">
                    {renderStars(Math.round(averageRating))}
                    <span className="ml-2 text-lg font-medium">{averageRating.toFixed(1)}</span>
                    <span className="ml-2 text-gray-600">({reviewCount} reviews)</span>
                  </div>
                </div>
                {isAuthenticated && (
                  <Button
                    onClick={() => setShowReviewForm(true)}
                    variant={userReview ? "secondary" : "primary"}
                  >
                    {userReview ? '✏️ Edit Review' : '⭐ Write Review'}
                  </Button>
                )}
              </div>

              {/* Reviews List */}
              {reviews.length > 0 ? (
                <div className="space-y-4">
                  {reviews.map((review) => (
                    <div key={review.reviewId} className="border-b border-gray-200 pb-4 last:border-b-0">
                      <div className="flex justify-between items-start mb-2">
                        <div className="flex items-center">
                          <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                            <span className="text-blue-600 text-sm">👤</span>
                          </div>
                          <div>
                            <p className="font-medium">
                              {review.user.firstName} {review.user.lastName}
                            </p>
                            <p className="text-sm text-gray-500">
                              {new Date(review.createdAt).toLocaleDateString()}
                            </p>
                          </div>
                        </div>
                        {renderStars(review.rating)}
                      </div>
                      {review.comment && (
                        <p className="text-gray-700 ml-11">{review.comment}</p>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <div className="text-4xl mb-2">⭐</div>
                  <p>No reviews yet. Be the first to review this property!</p>
                </div>
              )}
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
                  className="w-full text-lg py-3"
                  variant="secondary"
                  onClick={handleScheduleVisit}
                >
                  📅 Schedule a Visit
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

      {/* Booking Modal */}
      {showBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="p-6 max-w-md w-full mx-4">
            <h3 className="text-xl font-semibold mb-4">Schedule a Visit</h3>
            <div className="space-y-4">
              {bookingError && (
                <div className="text-red-600 text-sm">{bookingError}</div>
              )}
              {bookingSuccess && (
                <div className="text-green-600 text-sm">{bookingSuccess}</div>
              )}
              <div>
                <label className="block text-sm text-gray-700 mb-1">Date</label>
                <input
                  type="date"
                  className="w-full border rounded px-3 py-2"
                  value={bookingDate}
                  onChange={(e) => setBookingDate(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-sm text-gray-700 mb-1">Time</label>
                <input
                  type="time"
                  className="w-full border rounded px-3 py-2"
                  value={bookingTime}
                  onChange={(e) => setBookingTime(e.target.value)}
                />
              </div>
              <div>
                <label className="block text-sm text-gray-700 mb-1">Message (optional)</label>
                <textarea
                  className="w-full border rounded px-3 py-2"
                  rows="3"
                  value={bookingMessage}
                  onChange={(e) => setBookingMessage(e.target.value)}
                  placeholder="Any notes for the seller"
                />
              </div>
              <div className="flex gap-2">
                <Button onClick={submitBooking} className="flex-1" disabled={bookingSubmitting}>
                  {bookingSubmitting ? 'Scheduling...' : 'Schedule'}
                </Button>
                <Button variant="outline" onClick={() => setShowBooking(false)}>
                  Cancel
                </Button>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* Review Form Modal */}
      {showReviewForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <Card className="p-6 max-w-md w-full mx-4">
            <h3 className="text-xl font-semibold mb-4">
              {userReview ? 'Edit Your Review' : 'Write a Review'}
            </h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm text-gray-700 mb-2">Rating</label>
                <div className="flex items-center">
                  {renderStars(reviewRating, true, setReviewRating)}
                  <span className="ml-2 text-sm text-gray-600">({reviewRating} stars)</span>
                </div>
              </div>
              <div>
                <label className="block text-sm text-gray-700 mb-2">Comment (optional)</label>
                <textarea
                  className="w-full border rounded px-3 py-2"
                  rows="4"
                  value={reviewComment}
                  onChange={(e) => setReviewComment(e.target.value)}
                  placeholder="Share your experience with this property..."
                  maxLength="500"
                />
                <p className="text-xs text-gray-500 mt-1">
                  {reviewComment.length}/500 characters
                </p>
              </div>
              <div className="flex gap-2">
                <Button
                  onClick={submitReview}
                  className="flex-1"
                  disabled={reviewSubmitting}
                >
                  {reviewSubmitting ? 'Submitting...' : (userReview ? 'Update Review' : 'Submit Review')}
                </Button>
                <Button
                  variant="outline"
                  onClick={() => setShowReviewForm(false)}
                >
                  Cancel
                </Button>
              </div>
            </div>
          </Card>
        </div>
      )}
    </div>
  );
};

export default PropertyDetail;