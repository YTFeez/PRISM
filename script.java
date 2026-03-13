(function () {
  "use strict";

  const header = document.querySelector(".header");
  const navToggle = document.querySelector(".nav-toggle");
  const navLinks = document.querySelector(".nav-links");
  const skillsSection = document.querySelector(".section.skills");
  const scrollProgress = document.querySelector(".scroll-progress");
  const revealEls = document.querySelectorAll(".reveal");
  const statNumbers = document.querySelectorAll(".stat-number");
  const projectCards = document.querySelectorAll(".project-card[data-tilt]");
  const projectsSection = document.querySelector(".section.projects");

  function updateScrollProgress() {
    if (!scrollProgress) return;
    const h = document.documentElement.scrollHeight - window.innerHeight;
    const pct = h > 0 ? (window.scrollY / h) * 100 : 0;
    scrollProgress.style.width = pct + "%";
  }

  function onScroll() {
    if (window.scrollY > 50) {
      header.classList.add("scrolled");
    } else {
      header.classList.remove("scrolled");
    }
    updateScrollProgress();
  }

  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();
  updateScrollProgress();

  if (revealEls.length) {
    var revealObserver = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible");
          }
        });
      },
      { threshold: 0.1, rootMargin: "0px 0px -40px 0px" }
    );
    revealEls.forEach(function (el) {
      revealObserver.observe(el);
    });
  }

  if (skillsSection) {
    var skillsObserver = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible");
          }
        });
      },
      { threshold: 0.3, rootMargin: "0px 0px -50px 0px" }
    );
    skillsObserver.observe(skillsSection);
  }

  function animateValue(el, end, duration) {
    var start = 0;
    var startTime = null;
    function step(timestamp) {
      if (!startTime) startTime = timestamp;
      var progress = Math.min((timestamp - startTime) / duration, 1);
      var easeOut = 1 - Math.pow(1 - progress, 3);
      var current = Math.round(start + (end - start) * easeOut);
      el.textContent = current;
      if (progress < 1) requestAnimationFrame(step);
    }
    requestAnimationFrame(step);
  }

  if (statNumbers.length) {
    var statsObserver = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (!entry.isIntersecting) return;
          var el = entry.target;
          var count = parseInt(el.getAttribute("data-count"), 10);
          if (isNaN(count)) return;
          animateValue(el, count, 1500);
          statsObserver.unobserve(el);
        });
      },
      { threshold: 0.5 }
    );
    statNumbers.forEach(function (el) {
      statsObserver.observe(el);
    });
  }

  if (projectsSection) {
    projectsSection.addEventListener("mousemove", function (e) {
      var rect = projectsSection.getBoundingClientRect();
      var x = ((e.clientX - rect.left) / rect.width) * 100;
      var y = ((e.clientY - rect.top) / rect.height) * 100;
      projectsSection.style.setProperty("--mx", x + "%");
      projectsSection.style.setProperty("--my", y + "%");
    });
    projectsSection.addEventListener("mouseleave", function () {
      projectsSection.style.setProperty("--mx", "50%");
      projectsSection.style.setProperty("--my", "0%");
    });
  }

  projectCards.forEach(function (card) {
    var inner = card.querySelector(".project-card-inner");
    if (!inner) inner = card;
    card.addEventListener("mousemove", function (e) {
      var rect = card.getBoundingClientRect();
      var x = (e.clientX - rect.left) / rect.width;
      var y = (e.clientY - rect.top) / rect.height;
      var tiltX = (y - 0.5) * -10;
      var tiltY = (x - 0.5) * 10;
      card.style.transform = "translateY(-6px) scale(1.02)";
      inner.style.transform = "rotateX(" + tiltX + "deg) rotateY(" + tiltY + "deg) translateZ(8px)";
    });
    card.addEventListener("mouseleave", function () {
      card.style.transform = "";
      inner.style.transform = "";
    });
  });

  if (navToggle && navLinks) {
    navToggle.addEventListener("click", function () {
      navToggle.classList.toggle("open");
      navLinks.classList.toggle("open");
      document.body.style.overflow = navLinks.classList.contains("open") ? "hidden" : "";
    });

    navLinks.querySelectorAll("a").forEach(function (link) {
      link.addEventListener("click", function () {
        navToggle.classList.remove("open");
        navLinks.classList.remove("open");
        document.body.style.overflow = "";
      });
    });
  }

  if (skillsSection) {
    const observer = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.classList.add("visible");
          }
        });
      },
      { threshold: 0.3, rootMargin: "0px 0px -50px 0px" }
    );
    observer.observe(skillsSection);
  }

  // THREE.JS - Modèle 3D du robot mBot
  const canvas = document.getElementById('canvas3d');
  if (canvas && typeof THREE !== 'undefined') {
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ canvas: canvas, alpha: true, antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setClearColor(0x000000, 0);

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.6);
    scene.add(ambientLight);
    const pointLight = new THREE.PointLight(0xf59e0b, 1.5, 100);
    pointLight.position.set(5, 10, 5);
    scene.add(pointLight);
    const pointLight2 = new THREE.PointLight(0xfbbf24, 1, 100);
    pointLight2.position.set(-5, 5, -5);
    scene.add(pointLight2);

    const robot = new THREE.Group();

    const chassisGeom = new THREE.BoxGeometry(3, 0.3, 2);
    const chassisMat = new THREE.MeshPhongMaterial({ color: 0x1a1a1a, shininess: 80 });
    const chassis = new THREE.Mesh(chassisGeom, chassisMat);
    robot.add(chassis);

    const topGeom = new THREE.BoxGeometry(2, 0.8, 1.5);
    const topMat = new THREE.MeshPhongMaterial({ color: 0xf59e0b, emissive: 0xf59e0b, emissiveIntensity: 0.3 });
    const top = new THREE.Mesh(topGeom, topMat);
    top.position.y = 0.55;
    robot.add(top);

    const ledGeom = new THREE.SphereGeometry(0.15, 16, 16);
    const ledMat = new THREE.MeshPhongMaterial({ color: 0x00ffff, emissive: 0x00ffff, emissiveIntensity: 1 });
    const led1 = new THREE.Mesh(ledGeom, ledMat);
    led1.position.set(0.5, 0.6, 0.85);
    robot.add(led1);
    const led2 = new THREE.Mesh(ledGeom, ledMat);
    led2.position.set(-0.5, 0.6, 0.85);
    robot.add(led2);

    const wheelGeom = new THREE.CylinderGeometry(0.4, 0.4, 0.2, 32);
    const wheelMat = new THREE.MeshPhongMaterial({ color: 0x333333 });

    const wheel1 = new THREE.Mesh(wheelGeom, wheelMat);
    wheel1.rotation.z = Math.PI / 2;
    wheel1.position.set(1.3, -0.2, 0.7);
    robot.add(wheel1);

    const wheel2 = new THREE.Mesh(wheelGeom, wheelMat);
    wheel2.rotation.z = Math.PI / 2;
    wheel2.position.set(-1.3, -0.2, 0.7);
    robot.add(wheel2);

    const wheel3 = new THREE.Mesh(wheelGeom, wheelMat);
    wheel3.rotation.z = Math.PI / 2;
    wheel3.position.set(1.3, -0.2, -0.7);
    robot.add(wheel3);

    const wheel4 = new THREE.Mesh(wheelGeom, wheelMat);
    wheel4.rotation.z = Math.PI / 2;
    wheel4.position.set(-1.3, -0.2, -0.7);
    robot.add(wheel4);

    const sensorGeom = new THREE.BoxGeometry(0.8, 0.4, 0.3);
    const sensorMat = new THREE.MeshPhongMaterial({ color: 0x555555 });
    const sensor = new THREE.Mesh(sensorGeom, sensorMat);
    sensor.position.set(0, 0.2, 1.15);
    robot.add(sensor);

    const holeGeom = new THREE.CylinderGeometry(0.12, 0.12, 0.35, 16);
    const holeMat = new THREE.MeshPhongMaterial({ color: 0x000000, emissive: 0xf59e0b, emissiveIntensity: 0.2 });
    const hole1 = new THREE.Mesh(holeGeom, holeMat);
    hole1.rotation.x = Math.PI / 2;
    hole1.position.set(-0.25, 0.2, 1.3);
    robot.add(hole1);
    const hole2 = new THREE.Mesh(holeGeom, holeMat);
    hole2.rotation.x = Math.PI / 2;
    hole2.position.set(0.25, 0.2, 1.3);
    robot.add(hole2);

    const detailGeom = new THREE.BoxGeometry(0.3, 0.1, 0.3);
    const detailMat = new THREE.MeshPhongMaterial({ color: 0xfbbf24, emissive: 0xfbbf24, emissiveIntensity: 0.4 });
    const detail1 = new THREE.Mesh(detailGeom, detailMat);
    detail1.position.set(0.8, 0.2, -0.9);
    robot.add(detail1);
    const detail2 = new THREE.Mesh(detailGeom, detailMat);
    detail2.position.set(-0.8, 0.2, -0.9);
    robot.add(detail2);

    scene.add(robot);
    camera.position.z = 8;
    camera.position.y = 3;

    let mouseX = 0, mouseY = 0;
    document.addEventListener('mousemove', (e) => {
      mouseX = (e.clientX / window.innerWidth) * 2 - 1;
      mouseY = -(e.clientY / window.innerHeight) * 2 + 1;
    });

    function animate() {
      requestAnimationFrame(animate);
      robot.rotation.y += 0.005;
      robot.rotation.y += mouseX * 0.01;
      robot.rotation.x = mouseY * 0.3;
      robot.position.y = Math.sin(Date.now() * 0.001) * 0.2;

      led1.material.emissiveIntensity = 0.5 + Math.sin(Date.now() * 0.003) * 0.5;
      led2.material.emissiveIntensity = 0.5 + Math.cos(Date.now() * 0.003) * 0.5;

      renderer.render(scene, camera);
    }
    animate();

    window.addEventListener('resize', () => {
      camera.aspect = window.innerWidth / window.innerHeight;
      camera.updateProjectionMatrix();
      renderer.setSize(window.innerWidth, window.innerHeight);
    });
  }
})();
