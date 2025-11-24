-- Rollback: Remove image columns from properties table
-- Date: 2024
-- Description: Removes the image columns added for photo storage

-- Connect to the database
\c realestate_db;

-- Drop the index first
DROP INDEX IF EXISTS idx_properties_image_name;

-- Remove the image columns
ALTER TABLE properties 
DROP COLUMN IF EXISTS image_name,
DROP COLUMN IF EXISTS image_type,
DROP COLUMN IF EXISTS image_data;

-- Verify the columns are removed
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'properties' 
AND column_name IN ('image_name', 'image_type', 'image_data')
ORDER BY column_name;
