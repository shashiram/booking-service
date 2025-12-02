# Booking Service

## Overview

A Spring Boot based booking service application.

## Tech Stack

-   **Java 21**
-   **Spring Boot 4**
-   **PostgreSQL**
-   **Maven**

## Running the Application Locally

1.  Ensure PostgreSQL is running and accessible.DB schema file name is db_schema.sql
     - run db schema script
     - setup 5 vehicle record
     - set 25 cleaners, five for each vehicle
     - Run createDailySlotsCron cron to prepopulate all time slot for the day.

2.  Configure your database credentials in `application.yml`.

3.  Build the project:

    ``` bash
    mvn clean install
    ```

4.  Run the application by starting the `BookingServiceApplication` main
    class:

    ``` bash
    ./mvnw spring-boot:run
    ```

    Or run it directly from your IDE.
5. swagger url : http://localhost:8080/swagger-ui/index.html


    