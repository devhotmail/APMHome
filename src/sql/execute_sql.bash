#!/bin/bash

set -e
set -u

# geapm-st-psql
export PGHOST="10.120.8.137"
export PGDATABASE="dc1042391d5334b90b09234bc5daa11d4"
export PGUSER="uc1042391d5334b90b09234bc5daa11d4"
export PGPASSWORD="1e35fb586d044d3794980dbe20c96f54"
export PGPORT="5432"

#psql -X -f drop.sql --echo-all --set ON_ERROR_STOP=off
#psql -X -f dump.sql --echo-all --set ON_ERROR_STOP=off

# local pgdb
export PGHOST="localhost"
export PGDATABASE="ge_apm"
export PGUSER="postgres"
export PGPASSWORD="root"
export PGPORT="5432"


psql -X -f drop.sql --echo-all --set ON_ERROR_STOP=off
psql -X -f dump.sql --echo-all --set ON_ERROR_STOP=off
#pg_restore --clean dump.sql
