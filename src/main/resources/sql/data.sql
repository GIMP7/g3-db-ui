-- BIR Form 1904 — Sample Data Initialization
-- 1. registration_details (insert with agent_tin temporarily null)
INSERT INTO registration_details
    (registration_id, agent_tin, reg_date, taxpayer_type, purpose)
VALUES
    ('REG-000001', NULL, '2024-01-10', 'Filipino Citizen', 'Local Employment'),
    ('REG-000002', NULL, '2024-01-15', 'Filipino Citizen', 'Local Employment'),
    ('REG-000003', NULL, '2024-02-01', 'Filipino Citizen', 'Business'),
    ('REG-000004', NULL, '2024-02-14', 'Estate', 'Estate Settlement'),
    ('REG-000005', NULL, '2024-03-05', 'Filipino Citizen', 'Local Employment'),
    ('REG-000006', NULL, '2024-03-18', 'Foreign National', 'Business'),
    ('REG-000007', NULL, '2024-04-02', 'Filipino Citizen', 'Local Employment'),
    ('REG-000008', NULL, '2024-04-20', 'One-time Filipino', 'One-time Transaction'),
    ('REG-000009', NULL, '2024-05-07', 'Filipino Citizen', 'Business'),
    ('REG-000010', NULL, '2024-05-22', 'Passive Income', 'Passive Income'),
    ('REG-000011', NULL, '2024-06-03', 'Filipino Citizen', 'Local Employment'),
    ('REG-000012', NULL, '2024-06-17', 'Foreign National', 'Business'),
    ('REG-000013', NULL, '2024-07-01', 'Filipino Citizen', 'Local Employment'),
    ('REG-000014', NULL, '2024-07-19', 'Estate', 'Estate Settlement'),
    ('REG-000015', NULL, '2024-08-05', 'One-time Foreign', 'One-time Transaction'),
    ('REG-000016', NULL, '2024-08-21', 'Filipino Citizen', 'Local Employment'),
    ('REG-000017', NULL, '2024-09-09', 'Filipino Citizen', 'Business'),
    ('REG-000018', NULL, '2024-09-25', 'Foreign National', 'Local Employment'),
    ('REG-000019', NULL, '2024-10-10', 'Filipino Citizen', 'Local Employment'),
    ('REG-000020', NULL, '2024-10-30', 'Passive Income', 'Passive Income');

-- 2. taxpayer_information
INSERT INTO taxpayer_information
    (registration_id, philsys_number, foreign_tin, residence, taxpayer_name,
     name_category, birth_date, birth_place, local_address, foreign_address,
     arrival_date, gender, civil_status, contact_no, email, mother_name, father_name)
VALUES
    ('REG-000001', '1234-5678-9012-3456', NULL, NULL, 'DELA CRUZ, JUAN PEDRO SANTOS',
     'Individual', '1990-03-15', 'Manila, Metro Manila', '123 Mabini St., Sampaloc, Manila', NULL,
     NULL, 'M', 'S', '09171000001', 'juanpedro.delacruz@gmail.com', 'MARIA SANTOS DELA CRUZ', 'PEDRO REYES DELA CRUZ'),
    ('REG-000002', '2345-6789-0123-4567', NULL, NULL, 'REYES, ANNA MARIE FLORES',
     'Individual', '1985-07-22', 'Quezon City, Metro Manila', '45 Katipunan Ave., Loyola Heights, Quezon City', NULL,
     NULL, 'F', 'M', '09282000002', 'annamarie.reyes@yahoo.com', 'LOURDES FLORES REYES', 'ROBERTO SANTOS REYES'),
    ('REG-000003', '3456-7890-1234-5678', NULL, NULL, 'SANTOS, MICHAEL ANGELO CRUZ',
     'Individual', '1978-11-05', 'Makati City, Metro Manila', '88 Salcedo St., Legaspi Village, Makati City', NULL,
     NULL, 'M', 'M', '09393000003', 'michaelangelo.santos@gmail.com', 'TERESITA CRUZ SANTOS', 'ANTONIO REYES SANTOS'),
    ('REG-000004', NULL, NULL, NULL, 'DELA CRUZ ESTATE',
     'Estate', '1940-06-30', 'Pasig City, Metro Manila', '12 Meralco Ave., Ortigas Center, Pasig City', NULL,
     NULL, 'I', 'S', '09504000004', 'delacruzestate@gmail.com', 'N/A', 'N/A'),
    ('REG-000005', '4567-8901-2345-6789', NULL, NULL, 'GARCIA, MARIA CLARA DIZON',
     'Individual', '1995-01-18', 'Cebu City, Cebu', '22 Osmeña Blvd., Capitol Site, Cebu City', NULL,
     NULL, 'F', 'S', '09165000005', 'mariaclara.garcia@outlook.com', 'CARMEN DIZON GARCIA', 'JOSE ANTONIO GARCIA'),
    ('REG-000006', NULL, 'US-987654321', '5120 Wilshire Blvd, Los Angeles, CA 90036, USA', 'JOHNSON, ROBERT WILLIAM',
     'Individual', '1975-09-12', 'Los Angeles, California, USA', '36F Shangri-La Place, Mandaluyong City', '5120 Wilshire Blvd, Los Angeles, CA 90036, USA',
     '2024-03-10', 'M', 'M', '09176000006', 'robert.johnson@usmail.com', 'PATRICIA ADAMS JOHNSON', 'WILLIAM HENRY JOHNSON'),
    ('REG-000007', '5678-9012-3456-7890', NULL, NULL, 'FERNANDEZ, ROSARIO LINDA BAUTISTA',
     'Individual', '1988-04-25', 'Davao City, Davao del Sur', '101 Quirino Ave., Poblacion, Davao City', NULL,
     NULL, 'F', 'M', '09287000007', 'rosariolinda.fernandez@gmail.com', 'LINDA BAUTISTA FERNANDEZ', 'CARLOS DELA CRUZ FERNANDEZ'),
    ('REG-000008', '6789-0123-4567-8901', NULL, NULL, 'MENDOZA, CARLO JEROME RAMOS',
     'Individual', '1992-12-03', 'Baguio City, Benguet', '5 Leonard Wood Rd., Baguio City', NULL,
     NULL, 'M', 'M', '09398000008', 'carlojero.mendoza@gmail.com', 'ELENA RAMOS MENDOZA', 'JEROME AGUILAR MENDOZA'),
    ('REG-000009', '7890-1234-5678-9012', NULL, NULL, 'AQUINO, PATRICIA ANNE VILLANUEVA',
     'Individual', '1982-08-17', 'Iloilo City, Iloilo', '78 General Luna St., Iloilo City', NULL,
     NULL, 'F', 'M', '09509000009', 'patriciaanne.aquino@yahoo.com', 'GRACE VILLANUEVA AQUINO', 'BENIGNO SANTOS AQUINO'),
    ('REG-000010', '8901-2345-6789-0123', NULL, NULL, 'LORENZO, ALFREDO JOSE CASTILLO',
     'Individual', '1960-02-28', 'Mandaluyong City, Metro Manila', '330 Shaw Blvd., Mandaluyong City', NULL,
     NULL, 'M', 'W', '09160000010', 'alfredojose.lorenzo@gmail.com', 'ROSARIO CASTILLO LORENZO', 'JOSE FRANCISCO LORENZO'),
    ('REG-000011', '9012-3456-7890-1234', NULL, NULL, 'RAMOS, JENNIFER GRACE TOLENTINO',
     'Individual', '1997-06-14', 'Antipolo City, Rizal', '9 Circumferential Rd., Antipolo City', NULL,
     NULL, 'F', 'S', '09271000011', 'jennifergrace.ramos@gmail.com', 'GRACE TOLENTINO RAMOS', 'EDUARDO MANUEL RAMOS'),
    ('REG-000012', NULL, 'SG-T1234567H', '80 Raffles Place, Singapore 048624', 'LIM, DAVID CHEN WEI',
     'Individual', '1980-03-30', 'Singapore', '12th Floor GT Tower, Ayala Ave., Makati City', '80 Raffles Place, Singapore 048624',
     '2024-06-01', 'M', 'M', '09382000012', 'david.lim@sgmail.com', 'JESSICA TAN LIM', 'HENRY LIM CHEN'),
    ('REG-000013', '0123-4567-8901-2345', NULL, NULL, 'TORRES, PAULO GABRIEL ESPIRITU',
     'Individual', '1993-10-09', 'San Fernando, Pampanga', 'Block 4 Lot 12 Filinvest 2, Batasan Hills, Quezon City', NULL,
     NULL, 'M', 'M', '09493000013', 'paulogabriel.torres@yahoo.com', 'MARICEL ESPIRITU TORRES', 'GABRIEL REYES TORRES'),
    ('REG-000014', NULL, NULL, NULL, 'GONZALES ESTATE',
     'Estate', '1935-04-21', 'Cagayan de Oro, Misamis Oriental', '25 Velez St., Cagayan de Oro City', NULL,
     NULL, 'I', 'W', '09504000014', 'gonzalesestate@outlook.com', 'N/A', 'N/A'),
    ('REG-000015', NULL, 'JP-AB1234567', '3-1-1 Marunouchi, Chiyoda-ku, Tokyo, Japan', 'YAMAMOTO, KENJI',
     'Individual', '1970-05-15', 'Tokyo, Japan', 'Unit 18B One Rockwell, Makati City', '3-1-1 Marunouchi, Chiyoda-ku, Tokyo, Japan',
     '2024-07-20', 'M', 'M', '09215000015', 'kenji.yamamoto@jpmail.com', 'YUKI TANAKA YAMAMOTO', 'HIROSHI YAMAMOTO'),
    ('REG-000016', '1122-3344-5566-7788', NULL, NULL, 'PASCUAL, MARIA THERESA NAVARRO',
     'Individual', '1991-09-27', 'Batangas City, Batangas', '17 P. Burgos St., Batangas City', NULL,
     NULL, 'F', 'S', '09326000016', 'mariatheresa.pascual@gmail.com', 'THERESA NAVARRO PASCUAL', 'ERNESTO ABAD PASCUAL'),
    ('REG-000017', '2233-4455-6677-8899', NULL, NULL, 'AGUILAR REALTY CORP',
     'Non-Individual', '2001-08-15', 'Taguig City, Metro Manila', '32nd Floor BGC Corporate Center, Taguig City', NULL,
     NULL, 'I', 'S', '09437000017', 'aguilarrealty@corp.ph', 'N/A', 'N/A'),
    ('REG-000018', NULL, 'AU-N1234567', '42 Park St, Sydney NSW 2000, Australia', 'SMITH, EMILY ROSE',
     'Individual', '1987-11-22', 'Sydney, New South Wales, Australia', 'Unit 3C Residences, BGC, Taguig', '42 Park St, Sydney NSW 2000, Australia',
     '2024-09-15', 'F', 'S', '09548000018', 'emily.smith@aumail.com', 'MARGARET WHITE SMITH', 'DAVID JOHN SMITH'),
    ('REG-000019', '3344-5566-7788-9900', NULL, NULL, 'VILLANUEVA, JOSE MARTIN GARCIA',
     'Individual', '1984-07-04', 'Legazpi City, Albay', '55 Kamuning Rd., Quezon City', NULL,
     NULL, 'M', 'M', '09159000019', 'josemartin.villanueva@gmail.com', 'CORAZON GARCIA VILLANUEVA', 'MARTIN SANTOS VILLANUEVA'),
    ('REG-000020', '4455-6677-8899-0011', NULL, NULL, 'BUENAVENTURA, ELEANOR FAITH REYES',
     'Individual', '1955-12-10', 'Vigan City, Ilocos Sur', '6 Urdaneta Village, Makati City', NULL,
     NULL, 'F', 'W', '09260000020', 'eleanor.buenaventura@yahoo.com', 'FAITH REYES BUENAVENTURA', 'ERNESTO DIAZ BUENAVENTURA');

-- 3. id_information
INSERT INTO id_information
    (id_number, id_type, id_effective, id_expiry, registration_id)
VALUES
    ('PH-PSN-2019-001234', 'Passport', '2019-05-10', '2029-05-09', 'REG-000001'),
    ('DL-NCR-2021-987654', 'Driver''s License', '2021-08-15', '2025-08-14', 'REG-000001'),
    ('PH-PSN-2018-005678', 'Passport', '2018-03-22', '2028-03-21', 'REG-000002'),
    ('SSSID-0045671234', 'SSS ID', '2015-06-01', NULL, 'REG-000002'),
    ('PH-PSN-2020-009012', 'Passport', '2020-11-30', '2030-11-29', 'REG-000003'),
    ('DL-NCR-2022-543210', 'Driver''s License', '2022-01-10', '2026-01-09', 'REG-000003'),
    ('PH-31200012345', 'PhilHealth ID', '2018-07-20', NULL, 'REG-000003'),
    ('NBI-2023-NCR-00123', 'NBI Clearance', '2023-09-05', '2024-09-04', 'REG-000004'),
    ('SSSID-0078902345', 'SSS ID', '2010-03-14', NULL, 'REG-000005'),
    ('DL-VII-2023-112233', 'Driver''s License', '2023-02-28', '2027-02-27', 'REG-000005'),
    ('US-PSN-A12345678', 'Foreign Passport', '2017-06-15', '2027-06-14', 'REG-000006'),
    ('US-DL-CA9876543', 'Foreign Driver''s License', '2020-10-01', '2025-09-30', 'REG-000006'),
    ('PH-PSN-2021-007654', 'Passport', '2021-04-18', '2031-04-17', 'REG-000007'),
    ('UMID-0011223344', 'UMID', '2019-11-25', NULL, 'REG-000007'),
    ('PH-PSN-2022-003456', 'Passport', '2022-07-09', '2032-07-08', 'REG-000008'),
    ('SSSID-0065437890', 'SSS ID', '2014-09-30', NULL, 'REG-000009'),
    ('DL-VI-2020-667788', 'Driver''s License', '2020-05-12', '2024-05-11', 'REG-000009'),
    ('PH-40300067890', 'PhilHealth ID', '2012-01-08', NULL, 'REG-000010'),
    ('UMID-0099887766', 'UMID', '2017-04-22', NULL, 'REG-000010'),
    ('VOTER-QC-2022-445566', 'Voter''s ID', '2022-05-01', NULL, 'REG-000011'),
    ('SSSID-0034561234', 'SSS ID', '2020-11-03', NULL, 'REG-000011'),
    ('SG-PSN-K9876543A', 'Foreign Passport', '2019-08-20', '2029-08-19', 'REG-000012'),
    ('SG-NRIC-S1234567D', 'Foreign National ID', '2000-01-01', NULL, 'REG-000012'),
    ('DL-NCR-2019-334455', 'Driver''s License', '2019-06-30', '2023-06-29', 'REG-000013'),
    ('PH-PSN-2023-011234', 'Passport', '2023-10-05', '2033-10-04', 'REG-000013'),
    ('NBI-2022-X-00456', 'NBI Clearance', '2022-11-18', '2023-11-17', 'REG-000014'),
    ('SSSID-0023456789', 'SSS ID', '2008-04-15', NULL, 'REG-000014'),
    ('JP-PSN-TK9876543', 'Foreign Passport', '2018-05-30', '2028-05-29', 'REG-000015'),
    ('JP-DL-123456789012', 'Foreign Driver''s License', '2021-03-01', '2025-02-28', 'REG-000015'),
    ('PH-21100045678', 'PhilHealth ID', '2017-08-10', NULL, 'REG-000016'),
    ('VOTER-BAT-778899', 'Voter''s ID', '2022-05-01', NULL, 'REG-000016'),
    ('SEC-REG-CS2001-12345', 'SEC Certificate', '2001-08-15', NULL, 'REG-000017'),
    ('DTI-REG-2001-00789', 'DTI Registration', '2001-09-01', '2026-09-01', 'REG-000017'),
    ('AU-PSN-PA1234567', 'Foreign Passport', '2020-07-14', '2030-07-13', 'REG-000018'),
    ('AU-DL-NSW12345678', 'Foreign Driver''s License', '2019-02-20', '2024-02-19', 'REG-000018'),
    ('SSSID-0087654321', 'SSS ID', '2013-10-28', NULL, 'REG-000019'),
    ('DL-NCR-2021-556677', 'Driver''s License', '2021-12-01', '2025-11-30', 'REG-000019'),
    ('PH-PSN-2015-000789', 'Passport', '2015-01-20', '2025-01-19', 'REG-000020'),
    ('UMID-0055443322', 'UMID', '2016-06-14', NULL, 'REG-000020');

-- 4. spouse_information
INSERT INTO spouse_information
    (spouse_id, spouse_employment, spouse_name, spouse_tin,
     spouse_employer_name, spouse_employer_tin, registration_id)
VALUES
    ('SP-000001', 'Employed-Locally', 'REYES, MARIA SANTOS', '456-123-789-001',
     'BDO Unibank Inc.', '002-456-789-001', 'REG-000002'),
    ('SP-000002', 'Engaged in Business', 'SANTOS, ELENA CRUZ', '567-234-890-002',
     NULL, NULL, 'REG-000003'),
    ('SP-000003', 'Employed-Locally', 'MENDOZA, CARLA SANTOS', '321-654-987-001',
     'Smart Communications Inc.', '009-876-543-002', 'REG-000008'),
    ('SP-000004', 'Employed-Locally', 'JOHNSON, LISA MARIE', NULL,
     'Accenture Inc.', '004-678-901-003', 'REG-000006'),
    ('SP-000005', 'Unemployed', 'FERNANDEZ, RICARDO BAUTISTA', '789-456-012-004',
     NULL, NULL, 'REG-000007'),
    ('SP-000006', 'Employed-Locally', 'AQUINO, JAMES VILLANUEVA', '890-567-123-005',
     'Department of Education', '005-789-012-004', 'REG-000009'),
    ('SP-000007', 'Engaged in Business', 'TORRES, ANDREA ESPIRITU', '901-678-234-006',
     NULL, NULL, 'REG-000013'),
    ('SP-000008', 'Employed-Locally', 'LIM, SARAH CHEN', NULL,
     'KPMG Philippines', '006-890-123-005', 'REG-000012'),
    ('SP-000009', 'Employed-Locally', 'VILLANUEVA, RACHEL GARCIA', '012-789-345-007',
     'Philippine National Bank', '007-901-234-006', 'REG-000019'),
    ('SP-000010', 'Employed-Abroad', 'YAMAMOTO, HIROKO', NULL,
     'Sony Corporation', '008-012-345-007', 'REG-000015');

-- 5. agent_information (after registration_details exists)
INSERT INTO agent_information
    (agent_tin, agent_name, agent_rdo, agent_address, agent_contact, agent_email, registration_id)
VALUES
    ('123-456-789-000', 'Santos Accounting Services', '043', 'Unit 5B Greenbelt Tower, Ayala Ave., Makati City', '09171234567', 'santos.acctg@gmail.com', 'REG-000003'),
    ('234-567-890-001', 'Reyes & Associates CPA', '033', '2nd Floor Robinsons Place, Ermita, Manila', '09281234568', 'reyes.assoc@yahoo.com', 'REG-000006'),
    ('345-678-901-002', 'Cruz Tax Consultancy', '039', 'Blk 3 Lot 7 Alabang Hills, Muntinlupa City', '09391234569', 'cruztax@outlook.com', 'REG-000009'),
    ('456-789-012-003', 'Lim Professional Services', '047', 'Suite 12 One Corporate Center, Ortigas, Pasig City', '09501234570', 'lim.proserv@gmail.com', 'REG-000012'),
    ('567-890-123-004', 'Tan & Co. CPA Firm', '080', '3rd Floor Cebu IT Park, Apas, Cebu City', '09161234571', 'tanco.cpa@gmail.com', 'REG-000015');

-- 6. link registrations back to agents
UPDATE registration_details SET agent_tin = '123-456-789-000' WHERE registration_id = 'REG-000003';
UPDATE registration_details SET agent_tin = '234-567-890-001' WHERE registration_id = 'REG-000006';
UPDATE registration_details SET agent_tin = '345-678-901-002' WHERE registration_id = 'REG-000009';
UPDATE registration_details SET agent_tin = '456-789-012-003' WHERE registration_id = 'REG-000012';
UPDATE registration_details SET agent_tin = '567-890-123-004' WHERE registration_id = 'REG-000015';
