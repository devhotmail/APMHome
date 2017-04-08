#!/bin/bash
for dir in `ls ./react`;
do
    ( cd ./react/$dir && npm run build )
done