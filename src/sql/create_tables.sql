CREATE TABLE institution (
  id serial NOT NULL,
  name varchar(100) DEFAULT NULL,
  name_en varchar(100) DEFAULT NULL,
  alias varchar(50) DEFAULT NULL,
  description varchar(200) DEFAULT NULL,
  contact_person varchar(100) DEFAULT NULL,
  contact_phone varchar(100) DEFAULT NULL,
  contact_email varchar(100) DEFAULT NULL,
  location varchar(100) DEFAULT NULL,
  location_en varchar(200) DEFAULT NULL,
  time_zone int DEFAULT NULL,
  default_lang varchar(20) DEFAULT NULL,
  is_enabled boolean DEFAULT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE user_account (
  id serial NOT NULL,
  login_name varchar(50) DEFAULT NULL,
  name varchar(50) DEFAULT NULL,
  password varchar(40) DEFAULT NULL,
  email varchar(50) DEFAULT NULL,
  telephone varchar(20) DEFAULT NULL,
  last_update_datetime varchar(20) DEFAULT NULL,
  last_login_datetime varchar(20) DEFAULT NULL,
  is_super_admin boolean DEFAULT NULL,
  is_tenant_admin boolean DEFAULT NULL,
  institution_id int DEFAULT NULL,
  status varchar(20) DEFAULT NULL,
  is_online boolean,
  PRIMARY KEY (id)
)

alter table 

CREATE TABLE chart_config (
  id serial not null,
  chart_name varchar(30) NOT NULL,
  chart_data_sql varchar(500) NOT NULL,
  is_default_empty_result boolean NOT NULL,
  chart_type varchar(20) NOT NULL,
  series_data_field varchar(20) NOT NULL,
  x_axis_data_field varchar(20) NOT NULL,
  y_axis_data_field varchar(20) NOT NULL,
  chart_title varchar(40) DEFAULT NULL,
  legend_position varchar(1) DEFAULT NULL,
  legend_rows int DEFAULT NULL,
  legend_cols int DEFAULT NULL,
  zoom_enabled int DEFAULT NULL,
  x_axis_label varchar(20) DEFAULT NULL,
  x_axis_min varchar(20) DEFAULT NULL,
  x_axis_max varchar(20) DEFAULT NULL,
  x_axis_tick_angle varchar(20) DEFAULT NULL,
  x_axis_tick_count varchar(20) DEFAULT NULL,
  x_axis_tick_format varchar(20) DEFAULT NULL,
  x_axis_tick_interval varchar(20) DEFAULT NULL,
  y_axis_label varchar(20) DEFAULT NULL,
  y_axis_min varchar(20) DEFAULT NULL,
  y_axis_max varchar(20) DEFAULT NULL,
  add_date_axis int DEFAULT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE data_table_config (
  id serial not null,
  data_table_name varchar(30) NOT NULL,
  data_count_sql varchar(500) NOT NULL,
  data_sql varchar(500) NOT NULL,
  is_default_empty_result boolean NOT NULL,
  is_pagination_supported boolean DEFAULT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE i18n_message (
  id serial not null,
  msg_type varchar(60) NOT NULL,
  msg_key varchar(30) NOT NULL,
  value_zh varchar(128) DEFAULT NULL,
  value_en varchar(128) DEFAULT NULL,
  value_tw varchar(128) DEFAULT NULL,
  institution_id int NOT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE sys_role (
  id serial not null,
  name varchar(50) DEFAULT NULL,
  role_desc varchar(50) DEFAULT NULL,
  institution_id int DEFAULT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE user_role (
  id serial not null,
  user_id int DEFAULT NULL,
  role_id int DEFAULT NULL,
  institution_id int DEFAULT NULL,
  PRIMARY KEY (id)
);



CREATE TABLE demo_data (
  id serial NOT NULL,
  name varchar(255) NOT NULL,
  number1 int  NOT NULL,
  number2 int  NOT NULL DEFAULT,
  number3 decimal(4,2) NOT NULL,
  number4 int  DEFAULT NULL,
  type varchar(10),
  the_date varchar(20) DEFAULT NULL
);

