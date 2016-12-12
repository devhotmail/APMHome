#!/bin/bash

cd "${0%/*}"
cat ../create_tables.sql ../init_data.sql ../i18n_message.sql ../test_data.sql > test_date.sql
#cf login -a https://api.system.aws-jp01-pr.ice.predix.io
cf push
