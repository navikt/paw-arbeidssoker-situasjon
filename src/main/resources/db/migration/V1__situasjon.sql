CREATE TABLE situasjon (
  id SERIAL PRIMARY KEY,
  foedselsnummer VARCHAR(11),
  opprettet TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  endret TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX situasjon_foedselsnummer_index ON situasjon (foedselsnummer);

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
  RETURNS TRIGGER AS
$$
BEGIN
  NEW.endret = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER situasjon_endret
  BEFORE UPDATE ON situasjon
  FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
