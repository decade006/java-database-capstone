// updateAppointment.js
import { updateAppointment } from "../js/services/appointmentRecordService.js";
import { getDoctors } from "../js/services/doctorServices.js";
document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  const token = localStorage.getItem("token"); // Assuming token is stored in localStorage
  // Get appointmentId and patientId from the URL query parameters
  const urlParams = new URLSearchParams(window.location.search);
  const appointmentId = urlParams.get("appointmentId");
  const patientId = urlParams.get("patientId");
  const doctorId = urlParams.get("doctorId");
  const patientName = urlParams.get("patientName");
  const doctorName = urlParams.get("doctorName");
  const appointmentDate = urlParams.get("appointmentDate");
  const appointmentTime = urlParams.get("appointmentTime");

  console.log(doctorId)
  if (!token || !patientId) {
    alert("Missing session data, redirecting to appointments page.");
    window.location.href = "/pages/patientAppointments.html";
    return;
  }

  // get doctor to display only the available time of doctor
  getDoctors()
    .then(doctors => {
      // Find the doctor by the ID from the URL
      const doctor = doctors.find(d => d.id == doctorId);
      if (!doctor) {
        alert("Doctor not found.");
        return;
      }

      // Fill the form with the appointment data passed in the URL
      document.getElementById("patientName").value = patientName || "You";
      document.getElementById("doctorName").value = doctorName;
      document.getElementById("appointmentDate").value = appointmentDate;

      const timeSelect = document.getElementById("appointmentTime");
      timeSelect.innerHTML = "<option value=''>Select time</option>";

      // Normalize doctor's availableTimes to strings like "HH:mm-HH:mm"
      const slots = (doctor.availableTimes || []).map((s) => {
        if (typeof s === "string") return s;
        const st = (s?.startTime || "").toString().slice(0, 5);
        const et = (s?.endTime || "").toString().slice(0, 5);
        return st && et ? `${st}-${et}` : null;
      }).filter(Boolean);

      // Build options and preselect current one if it matches
      slots.forEach((time) => {
        const opt = document.createElement("option");
        opt.value = time;
        opt.textContent = time;
        if (appointmentTime && time.includes(appointmentTime)) {
          opt.selected = true;
        }
        timeSelect.appendChild(opt);
      });

      // Handle form submission for updating the appointment
      document.getElementById("updateAppointmentForm").addEventListener("submit", async (e) => {
        e.preventDefault(); // Prevent default form submission

        const date = document.getElementById("appointmentDate").value;
        const time = document.getElementById("appointmentTime").value;
        const startTime = time.split('-')[0];
        if (!date || !time) {
          alert("Please select both date and time.");
          return;
        }

        const updatedAppointment = {
          id: appointmentId,
          doctor: { id: doctor.id },
          patient: { id: patientId },
          appointmentTime: `${date}T${startTime}:00`,
          status: 0
        };

        const updateResponse = await updateAppointment(updatedAppointment, token);

        if (updateResponse.success) {
          alert("Appointment updated successfully!");
          window.location.href = "/pages/patientAppointments.html"; // Redirect back to the appointments page
        } else {
          alert("❌ Failed to update appointment: " + updateResponse.message);
        }
      });
    })
    .catch(error => {
      console.error("Error fetching doctors:", error);
      alert("❌ Failed to load doctor data.");
    });
}
