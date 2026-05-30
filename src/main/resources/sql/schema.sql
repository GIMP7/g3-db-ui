-- TABLE: agent_informationagent_tin
-- Must be created before registration_details (referenced FK)
CREATE TABLE IF NOT EXISTS agent_information (
    agent_tin       VARCHAR(15)     NOT NULL,
    agent_name      VARCHAR(70)     NOT NULL,
    agent_rdo       VARCHAR(3)      NOT NULL        COMMENT 'RDO code range: 001–126',
    agent_address   VARCHAR(150)    NOT NULL,
    agent_contact   VARCHAR(15)     NOT NULL,
    agent_email     VARCHAR(40)     NOT NULL,
    registration_id VARCHAR(10)     NOT NULL,

    CONSTRAINT pk_agent_information
        PRIMARY KEY (agent_tin)
);

-- TABLE: registration_details
CREATE TABLE IF NOT EXISTS registration_details (
    registration_id VARCHAR(10)     NOT NULL,
    agent_tin       VARCHAR(15)     NULL            COMMENT 'TIN of applicant''s agent; nullable if no agent',
    reg_date        DATE            NOT NULL        DEFAULT (CURRENT_DATE) COMMENT 'Date of successful registration',
    taxpayer_type   CHAR(40)        NOT NULL,
    purpose         VARCHAR(20)     NOT NULL,

    CONSTRAINT pk_registration_details
        PRIMARY KEY (registration_id),

    CONSTRAINT chk_taxpayer_type CHECK (
        taxpayer_type IN (
            'Filipino Citizen',
            'Foreign National',
            'One-time Filipino',
            'One-time Foreign',
            'Passive Income',
            'Estate'
        )
    ),

    CONSTRAINT fk_registration_agent
        FOREIGN KEY (agent_tin)
        REFERENCES agent_information (agent_tin)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- TABLE: taxpayer_information
CREATE TABLE IF NOT EXISTS taxpayer_information (
    registration_id     VARCHAR(10)     NOT NULL,
    philsys_number      VARCHAR(19)     NULL            UNIQUE COMMENT 'PhilSys card ID number',
    foreign_tin         VARCHAR(20)     NULL,
    residence           VARCHAR(150)    NULL            COMMENT 'Foreign residence (foreign applicants only)',
    taxpayer_name       VARCHAR(70)     NOT NULL,
    name_category       VARCHAR(15)     NOT NULL,
    birth_date          DATE            NOT NULL,
    birth_place         VARCHAR(150)    NOT NULL,
    local_address       VARCHAR(150)    NOT NULL,
    foreign_address     VARCHAR(150)    NULL,
    arrival_date        DATE            NULL            COMMENT 'Date of arrival in PH (foreign applicants only)',
    gender              CHAR(1)         NOT NULL,
    civil_status        CHAR(1)         NOT NULL,
    contact_no          VARCHAR(15)     NOT NULL,
    email               VARCHAR(40)     NOT NULL,
    mother_name         VARCHAR(70)     NOT NULL,
    father_name         VARCHAR(70)     NOT NULL,

    CONSTRAINT pk_taxpayer_information
        PRIMARY KEY (registration_id),

    CONSTRAINT chk_name_category CHECK (
        name_category IN ('Individual', 'Non-Individual', 'Estate')
    ),

    CONSTRAINT chk_gender CHECK (
        gender IN ('M', 'F', 'I')
    ),

    CONSTRAINT chk_civil_status CHECK (
        civil_status IN ('S', 'M', 'W', 'L')
    ),

    CONSTRAINT fk_taxpayer_registration
        FOREIGN KEY (registration_id)
        REFERENCES registration_details (registration_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- TABLE: id_information
CREATE TABLE IF NOT EXISTS id_information (
    id_number       VARCHAR(20)     NOT NULL,
    id_type         VARCHAR(30)     NOT NULL,
    id_effective    DATE            NOT NULL,
    id_expiry       DATE            NULL,
    registration_id VARCHAR(10)     NOT NULL,

    CONSTRAINT pk_id_information
        PRIMARY KEY (id_number),

    CONSTRAINT fk_id_registration
        FOREIGN KEY (registration_id)
        REFERENCES registration_details (registration_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- TABLE: spouse_information
CREATE TABLE IF NOT EXISTS spouse_information (
    spouse_id               VARCHAR(10)     NOT NULL,
    spouse_employment       VARCHAR(40)     NOT NULL,
    spouse_name             VARCHAR(70)     NOT NULL,
    spouse_tin              VARCHAR(15)     NULL,
    spouse_employer_name    VARCHAR(70)     NULL,
    spouse_employer_tin     VARCHAR(15)     NULL,
    registration_id         VARCHAR(10)     NOT NULL,

    CONSTRAINT pk_spouse_information
        PRIMARY KEY (spouse_id),

    CONSTRAINT chk_spouse_employment CHECK (
        spouse_employment IN (
            'Unemployed',
            'Employed-Locally',
            'Employed-Abroad',
            'Engaged in Business'
        )
    ),

    CONSTRAINT fk_spouse_registration
        FOREIGN KEY (registration_id)
        REFERENCES registration_details (registration_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Add FK back-reference: agent_information → registration_details
-- (Added after registration_details is created)
ALTER TABLE agent_information
    ADD CONSTRAINT fk_agent_registration
        FOREIGN KEY (registration_id)
        REFERENCES registration_details (registration_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

-- Verify tables were created
-- Spring Boot creates and populates the in-memory database on startup.
