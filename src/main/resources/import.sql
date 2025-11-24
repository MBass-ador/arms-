-- SQL script to create test data
-- arms application
-- author: Matthew Bass
-- version:3.0


-- accounts

INSERT INTO accounts (screen_name, password, first_name, last_name, email, phone_number, provider, street, city, state, zip_code) VALUES ('alice', 'password', 'Alice', 'Anderson','alice@example.com', '555-0100', FALSE,'1 Main St', 'Springfield', 'IL', '62701');


INSERT INTO accounts (screen_name, password, first_name, last_name, email, phone_number, provider, street, city, state, zip_code) VALUES ( 'bob', 'password', 'Bob', 'Baker','bob@example.com', '555-0200', FALSE,'2 Oak Ave', 'Springfield', 'IL', '62702');


INSERT INTO accounts (screen_name, password, first_name, last_name, email, phone_number, provider, street, city, state, zip_code) VALUES ('clara', 'password', 'Clara', 'Clayton','clara@example.com', '555-0300', TRUE,'4 Birch Ln', 'Springfield', 'IL', '62704');


INSERT INTO accounts (screen_name, password, first_name, last_name, email, phone_number, provider, street, city, state, zip_code) VALUES ('derek', 'password', 'Derek', 'Dillon','derek@example.com', '555-0400', TRUE,'5 Cedar Ct', 'Springfield', 'IL', '62705');


INSERT INTO accounts (screen_name, password, first_name, last_name, email, phone_number, provider, street, city, state, zip_code) VALUES ( 'ernest', 'password', 'Ernest', 'Edwards','ernest@example.com', '555-0500', TRUE,'3 Pine St', 'Springfield', 'IL', '62703');


-- bookings
ALTER TABLE bookings ALTER COLUMN booking_id RESTART WITH 1001;

INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES (3, 1, 50.00, '2025-06-01 09:00:00','2025-06-01 11:00:00', '10 Market St', 'Springfield', 'IL','62701', FALSE, 0.000, FALSE);


INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES ( 4, 1, 60.00, '2025-06-05 13:00:00','2025-06-05 15:30:00', '22 Oak Ave', 'Springfield', 'IL','62702', TRUE, 0.500, FALSE);


INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES ( 5, 1, 55.00, '2025-06-10 08:00:00','2025-06-10 09:30:00', '7 River Rd', 'Springfield', 'IL','62703', TRUE, -0.500, FALSE);


INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES ( 3, 2, 45.00, '2025-07-01 10:00:00','2025-07-01 11:00:00', '14 First St', 'Springfield', 'IL','62704', FALSE, 0.000, FALSE);


INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES ( 4, 2, 70.00, '2025-07-03 14:00:00','2025-07-03 16:00:00', '9 Willow Ln', 'Springfield', 'IL', '62705', TRUE, 0.000, FALSE);


INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES ( 5, 2, 65.00, '2025-07-07 12:00:00','2025-07-07 13:45:00', '3 Cedar Ct', 'Springfield', 'IL','62706', TRUE, 0.750, FALSE);

INSERT INTO bookings (provider_id, customer_id, hourly_rate, start_time, end_time, loc_street, loc_city, loc_state, loc_zip_code, completed, over_hours, paid) VALUES (4, 1, 80.00, '2025-06-12 10:00:00', '2025-06-12 12:00:00', '33 Elm St', 'Springfield', 'IL', '62701', TRUE, 0.000, FALSE);


-- invoices
INSERT INTO invoices (provider_id, customer_id, total_amount_due, last_contacted) VALUES ( 4, 1, 260.00, NULL);

INSERT INTO invoices (provider_id, customer_id, total_amount_due, last_contacted) VALUES ( 5, 1, 55.00, NULL);

INSERT INTO invoices (provider_id, customer_id, total_amount_due, last_contacted) VALUES ( 4, 2, 140.00, NULL);

INSERT INTO invoices (provider_id, customer_id, total_amount_due, last_contacted) VALUES ( 5, 2, 162.50, NULL);


-- invoice_bookings facilitate bookings inclusion in invoice objects

-- Invoice 1: provider_id=4, customer_id=1, total_amount_due=180.00 -> Booking 2: provider_id=4, customer_id=1, start_time='2025-06-05 13:00:00'
INSERT INTO invoice_bookings (invoice_id, booking_id) VALUES ((SELECT invoice_id FROM invoices WHERE provider_id=4 AND customer_id=1 AND total_amount_due=260.00), (SELECT booking_id FROM bookings WHERE provider_id=4 AND customer_id=1 AND start_time='2025-06-05 13:00:00'));

INSERT INTO invoice_bookings (invoice_id, booking_id) VALUES ((SELECT invoice_id FROM invoices WHERE provider_id=4 AND customer_id=1 AND total_amount_due=260.00), (SELECT booking_id FROM bookings WHERE provider_id=4 AND customer_id=1 AND start_time='2025-06-12 10:00:00'));


-- Invoice 2: provider_id=5, customer_id=1, total_amount_due=55.00 -> Booking 3: provider_id=5, customer_id=1, start_time='2025-06-10 08:00:00'
INSERT INTO invoice_bookings (invoice_id, booking_id) VALUES ((SELECT invoice_id FROM invoices WHERE provider_id=5 AND customer_id=1 AND total_amount_due=55.00), (SELECT booking_id FROM bookings WHERE provider_id=5 AND customer_id=1 AND start_time='2025-06-10 08:00:00'));

-- Invoice 3: provider_id=4, customer_id=2, total_amount_due=140.00 -> Booking 5: provider_id=4, customer_id=2, start_time='2025-07-03 14:00:00'
INSERT INTO invoice_bookings (invoice_id, booking_id) VALUES ((SELECT invoice_id FROM invoices WHERE provider_id=4 AND customer_id=2 AND total_amount_due=140.00), (SELECT booking_id FROM bookings WHERE provider_id=4 AND customer_id=2 AND start_time='2025-07-03 14:00:00'));

-- Invoice 4: provider_id=5, customer_id=2, total_amount_due=162.50 -> Booking 6: provider_id=5, customer_id=2, start_time='2025-07-07 12:00:00'
INSERT INTO invoice_bookings (invoice_id, booking_id) VALUES ((SELECT invoice_id FROM invoices WHERE provider_id=5 AND customer_id=2 AND total_amount_due=162.50), (SELECT booking_id FROM bookings WHERE provider_id=5 AND customer_id=2 AND start_time='2025-07-07 12:00:00'));