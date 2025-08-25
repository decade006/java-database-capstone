// header.js - dynamic header renderer
(function () {
    function isRoot() {
        const p = window.location.pathname || '';
        return p === '/' || p.endsWith('/index.html');
    }

    function logout() {
        try {
            localStorage.removeItem('userRole');
            localStorage.removeItem('token');
        } catch (e) {
        }
        window.location.href = '/';
    }

    function logoutPatient() {
        try {
            localStorage.removeItem('userRole');
            localStorage.removeItem('token');
        } catch (e) {
        }
        window.location.href = '/pages/patientDashboard.html';
    }

    function renderHeader() {
        const mount = document.getElementById('header');
        if (!mount) return;

        // Basic logo section (absolute asset path works in templates and static)
        let html = `
      <header class="header">
        <a class="logo-link" href="/">
          <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img" />
          <span class="logo-title">Hospital CMS</span>
        </a>
        <nav>
    `;

        if (isRoot()) {
            // html += `
            //   <button id="adminLogin" class="adminBtn">Admin Login</button>
            //   <button id="doctorLogin" class="adminBtn">Doctor Login</button>
            //   <button id="patientLogin" class="adminBtn">Login</button>
            //   <button id="patientSignup" class="adminBtn">Sign Up</button>
            // `;
        } else {
            const role = localStorage.getItem('userRole');
            const token = localStorage.getItem('token');

            if (role === 'admin') {
                if (token) {
                    html += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" class="doctorHeader" id="logoutLink">Logout</a>
          `;
                } else {
                    // Not authenticated: show login/signup options right on dashboard
                    html += `
            <button id="adminLogin" class="adminBtn">Admin Login</button>
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
          `;
                }
            } else if (role === 'doctor') {
                if (token) {
                    html += `
            <button class="adminBtn" id="doctorHomeBtn">Home</button>
            <a href="#" class="doctorHeader" id="logoutLink">Logout</a>
          `;
                } else {
                    html += `
            <button id="doctorLogin" class="adminBtn">Doctor Login</button>
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
          `;
                }
            } else if (role === 'patient') {
                html += `
          <button id="patientLogin" class="adminBtn">Login</button>
          <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
            } else if (role === 'loggedPatient') {
                if (token) {
                    html += `
            <button id="home" class="adminBtn">Home</button>
            <button id="patientAppointments" class="adminBtn">Appointments</button>
            <a href="#" id="logoutPatientLink">Logout</a>
          `;
                } else {
                    html += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
          `;
                }
            } else {
                // No role set: show generic login/signup on dashboards too
                html += `
          <button id="adminLogin" class="adminBtn">Admin Login</button>
          <button id="doctorLogin" class="adminBtn">Doctor Login</button>
          <button id="patientLogin" class="adminBtn">Login</button>
          <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
            }
        }

        html += '</nav></header>';

        mount.innerHTML = html;

        attachHeaderButtonListeners();
    }

    function attachHeaderButtonListeners() {
        const q = (id) => document.getElementById(id);

        const safeOpen = (type) => {
            // openModal is an ES module export; only call if attached globally elsewhere
            if (typeof window.openModal === 'function') {
                window.openModal(type);
            }
        };

        // Landing/login buttons
        q('patientLogin')?.addEventListener('click', () => safeOpen('patientLogin'));
        q('patientSignup')?.addEventListener('click', () => safeOpen('patientSignup'));
        q('adminLogin')?.addEventListener('click', () => safeOpen('adminLogin'));
        q('doctorLogin')?.addEventListener('click', () => safeOpen('doctorLogin'));

        // Admin add doctor button
        q('addDocBtn')?.addEventListener('click', () => safeOpen('addDoctor'));

        // Doctor home
        q('doctorHomeBtn')?.addEventListener('click', () => {
            // Use existing selectRole if available to keep routing consistent
            if (typeof window.selectRole === 'function') {
                window.selectRole('doctor');
            }
        });

        // Logged patient nav
        q('home')?.addEventListener('click', () => {
            window.location.href = '/pages/loggedPatientDashboard.html';
        });
        q('patientAppointments')?.addEventListener('click', () => {
            window.location.href = '/pages/patientAppointments.html';
        });

        // Logout links
        q('logoutLink')?.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
        q('logoutPatientLink')?.addEventListener('click', (e) => {
            e.preventDefault();
            logoutPatient();
        });
    }

    // Expose minimal API for other scripts if needed
    window.renderHeader = renderHeader;
    window.logout = logout;
    window.logoutPatient = logoutPatient;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', renderHeader);
    } else {
        renderHeader();
    }
})();
   
