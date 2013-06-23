# public_key and users SCHEMA

# --- !Ups

CREATE TABLE public_key (
  id           SERIAL,
  key_id       VARCHAR(016) NOT NULL,
  fingerprint  VARCHAR(050) NOT NULL,
  algorithm    VARCHAR(050) NOT NULL,
  bit_strength INT          NOT NULL,
  create_date  TIMESTAMP    NOT NULL,
  is_revoked   BOOLEAN      NOT NULL,
  rawkey       TEXT         NOT NULL,
  CONSTRAINT pk_public_key PRIMARY KEY (id)
);

CREATE TABLE public_key_user (
  id            SERIAL,
  public_key_id INT          NOT NULL,
  user_id       VARCHAR(100) NOT NULL,
  CONSTRAINT pk_public_key_user_id PRIMARY KEY (id)
);
ALTER TABLE public_key_user ADD CONSTRAINT fk_public_key_user_public_key FOREIGN KEY (public_key_id) REFERENCES public_key (id);

# --- !Downs

DROP TABLE public_key_user;
DROP TABLE public_key;