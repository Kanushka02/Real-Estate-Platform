# Database Migration Guide - Image Support

## Overview
This migration adds support for storing image data directly in the properties table using the new image fields: `imageName`, `imageType`, and `imageData`.

## Migration Files
- `migration_add_image_columns.sql` - Adds the new image columns
- `rollback_image_columns.sql` - Removes the image columns (rollback)

## New Columns Added

### properties table
| Column Name | Data Type | Description | Nullable |
|-------------|-----------|-------------|----------|
| `image_name` | VARCHAR(255) | Original filename of the uploaded image | Yes |
| `image_type` | VARCHAR(100) | MIME type (e.g., image/jpeg, image/png) | Yes |
| `image_data` | BYTEA | Binary image data | Yes |

## How to Apply Migration

### Option 1: Using psql command line
```bash
# Navigate to the database directory
cd backend/database

# Apply migration
psql -U your_username -d realestate_db -f migration_add_image_columns.sql

# Verify migration
psql -U your_username -d realestate_db -c "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'properties' AND column_name IN ('image_name', 'image_type', 'image_data');"
```

### Option 2: Using pgAdmin or other GUI tools
1. Open the migration file `migration_add_image_columns.sql`
2. Execute the SQL commands in your database management tool
3. Verify the columns were added successfully

## How to Rollback (if needed)
```bash
# Navigate to the database directory
cd backend/database

# Apply rollback
psql -U your_username -d realestate_db -f rollback_image_columns.sql
```

## Verification
After applying the migration, you should see these columns in the properties table:
- `image_name` (VARCHAR)
- `image_type` (VARCHAR) 
- `image_data` (BYTEA)

## Notes
- The migration uses `IF NOT EXISTS` to prevent errors if columns already exist
- An index is created on `image_name` for better query performance
- The `image_data` column uses BYTEA (PostgreSQL's binary data type)
- All new columns are nullable to maintain backward compatibility

## Testing
After migration, test the photo upload functionality:
1. Start the Spring Boot application
2. Try uploading a photo through the API endpoints
3. Verify the image data is stored correctly in the database
4. Test image retrieval and display

