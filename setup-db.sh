#!/usr/bin/env bash

sudo mysql -u root -e "CREATE DATABASE safranlices"
sudo mysql -u root -e "CREATE DATABASE safranlicestest"
sudo mysql -u root -e "CREATE USER 'safranlices'@'localhost' IDENTIFIED BY 'safranlices';"

sudo mysql -u root -e "GRANT ALL ON safranlices.* TO 'safranlices'@'localhost';"
sudo mysql -u root -e "GRANT ALL ON safranlicestest.* TO 'safranlices'@'localhost';"
