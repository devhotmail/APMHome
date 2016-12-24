#!/bin/bash

set -e
set -u

# geapm-st-psql
#export PGHOST="localhost"
#export PGDATABASE="ddf7cc4c6ad0a4f1384eea37fab819440"
#export PGUSER="udf7cc4c6ad0a4f1384eea37fab819440"
#export PGPASSWORD="781178f115d442b794bc5b23b3ca844a"
#export PGPORT="7999"

# local pgdb
export PGHOST="localhost"
export PGDATABASE="ge_apm"
export PGUSER="postgres"
export PGPASSWORD="root"
export PGPORT="5432"

psql -X -f create_tables.sql --echo-all --set ON_ERROR_STOP=on
psql -X -f init_data.sql --echo-all --set ON_ERROR_STOP=on
psql -X -f i18n_message.sql --echo-all --set ON_ERROR_STOP=on
# psql -X -f demo_data.sql --echo-all --set ON_ERROR_STOP=on
# psql -X -f test_data.sql --echo-all --set ON_ERROR_STOP=on
psql -X -f test_data.sql --echo-all --set ON_ERROR_STOP=on
psql -X -f alter_database.sql --echo-all --set ON_ERROR_STOP=off
