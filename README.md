# Safran des Lices

[![Build Status](https://travis-ci.org/remipassmoilesel/safran-des-lices.svg?branch=master)](https://travis-ci.org/remipassmoilesel/safran-des-lices)

## Launch a demo

You need a functionnal MySQL server. On Ubuntu 17.04:

    $ sudo apt install mysql-server
    
Clone the project:

    $ git clone https://github.com/remipassmoilesel/safran-des-lices
    $ cd safran-des-lices
    
Install dependencies:    
    
    $ src/main/resources/static
    $ bower install
    
Configure a database:

    $ ./setup-db.sh
    
Launch embedded HTTP server:
    
	$ ./launcher-example.sh

After visit http://localhost:8085 .

## IntelliJ configuration

If you want to embed env vars without use the form of IntelliJ, you can include them 
temporarily in your /etc/environment file (see setenv.sh)