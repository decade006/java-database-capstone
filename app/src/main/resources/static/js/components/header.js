/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.

  3. Check if the Current Page is the Root Page

     * On the root page, render a basic header with role controls and clear any previous role from localStorage.

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` are retrieved from `localStorage` to determine the user's current session.

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.

  6. Handle Session Expiry or Invalid Login

     * If role requires a token (admin/doctor/loggedPatient) but token is missing, clear role and redirect to root.

  7. Add Role-Specific Header Content

     * Depending on the user's role, render the appropriate actions as described in the instructions.

  9. Close the Header Section and Render

  10. Attach Event Listeners to Header Buttons

  Helper functions: attachHeaderButtonListeners, logout, logoutPatient.
*/

function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    const onRoot = window.location.pathname === "/" || window.location.pathname.endsWith("/index.html");
    if (onRoot) {
        // On the root page, show only the logo header; the main section contains the active role buttons
        localStorage.removeItem("userRole");
        const headerContent = `
      <header class="header py-3 mb-4 border-bottom">
        <div class="container d-flex flex-wrap justify-content-between align-items-center">
          <div class="d-flex align-items-center">
            <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" width="24" class="me-2">
            <span class="fs-4">Hospital CMS</span>
          </div>
        </div>
      </header>`;
        headerDiv.innerHTML = headerContent;
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // If role requires token but missing -> logout
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = `<header class="header py-3 mb-4 border-bottom">
    <div class="container d-flex flex-wrap justify-content-between align-items-center">
      <div class="d-flex align-items-center">
        <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" width="40" class="me-2">
        <span class="fs-4">Hospital CMS</span>
      </div>
      <nav class="d-flex gap-2">`;

    if (role === "admin") {
        headerContent += `
      <button id="addDocBtn" class="btn btn-success" onclick="openModal('addDoctor')">Add Doctor</button>
      <button class="btn btn-outline-danger" onclick="logout()">Logout</button>`;
    } else if (role === "doctor") {
        headerContent += `
      <button class="btn btn-primary" onclick="selectRole('doctor')">Home</button>
      <button class="btn btn-outline-danger" onclick="logout()">Logout</button>`;
    } else if (role === "patient") {
        headerContent += `
      <button id="patientLogin" class="btn btn-outline-primary">Login</button>
      <button id="patientSignup" class="btn btn-primary">Sign Up</button>`;
    } else if (role === "loggedPatient") {
        headerContent += `
      <button id="home" class="btn btn-primary" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button id="patientAppointments" class="btn btn-outline-secondary" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <button class="btn btn-outline-danger" onclick="logoutPatient()">Logout</button>`;
    }

    headerContent += `</nav></div></header>`;
    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const patientLogin = document.getElementById("patientLogin");
    const patientSignup = document.getElementById("patientSignup");
    if (patientLogin) patientLogin.addEventListener("click", () => openModal("patientLogin"));
    if (patientSignup) patientSignup.addEventListener("click", () => openModal("patientSignup"));
}

function _logoutCommon() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logout() {
    _logoutCommon();
}

function logoutPatient() {
    _logoutCommon();
}

// expose for inline handlers
window.logout = logout;
window.logoutPatient = logoutPatient;

document.addEventListener("DOMContentLoaded", renderHeader);
   
