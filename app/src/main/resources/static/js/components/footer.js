/*
  Function to render the footer content into the page
      Select the footer element from the DOM
      Set the inner HTML of the footer element to include the footer content
  This section dynamically generates the footer content for the web page, including the hospital's logo, copyright information, and various helpful links.

  1. Insert Footer HTML Content

     * The content is inserted into the `footer` element with the ID "footer" using `footer.innerHTML`.
     * This is done dynamically via JavaScript to ensure that the footer is properly rendered across different pages.

  2. Create the Footer Wrapper

     * The `<footer>` tag with class `footer` wraps the entire footer content, ensuring that it is styled appropriately.

  3. Create the Footer Container

     * Inside the footer, a container div ensures proper alignment and spacing.

  4-11. Structure links and render footer (see instructions above). 

  Call the renderFooter function to populate the footer in the page
*/

function renderFooter() {
    const container = document.getElementById("footer");
    if (!container) return;
    container.innerHTML = `
    <footer class="footer bg-light border-top py-4 mt-5">
      <div class="container">
        <div class="row align-items-center mb-3">
          <div class="col-md-6 d-flex align-items-center">
            <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" width="24" class="me-2">
            <p class="mb-0">© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
          </div>
        </div>
        <div class="row g-3">
          <div class="col-6 col-md-4">
            <h5>Company</h5>
            <ul class="list-unstyled small">
              <li><a href="#" class="link-secondary text-decoration-none">About</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Careers</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Press</a></li>
            </ul>
          </div>
          <div class="col-6 col-md-4">
            <h5>Support</h5>
            <ul class="list-unstyled small">
              <li><a href="#" class="link-secondary text-decoration-none">Account</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Help Center</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Contact Us</a></li>
            </ul>
          </div>
          <div class="col-6 col-md-4">
            <h5>Legals</h5>
            <ul class="list-unstyled small">
              <li><a href="#" class="link-secondary text-decoration-none">Terms & Conditions</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Privacy Policy</a></li>
              <li><a href="#" class="link-secondary text-decoration-none">Licensing</a></li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  `;
}

document.addEventListener("DOMContentLoaded", renderFooter);
