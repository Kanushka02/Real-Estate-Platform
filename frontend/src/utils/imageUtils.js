/**
 * Utility functions for handling image data in the real estate application
 */

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
 * Convert file to byte array for upload
 * @param {File} file - File object from input
 * @returns {Promise<Array>} Promise that resolves to byte array
 */
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

/**
 * Get image source for display
 * @param {Object} property - Property object
 * @returns {string} Image source URL
 */
export const getImageSource = (property) => {
  if (!property) {
    return '/no-image.png';
  }

  // If we have image data directly in the property object
  if (property.imageData && property.imageType) {
    return byteArrayToDataURL(property.imageData, property.imageType);
  }

  // If we have an ID, use the image endpoint
  if (property.id) {
    return `${process.env.REACT_APP_API_URL || 'http://localhost:8083/api'}/properties/${property.id}/image`;
  }

  return '/no-image.png';
};

/**
 * Get image source with photo fetching support
 * @param {Object} property - Property object
 * @param {Function} fetchPhoto - Function to fetch photo from API
 * @returns {Promise<string>} Promise that resolves to image source URL
 */
export const getImageSourceWithFetch = async (property, fetchPhoto) => {
  // Handle new image fields first
  if (property.imageData) {
    return byteArrayToDataURL(property.imageData, property.imageType);
  }
  
  // Try to fetch photo from API if property has ID
  if (property.id && fetchPhoto) {
    try {
      const photoBlob = await fetchPhoto(property.id, 1); // Assuming photoId = 1 for now
      return URL.createObjectURL(photoBlob);
    } catch (error) {
      console.warn('Failed to fetch photo from API:', error);
    }
  }
  
  // Fallback to legacy image fields
  if (property.image || property.photo) {
    return property.image || property.photo;
  }
  
  // Default no-image placeholder
  return '/no-image.png';
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
