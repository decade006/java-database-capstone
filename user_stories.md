# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]


Of course! Here are the user stories for the Admin, Patient, and Doctor roles, formatted according to the provided template.

---

### **Admin User Stories**

**Title:**
*As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely.*

**Acceptance Criteria:**
1. Given I am on the login page, when I enter valid admin credentials and click "Login", then I am redirected to the admin dashboard.
2. Given I am on the login page, when I enter invalid credentials, then an "Invalid username or password" error message is displayed.
3. The password input field must mask the characters as they are typed.

**Priority:** High
**Story Points:** 3
**Notes:**
- This applies to the `AdminDashboard` which uses the Thymeleaf-based login form.

---

**Title:**
*As an admin, I want to log out of the portal, so that I can protect system access after my session.*

**Acceptance Criteria:**
1. Given I am logged in as an admin, when I click the "Logout" button, then my session is terminated.
2. After logging out, I am redirected to the public login page.
3. I cannot use the browser's back button to access any protected admin pages after logging out.

**Priority:** High
**Story Points:** 2
**Notes:**
- The logout button should be clearly visible in the admin dashboard's navigation bar.

---

**Title:**
*As an admin, I want to add new doctors to the portal, so that the clinic's list of available physicians is up-to-date.*

**Acceptance Criteria:**
1. Given I am on the admin dashboard, I can navigate to a "Manage Doctors" section.
2. When I click "Add Doctor", a form appears with fields for name, specialization, email, and a temporary password.
3. Upon submitting the form with valid data, a new doctor account is created, and the doctor appears in the list of all doctors.

**Priority:** High
**Story Points:** 5
**Notes:**
- Input validation should be in place for all required fields. An email should be sent to the doctor with their login credentials.

---

**Title:**
*As an admin, I want to delete a doctor's profile from the portal, so that I can remove staff who are no longer with the clinic.*

**Acceptance Criteria:**
1. Given I am viewing the list of doctors, each doctor record has a "Delete" option.
2. When I click "Delete" for a specific doctor, a confirmation prompt (e.g., "Are you sure you want to delete this profile?") appears.
3. Upon confirming, the doctor's profile is permanently removed from the database and no longer appears in any doctor listings.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Consider what happens to the doctor's existing appointments. They might need to be reassigned or cancelled. This could be a soft delete initially.

---

**Title:**
*As an admin, I want to run a report on the number of appointments per month, so that I can track usage statistics and clinic performance.*

**Acceptance Criteria:**
1. Given I am in the "Reports" section of the admin dashboard, I can select a month and year.
2. When I click "Generate Report", the system displays the total count of appointments for the selected month.
3. The data is retrieved accurately from the database and presented in a clear, readable format.

**Priority:** Medium
**Story Points:** 8
**Notes:**
- The backend for this feature will execute a stored procedure in MySQL for efficient data aggregation.

---

### **Patient User Stories**

**Title:**
*As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering for an account.*

**Acceptance Criteria:**
1. Given I am a visitor on the website's homepage, I can find and click a link to a "Find a Doctor" page.
2. This page displays a list of all available doctors, showing their name, photo, and specialization.
3. I do not need to be logged in to view this public list.

**Priority:** High
**Story Points:** 3
**Notes:**
- This page will likely interact with a REST endpoint to fetch doctor information.

---

**Title:**
*As a patient, I want to sign up for an account using my email and password, so that I can book and manage appointments.*

**Acceptance Criteria:**
1. Given I am on the "Sign Up" page, I can enter my full name, email address, and a password.
2. The system validates that the email address is not already registered.
3. Upon successful submission, my account is created, and I am redirected to the patient dashboard.

**Priority:** High
**Story Points:** 5
**Notes:**
- Password requirements (e.g., minimum length) should be clearly stated on the form.

---

**Title:**
*As a patient, I want to log into the portal, so that I can manage my personal health information and bookings securely.*

**Acceptance Criteria:**
1. Given I am on the login page, when I enter my correct registered email and password, I am granted access.
2. Upon successful login, I am redirected to my personal dashboard.
3. If I enter incorrect credentials, an appropriate error message is displayed on the screen.

**Priority:** High
**Story Points:** 3
**Notes:**
- This will use the REST API for authentication.

---

**Title:**
*As a patient, I want to log out of the portal, so that I can ensure my account is secure on a shared or public computer.*

**Acceptance Criteria:**
1. Given I am logged in, there is a clearly visible "Logout" button.
2. When I click "Logout", my session is securely terminated.
3. I am redirected to the public homepage or login screen after logging out.

**Priority:** High
**Story Points:** 2
**Notes:**
- The frontend client should clear any stored authentication tokens.

---

**Title:**
*As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor.*

**Acceptance Criteria:**
1. Given I am logged in and viewing a doctor's profile, I can see a calendar with their available time slots.
2. When I select an available one-hour slot and confirm, the appointment is booked.
3. The selected time slot is no longer shown as available to other patients.

**Priority:** High
**Story Points:** 8
**Notes:**
- An email confirmation should be sent to the patient upon successful booking.

---

**Title:**
*As a patient, I want to view my upcoming appointments, so that I can remember the date and time and prepare accordingly.*

**Acceptance Criteria:**
1. Given I am logged into my dashboard, there is a section titled "My Upcoming Appointments".
2. This section lists all my scheduled future appointments in chronological order.
3. Each entry displays the doctor's name, specialization, date, and time of the appointment.

**Priority:** High
**Story Points:** 3
**Notes:**
- This feature is a core part of the patient dashboard.

---

### **Doctor User Stories**

**Title:**
*As a doctor, I want to log into the portal, so that I can manage my schedule and view patient information.*

**Acceptance Criteria:**
1. Given I am on the login page, I can enter my doctor-specific credentials (username/password).
2. Upon successful authentication, I am redirected to my doctor dashboard.
3. If login fails, a clear error message is displayed.

**Priority:** High
**Story Points:** 3
**Notes:**
- This uses the Thymeleaf `DoctorDashboard` flow.

---

**Title:**
*As a doctor, I want to log out of the portal, so that I can protect sensitive patient data and my personal account.*

**Acceptance Criteria:**
1. Given I am logged into the doctor dashboard, I can find and click a "Logout" button.
2. Clicking the button terminates my session and invalidates my login.
3. I am redirected to the main login page and cannot access the dashboard again without logging in.

**Priority:** High
**Story Points:** 2
**Notes:**
- Session management is critical for HIPAA compliance and data security.

---

**Title:**
*As a doctor, I want to view my appointment calendar, so that I can stay organized and know my schedule for the day, week, and month.*

**Acceptance Criteria:**
1. Given I am on my dashboard, the primary view is a calendar showing my scheduled appointments.
2. I can switch between daily, weekly, and monthly views of the calendar.
3. Each appointment on the calendar clearly shows the patient's name and the appointment time.

**Priority:** High
**Story Points:** 5
**Notes:**
- The calendar should be interactive, allowing me to click on appointments for more details.

---

**Title:**
*As a doctor, I want to mark my unavailability on the calendar, so that patients can only book appointments when I am actually available.*

**Acceptance Criteria:**
1. Given I am viewing my calendar, I can select a specific date or a range of time slots.
2. When I mark the selected time as "Unavailable," it is blocked out on my calendar.
3. These blocked-off time slots are not visible to patients in the appointment booking interface.

**Priority:** Medium
**Story Points:** 5
**Notes:**
- This is crucial for preventing double-bookings or bookings during vacation time.

---

**Title:**
*As a doctor, I want to update my profile with my specialization and contact information, so that patients have accurate and up-to-date information about me.*

**Acceptance Criteria:**
1. Given I am logged in, I can navigate to a "My Profile" or "Edit Profile" page.
2. I can modify fields such as my specialization, phone number, and a short biography.
3. When I save the changes, the information is immediately updated on my public-facing doctor profile.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Some fields, like name or email, might be read-only and only changeable by an admin.

---

**Title:**
*As a doctor, I want to view the patient details for my upcoming appointments, so that I can be prepared for the consultation.*

**Acceptance Criteria:**
1. Given I am viewing my appointment calendar, I can click on any upcoming appointment to open a details view.
2. The details view displays the patient's full name, date of birth, and the stated reason for the visit.
3. There is a link to access the patient's medical history or previous prescriptions, if available.

**Priority:** High
**Story Points:** 3
**Notes:**
- Access to patient records must be secure and comply with privacy regulations.