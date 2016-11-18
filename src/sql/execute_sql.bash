#!/bin/bash

set -e
set -u

export PGHOST="localhost"
export PGDATABASE="ge_apm"
export PGUSER="postgres"
export PGPASSWORD="root"

psql -X -f create_tables.sql --echo-all --set AUTOCOMMIT=off --set ON_ERROR_STOP=on
psql -X -f init_data.sql --echo-all --set AUTOCOMMIT=off --set ON_ERROR_STOP=on
psql -X -f init_data.sql --echo-all --set AUTOCOMMIT=off --set ON_ERROR_STOP=on
psql -X -f demo_data.sql --echo-all --set AUTOCOMMIT=off --set ON_ERROR_STOP=on

ruby insert_datetimes.rb
