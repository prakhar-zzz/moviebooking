const API_URL = "http://localhost:8080";  // backend base URL

// ===== LOGIN =====
function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  if (!username || !password) {
    alert("Please enter both username and password.");
    return;
  }

  fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
  .then(res => {
    if (res.ok) {
      localStorage.setItem("username", username);
      window.location.href = "dashboard.html";
    } else {
      return res.text().then(msg => {
        throw new Error(msg || "Invalid credentials!");
      });
    }
  })
  .catch(err => alert("Login failed: " + err.message));
}

// ===== REGISTER =====
function register() {
  const username = document.getElementById("regUsername").value;
  const password = document.getElementById("regPassword").value;

  if (!username || !password) {
    alert("Please fill in both fields.");
    return;
  }

  fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
  .then(res => {
    if (res.ok) {
      alert("Registration successful! Please login.");
      window.location.href = "index.html"; // back to login
    } else {
      return res.text().then(msg => {
        throw new Error(msg || "Registration failed!");
      });
    }
  })
  .catch(err => alert("Registration failed: " + err.message));
}

// ===== FETCH SHOWS =====
function fetchShows() {
  fetch(`${API_URL}/shows`)
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch shows");
      return res.json();
    })
    .then(data => {
      const showsList = document.getElementById("showsList");
      showsList.innerHTML = "";
      data.forEach(show => {
        const div = document.createElement("div");
        div.className = "movie-card";
        div.innerHTML = `
          <h3>${show.movieName}</h3>
          <p>Time: ${show.showTime}</p>
          <p>Available: ${show.availableSeats}</p>
          <button onclick="goToShow(${show.id})">Book Now</button>
        `;
        showsList.appendChild(div);
      });
    })
    .catch(err => alert(err.message));
}

// ===== LOAD SEATS =====
function loadSeats() {
  const urlParams = new URLSearchParams(window.location.search);
  const showId = urlParams.get("showId");
  localStorage.setItem("currentShow", showId);

  fetch(`${API_URL}/shows/${showId}/seats`)
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch seats");
      return res.json();
    })
    .then(data => {
      const seatContainer = document.getElementById("seatContainer");
      seatContainer.innerHTML = "";
      data.forEach(seat => {
        const seatDiv = document.createElement("div");
        seatDiv.className = "seat " + (seat.booked ? "booked" : "available");
        seatDiv.textContent = seat.seatNumber;
        seatDiv.onclick = () => toggleSeat(seatDiv, seat.seatNumber, seat.booked);
        seatContainer.appendChild(seatDiv);
      });
    })
    .catch(err => alert(err.message));
}

let selectedSeats = [];
function toggleSeat(div, seatNumber, booked) {
  if (booked) return;
  if (selectedSeats.includes(seatNumber)) {
    selectedSeats = selectedSeats.filter(s => s !== seatNumber);
    div.classList.remove("selected");
  } else {
    selectedSeats.push(seatNumber);
    div.classList.add("selected");
  }
}

// ===== BOOK SEATS =====
function bookSeats() {
  const showId = localStorage.getItem("currentShow");
  const username = localStorage.getItem("username");

  if (selectedSeats.length === 0) {
    alert("Please select at least one seat.");
    return;
  }

  fetch(`${API_URL}/bookings`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      showId,
      customerName: username,
      seatNumbers: selectedSeats
    })
  })
  .then(res => {
    if (!res.ok) {
      return res.text().then(msg => { throw new Error(msg); });
    }
    return res.json();
  })
  .then(data => {
    localStorage.setItem("lastBooking", JSON.stringify(data));
    window.location.href = "confirm.html";
  })
  .catch(err => alert("Booking failed: " + err.message));
}

// ===== SHOW CONFIRMATION =====
function showConfirmation() {
  const booking = JSON.parse(localStorage.getItem("lastBooking"));
  if (!booking) {
    document.getElementById("bookingDetails").innerHTML = "No booking found.";
    return;
  }
  document.getElementById("bookingDetails").innerHTML =
    `Booking ID: ${booking.id}<br>
     Movie: ${booking.show.movieName}<br>
     Seats: ${booking.seats.map(s => s.seatNumber).join(", ")}`;
}

// ===== NAVIGATION =====
function goToShow(showId) {
  window.location.href = `show.html?showId=${showId}`;
}
