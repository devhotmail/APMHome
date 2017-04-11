#!/bin/bash
for dir in `ls ./react`;
do
    ( cd ./react/$dir && yarn install && npm run build )
done