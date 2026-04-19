INSERT INTO etat_avion (code_etat, description) VALUES
(1,  'Disponible'),
(11, 'En maintenance'),
(21, 'En vol'),
(31, 'Hors service'); 

INSERT INTO etat_vol (code_etat, description) VALUES
(1,  'Programmé'),
(11, 'Ouvert à la réservation'),
(21, 'En cours'),
(31, 'Arrivé'),

(2,  'Annulé'),
(12, 'Retardé'),
(22, 'Reporté'),
(32, 'Clôturé');

INSERT INTO avion (immatriculation, modele, capacite_passagers, capacite_cargo) VALUES
('5R-MAA', 'ATR 72', 70, 7500),
('5R-MAB', 'Boeing 737', 160, 20000),
('5R-MAC', 'Dash 8 Q400', 78, 8000);

INSERT INTO aeroport (code, nom, ville, pays) VALUES
('TNR', 'Ivato International Airport', 'Antananarivo', 'Madagascar'),
('TMM', 'Toamasina Airport', 'Toamasina', 'Madagascar'),
('DIE', 'Arrachart Airport', 'Antsiranana', 'Madagascar'),
('TLE', 'Toliara Airport', 'Toliara', 'Madagascar'),
('MJN', 'Amborovy Airport', 'Mahajanga', 'Madagascar');

INSERT INTO passager (
    nom, prenom, date_naissance, sexe,
    nationalite, numero_passeport, contact, email
) VALUES
('Rakoto', 'Jean', '1995-06-15', 'M', 'Malagasy', 'MG123456', '0341234567', 'jean.rakoto@gmail.com'),
('Rasoa', 'Marie', '1998-03-22', 'F', 'Malagasy', 'MG789012', '0329876543', 'marie.rasoa@gmail.com');

INSERT INTO devise (code_devise, nom_devise, symbole, est_reference) VALUES
('MGA', 'Ariary Malagasy', 'Ar', TRUE),
('EUR', 'Euro', '€', FALSE),
('USD', 'Dollar Américain', '$', FALSE);

INSERT INTO taux_change (id_devise_source, id_devise_cible, taux) VALUES
(2, 1, 4800),  -- 1 EUR = 4800 MGA
(3, 1, 4500);  -- 1 USD = 4500 MGA
