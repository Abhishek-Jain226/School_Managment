-- Remove unique constraint from user_name column in user table
-- This allows multiple users to have the same username

ALTER TABLE user DROP INDEX UKob8kqyqqgmefl0aco34akdtpe;

-- Verify the constraint is removed
SHOW INDEX FROM user WHERE Key_name = 'UKob8kqyqqgmefl0aco34akdtpe';
