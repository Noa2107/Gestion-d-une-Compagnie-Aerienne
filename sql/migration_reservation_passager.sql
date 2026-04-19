-- Option B: 1 reservation peut contenir plusieurs passagers (1 passager par siege)

CREATE TABLE IF NOT EXISTS reservation_passager (
    id_reservation_passager BIGSERIAL PRIMARY KEY,
    id_reservation BIGINT NOT NULL,
    id_passager BIGINT NOT NULL,
    siege VARCHAR(10) NOT NULL,
    CONSTRAINT fk_rp_reservation FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    CONSTRAINT fk_rp_passager FOREIGN KEY (id_passager) REFERENCES passager(id_passager) ON DELETE RESTRICT,
    CONSTRAINT uq_rp_reservation_siege UNIQUE (id_reservation, siege)
);

CREATE INDEX IF NOT EXISTS idx_rp_reservation ON reservation_passager(id_reservation);
CREATE INDEX IF NOT EXISTS idx_rp_passager ON reservation_passager(id_passager);
