# public key signatures SCHEMA

# --- !Ups

CREATE TABLE public_key_signature (
  id            SERIAL,
  public_key_id INT          NOT NULL,
  key_id        VARCHAR(016) NOT NULL,
  create_date   TIMESTAMP    NOT NULL,
  CONSTRAINT pk_public_key_signature_id PRIMARY KEY (id)
);
ALTER TABLE public_key_signature ADD CONSTRAINT fk_public_key_signature_public_key FOREIGN KEY (public_key_id) REFERENCES public_key (id);

# --- !Downs

DROP TABLE public_key_signature;
