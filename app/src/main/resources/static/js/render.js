// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');

  if (role === 'admin') {
    const url = token ? `/adminDashboard?token=${encodeURIComponent(token)}` : '/adminDashboard';
    window.location.href = url;
  } else if (role === 'doctor') {
    const url = token ? `/doctorDashboard?token=${encodeURIComponent(token)}` : '/doctorDashboard';
    window.location.href = url;
  } else if (role === 'patient') {
    window.location.href = '/pages/patientDashboard.html';
  } else if (role === 'loggedPatient') {
    window.location.href = '/pages/loggedPatientDashboard.html';
  }
}

function renderContent() {
  // Relaxed guard: allow pages to render even when role is missing so header can show login/signup.
  // Keep this as a placeholder for future per-page checks.
  return;
}
