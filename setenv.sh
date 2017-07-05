#!/usr/bin/env bash

# Launch script for Abc-Map
# You can modify environment vars to modify site behavior

export SAFRANLICES_DB_NAME=abcmapfr
# Specify JDBC url without database name
export SAFRANLICES_DB_URL=jdbc:mysql://localhost:3306/
export SAFRANLICES_DB_USERNAME=safranlices
export SAFRANLICES_DB_PASSWORD=safranlices


export SAFRANLICES_DB_BACKUP_DIRECTORY=./db-backup/
export SAFRANLICES_DB_BACKUP_NUMBER=40

export SAFRANLICES_ADMIN_LOGIN=admin
export SAFRANLICES_ADMIN_PASSWORD=admin

export SAFRANLICES_ACTIVE_PROFILE=dev



