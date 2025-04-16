-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS kyc_documents;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS transaction_references;
DROP TABLE IF EXISTS account_preferences;
DROP TABLE IF EXISTS contact_details;
DROP TABLE IF EXISTS accounts;

-- Create accounts table
CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          account_number VARCHAR NOT NULL UNIQUE,
                          account_holder_name VARCHAR NOT NULL,
                          status VARCHAR,  -- enum: ACTIVE, PENDING, FROZEN, CLOSED, SUSPENDED, KYC_PENDING, DORMANT
                          account_type VARCHAR NOT NULL,
                          branch_code VARCHAR NOT NULL,
                          current_balance DECIMAL NOT NULL,
                          available_balance DECIMAL NOT NULL,
                          minimum_balance DECIMAL NOT NULL,
                          overdraft_limit DECIMAL,
                          interest_rate DECIMAL,
                          date_of_birth DATE,
                          currency VARCHAR NOT NULL,
                          tax_id_number VARCHAR,
                          is_overdraft_enabled BOOLEAN,
                          opening_date TIMESTAMP NOT NULL,
                          last_active_date TIMESTAMP,
                          nationality VARCHAR,
                          customerID UUID NOT NULL,
                          contact_details_id UUID,
                          preference_id UUID,
                          closed_date TIMESTAMP,
                          closure_reason VARCHAR,
                          risk_rating VARCHAR,
                          created_date TIMESTAMP NOT NULL,
                          last_modified_date TIMESTAMP,
                          created_by VARCHAR,
                          last_modified_by VARCHAR,
                          version INTEGER
);

-- Create contact_details table
CREATE TABLE contact_details (
                                 id UUID PRIMARY KEY,
                                 email VARCHAR NOT NULL,
                                 phone_number VARCHAR NOT NULL,
                                 alternate_phone_number VARCHAR,
                                 is_email_verified BOOLEAN,
                                 is_phone_verified BOOLEAN,
                                 email_verification_date TIMESTAMP,
                                 phone_verification_date TIMESTAMP,
                                 preferred_contact_method VARCHAR,
                                 created_date TIMESTAMP NOT NULL
);

-- Create account_preferences table
CREATE TABLE account_preferences (
                                     id UUID PRIMARY KEY,
                                     statement_frequency VARCHAR,
                                     statement_delivery_mode VARCHAR,
                                     notification_preference BOOLEAN NOT NULL,
                                     transaction_alerts BOOLEAN NOT NULL,
                                     balance_alerts BOOLEAN NOT NULL,
                                     marketing_communications BOOLEAN NOT NULL,
                                     threshold_alert_amount DOUBLE PRECISION,
                                     preferred_language VARCHAR,
                                     created_date TIMESTAMP NOT NULL,
                                     last_modified_date TIMESTAMP
);

-- Create transaction_references table (for the @ElementCollection)
CREATE TABLE transaction_references (
                                        account_id UUID NOT NULL,
                                        transaction_reference VARCHAR,
                                        FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Create addresses table
CREATE TABLE addresses (
                           id UUID PRIMARY KEY,
                           address_type VARCHAR NOT NULL,
                           address_line1 VARCHAR NOT NULL,
                           address_line2 VARCHAR,
                           city VARCHAR NOT NULL,
                           state VARCHAR NOT NULL,
                           postal_code VARCHAR NOT NULL,
                           country VARCHAR NOT NULL,
                           is_primary BOOLEAN,
                           is_verified BOOLEAN,
                           verification_date TIMESTAMP,
                           account_id UUID,
                           created_date TIMESTAMP NOT NULL,
                           last_modified_date TIMESTAMP,
                           FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Create kyc_documents table
CREATE TABLE kyc_documents (
                               id UUID PRIMARY KEY,
                               document_type VARCHAR NOT NULL,  -- enum: PASSPORT, DRIVERS_LICENSE, NATIONAL_ID, TAX_ID, UTILITY_BILL
                               document_number VARCHAR NOT NULL,
                               issuing_authority VARCHAR NOT NULL,
                               issue_date DATE NOT NULL,
                               expiry_date DATE NOT NULL,
                               document_path VARCHAR NOT NULL,
                               verification_status VARCHAR,
                               verification_comments VARCHAR,
                               verified_by VARCHAR,
                               verification_date TIMESTAMP,
                               account_id UUID,
                               created_date TIMESTAMP NOT NULL,
                               last_modified_date TIMESTAMP,
                               FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Add foreign key constraints to accounts table
ALTER TABLE accounts
    ADD CONSTRAINT fk_account_contact_details
        FOREIGN KEY (contact_details_id) REFERENCES contact_details(id);

ALTER TABLE accounts
    ADD CONSTRAINT fk_account_preferences
        FOREIGN KEY (preference_id) REFERENCES account_preferences(id);