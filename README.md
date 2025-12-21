# Electronic Voting System

A comprehensive, secure, and modern electronic voting system designed for colleges and companies to conduct fair, transparent, and efficient elections with advanced features including OTP verification, email integration, real-time analytics, and bulk user management.

## 🌟 Features

### Core Functionality
- **Secure Authentication**: OTP-based email verification for voter registration
- **Role-Based Access Control**: Admin and Voter roles with different permissions
- **Election Management**: Create, schedule, activate, and manage elections
- **Candidate Management**: Add candidates with detailed profiles and information
- **Voting System**: Secure vote casting with one-time vote per election per user
- **Real-time Analytics**: Live election results and voter participation statistics
- **Email Integration**: Automated invitations, announcements, and notifications
- **Announcement System**: Broadcast important election information
- **Bulk User Management**: Import voter lists from Excel files

### Security Features
- **Spring Security Integration**: Comprehensive authentication and authorization
- **OTP Verification**: Email-based one-time password for account verification
- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session handling
- **CSRF Protection**: Cross-site request forgery protection
- **Input Validation**: Comprehensive data validation and sanitization

### User Experience
- **Responsive Design**: Mobile-friendly interface using Bootstrap 5
- **Modern UI/UX**: Clean, professional interface with FontAwesome icons
- **Real-time Updates**: Live data updates and notifications
- **Multi-language Support**: Ready for internationalization
- **Accessibility**: WCAG compliant design elements

## 🛠 Technology Stack

### Backend
- **Java 17**: Modern Java development
- **Spring Boot 3.2.0**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **Spring Web**: REST API development
- **Spring Thymeleaf**: Server-side templating
- **Spring Mail**: Email services
- **Hibernate**: ORM framework

### Database
- **Microsoft SQL Server**: Primary database
- **Hibernate DDL**: Automatic schema generation
- **JPA Annotations**: Entity mapping and validation

### Frontend
- **HTML5**: Modern web standards
- **Bootstrap 5**: Responsive CSS framework
- **Thymeleaf**: Server-side templating
- **FontAwesome**: Icon library
- **Vanilla JavaScript**: Client-side functionality
- **AJAX**: Asynchronous data fetching

### Development Tools
- **Maven**: Build and dependency management
- **Spring DevTools**: Development utilities
- **Actuator**: Application monitoring
- **H2 Console**: Database visualization (disabled in production)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Microsoft SQL Server 2019 or higher
- Email SMTP server (Gmail, Outlook, etc.)

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd electronic-voting-system
```

### 2. Database Setup

#### Microsoft SQL Server Configuration
1. Install Microsoft SQL Server
2. Create a new database:
```sql
CREATE DATABASE ElectronicVotingSystem;
```

3. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ElectronicVotingSystem;encrypt=false;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Email Configuration

Configure SMTP settings in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 4. Build and Run

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## 🎯 User Roles & Capabilities

### Admin Users
- **Election Management**: Create, edit, schedule, activate, and delete elections
- **Candidate Management**: Add, edit, and manage candidates
- **User Management**: Import voters, manage user accounts
- **Announcements**: Create and publish announcements
- **Analytics**: View detailed election analytics and reports
- **Bulk Operations**: Import voter lists from Excel files

### Voter Users
- **Registration**: Self-registration with OTP verification
- **Voting**: Cast votes in active elections
- **History**: View personal voting history
- **Announcements**: View election-related announcements
- **Results**: View election results after completion

## 📡 API Endpoints

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/admin-login` - Admin login
- `POST /api/auth/register-voter` - Voter registration
- `POST /api/auth/verify-otp` - OTP verification
- `POST /api/auth/resend-otp` - Resend OTP
- `POST /api/auth/create-admin` - Create admin account

### Admin Endpoints
- `GET /api/admin/dashboard/stats` - Dashboard statistics
- `GET /api/admin/elections` - Get all elections
- `POST /api/admin/elections` - Create election
- `PUT /api/admin/elections/{id}` - Update election
- `DELETE /api/admin/elections/{id}` - Delete election
- `POST /api/admin/elections/{id}/schedule` - Schedule election
- `POST /api/admin/elections/{id}/activate` - Activate election
- `POST /api/admin/elections/{id}/complete` - Complete election
- `POST /api/admin/candidates` - Create candidate
- `GET /api/admin/elections/{electionId}/candidates` - Get candidates
- `POST /api/admin/announcements` - Create announcement
- `POST /api/admin/announcements/{id}/publish` - Publish announcement
- `POST /api/admin/voters/import` - Import voters from Excel

### Voter Endpoints
- `GET /api/voter/elections/active` - Get active elections
- `GET /api/voter/elections/{electionId}/vote` - Get election for voting
- `POST /api/voter/elections/{electionId}/vote` - Cast vote
- `GET /api/voter/votes/history` - Get voting history
- `GET /api/voter/announcements` - Get announcements
- `GET /api/voter/elections/upcoming` - Get upcoming elections
- `GET /api/voter/elections/{electionId}/results` - Get election results

## 🏗 Project Structure

```
src/main/java/com/evoting/
├── ElectronicVotingSystemApplication.java  # Main application class
├── config/
│   └── SecurityConfig.java                 # Security configuration
├── controller/
│   ├── AuthController.java                 # Authentication endpoints
│   ├── AdminController.java                # Admin management endpoints
│   ├── VoterController.java                # Voter endpoints
│   ├── WebController.java                  # Web page routing
│   └── HealthController.java               # Health check endpoint
├── model/
│   ├── User.java                          # User entity
│   ├── Election.java                      # Election entity
│   ├── Candidate.java                     # Candidate entity
│   ├── Vote.java                          # Vote entity
│   ├── Announcement.java                  # Announcement entity
│   ├── UserRole.java                      # User roles enum
│   ├── ElectionStatus.java                # Election status enum
│   ├── CandidateStatus.java               # Candidate status enum
│   ├── AnnouncementType.java              # Announcement types
│   └── AnnouncementPriority.java          # Announcement priorities
├── repository/
│   ├── UserRepository.java                # User data access
│   ├── ElectionRepository.java            # Election data access
│   ├── CandidateRepository.java           # Candidate data access
│   ├── VoteRepository.java                # Vote data access
│   └── AnnouncementRepository.java        # Announcement data access
├── service/
│   ├── UserService.java                   # User business logic
│   ├── ElectionService.java               # Election business logic
│   ├── CandidateService.java              # Candidate business logic
│   ├── VoteService.java                   # Vote business logic
│   ├── AnnouncementService.java           # Announcement business logic
│   ├── EmailService.java                  # Email functionality
│   └── CandidateService.java              # Candidate analytics
└── security/
    └── CustomUserDetailsService.java      # User details service

src/main/resources/
├── templates/
│   ├── index.html                         # Landing page
│   ├── login.html                         # Login page
│   ├── admin-dashboard.html               # Admin dashboard
│   └── voter-dashboard.html               # Voter dashboard
└── application.properties                 # Application configuration
```

## 🚀 Key Features Implemented

### 1. OTP-Based Registration System
- Email verification using 6-digit OTP codes
- 15-minute expiration for OTP codes
- Resend OTP functionality
- Automatic account activation upon verification

### 2. Election Lifecycle Management
- **Draft**: Initial creation phase
- **Scheduled**: Set future date/time
- **Active**: Voting period open
- **Completed**: Election ended, results available
- **Cancelled**: Election stopped

### 3. Comprehensive Voting System
- One vote per user per election
- Secure vote recording with IP and session tracking
- Real-time vote counting and analytics
- Candidate ranking and percentage calculations

### 4. Email Integration
- **Welcome emails** for new registrations
- **OTP codes** for account verification
- **Voting invitations** for active elections
- **Announcement notifications** for new broadcasts
- **Election results** notifications upon completion

### 5. Admin Dashboard
- Real-time statistics and metrics
- Election management interface
- Candidate management tools
- User import functionality
- Announcement creation and publishing
- Analytics and reporting

### 6. Voter Dashboard
- Active elections overview
- Upcoming elections calendar
- Personal voting history
- Latest announcements
- Election results viewing

### 7. Security Implementation
- Spring Security role-based access control
- BCrypt password encryption
- OTP verification for sensitive operations
- CSRF protection
- Session management
- Input validation and sanitization

## 🔮 Future Enhancements

### Planned Features
- **Mobile Application**: React Native mobile app
- **Blockchain Integration**: Immutable vote recording
- **Advanced Analytics**: Machine learning insights
- **Multi-language Support**: Internationalization
- **Audit Trail**: Complete voting audit logs
- **API Documentation**: Swagger/OpenAPI integration
- **Bulk SMS**: SMS notifications alongside email
- **Advanced Reporting**: PDF report generation

### Technical Improvements
- **Microservices Architecture**: Service decomposition
- **Redis Caching**: Performance optimization
- **Message Queues**: Asynchronous processing
- **Load Balancing**: High availability
- **Docker Support**: Containerized deployment
- **Kubernetes**: Orchestration and scaling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -am 'Add feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact: support@evotingsystem.com
- Documentation: [Wiki](wiki-url)

## 🔒 Security

This application implements industry-standard security practices. For security concerns, please email security@evotingsystem.com instead of creating public issues.

---

**Built with ❤️ for secure and transparent voting**
#   e l e c t i o n -  
 