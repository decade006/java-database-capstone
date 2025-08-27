/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

import {getAllAppointments} from './services/appointmentRecordService.js';
import {createPatientRow} from './components/patientRows.js';

function getToday() {
    return new Date().toISOString().slice(0, 10);
}

let selectedDate = getToday();
let token = localStorage.getItem('token');
let patientName = 'null';

async function loadAppointments() {
    window.alert("hello");
    try {
        const tbody = document.getElementById('patientTableBody');
        if (!tbody) return;
        tbody.innerHTML = '';

        const data = await getAllAppointments(selectedDate, patientName, token);
        const list = Array.isArray(data?.appointments) ? data.appointments : [];

        if (!list.length) {
            const tr = document.createElement('tr');
            const td = document.createElement('td');
            td.colSpan = 8;
            td.className = 'text-center text-muted';
            td.textContent = 'No Appointments found for today.';
            tr.appendChild(td);
            tbody.appendChild(tr);
            return;
        }

        list.forEach(appt => {
            const patient = {
                id: appt.patientId,
                name: appt.patientName,
                phone: appt.patientPhone,
                email: appt.patientEmail
            };
            const tr = createPatientRow(patient, appt.id, appt.doctorId);

            // Show Appointment ID as the first cell's text
            const firstTd = tr.querySelector('td');
            if (firstTd) firstTd.textContent = appt.id;

            // Insert Date, Time, Status before the last (prescription) cell
            const lastTd = tr.lastElementChild;

            const dateTd = document.createElement('td');
            dateTd.textContent = appt.appointmentDate || (appt.appointmentTime ? appt.appointmentTime.slice(0, 10) : '');

            const timeTd = document.createElement('td');
            const timeOnly = appt.appointmentTimeOnly || (appt.appointmentTime ? appt.appointmentTime.substring(11, 16) : '');
            timeTd.textContent = timeOnly;

            const statusTd = document.createElement('td');
            const badge = document.createElement('span');
            const completed = Number(appt.status) === 1;
            badge.className = 'badge ' + (completed ? 'bg-success' : 'bg-secondary');
            badge.textContent = completed ? 'Completed' : 'Scheduled';
            statusTd.appendChild(badge);

            tr.insertBefore(dateTd, lastTd);
            tr.insertBefore(timeTd, lastTd);
            tr.insertBefore(statusTd, lastTd);

            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Error loading appointments:', err);
        const tbody = document.getElementById('patientTableBody');
        if (!tbody) return;
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = 8;
        td.className = 'text-center text-danger';
        td.textContent = 'Error loading appointments. Try again later.';
        tr.appendChild(td);
        tbody.appendChild(tr);
    }
}

function setupEventListeners() {
    const datePicker = document.getElementById('datePicker');
    const searchInput = document.getElementById('searchBar');
    const todayBtn = document.getElementById('todayButton');

    if (datePicker) {
        datePicker.value = selectedDate;
        datePicker.addEventListener('change', (e) => {
            if (e.target.value) {
                selectedDate = e.target.value;
                loadAppointments();
            }
        });
    }

    if (todayBtn) {
        todayBtn.addEventListener('click', () => {
            selectedDate = getToday();
            if (datePicker) datePicker.value = selectedDate;
            loadAppointments();
        });
    }

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const val = (e.target.value || '').trim();
            patientName = val.length ? val : 'null';
            loadAppointments();
        });
    }
}

// Initialize on DOM ready
window.addEventListener('DOMContentLoaded', () => {
    try {
        if (typeof renderContent === 'function') renderContent();
    } catch (_) {
    }
    setupEventListeners();
    loadAppointments();
});
