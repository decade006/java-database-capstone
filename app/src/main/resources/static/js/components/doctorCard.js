/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/

import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const col = document.createElement("div");
  col.className = "col";

  const card = document.createElement("div");
  card.className = "card shadow-sm h-100";

  const body = document.createElement("div");
  body.className = "card-body d-flex flex-column";

  const title = document.createElement("h5");
  title.className = "card-title";
  title.textContent = doctor.name || "Doctor";

  const spec = document.createElement("p");
  spec.className = "card-text mb-1";
  spec.textContent = `Specialty: ${doctor.specialty || "N/A"}`;

  const email = document.createElement("p");
  email.className = "card-text text-muted mb-2";
  email.textContent = doctor.email ? `Email: ${doctor.email}` : "";

  // Normalize available times for display and booking overlay
  const slots = (doctor.availableTimes || []).map((s) => {
    if (typeof s === "string") return s; // already normalized
    const st = (s?.startTime || "").toString().slice(0, 5);
    const et = (s?.endTime || "").toString().slice(0, 5);
    return st && et ? `${st}-${et}` : null;
  }).filter(Boolean);

  const times = document.createElement("p");
  times.className = "card-text";
  times.textContent = slots.length ? `Available: ${slots.join(", ")}` : "Available: -";

  body.appendChild(title);
  body.appendChild(spec);
  body.appendChild(email);
  body.appendChild(times);

  const actions = document.createElement("div");
  actions.className = "d-flex gap-2 mt-2";

  const role = localStorage.getItem("userRole");

  if (role === "admin") {
    const delBtn = document.createElement("button");
    delBtn.className = "btn btn-sm btn-outline-danger";
    delBtn.textContent = "Delete";
    delBtn.addEventListener("click", async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Unauthorized. Please log in again.");
        return;
      }
      const { success, message } = await deleteDoctor(doctor.id, token);
      alert(message || (success ? "Deleted" : "Failed"));
      if (success) card.remove();
    });
    actions.appendChild(delBtn);
  } else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.className = "btn btn-sm btn-primary";
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", () => alert("Please login to continue."));
    actions.appendChild(bookBtn);
  } else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.className = "btn btn-sm btn-primary";
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        return;
      }
      const patient = await getPatientData(token);
      if (!patient) {
        alert("Unable to load patient details.");
        return;
      }
      const docForBooking = { ...doctor, availableTimes: slots };
      showBookingOverlay(e, docForBooking, patient);
    });
    actions.appendChild(bookBtn);
  }

  body.appendChild(actions);
  card.appendChild(body);
  col.appendChild(card);
  return col;
}
