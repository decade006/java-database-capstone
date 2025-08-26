/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";

// Load initial list and attach listeners
window.addEventListener("DOMContentLoaded", () => {
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) addBtn.addEventListener("click", () => openModal("addDoctor"));

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");
  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

  loadDoctorCards();
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (e) {
    console.error("Failed to load doctors:", e);
  }
}

function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  const name = searchBar && searchBar.value.trim().length > 0 ? searchBar.value.trim() : null;
  const time = filterTime && filterTime.value.length > 0 ? filterTime.value : null;
  const specialty = filterSpecialty && filterSpecialty.value.length > 0 ? filterSpecialty.value : null;

  filterDoctors(name, time, specialty)
    .then((response) => {
      const doctors = response.doctors || [];
      renderDoctorCards(doctors);
      if (!doctors.length) {
        const contentDiv = document.getElementById("content");
        if (contentDiv) contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      }
    })
    .catch((error) => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Expose for modal save action
window.adminAddDoctor = async function adminAddDoctor() {
  const name = document.getElementById("doctorName")?.value?.trim();
  const email = document.getElementById("doctorEmail")?.value?.trim();
  const password = document.getElementById("doctorPassword")?.value?.trim();
  const phone = document.getElementById("doctorPhone")?.value?.trim();
  const specialty = document.getElementById("specialization")?.value?.trim();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Unauthorized. Please log in as admin.");
    return;
  }

  const selected = Array.from(document.querySelectorAll('input[name="availability"]:checked'))
    .map((c) => c.value);
  const availableTimes = selected.map((range) => {
    const [start, end] = range.split("-");
    const startTime = start.length === 5 ? `${start}:00` : start;
    const endTime = end.length === 5 ? `${end}:00` : end;
    return { startTime, endTime };
  });

  const doctor = { name, email, password, phone, specialty, availableTimes };

  const { success, message } = await saveDoctor(doctor, token);
  alert(message);
  if (success) {
    document.getElementById("modal").style.display = "none";
    window.location.reload();
  }
};
