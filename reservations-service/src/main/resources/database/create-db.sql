CREATE TABLE reservation
(
  id integer,
  date date,
  deposit boolean,
  from_hour integer,
  to_hour integer,
  facility_id integer,
  user_id integer,
  is_deleted boolean DEFAULT false
);
