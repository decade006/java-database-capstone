## Architecture Summary

This Spring Boot application features a hybrid architecture that leverages both Spring MVC for server-rendered views and REST APIs for client-side interactions. The user-facing dashboards for administrators and doctors are built with Thymeleaf templates, providing dynamic HTML pages. All other modules, including appointments and patient records, are exposed through JSON-based REST APIs, allowing for flexible integration with various clients like mobile or web applications.

The backend is structured in a classic three-tier pattern. It connects to two distinct databases to handle different types of data: a **MySQL** relational database for structured information such as patient, doctor, and appointment details, and a **MongoDB** NoSQL database for unstructured data like medical prescriptions. A central service layer contains the core business logic, orchestrating data flow between the controllers and the appropriate data repositories (JPA for MySQL and Document-based for MongoDB).

## Flow of Data and Control

1.  **User Interface Layer**: A user initiates an action, either by accessing a server-rendered page like the `AdminDashboard` or by making a request from a client application (e.g., a patient portal) that consumes the `REST Modules` via a JSON API.

2.  **Controller Layer**: The incoming request is routed to the appropriate controller based on its URL. Requests for dashboards are handled by `Thymeleaf Controllers`, which prepare to render an HTML view. API requests are managed by `REST Controllers`, which will return data in JSON format.

3.  **Service Layer**: Both `Thymeleaf` and `REST` controllers call methods in the unified `Service Layer`. This central layer enforces business rules, performs validations, and orchestrates the application's core logic, ensuring a clear separation of concerns from the web layer.

4.  **Repository Layer**: To fetch or persist data, the service layer communicates with the `Repository Layer`. It uses `MySQL Repositories` (built with Spring Data JPA) for relational data and the `MongoDB Repository` (built with Spring Data MongoDB) for document data. This layer abstracts the underlying data storage details.

5.  **Database Access**: The repositories interact directly with the databases. The `MySQL Database` is accessed to manage structured entities like `Patient` and `Appointment`, while the `MongoDB Database` is used for flexible, document-based records such as `Prescription`.

6.  **Model Binding**: Data retrieved from the databases is mapped into corresponding Java objects. Data from MySQL tables are converted into `JPA Entity` objects (e.g., `Patient`, `Doctor`), while data from MongoDB collections are mapped to `MongoDB Model` objects (e.g., `Prescription` document).

7.  **Response Generation**: These populated model objects are used to construct the final response. In the MVC flow, the models are passed to Thymeleaf templates to generate dynamic HTML for the browser. In the REST flow, the models are serialized into a JSON string and sent back to the client as the HTTP response body.