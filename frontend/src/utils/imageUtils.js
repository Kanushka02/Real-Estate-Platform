/**
 * Utility functions for handling image data in the real estate application
 */

/**
 * Get image source for display
 * UPDATED: Prioritizes the new URL-based list (Multiple Photos), then falls back to legacy data
 * @param {Object} property - Property object from API
 * @returns {string} Image source URL
 */
export const getImageSource = (property) => {
  if (!property) return 'https://placehold.co/400x300?text=No+Image';

  // 1. CHECK FOR NEW SYSTEM: List of URLs
  if (property.imageUrls && Array.isArray(property.imageUrls) && property.imageUrls.length > 0) {
    // Filter out empty strings and return first valid URL
    const validUrl = property.imageUrls.find(url => url && url.trim() !== '');
    if (validUrl) {
      return validUrl;
    }
  }

  // 2. CHECK FOR LEGACY SYSTEM: Byte Array
  if (property.imageData) {
    return byteArrayToDataURL(property.imageData, property.imageType);
  }

  // 3. CHECK FOR LEGACY SYSTEM: Strings
  if (property.image) return property.image;
  if (property.photo) return property.photo;

  // 4. Fallback (Updated to a working service)
  return 'https://placehold.co/400x300?text=No+Image'; 
};

/**
 * Get all images for a property (for gallery/carousel)
 * @param {Object} property - Property object from API
 * @returns {Array} Array of valid image URLs
 */
export const getAllPropertyImages = (property) => {
  if (!property) return [];

  // Return imageUrls if available
  if (property.imageUrls && Array.isArray(property.imageUrls)) {
    return property.imageUrls.filter(url => url && url.trim() !== '');
  }

  return [];
};

/**
 * Convert byte array to base64 data URL for display
 * @param {Array} imageData - Byte array of image data
 * @param {string} mimeType - MIME type of the image (e.g., 'image/jpeg')
 * @returns {string} Base64 data URL
 */
export const byteArrayToDataURL = (imageData, mimeType = 'image/jpeg') => {
  if (!imageData || !Array.isArray(imageData)) {
    return null;
  }
  
  try {
    const base64String = btoa(String.fromCharCode(...new Uint8Array(imageData)));
    return `data:${mimeType};base64,${base64String}`;
  } catch (error) {
    console.error('Error converting byte array to data URL:', error);
    return null;
  }
};

/**
 * Validate image file type
 * @param {File} file - File to validate
 * @returns {boolean} True if valid image type
 */
export const isValidImageType = (file) => {
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
  return validTypes.includes(file.type);
};

/**
 * Get file size in MB
 * @param {File} file - File object
 * @returns {number} File size in MB
 */
export const getFileSizeMB = (file) => {
  return (file.size / (1024 * 1024)).toFixed(2);
};

/**
 * Validate image file size (max 5MB)
 * @param {File} file - File to validate
 * @returns {boolean} True if file size is valid
 */
export const isValidImageSize = (file) => {
  const maxSizeMB = 5;
  return file.size <= maxSizeMB * 1024 * 1024;
};

// Note: fileToByteArray is removed or deprecated as we now upload files directly via FormData
export const fileToByteArray = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const arrayBuffer = e.target.result;
      const byteArray = new Uint8Array(arrayBuffer);
      resolve(Array.from(byteArray));
    };
    reader.onerror = reject;
    reader.readAsArrayBuffer(file);
  });
};