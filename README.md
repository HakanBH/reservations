Deployment guide:
  Required files:
	- reservationsScript.sql 
	- eureka-server.jar
	- reservations.jar
	- search-and-register.jar
	- admin-panel.jar
	- security-commons.jar
  On Windows:
	I. Install required software :
		1. JDK 1.8, can be downloaded from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
		2. PostgreSQL 9.4.10 - http://www.enterprisedb.com/products-services-training/pgdownload#windows or https://www.postgresql.org/
	    
	II. Create the databse:
		- Open Control Panel -> System -> Advanced System Settings. Open Environment Variables. Find the "Path" variable in the System variables list and click edit. Add the paths to the \bin and \lib folders,
		    in your postgreSQL installation directory ( "C:\Program Files\PostgreSQL\9.4\lib" and "C:\Program Files\PostgreSQL\9.4\bin" by default )
		
		1. Open cmd and go to the directory where reservationsScript.sql is located
		2. "createdb -U postgres reservations"
		3. "psql -U postgres -d reservations"
		4. "\i reservationsScript.sql"
	
	III. Run the application:
		Epam reservations has a microservice architecture, so each of the services must be started seperately in the given manner.

		1. Go to the directory where the provided .jar file are located.
		2. Open cmd and run "java -jar eureka-server.jar" to run the eureka discovery server.
		3. Open cmd and run "java -jar search-and-register-service.jar" to run the main service of the application.
		4. Open cmd and run "java -jar reservations-service.jar" to run the main service of the application.
		5. Open cmd and run "java -jar admin-panel.jar" to run the main service of the application.

