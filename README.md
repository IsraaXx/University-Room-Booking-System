# Room Booking System

A comprehensive Spring Boot application for managing university room bookings, built with modern Java technologies and best practices.

## 🚀 Features

- **Room Management**: Create, update, and manage rooms across different buildings
- **Booking System**: Schedule and manage room bookings with conflict detection
- **User Management**: Role-based access control with different user types
- **Department Support**: Organize users by departments
- **Holiday Management**: Configure holidays and non-working days
- **Audit Trail**: Complete booking history and change tracking
- **RESTful API**: Clean, documented REST endpoints
- **Security**: Spring Security integration with JWT authentication

## 🛠️ Technology Stack

- **Java 17** - Latest LTS version
- **Spring Boot 3.5.4** - Modern Spring framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **MySQL** - Relational database
- **Liquibase** - Database migration and versioning
- **Maven** - Build and dependency management
- **Lombok** - Reduce boilerplate code

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/room-booking-system.git
cd room-booking-system
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE room_booking_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configuration

The application uses Spring profiles for different environments:

- **Development**: `application-dev.yml` (default)
- **Production**: `application-prod.yml`

For development, update `src/main/resources/application-dev.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/room_booking_system?useSSL=false&serverTimezone=UTC
    username: your_username
    password: your_password
```

### 4. Run the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### 5. Database Migration

Liquibase will automatically create the database schema on first run. The initial schema includes:

- Users and roles
- Departments
- Buildings and rooms
- Room features
- Bookings and booking history
- Holidays

## 🔧 Configuration

### Profiles

- **dev**: Development environment with debug logging and auto-update schema
- **prod**: Production environment with optimized settings and file logging

### Environment Variables (Production)

```bash
export DB_URL=jdbc:mysql://your-db-host:3306/room_booking_system
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
export SERVER_PORT=8080
```

### Logging

- **Development**: Console logging with DEBUG level for application packages
- **Production**: File logging with INFO level, rotating log files

## 📚 API Documentation

Once the application is running, you can access:

- **Health Check**: `GET /api/actuator/health`
- **Application Info**: `GET /api/actuator/info`
- **Metrics**: `GET /api/actuator/metrics`

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

## 🚀 Deployment

### Production Build

```bash
./mvnw clean package -Pprod
```

### Docker (Optional)

```bash
docker build -t room-booking-system .
docker run -p 8080:8080 room-booking-system
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/sprints/room_booking_system/
│   │       ├── model/          # Entity classes
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic
│   │       ├── controller/     # REST endpoints
│   │       └── config/         # Configuration classes
│   └── resources/
│       ├── db/
│       │   └── changelog/      # Liquibase migrations
│       ├── application.yml     # Main configuration
│       ├── application-dev.yml # Development profile
│       ├── application-prod.yml # Production profile
│       └── logback-spring.xml  # Logging configuration
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/room-booking-system/issues) page
2. Create a new issue with detailed information
3. Contact the development team

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial development version with basic CRUD operations

---

**Built with ❤️ using Spring Boot and modern Java technologies**
