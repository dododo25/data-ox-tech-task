# data-ox tech task
## Prerequesties
Java 17 - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html <br />
PostgreSQL 17 - https://www.postgresql.org/download/ <br />
The latest version of Docker - https://www.docker.com/ <br />

## Installment
1. Download this repository into your computer, either by:
   * executing <code>git clone https://github.com/dododo25/data-ox-tech-task</code> using the terminal.
   * navigating to this link https://github.com/dododo25/data-ox-tech-task/archive/refs/heads/main.zip and unzip its files.
2. Using terminal, navigate to the folder where this project is located and execute<code>docker compose up -d</code>

## Scenarios
There are three scenarios that are available at this time. <br />To access them:
1. Navigate to \"<a href="https://github.com/dododo25/data-ox-tech-task/releases/latest">Releases</a>\" page and download the latest versions of jar file.
2. Execute <code>java -jar data-ox-scenario.jar --scenario <<scenario_number>></code>.

## Swagger
To access Swagger API, type in your browser: <br />
http://localhost:8081/swagger-ui/index.html - (for client APIs) <br />
http://localhost:8082/swagger-ui/index.html - (for admin APIs)
