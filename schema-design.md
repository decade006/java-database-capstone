This document outlines the database schema for the Smart Clinic Management System, detailing the structures for both the MySQL relational database and the MongoDB document database based on the application's models.

## MySQL Database Design

The relational schema is designed to handle the core, structured data of the clinic's operations.

### Table: `patients`
- `id`: BIGINT, Primary Key, Auto Increment
- `name`: VARCHAR(100), Not Null
- `email`: VARCHAR(255), Not Null, Unique
- `password`: VARCHAR(255), Not Null
- `phone`: VARCHAR(10), Not Null
- `address`: VARCHAR(255), Not Null

### Table: `doctors`
- `id`: BIGINT, Primary Key, Auto Increment
- `name`: VARCHAR(100), Not Null
- `specialty`: VARCHAR(50), Not Null
- `email`: VARCHAR(255), Not Null, Unique
- `password`: VARCHAR(255), Not Null
- `phone`: VARCHAR(10), Not Null

### Table: `doctor_available_times`
- `doctor_id`: BIGINT, Foreign Key -> `doctors(id)`
- `available_times`: VARCHAR(255)

### Table: `admin`
- `id`: BIGINT, Primary Key, Auto Increment
- `username`: VARCHAR(255), Not Null, Unique
- `password`: VARCHAR(255), Not Null

### Table: `appointments`
- `id`: BIGINT, Primary Key, Auto Increment
- `doctor_id`: BIGINT, Foreign Key -> `doctors(id)`, Not Null
- `patient_id`: BIGINT, Foreign Key -> `patients(id)`, Not Null
- `appointment_time`: DATETIME, Not Null
- `status`: INT, Not Null (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: `schedules`
- `id`: BIGINT, Primary Key, Auto Increment
- `doctor_id`: BIGINT, Foreign Key -> `doctors(id)`
- `date_of_week`: INT (1 = Monday, ..., 7 = Sunday)
- `start_time`: TIME
- `end_time`: TIME

### Table: `payments`
- `id`: BIGINT, Primary Key, Auto Increment
- `appointment_id`: BIGINT, Foreign Key -> `appointments(id)`
- `amount`: INT
- `status`: INT (1 = Paid, 2 = Wait)

### Table: `medical_records`
- `id`: BIGINT, Primary Key, Auto Increment
- `patient_id`: BIGINT, Foreign Key -> `patients(id)`

### Table: `clinic_locations`
- `id`: BIGINT, Primary Key, Auto Increment
- `address`: VARCHAR(255)

## MongoDB Collection Design

The document-based schema is designed for data that is less structured, more flexible, or related to logging and metadata.

### Collection: `messages`
```json
{
  "_id": "ObjectId(...)",
  "patientId": 1,
  "doctorId": 1,
  "message": "Is the clinic open on Saturday?",
  "timestamp": "2023-10-27T10:00:00Z"
}
```

### Collection: `prescriptions`
```json
{
  "_id": "ObjectId(...)",
  "appointmentId": 101,
  "patientName": "Jane Doe",
  "medications": ["Ibuprofen", "Amoxicillin"],
  "dosage": "500mg",
  "doctorNotes": "Take with food. Finish the entire course of antibiotics."
}
```

### Collection: `patient_logs`
```json
{
  "_id": "ObjectId(...)",
  "action": "CHECK IN",
  "patientId": 1,
  "doctorId": 1,
  "appointmentId": 101,
  "timestamp": "2023-10-27T14:00:00Z"
}
```

### Collection: `file_attachments`
```json
{
  "_id": "ObjectId(...)",
  "fileName": "x-ray_report.pdf",
  "fileType": "pdf",
  "contentType": "application/pdf",
  "size": 204800,
  "url": "/files/x-ray_report.pdf"
}
```

### Collection: `patient_feedback`
```json
{
  "_id": "ObjectId(...)",
  "appointmentId": 101,
  "rating": 5,
  "comment": "The doctor was very attentive and helpful.",
  "createdAt": "2023-10-28T11:00:00Z"
}
```
