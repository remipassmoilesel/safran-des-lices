#!/usr/bin/env bash

# You can modify these environment vars to modify site behavior

export SAFRANLICES_FR_DB_NAME=safranlices
# Specify JDBC url without database name
export SAFRANLICES_FR_DB_URL=jdbc:mysql://localhost:3306/
export SAFRANLICES_FR_DB_USERNAME=safranlices
export SAFRANLICES_FR_DB_PASSWORD=safranlices


export SAFRANLICES_FR_DB_BACKUP_DIRECTORY=./db-backup/
export SAFRANLICES_FR_DB_BACKUP_NUMBER=40

export SAFRANLICES_FR_ADMIN_LOGIN=admin
export SAFRANLICES_FR_ADMIN_PASSWORD=admin

export SAFRANLICES_FR_ACTIVE_PROFILE=dev

export SAFRANLICES_FR_PRODUCTS_SOURCE=./products.json
export SAFRANLICES_FR_EXPENSES_SOURCE=./expenses.json

export SAFRANLICES_FR_MAIL_HOST=smtp.mail.com
export SAFRANLICES_FR_MAIL_FROM=user@mail.com
export SAFRANLICES_FR_MAIL_ADMIN_USERS=user1@mail.com,user2@mail.com,user3@mail.com
export SAFRANLICES_FR_MAIL_MAIN=user1@mail.com

export SAFRANLICES_FR_PAYPAL_CLIENT_ID=asafefefzfaz
export SAFRANLICES_FR_PAYPAL_CLIENT_PWD=lknenejncelcnke



