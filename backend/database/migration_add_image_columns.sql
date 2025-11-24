-- Migration: Add image columns to properties table
-- Date: 2024
-- Description: Adds support for storing image data directly in the properties table

-- Connect to the database
\c realestate_db;

-- Add new image columns to properties table
ALTER TABLE properties 
ADD COLUMN IF NOT EXISTS image_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS image_type VARCHAR(100),
ADD COLUMN IF NOT EXISTS image_data BYTEA;

-- Add comments for documentation
COMMENT ON COLUMN properties.image_name IS 'Original filename of the uploaded image';
COMMENT ON COLUMN properties.image_type IS 'MIME type of the image (e.g., image/jpeg, image/png)';
COMMENT ON COLUMN properties.image_data IS 'Binary data of the image stored as BYTEA';

-- Create index on image_name for faster queries (optional)
CREATE INDEX IF NOT EXISTS idx_properties_image_name ON properties(image_name);

-- Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'properties' 
AND column_name IN ('image_name', 'image_type', 'image_data')
ORDER BY column_name;
