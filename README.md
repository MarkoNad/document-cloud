# Document Cloud

## Table of Contents
* [General Info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)
    * [Database](#database)
    * [Backend](#backend)
    * [Frontend](#frontend)

## General Info
Document Cloud is a three-tier personal cloud web application.
It consists of a front end that exposes functionalities such as file or directory upload and download, 
and image viewing.
The front end communicates with a standalone Spring Boot back-end application which is in charge of 
storing documents to the database.

## Technologies
This project uses the folowing technologies:
* Java version: 1.8
* Spring Boot version: 2.3.3
* PostgreSQL 13.1
* HTML, CSS, JavaScript
* Bootstrap version: 3.3.6
* jQuery version: 2.1.4
	
## Setup
The setup the application, you need to install the database, run the back end, and host the front end on a web server.

### Database

* Install Postgres:
```
$ sudo apt-get update
$ sudo apt-get -y install postgresql
```

* Add the following line to pg_hba.conf:
```
host    all             all             127.0.0.1/32           md5
$ sudo vim /etc/postgresql/13/main/pg_hba.conf
```

* Create a new database user:
```
$ sudo -u postgres createuser --interactive
$ sudo -u postgres createdb documentcloud
$ sudo adduser documentcloud
$ sudo -u documentcloud psql
```

* Create a password for the new user:
```
$ sudo -u documentcloud psql
```

* Determine the data directory and use it in the next command:
```
$ sudo -u postgres psql -c "show data_directory;"
```

* Start Postgres:
```
$ sudo -u postgres /usr/lib/postgresql/13/bin/pg_ctl start -D "/etc/postgresql/13/main"
```

### Backend

* Edit application.properties file:
    * set the port on which the back end should be running
    * set database connection details

* Run the following command from the project root (the directory which contains the pom.xml file):
```
$ mvn spring-boot:run
```

Running the application will automatically create database objects because of the following property
```
spring.jpa.hibernate.ddl-auto=update
```

### Frontend

* Install NGINX:
```
$ sudo apt-get update
$ sudo apt-get install nginx
```

* Edit the NGINX configuration file 'nginx.conf' located in directory other' of this project:
    * change server name
    * change certificate information
    * set the port to the port on which the back end is running

* Add the configuration file to NGINX's conf.d directory:
```
$ sudo cp ./other/nginx.conf /etc/nginx/conf.d/
```

* Host the frontend:
```
$ sudo cp -r ./frontend/* /var/www/html/document-cloud/
```

* Restart NGINX:
```
$ sudo systemctl reload nginx
$ tail -f /var/log/nginx/access.log
```
