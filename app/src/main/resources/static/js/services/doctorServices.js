/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = `${API_BASE_URL}/doctor`;

export async function getDoctors() {
  try {
    const res = await fetch(DOCTOR_API, { method: "GET" });
    if (!res.ok) {
      console.error("getDoctors failed with status:", res.status);
      return [];
    }
    const data = await res.json();
    return Array.isArray(data.doctors) ? data.doctors : [];
  } catch (err) {
    console.error("Error in getDoctors:", err);
    return [];
  }
}

export async function deleteDoctor(id, token) {
  try {
    const res = await fetch(`${DOCTOR_API}/${id}/${token}`, { method: "DELETE" });
    const data = await res.json().catch(() => ({ message: "Unexpected response" }));
    return { success: res.ok, message: data.message || (res.ok ? "Deleted" : "Failed to delete") };
  } catch (err) {
    console.error("Error in deleteDoctor:", err);
    return { success: false, message: "Network error" };
  }
}

export async function saveDoctor(doctor, token) {
  try {
    const res = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });
    const data = await res.json().catch(() => ({ message: "Unexpected response" }));
    return { success: res.ok, message: data.message || (res.ok ? "Saved" : "Failed to save") };
  } catch (err) {
    console.error("Error in saveDoctor:", err);
    return { success: false, message: "Network error" };
  }
}

export async function filterDoctors(name, time, speciality) {
  try {
    // fallback to 'null' for API which expects path variables
    const n = name && name.length ? name : "null";
    const t = time && time.length ? time : "null";
    const s = speciality && speciality.length ? speciality : "null";
    const res = await fetch(`${DOCTOR_API}/filter/${encodeURIComponent(n)}/${encodeURIComponent(t)}/${encodeURIComponent(s)}`);
    if (res.ok) {
      const data = await res.json();
      return data;
    } else {
      console.error("filterDoctors failed:", res.status, res.statusText);
      return { doctors: [] };
    }
  } catch (err) {
    console.error("Error in filterDoctors:", err);
    alert("Something went wrong!");
    return { doctors: [] };
  }
}
