(function () {
  var header = document.getElementById("header");
  var toggle = document.getElementById("navToggle");
  var mobile = document.getElementById("navMobile");
  var bar = document.getElementById("scrollProgress");
  var navLinks = document.querySelectorAll('.nav-desktop a[href^="#"], .nav-mobile a[href^="#"]');
  var sections = ["about", "specs", "perf", "mission"];
  var perfEl = document.getElementById("perf");
  var rows = document.querySelectorAll("#perfBars .cmp-block");
  var kpiBox = document.getElementById("kpiRow");
  var perfDone = false;
  var kpiDone = false;
  var ticking = false;

  function debounce(fn, ms) {
    var t;
    return function () {
      clearTimeout(t);
      t = setTimeout(fn, ms);
    };
  }

  function closeMenu() {
    if (!toggle || !mobile) return;
    toggle.setAttribute("aria-expanded", "false");
    mobile.classList.remove("is-open");
  }

  function markNav(id) {
    navLinks.forEach(function (a) {
      a.classList.toggle("is-active", a.getAttribute("href") === "#" + id);
    });
  }

  function onScroll() {
    var y = window.scrollY || document.documentElement.scrollTop;
    if (header) header.classList.toggle("is-scrolled", y > 24);
    if (bar) {
      var doc = document.documentElement;
      var maxScroll = doc.scrollHeight - doc.clientHeight;
      bar.style.width = maxScroll > 0 ? (y / maxScroll) * 100 + "%" : "0%";
    }
    var ref = y + window.innerHeight * 0.28;
    var hit = null;
    for (var i = sections.length - 1; i >= 0; i--) {
      var el = document.getElementById(sections[i]);
      if (!el) continue;
      var top = el.getBoundingClientRect().top + y;
      if (top <= ref) {
        hit = sections[i];
        break;
      }
    }
    if (hit) markNav(hit);
    else navLinks.forEach(function (a) { a.classList.remove("is-active"); });
  }

  function loopScroll() {
    if (!ticking) {
      requestAnimationFrame(function () {
        onScroll();
        ticking = false;
      });
      ticking = true;
    }
  }

  if (typeof Lenis !== "undefined") {
    var lenis = new Lenis({ duration: 1.05, smoothWheel: true });
    function raf(time) {
      lenis.raf(time);
      requestAnimationFrame(raf);
    }
    requestAnimationFrame(raf);
    lenis.on("scroll", onScroll);
  } else {
    window.addEventListener("scroll", loopScroll, { passive: true });
  }
  onScroll();

  if (toggle && mobile) {
    toggle.addEventListener("click", function () {
      var o = toggle.getAttribute("aria-expanded") === "true";
      toggle.setAttribute("aria-expanded", o ? "false" : "true");
      mobile.classList.toggle("is-open", !o);
    });
    mobile.querySelectorAll("a").forEach(function (a) {
      a.addEventListener("click", closeMenu);
    });
  }

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") closeMenu();
  });

  window.addEventListener(
    "resize",
    debounce(function () {
      if (window.innerWidth >= 768) closeMenu();
    }, 150)
  );

  document.querySelectorAll(".reveal").forEach(function (node) {
    var io = new IntersectionObserver(
      function (ents) {
        ents.forEach(function (ent) {
          if (ent.isIntersecting) {
            ent.target.classList.add("is-visible");
            io.unobserve(ent.target);
          }
        });
      },
      { threshold: 0.12, rootMargin: "0px 0px -40px 0px" }
    );
    io.observe(node);
  });

  function ease3(t) {
    return 1 - Math.pow(1 - t, 3);
  }

  function countUp(el, target, ms) {
    var start = null;
    function frame(ts) {
      if (start === null) start = ts;
      var p = Math.min((ts - start) / ms, 1);
      el.textContent = Math.round(target * ease3(p));
      if (p < 1) requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
  }

  if (perfEl) {
    var obs = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (!entry.isIntersecting) return;
          if (!perfDone) {
            perfDone = true;
            rows.forEach(function (row) {
              row.classList.add("is-on");
              var w = row.getAttribute("data-w");
              var r = row.getAttribute("data-ref");
              var us = row.querySelector(".cmp-fill--us");
              var ref = row.querySelector(".cmp-fill--ref");
              var lab = row.querySelector(".cmp-val--us");
              if (us) us.style.width = w + "%";
              if (ref) ref.style.width = r + "%";
              if (lab) lab.textContent = w + "%";
            });
          }
          if (!kpiDone && kpiBox && entry.target.contains(kpiBox)) {
            kpiDone = true;
            kpiBox.querySelectorAll(".n").forEach(function (n) {
              countUp(n, parseInt(n.getAttribute("data-target"), 10), 1200);
            });
          }
        });
      },
      { threshold: 0.22 }
    );
    obs.observe(perfEl);
  }
})();
