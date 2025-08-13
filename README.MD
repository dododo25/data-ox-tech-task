# data-ox tech task
## Prerequesties
Java 17 - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html <br />
PostgreSQL 17 - https://www.postgresql.org/download/ <br />
Existing PostgreSQL user: name=postgres, password=postgres

## Installment
1. Open your terminal and type<code>psql -U postgres -d postgres</code>. If you're receiving an error, please refer to this page: https://www.commandprompt.com/education/how-to-fix-psql-command-not-found-error-in-postgresql/
1. Execute this command:<code>create database dataox owner postgres template template0;</code>
2. Navigate to \"<a href="https://github.com/dododo25/data-ox-tech-task/releases/latest">Releases</a>\" page and download the latest versions of jar files.
3. On your PC, open a terminal and navigate to the folder with the files that you downloaded on the step 1.
4. Execute this command: <code>java -jar data-ox-task-migrations.jar</code> and wait until it completes.
4. Execute this command: <code>java -jar data-ox-task-admin.jar</code>
5. Open another terminal and navigate to the folder with the files that you downloaded on the step 1.
6. Execute this command: <code>java -jar data-ox-task-client.jar</code>

## Scenarios
There are three scenarios that are available at this time. <br />To access them, execute <code>java -jar data-ox-scenario.jar --scenario <<scenario_number>></code>.

## Swagger
To access Swagger API, type in your browser: <br />
http://localhost:8081/swagger-ui/index.html - (for client APIs) <br />
http://localhost:8082/swagger-ui/index.html - (for admin APIs)
