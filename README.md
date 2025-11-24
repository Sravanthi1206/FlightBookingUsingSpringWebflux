# Flight Booking System – Spring WebFlux (Reactive)

A fully reactive **Flight Booking System** built using **Spring WebFlux**, **Reactive MongoDB**, and **Project Reactor**.
This project supports airlines, flights, bookings, seats, and search operations with non-blocking reactive APIs.

## Features

### Airlines
- Add new airlines
- Fetch airline details by ID

### Flights
- Add flights with validation
- Search flights by origin & destination
- Fetch flight details
- Fully reactive design (Mono/Flux)

### Bookings
- Create a booking
- Auto PNR generation
- Retry on duplicate key (PNR collision)
- Cancel booking & restore seats
- Search bookings by email
- Get booking by PNR

### Seats
- View seats for a flight
- View specific seat details

### Exception Handling
- Custom `NotFoundException`
- Custom `BadRequestException`
- Global exception handler with structured `ErrorResponse`

## Tech Stack
- Spring Boot 3 (WebFlux)
- Reactive MongoDB
- Project Reactor
- Maven
- JUnit 5, Mockito, WebTestClient, Reactor Test
- JaCoCo coverage

## How to Run

1. Clone Repo  
2. Configure `spring.data.mongodb.uri`  
3. Run:
```
mvn spring-boot:run
```

## Tests
```
mvn test
```

## Coverage
`target/site/jacoco/index.html`

## Build
```
mvn clean package
java -jar target/flight-booking.jar
```
