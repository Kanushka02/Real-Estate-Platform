// Sri Lanka Districts
export const DISTRICTS = [
  'Colombo',
  'Gampaha',
  'Kalutara',
  'Kandy',
  'Matale',
  'Nuwara Eliya',
  'Galle',
  'Matara',
  'Hambantota',
  'Jaffna',
  'Kilinochchi',
  'Mannar',
  'Vavuniya',
  'Mullaitivu',
  'Batticaloa',
  'Ampara',
  'Trincomalee',
  'Kurunegala',
  'Puttalam',
  'Anuradhapura',
  'Polonnaruwa',
  'Badulla',
  'Monaragala',
  'Ratnapura',
  'Kegalle',
];

// Major Cities by District
export const CITIES = {
  Colombo: ['Colombo', 'Dehiwala', 'Moratuwa', 'Kotte', 'Maharagama', 'Nugegoda', 'Battaramulla'],
  Gampaha: ['Gampaha', 'Negombo', 'Katunayake', 'Ja-Ela', 'Wattala', 'Kelaniya'],
  Kalutara: ['Kalutara', 'Panadura', 'Horana', 'Beruwala', 'Wadduwa'],
  Kandy: ['Kandy', 'Peradeniya', 'Gampola', 'Katugastota'],
  Galle: ['Galle', 'Hikkaduwa', 'Ambalangoda', 'Bentota'],
  Matara: ['Matara', 'Weligama', 'Mirissa'],
  // Add more cities as needed
};

export const PROPERTY_TYPES = [
  'HOUSE',
  'APARTMENT',
  'LAND',
  'COMMERCIAL',
  'VILLA',
  'CONDO',
];

export const LISTING_TYPES = [
  'SALE',
  'RENT',
];

export const PROPERTY_STATUS = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
  SOLD: 'SOLD',
  RENTED: 'RENTED',
};

export const STATUS_COLORS = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  APPROVED: 'bg-green-100 text-green-800',
  REJECTED: 'bg-red-100 text-red-800',
  SOLD: 'bg-blue-100 text-blue-800',
  RENTED: 'bg-purple-100 text-purple-800',
};

export const PRICE_RANGES = [
  { label: 'Under 5M', min: 0, max: 5000000 },
  { label: '5M - 10M', min: 5000000, max: 10000000 },
  { label: '10M - 20M', min: 10000000, max: 20000000 },
  { label: '20M - 50M', min: 20000000, max: 50000000 },
  { label: 'Above 50M', min: 50000000, max: null },
];

export const formatPrice = (price) => {
  if (price >= 10000000) {
    return `Rs. ${(price / 10000000).toFixed(1)}Cr`;
  } else if (price >= 100000) {
    return `Rs. ${(price / 100000).toFixed(1)}L`;
  } else {
    return `Rs. ${price.toLocaleString()}`;
  }
};

