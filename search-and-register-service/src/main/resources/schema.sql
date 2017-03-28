--DROP TABLE IF EXISTS address, comment, users, facility, facility_owner, facility_type, rating, reservation, role, users, working_hours CASCADE;
--
--DROP SEQUENCE IF EXISTS hibernate_sequence, address_id_seq, role_id_seq, users_id_seq, facility_type_id_seq, facility_id_seq,
-- working_hours_id_seq, rating_id_seq, reservation_id_seq, comment_id_seq CASCADE;

CREATE SEQUENCE hibernate_sequence;

CREATE SEQUENCE address_id_seq;
-- Table: address
-- DROP TABLE address;

CREATE TABLE address
(
  id integer NOT NULL DEFAULT nextval('address_id_seq'),
  city character varying(255),
  country character varying(255),
  neighbourhood character varying(255),
  street character varying(255),
  CONSTRAINT address_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE address
  OWNER TO postgres;

-- Table: role

-- DROP TABLE role;
--DROP SEQUENCE IF EXISTS CASCADE;
CREATE SEQUENCE role_id_seq;

CREATE TABLE role
(
  id integer NOT NULL DEFAULT nextval('role_id_seq'),
  role_type character varying(255),
  CONSTRAINT role_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE role
  OWNER TO postgres;

-- Table: users

-- DROP TABLE users;
--DROP SEQUENCE IF EXISTS  CASCADE;
CREATE SEQUENCE users_id_seq;

CREATE TABLE users
(
  user_id integer NOT NULL DEFAULT nextval('users_id_seq'),
  email character varying(255),
  first_name character varying(255),
  is_blocked boolean,
  last_name character varying(255),
  password character varying(255),
  phone character varying(255),
  times_skiped integer,
  address_id integer,
  role_id integer,
  profile_pic character varying(255),
  google_email varchar(255),
  googleid varchar(255),
  facebook_email varchar(255),
  facebookid varchar(255),
  facebook_pic character varying(255),
  CONSTRAINT users_pkey PRIMARY KEY (user_id),
  CONSTRAINT user_address_fk FOREIGN KEY (address_id)
      REFERENCES address (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT user_role_fk FOREIGN KEY (role_id)
      REFERENCES role (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT unique_email UNIQUE (email)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE users
  OWNER TO postgres;

-- Table: facility_type

-- DROP TABLE facility_type;
--DROP SEQUENCE IF EXISTS facility_type_id_seq CASCADE;
CREATE SEQUENCE facility_type_id_seq;

CREATE TABLE facility_type
(
  id integer NOT NULL DEFAULT nextval('facility_type_id_seq'),
  type character varying(255),
  CONSTRAINT facility_type_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facility_type
  OWNER TO postgres;

-- Table: facility_owner

-- DROP TABLE facility_owner;
CREATE TABLE facility_owner
(
  user_id integer NOT NULL,
  rating integer,
  website character varying,
  CONSTRAINT owner_pk PRIMARY KEY (user_id),
  CONSTRAINT user_fk FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facility_owner
  OWNER TO postgres;

-- Table: working_hours

-- DROP TABLE working_hours;
--DROP SEQUENCE IF EXISTS working_hours_id_seq CASCADE;
CREATE SEQUENCE working_hours_id_seq;

CREATE TABLE working_hours
(
  id integer NOT NULL DEFAULT nextval('working_hours_id_seq'),
  start_hour smallint,
  end_hour smallint,
  CONSTRAINT working_hours_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE working_hours
  OWNER TO postgres;

-- Table: facility

-- DROP TABLE facility;
--DROP SEQUENCE IF EXISTS facility_id_seq CASCADE;
CREATE SEQUENCE facility_id_seq;

CREATE TABLE facility
(
  id integer NOT NULL DEFAULT nextval('facility_id_seq'),
  price integer,
  description character varying(255),
  name character varying(255),
  rating double precision,
  address_id integer,
  owner_id integer,
  facility_type_id integer,
  discount_price integer,
  weekends_price integer,
  weekend_hours_id integer DEFAULT 1,
  weekday_hours_id integer DEFAULT 1,
  CONSTRAINT facility_pkey PRIMARY KEY (id),
  CONSTRAINT facility_address_fk FOREIGN KEY (address_id)
      REFERENCES address (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT facility_owner_fk FOREIGN KEY (owner_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT facility_type_fk FOREIGN KEY (facility_type_id)
      REFERENCES facility_type (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT weekday_hours_fk FOREIGN KEY (weekday_hours_id)
      REFERENCES working_hours (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE SET DEFAULT,
  CONSTRAINT weekend_hours_fk FOREIGN KEY (weekend_hours_id)
      REFERENCES working_hours (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE SET DEFAULT
)
WITH (
  OIDS=FALSE
);
ALTER TABLE facility
  OWNER TO postgres;


-- Table: reservation

-- DROP TABLE reservation;
--DROP SEQUENCE IF EXISTS reservation_id_seq CASCADE;
CREATE SEQUENCE reservation_id_seq;

CREATE TABLE reservation
(
  id integer NOT NULL DEFAULT nextval('reservation_id_seq'),
  date date,
  deposit boolean,
  from_hour integer,
  to_hour integer,
  facility_id integer,
  user_id integer,
  CONSTRAINT reservation_pkey PRIMARY KEY (id),
  CONSTRAINT reservation_facility_fk FOREIGN KEY (facility_id)
      REFERENCES facility (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT reservation_user_fk FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
      CONSTRAINT date_from_to_fac UNIQUE (date, from_hour, to_hour, facility_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE reservation
  OWNER TO postgres;

-- Table: comment

-- DROP TABLE comment;
--DROP SEQUENCE IF EXISTS comment_id_seq CASCADE;
CREATE SEQUENCE comment_id_seq;

CREATE TABLE comment
(
  id integer NOT NULL DEFAULT nextval('comment_id_seq'),
  text character varying(255),
  facility_id integer,
  user_id integer,
  date date,
  CONSTRAINT comment_pkey PRIMARY KEY (id),
  CONSTRAINT comment_facility_fk FOREIGN KEY (facility_id)
      REFERENCES facility (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT comment_user_fk FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE comment
  OWNER TO postgres;


-- Table: rating
--DROP SEQUENCE IF EXISTS rating_id_seq CASCADE;
CREATE SEQUENCE rating_id_seq;

-- DROP TABLE rating;
CREATE TABLE rating
(
  id integer NOT NULL DEFAULT nextval('rating_id_seq'),
  rating double precision,
  user_id integer,
  facility_id integer,
  CONSTRAINT rating_pk PRIMARY KEY (id),
  CONSTRAINT rating_facility_fk FOREIGN KEY (facility_id)
      REFERENCES facility (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT rating_user_fk FOREIGN KEY (user_id)
      REFERENCES users (user_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT unique_user_constraint UNIQUE (user_id, facility_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE rating
  OWNER TO postgres;

-- Table: pictures

-- DROP TABLE pictures;
CREATE SEQUENCE pictures_id_seq;

CREATE TABLE pictures
(
  id integer NOT NULL DEFAULT nextval('pictures_id_seq'),
  path character varying(255),
  facility_id integer,
  CONSTRAINT pictures_pkey PRIMARY KEY (id),
  CONSTRAINT picture_facility_fk FOREIGN KEY (facility_id)
      REFERENCES facility (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE pictures
  OWNER TO postgres;
