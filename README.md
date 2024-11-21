# CS203-Group5 Application

## Table of Contents
- [Project Overview](#project-overview)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Step-by-Step Setup](#step-by-step-setup)
- [Running the Application](#running-the-application)
  - [Frontend](#frontend)
  - [Backend](#backend)
- [Database Setup](#database-setup)
- [Using the Application](#using-the-application)

## Project Overview
This is a full-stack web application developed by CS203-Group5, with a backend powered by Spring Boot and a frontend using Streamlit. The backend interacts with a MySQL database, and the frontend provides a user-friendly interface for interacting with the system.

## Setup Instructions (deployed)

1. Access the web app via the link https://cs203group5-climbrank.com/
2. To use the web app as a player, follow the regular sign-up and login flow.
3. TO use the web app as an admin, use the following credentials:
   - Username: huiii
   - Password: Kuek12345!

## Setup Instructions (localhost)

### Prerequisites
Before you begin, make sure you have the following installed on your machine:
- [WAMP (or any localhost server)](https://www.wampserver.com/en/)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/)
- [Python 3.x](https://www.python.org/downloads/)
- [Java JDK 8+](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)

### Step-by-Step Setup

1. **Set up MySQL Database:**
   - Start your localhost server, such as WAMP.
   - Create a new database called `cs203-db` in your MySQL server.

2. **Install Python Dependencies:**
   - Open the project in your preferred IDE and navigate to the root project folder `CS203-Group5`.
   - Run the following command to install the required Python packages:
     ```bash
     pip install -r requirements.txt
     ```

3. **Run the Frontend and Backend:**
   - Open two separate terminal windows for the frontend and backend.
     - For the frontend:
       ```bash
       cd frontend
       streamlit run main.py
       ```
     - For the backend:
       ```bash
       cd backend
       .\mvnw spring-boot:run
       # OR, if you're using Maven installed globally:
       mvn spring-boot:run
       ```

### Database Setup

After the backend server is up and running, set up the necessary database procedures:

1. Open MySQL Workbench or any other MySQL client.
2. Run all SQL scripts located in the `backend/src/main/resources/sql` folder on your MySQL server. These scripts will set up the database tables and procedures required for the application.

### Using the Application

Once both the frontend and backend are running:
- Leave both terminal windows open and ensure they stay active.
- Navigate to the frontend application and **register for a new account** to begin using the system.

### Notes:
- Ensure your database server is running during the entire process.
- If you encounter any errors during setup, check that all dependencies are correctly installed.
- If at any point you receive 401 error, please relogin with your registered account!

---

For any additional queries or troubleshooting, please refer to the project's documentation or contact the development team.
