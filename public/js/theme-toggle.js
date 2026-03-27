/**
 * RESTEasy Theme Switcher
 * Toggles between light and dark modes using Bootstrap's data-bs-theme
 */

(function() {
  'use strict';

  // Get stored theme or default to light
  const getStoredTheme = () => localStorage.getItem('theme');
  const setStoredTheme = theme => localStorage.setItem('theme', theme);

  // Get preferred theme (stored or system preference)
  const getPreferredTheme = () => {
    const storedTheme = getStoredTheme();
    if (storedTheme) {
      return storedTheme;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  };

  // Apply theme to document
  const setTheme = theme => {
    document.documentElement.setAttribute('data-bs-theme', theme);
    updateToggleButton(theme);
  };

  // Update the toggle button icon and text
  const updateToggleButton = theme => {
    const toggleBtn = document.getElementById('theme-toggle');
    if (!toggleBtn) return;

    const icon = toggleBtn.querySelector('i');
    const text = toggleBtn.querySelector('.theme-toggle-text');

    if (theme === 'dark') {
      icon.className = 'fas fa-sun';
      if (text) text.textContent = 'Light';
    } else {
      icon.className = 'fas fa-moon';
      if (text) text.textContent = 'Dark';
    }
  };

  // Toggle between light and dark
  const toggleTheme = () => {
    const currentTheme = document.documentElement.getAttribute('data-bs-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    setStoredTheme(newTheme);
    setTheme(newTheme);
  };

  // Initialize theme on page load
  document.addEventListener('DOMContentLoaded', () => {
    setTheme(getPreferredTheme());

    // Add click handler to toggle button
    const toggleBtn = document.getElementById('theme-toggle');
    if (toggleBtn) {
      toggleBtn.addEventListener('click', toggleTheme);
    }
  });

  // Listen for system theme changes
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
    const storedTheme = getStoredTheme();
    if (!storedTheme) {
      setTheme(e.matches ? 'dark' : 'light');
    }
  });

  // Expose toggle function globally if needed
  window.toggleTheme = toggleTheme;
})();

/**
 * Mobile Navigation Toggle
 * Handles hamburger menu collapse on mobile devices
 */
(function() {
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {
    const toggler = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    if (toggler && navbarCollapse) {
      toggler.addEventListener('click', () => {
        const isExpanded = toggler.getAttribute('aria-expanded') === 'true';

        if (isExpanded) {
          navbarCollapse.classList.remove('show');
          toggler.setAttribute('aria-expanded', 'false');
        } else {
          navbarCollapse.classList.add('show');
          toggler.setAttribute('aria-expanded', 'true');
        }
      });

      // Close menu when clicking a link (mobile)
      const navLinks = navbarCollapse.querySelectorAll('a');
      navLinks.forEach(link => {
        link.addEventListener('click', () => {
          if (window.innerWidth <= 768) {
            navbarCollapse.classList.remove('show');
            toggler.setAttribute('aria-expanded', 'false');
          }
        });
      });

      // Close menu when clicking outside
      document.addEventListener('click', (e) => {
        if (window.innerWidth <= 768) {
          if (!toggler.contains(e.target) && !navbarCollapse.contains(e.target)) {
            navbarCollapse.classList.remove('show');
            toggler.setAttribute('aria-expanded', 'false');
          }
        }
      });
    }
  });
})();

/**
 * Active Navigation Highlighting
 * Highlights the current navigation item based on the URL
 */
(function() {
  'use strict';

  document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('#proj_nav [data-nav]');

    navItems.forEach(item => {
      const section = item.getAttribute('data-nav');
      item.classList.remove('current');

      // Check if current path matches this nav item
      if (section === 'overview' && (currentPath === '/' || currentPath === '/index.html')) {
        item.classList.add('current');
      } else if (section === 'downloads' && currentPath.startsWith('/downloads')) {
        item.classList.add('current');
      } else if (section === 'docs' && currentPath.startsWith('/docs')) {
        item.classList.add('current');
      } else if (section === 'community' && currentPath.startsWith('/community')) {
        item.classList.add('current');
      } else if (section === 'blogs' && (currentPath.startsWith('/blogs') || currentPath.startsWith('/posts'))) {
        item.classList.add('current');
      }
    });
  });
})();
