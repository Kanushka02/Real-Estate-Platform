-- Create property_images table for storing property photos
CREATE TABLE property_images (
    image_id BIGSERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    image_name VARCHAR(255) NOT NULL,
    image_type VARCHAR(50),
    image_size BIGINT,
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    property_id BIGINT NOT NULL,
    FOREIGN KEY (property_id) REFERENCES properties(property_id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_property_images_property_id ON property_images(property_id);
CREATE INDEX idx_property_images_is_primary ON property_images(is_primary);
CREATE INDEX idx_property_images_sort_order ON property_images(sort_order);

-- Ensure only one primary image per property
CREATE UNIQUE INDEX idx_property_images_primary_per_property
ON property_images(property_id) WHERE is_primary = TRUE;