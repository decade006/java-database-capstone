// services/index.js - entry page behavior: auto-login + login handlers
import { API_BASE_URL } from "../config/config.js";
import { openModal } from "../components/modals.js";

const ADMIN_LOGIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_LOGIN_API = `${API_BASE_URL}/doctor/login`;

function initEntryPage() {
  // Wire header buttons to modals (in case header didn't already attach)
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");
  const patientLoginBtn = document.getElementById("patientLogin");
  const patientSignupBtn = document.getElementById("patientSignup");

  adminBtn?.addEventListener("click", () => openModal("adminLogin"));
  doctorBtn?.addEventListener("click", () => openModal("doctorLogin"));
  patientLoginBtn?.addEventListener("click", () => openModal("patientLogin"));
  patientSignupBtn?.addEventListener("click", () => openModal("patientSignup"));

  // Auto-login: if both role and token are present, route to the right dashboard
  try {
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
    if (role && token && typeof window.selectRole === "function") {
      window.selectRole(role);
      return; // avoid flashing the landing content
    }
  } catch (e) {
    // ignore storage errors
  }
}

// Expose handlers globally for modals.js to attach
window.adminLoginHandler = async function adminLoginHandler() {
  const username = document.getElementById("username")?.value?.trim();
  const password = document.getElementById("password")?.value?.trim();
  if (!username || !password) {
    alert("Please enter username and password");
    return;
  }
  try {
    const res = await fetch(ADMIN_LOGIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      alert(data.message || "Invalid admin credentials");
      return;
    }
    if (data?.token) localStorage.setItem("token", data.token);
    if (typeof window.selectRole === "function") window.selectRole("admin");
  } catch (err) {
    console.error("Admin login error", err);
    alert("Unable to login. Please try again later.");
  }
};

window.doctorLoginHandler = async function doctorLoginHandler() {
  const email = document.getElementById("email")?.value?.trim();
  const password = document.getElementById("password")?.value?.trim();
  if (!email || !password) {
    alert("Please enter email and password");
    return;
  }
  try {
    const res = await fetch(DOCTOR_LOGIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      alert(data.message || "Invalid doctor credentials");
      return;
    }
    if (data?.token) localStorage.setItem("token", data.token);
    if (typeof window.selectRole === "function") window.selectRole("doctor");
  } catch (err) {
    console.error("Doctor login error", err);
    alert("Unable to login. Please try again later.");
  }
};

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initEntryPage);
} else {
  initEntryPage();
}
