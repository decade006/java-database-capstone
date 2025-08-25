// footer.js - dynamic footer renderer
(function() {
  function renderFooter() {
    const mount = document.getElementById('footer');
    if (!mount) return;

    // Use absolute asset path so it works on both static and Thymeleaf pages
    mount.innerHTML = `
      <footer class="footer">
        <div class="footer-container">
          <div class="footer-logo">
            <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" />
            <p>© Copyright 2025. All Rights Reserved by Hospital CMS.</p>
          </div>
          <div class="footer-links">
            <div class="footer-column">
              <h4>Company</h4>
              <a href="#">About</a>
              <a href="#">Careers</a>
              <a href="#">Press</a>
            </div>
            <div class="footer-column">
              <h4>Support</h4>
              <a href="#">Account</a>
              <a href="#">Help Center</a>
              <a href="#">Contact Us</a>
            </div>
            <div class="footer-column">
              <h4>Legals</h4>
              <a href="#">Terms &amp; Conditions</a>
              <a href="#">Privacy Policy</a>
              <a href="#">Licensing</a>
            </div>
          </div>
        </div>
      </footer>
    `;
  }

  // Expose for possible manual calls, but also auto-render
  window.renderFooter = renderFooter;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', renderFooter);
  } else {
    renderFooter();
  }
})();
