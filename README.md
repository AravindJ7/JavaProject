# Expense Split Backend

A Spring Boot web application for tracking shared expenses, settling balances among friends, and generating group-wise expense reports.

## Features

- **User Management**: Create and manage users
- **Group Management**: Create groups and add members
- **Expense Tracking**: Add expenses with amount, payer, and participants
- **Balance Calculation**: Automatic calculation of balances per user
- **Settlement Tracking**: Track who owes whom in each group
- **RESTful API**: Complete REST API for all operations

## Tech Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Java Version**: 17

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Setup Instructions

### 1. Database Setup

1. Install PostgreSQL on your Linux system:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

2. Start PostgreSQL service:
```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

3. Create database and user:
```bash
sudo -u postgres psql
```

In PostgreSQL shell:
```sql
CREATE DATABASE expense_split_db;
CREATE USER expense_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE expense_split_db TO expense_user;
\q
```

### 2. Application Configuration

Update the database configuration in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_split_db
spring.datasource.username=expense_user
spring.datasource.password=password
```

### 3. Build and Run

1. Navigate to project directory:
```bash
cd /home/aravind/JavaFinal
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Group Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/groups` | Create a new group |
| GET | `/api/groups` | Get all groups |
| GET | `/api/groups/{id}` | Get group by ID |
| POST | `/api/groups/{id}/members` | Add member to group |
| GET | `/api/groups/{id}/members` | Get group members |
| DELETE | `/api/groups/{id}/members/{userId}` | Remove member from group |
| GET | `/api/groups/user/{userId}` | Get groups by user ID |
| DELETE | `/api/groups/{id}` | Delete group |

### Expense Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/expenses` | Create a new expense |
| GET | `/api/expenses/group/{groupId}` | Get expenses by group ID |
| GET | `/api/expenses/{id}` | Get expense by ID |
| GET | `/api/expenses/{id}/participants` | Get expense participants |
| DELETE | `/api/expenses/{id}` | Delete expense |

### Balance Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/balances/group/{groupId}` | Get balances for group |

## Sample API Usage

### 1. Create Users

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "contactNo": "+1234567890"
  }'
```

### 2. Create Group

```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Trip to Paris"
  }'
```

### 3. Add Members to Group

```bash
curl -X POST http://localhost:8080/api/groups/1/members \
  -H "Content-Type: application/json" \
  -d '1'
```

### 4. Create Expense

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": 1,
    "paidByUserId": 1,
    "amount": 100.00,
    "description": "Dinner at restaurant",
    "participantUserIds": [1, 2, 3]
  }'
```

### 5. Get Group Balances

```bash
curl -X GET http://localhost:8080/api/balances/group/1
```

## Database Schema

The application uses the following main entities:

- **Users**: Store user information
- **Groups**: Store expense groups
- **GroupMembers**: Track group memberships
- **Expenses**: Store expense records
- **ExpenseParticipants**: Track expense participation and shares

## Development

### Project Structure

```
src/main/java/com/expensesplit/
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # Data access layer
├── model/              # JPA entities
├── dto/                # Data transfer objects
├── config/             # Configuration classes
└── ExpenseSplitApplication.java
```

### Running Tests

```bash
mvn test
```

### Building JAR

```bash
mvn clean package
```

The JAR file will be created in `target/expense-split-backend-1.0.0.jar`

## Troubleshooting

### Common Issues

1. **Database Connection Error**: Ensure PostgreSQL is running and credentials are correct
2. **Port Already in Use**: Change the port in `application.properties` or stop the conflicting service
3. **Maven Build Issues**: Ensure Java 17 is installed and Maven is properly configured

### Logs

Application logs are configured to show SQL queries and debug information. Check the console output for detailed information.

## Testing Results

The application has been successfully tested with the following scenarios:

### Test Data Created:
1. **Users**: John Doe, Jane Smith, Bob Wilson
2. **Group**: "Trip to Paris" with all 3 users as members
3. **Expenses**: 
   - Dinner at restaurant: $150.00 (paid by John, split among all 3)
   - Hotel room: $90.00 (paid by Jane, split among all 3)

### API Endpoints Tested:
- ✅ `POST /api/users` - Create users
- ✅ `GET /api/users` - List all users
- ✅ `POST /api/groups` - Create group
- ✅ `POST /api/groups/{id}/members` - Add members to group
- ✅ `GET /api/groups/{id}/members` - List group members
- ✅ `POST /api/expenses` - Create expenses
- ✅ `GET /api/expenses/group/{id}` - List group expenses
- ✅ `GET /api/balances/group/{id}` - Calculate balances

### Balance Calculation Results:
- **John Doe**: Paid $150.00, Owed $80.00, **Net Balance: +$70.00** (owed money)
- **Jane Smith**: Paid $90.00, Owed $80.00, **Net Balance: +$10.00** (owed money)
- **Bob Wilson**: Paid $0.00, Owed $80.00, **Net Balance: -$80.00** (owes money)

### Database:
- ✅ PostgreSQL database working correctly
- ✅ All tables created with proper relationships
- ✅ Foreign key constraints working

## Future Enhancements

- Authentication and authorization using Spring Security
- Different split strategies (percentage, custom amounts)
- Settlement suggestions
- Export functionality (CSV, PDF)
- Thymeleaf UI integration
- Real-time notifications
