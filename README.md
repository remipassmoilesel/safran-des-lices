# Safran des Lices

## Launch a demo

You need a functionnal MySQL server. On Ubuntu 17.04:

    $ sudo apt install mysql-server
    
Clone the project:

    $ git clone https://github.com/remipassmoilesel/safran-des-lices
    $ cd safran-des-lices
    $ git submodule init
    $ git submodule update
    
Configure a database:

    $ ./setup-db.sh
    
Launch embedded HTTP server:
    
	$ ./launcher-example.sh

After visit http://localhost:8085 .
