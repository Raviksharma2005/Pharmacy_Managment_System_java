-- Alter the Employee table to update the role column to accept Admin role
ALTER TABLE PMS.Employee MODIFY COLUMN role ENUM('Admin', 'Pharmacist', 'Cashier') NOT NULL;

-- If the above fails due to existing data, try this alternative approach
-- First, create a temporary column
ALTER TABLE PMS.Employee ADD COLUMN temp_role VARCHAR(50);

-- Copy data from role to temp_role
UPDATE PMS.Employee SET temp_role = role;

-- Drop the original role column
ALTER TABLE PMS.Employee DROP COLUMN role;

-- Create the new role column with the desired enumeration
ALTER TABLE PMS.Employee ADD COLUMN role ENUM('Admin', 'Pharmacist', 'Cashier') NOT NULL AFTER name;

-- Copy data back from temp_role to role
UPDATE PMS.Employee SET role = temp_role;

-- Drop the temporary column
ALTER TABLE PMS.Employee DROP COLUMN temp_role; 