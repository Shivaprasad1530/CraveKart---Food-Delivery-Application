# 🍔 CraveKart — Food Delivery Web Application

**CraveKart** is a Java-based food delivery web application developed using **Java Servlets, JSP, JDBC, MySQL, HTML, CSS, and Apache Tomcat**.

The project follows the **MVC (Model–View–Controller)** architecture and provides functionality for customers to browse restaurants, view menus, manage their cart, and place orders.

The main objective of this project was to build a complete server-side food delivery application while gaining practical experience with **Java EE/Jakarta EE concepts, Servlets, JSP, JDBC, session management, DAO design, database relationships, and MVC architecture**.

---

## 🚀 Features

# 👥 Multi-Role System

CraveKart implements a **role-based system** where different types of users have different interfaces, permissions, and responsibilities.

Each role has access only to the functionality required for its tasks.

| Role                      | Responsibilities                                                              |
| ------------------------- | ----------------------------------------------------------------------------- |
| 👤 **Customer**           | Browse restaurants, view menus, manage cart, place orders, and track orders   |
| 🏪 **Restaurant Manager** | Manage restaurant information, manage menu items, and process incoming orders |
| 🛵 **Delivery Agent**     | View assigned deliveries, update delivery status, and manage assigned orders  |

---

## 👤 Customer Interface

Customers interact with CraveKart through a dedicated customer interface.

### Customer Features

* Register and log in
* Browse available restaurants
* View restaurant details
* Browse restaurant menus
* View menu item details
* Add items to cart
* Update item quantities
* Remove items from cart
* View total cart amount
* Place orders
* View order details
* Track order status
* Logout

### Customer Flow

```text
Customer
   │
   ▼
Login / Signup
   │
   ▼
Browse Restaurants
   │
   ▼
Select Restaurant
   │
   ▼
View Menu
   │
   ▼
Add Items to Cart
   │
   ▼
Review Cart
   │
   ▼
Place Order
   │
   ▼
Track Order
```

---

## 🏪 Restaurant Manager Interface

Restaurant managers have a separate interface designed specifically for managing their restaurant operations.

### Restaurant Manager Features

* Restaurant manager authentication
* Access their restaurant dashboard
* View restaurant information
* Manage restaurant menu
* Add menu items
* Update menu items
* Remove menu items
* Manage item availability
* View incoming customer orders
* Process customer orders
* Update order status

### Restaurant Manager Flow

```text
Restaurant Manager
        │
        ▼
      Login
        │
        ▼
 Restaurant Dashboard
        │
        ├──────────────► Manage Restaurant
        │
        ├──────────────► Manage Menu
        │                       │
        │                       ├── Add Item
        │                       ├── Update Item
        │                       └── Remove Item
        │
        └──────────────► Manage Orders
                                │
                                ▼
                         Process Order
```

The restaurant manager is restricted to operations related to their own restaurant rather than having access to customer or delivery-agent functionality.

---

## 🛵 Delivery Agent Interface

Delivery agents have a separate interface focused on fulfilling and updating deliveries.

### Delivery Agent Features

* Delivery agent authentication
* View assigned orders
* View delivery details
* View customer/order information required for delivery
* Accept/manage assigned deliveries
* Update delivery status
* Mark orders as delivered

### Delivery Agent Flow

```text
Delivery Agent
      │
      ▼
    Login
      │
      ▼
Delivery Dashboard
      │
      ▼
View Assigned Orders
      │
      ▼
View Delivery Details
      │
      ▼
Update Delivery Status
      │
      ▼
Order Delivered
```

---

# 🔐 Role-Based Access

CraveKart uses **role-based access control (RBAC)** to separate the functionality available to each type of user.

After authentication, the application identifies the user's role and provides the appropriate interface.

```text
                         ┌─────────────────┐
                         │      Login      │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │ Identify Role   │
                         └────────┬────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             │                    │                    │
             ▼                    ▼                    ▼
       ┌───────────┐       ┌───────────────┐    ┌──────────────┐
       │ Customer  │       │Restaurant Mgr │    │Delivery Agent│
       └─────┬─────┘       └───────┬───────┘    └──────┬───────┘
             │                     │                   │
             ▼                     ▼                   ▼
       Customer UI            Manager UI          Delivery UI
             │                     │                   │
             ▼                     ▼                   ▼
       Place Orders          Manage Menu          Manage Delivery
       Manage Cart           Process Orders       Update Status
       Track Orders          Restaurant Ops       Assigned Orders
```

This separation ensures that each role sees and performs only the operations relevant to them.

---

# 🔄 Complete CraveKart Workflow

The overall application workflow can be represented as:

```text
                         CRAVEKART
                             │
                         User Login
                             │
                    ┌────────┴────────┐
                    │                 │
                 Role Check            │
                    │                 │
        ┌───────────┼───────────┐     │
        │           │           │     │
        ▼           ▼           ▼     │
    Customer    Restaurant    Delivery │
                 Manager       Agent   │
        │           │           │      │
        ▼           ▼           ▼      │
   Browse Food   Manage Menu   Assigned│
        │         & Orders     Orders  │
        ▼           │           │      │
   Add to Cart      │           │      │
        │           │           │      │
        ▼           │           │      │
   Place Order ─────┘           │      │
        │                       │      │
        ▼                       ▼      │
   Order Processing       Delivery     │
        │                 Management   │
        └──────────────┬───────────────┘
                       │
                       ▼
                Order Completed
```

---

# 🧩 Role-Based Responsibilities

The application's responsibilities are distributed among the three major roles.

### Customer

```text
Discover → Select Restaurant → Select Food
        → Cart → Place Order → Track Order
```

### Restaurant Manager

```text
Restaurant Management → Menu Management
                      → Receive Orders
                      → Process Orders
```

### Delivery Agent

```text
View Assigned Delivery → Manage Delivery
                       → Update Status
                       → Complete Delivery
```

This design allows CraveKart to simulate a real-world food delivery ecosystem where **customers, restaurants, and delivery personnel interact with the same underlying system but have completely different responsibilities**.

---

# 🏗️ Role-Based MVC Architecture

The role-based functionality is implemented on top of the MVC architecture.

```text
                         Browser
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
        Customer UI    Manager UI     Delivery UI
             │              │              │
             └──────────────┼──────────────┘
                            │
                            ▼
                     Java Servlets
                            │
                  Role / Request Handling
                            │
                            ▼
                          DAO Layer
                            │
                            ▼
                         MySQL DB
```

The **Servlet layer** handles incoming requests and determines the appropriate application flow, while the **DAO layer** handles database operations.

This separation keeps presentation, business/request handling, and database access organized and maintainable.

---

# 📌 Key Project Highlight

One of the major aspects of CraveKart is its **multi-role architecture**.

Instead of treating every authenticated user the same way, the application models different real-world participants in a food delivery platform:

> **Customers order food → Restaurant Managers prepare and process orders → Delivery Agents handle delivery.**

This provides a more realistic workflow and demonstrates practical understanding of:

* Role-based access
* Authentication and authorization concepts
* Session management
* MVC architecture
* Servlet-based request handling
* DAO architecture
* Relational database relationships
* Multi-user application design
* CRUD operations
* Order lifecycle management


### 👤 User Management

* User registration
* User login and logout
* Session-based authentication
* User profile information management

### 🍽️ Restaurant & Menu

* Browse available restaurants
* View restaurant details
* View restaurant menus
* Display cuisine, ratings, estimated delivery time, and other restaurant information
* View individual menu item details

### 🛒 Shopping Cart

* Add menu items to cart
* Update item quantities
* Remove items from cart
* Calculate total cart amount
* Maintain cart using user/session information

### 📦 Order Management

* Place food orders
* Store order details in the database
* Store individual order items
* Maintain relationships between users, restaurants, menus, and orders

### 🗄️ Database

* MySQL relational database
* JDBC-based database connectivity
* DAO layer for database operations
* Foreign-key relationships between related entities

---

## 🛠️ Technologies Used

| Technology           | Purpose                      |
| -------------------- | ---------------------------- |
| **Java**             | Core application development |
| **Java Servlets**    | Backend request handling     |
| **JSP**              | Dynamic web pages            |
| **JDBC**             | Database connectivity        |
| **MySQL**            | Relational database          |
| **HTML5**            | Page structure               |
| **CSS3**             | User interface styling       |
| **Apache Tomcat 9**  | Web/application server       |
| **Eclipse IDE**      | Development environment      |
| **MVC Architecture** | Application architecture     |

---

## 🏗️ Project Architecture

CraveKart follows the **MVC (Model–View–Controller)** architecture.

```text
                    ┌──────────────────┐
                    │      Browser     │
                    │   HTML / JSP UI  │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │    Controller    │
                    │    Servlets      │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │       DAO        │
                    │ Database Access  │
                    └────────┬─────────┘
                             │
                             ▼
                    ┌──────────────────┐
                    │      MySQL       │
                    │    Database      │
                    └──────────────────┘
```

### MVC Components

**Model**

* Java classes representing application entities
* DAO classes responsible for database operations

**View**

* JSP pages
* HTML
* CSS

**Controller**

* Java Servlets
* Handles HTTP requests
* Processes user actions
* Communicates with DAO classes
* Redirects/forwards users to appropriate JSP pages

---

## 📂 Project Structure

The project is organized to separate the different responsibilities of the application.

```text
CraveKart/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── cravekart/
│                   │
│                   ├── controller/
│                   │   ├── LoginServlet.java
│                   │   ├── SignupServlet.java
│                   │   ├── RestaurantServlet.java
│                   │   ├── MenuServlet.java
│                   │   ├── CartServlet.java
│                   │   └── OrderServlet.java
│                   │
│                   ├── dao/
│                   │   ├── UserDao.java
│                   │   ├── RestaurantDao.java
│                   │   ├── MenuDao.java
│                   │   └── OrderDao.java
│                   │
│                   ├── model/
│                   │   ├── User.java
│                   │   ├── Restaurant.java
│                   │   ├── Menu.java
│                   │   ├── Order.java
│                   │   └── OrderItem.java
│                   │
│                   └── util/
│                       └── DBConnection.java
│
├── src/
│   └── main/
│       └── webapp/
│           │
│           ├── css/
│           ├── images/
│           ├── js/
│           │
│           ├── index.jsp
│           ├── login.jsp
│           ├── signup.jsp
│           ├── restaurants.jsp
│           ├── menu.jsp
│           ├── cart.jsp
│           └── orders.jsp
│
└── README.md
```

> **Note:** The exact package and file names may vary depending on the current version of the project.

---

# 🗄️ Database Design

CraveKart uses **MySQL** as its relational database.

The database contains entities required for managing users, restaurants, menus, carts/orders, and related information.

Instead of including the SQL dump/schema directly in this repository, the **Entity Relationship (ER) Diagram** is provided below to explain the database structure and relationships between the tables.

## ER Diagram

> 📌 **Place your ER diagram image in the repository and update the filename below.**

```text
docs/
└── cravekart-er-diagram.png
```

Then display it in the README using:

```markdown
![CraveKart ER Diagram](docs/cravekart-er-diagram.png)
```

### Database Relationships

The database is designed around relationships such as:

* A **User** can place multiple orders.
* A **Restaurant** can contain multiple menu items.
* A **Menu Item** belongs to a restaurant.
* An **Order** contains multiple order items.
* An **Order Item** refers to a specific menu item.
* Foreign keys are used to maintain relationships between related entities.

The ER diagram provides the complete representation of the database schema.

---

# ⚙️ How to Run the Project

Follow the steps below to run CraveKart locally.

## 1. Prerequisites

Make sure the following software is installed:

* **Java JDK**
* **Eclipse IDE**
* **Apache Tomcat 9**
* **MySQL Server**
* **MySQL Workbench** *(recommended)*
* A web browser

---

## 2. Clone the Repository

Clone the project using Git:

```bash
git clone https://github.com/YOUR-USERNAME/CraveKart.git
```

Navigate into the project:

```bash
cd CraveKart
```

---

## 3. Import the Project into Eclipse

1. Open **Eclipse**.
2. Select:

```text
File → Import
```

3. Select the appropriate project import option.
4. Select the cloned CraveKart project.
5. Finish the import.

Make sure Eclipse recognizes the project as a **Dynamic Web Project**.

---

## 4. Configure MySQL

Create a MySQL database for CraveKart.

For example:

```sql
CREATE DATABASE cravekart;
```

The database tables should then be created according to the provided **ER diagram**.

> The SQL schema/dump is intentionally not included in this repository. The ER diagram is provided as the database reference.

---

## 5. Configure Database Connection

Open the database connection/configuration class in the project.

For example:

```text
DBConnection.java
```

Update the database configuration according to your local MySQL setup:

```java
String url = "jdbc:mysql://localhost:3306/cravekart";
String username = "YOUR_USERNAME";
String password = "YOUR_PASSWORD";
```

Replace:

```text
YOUR_USERNAME
YOUR_PASSWORD
```

with your local MySQL credentials.

**Do not commit your actual database password to GitHub.**

---

## 6. Configure Apache Tomcat

Add Apache Tomcat to Eclipse:

```text
Window
   ↓
Preferences
   ↓
Server
   ↓
Runtime Environments
   ↓
Add
```

Select the appropriate **Apache Tomcat** version.

Then:

1. Right-click the CraveKart project.
2. Select **Run As**.
3. Select **Run on Server**.
4. Choose your Tomcat server.
5. Start the server.

---

## 7. Open the Application

Once Tomcat has started successfully, open the application in your browser.

Typically:

```text
http://localhost:8080/CraveKart/
```

The exact URL may differ depending on the project's configured context path.

---

# 🔄 Application Flow

A typical user flow through the application is:

```text
User
 │
 ▼
Home Page
 │
 ├──────────────► Login / Signup
 │
 ▼
Restaurant Listing
 │
 ▼
Select Restaurant
 │
 ▼
View Menu
 │
 ▼
Add Items to Cart
 │
 ▼
View / Update Cart
 │
 ▼
Place Order
 │
 ▼
Order Stored in Database
```

---

# 🔐 Security & Configuration

This project is intended primarily as an educational and portfolio project.

Before deploying the application publicly, additional security measures should be implemented, including:

* Password hashing
* Secure session management
* Input sanitization
* CSRF protection
* Proper authorization checks
* Environment-based database credentials
* Protection against SQL injection through prepared statements
* Secure error handling

The project uses **PreparedStatement** for database operations where applicable to reduce SQL injection risks.

---

# 📸 Project Screenshots

You can add screenshots of the application here.

For example:

### Home Page

```markdown
![CraveKart Home Page](docs/screenshots/home.png)
```

### Restaurant Listing

```markdown
![Restaurant Listing](docs/screenshots/restaurants.png)
```

### Menu

```markdown
![Restaurant Menu](docs/screenshots/menu.png)
```

### Shopping Cart

```markdown
![Shopping Cart](docs/screenshots/cart.png)
```

### Order Page

```markdown
![Orders](docs/screenshots/orders.png)
```

---

# 🎯 Learning Outcomes

Building CraveKart helped me gain practical experience in:

* Java Servlets
* JSP and dynamic web pages
* MVC architecture
* JDBC
* DAO design pattern
* MySQL database design
* Relational database relationships
* Foreign keys
* HTTP request/response handling
* Session management
* CRUD operations
* Form handling and validation
* Apache Tomcat
* Git and GitHub
* Building a complete Java web application

---

# 🔮 Future Improvements

Some planned improvements include:

* [ ] Spring Boot migration
* [ ] Hibernate/JPA integration
* [ ] REST API architecture
* [ ] Online payment integration
* [ ] Restaurant manager dashboard
* [ ] Admin dashboard
* [ ] Order tracking
* [ ] Improved authentication and password security
* [ ] Responsive UI
* [ ] Search and filtering
* [ ] Restaurant reviews and ratings
* [ ] Deployment to a cloud platform

---

# 👨‍💻 Author

**Shivaprasad**

Computer Science Engineering Student

This project was developed as a practical Java Full Stack project to understand backend web development, database design, and MVC-based application architecture.

---

# ⭐ Acknowledgement

CraveKart was developed as a learning project to gain hands-on experience with **Java web development, database management, and backend application architecture**.

If you find the project useful or interesting, consider giving the repository a ⭐.
