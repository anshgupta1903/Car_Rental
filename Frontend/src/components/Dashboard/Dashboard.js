import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import { bookRide } from '../../services/formService';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const user = authService.getCurrentUser();

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };
  const [booking, setBooking] = useState({
    location: '',
    dropoffLocation: '',
    carType: '',
    pickupDate: '',
    pickupTime: '',
    returnDate: '',
    returnTime: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setBooking((prev) => ({ ...prev, [name]: value }));
  };

  const handleSearch = async (e) => {
    e.preventDefault();

    // Map UI fields to backend schema
    const pickupDateTime = `${booking.pickupDate}T${booking.pickupTime}:00`;
    const returnDateTime = booking.returnDate && booking.returnTime
      ? `${booking.returnDate}T${booking.returnTime}:00`
      : null;

    const payload = {
      pickupLocation: booking.location,
      dropoffLocation: booking.dropoffLocation || null,
      pickupDateTime,
      returnDateTime,
      carType: booking.carType || null,
      fullName: (user && (user.username || user.fullName)) || null,
      email: (user && user.email) || null,
    };

    try {
      const saved = await bookRide(payload);
      alert('Booking submitted and stored! ID: ' + saved.id);
    } catch (err) {
      console.error('Failed to store booking', err);
      alert('Failed to store booking. Please ensure backend is running and try again.');
    }
  };

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('visible');
          } else {
            // Remove visibility when out of view so it re-animates on re-entry
            entry.target.classList.remove('visible');
          }
        });
      },
      { threshold: 0.2, rootMargin: '0px 0px -10% 0px' }
    );

    const elements = document.querySelectorAll('.reveal');
    elements.forEach((el) => observer.observe(el));
    return () => observer.disconnect();
  }, [navigate]);

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <div className="logo" onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })} role="button">
            <i className="fas fa-car-alt"></i>
            <span>CarRental</span>
          </div>

          <nav className="nav-links">
            <a href="#book">Book</a>
            <a href="#cars">Cars</a>
            <a href="#features">Features</a>
            <a href="#contact">Contact</a>
          </nav>

          <div className="user-menu">
            <div className="user-info">
              <span>Welcome{user ? `, ${user.username || user.email}` : ''}</span>
            </div>
            {user ? (
              <button className="logout-btn" onClick={handleLogout}>
                <i className="fas fa-sign-out-alt"></i>
                Logout
              </button>
            ) : (
              <button className="logout-btn" onClick={() => navigate('/login')}>
                <i className="fas fa-sign-in-alt"></i>
                Login
              </button>
            )}
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="container">
          <div className="welcome-section reveal">
            <h1>Welcome to CarRental</h1>
            <p>Your premium car rental experience starts here</p>
          </div>

          {/* Booking Form */}
          <section id="book" className="booking-section reveal">
            <div className="booking-card">
              <h2>Book your ride</h2>
              <form className="booking-form" onSubmit={handleSearch}>
                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="location">Location</label>
                    <input id="location" name="location" type="text" placeholder="City or Airport" value={booking.location} onChange={handleChange} required />
                  </div>
                  <div className="form-group">
                    <label htmlFor="dropoffLocation">Dropoff location</label>
                    <input id="dropoffLocation" name="dropoffLocation" type="text" placeholder="Where to drop" value={booking.dropoffLocation} onChange={handleChange} />
                  </div>
                  <div className="form-group">
                    <label htmlFor="carType">Car type</label>
                    <select id="carType" name="carType" value={booking.carType} onChange={handleChange}>
                      <option value="">Any</option>
                      <option value="economy">Economy</option>
                      <option value="suv">SUV</option>
                      <option value="sedan">Sedan</option>
                      <option value="luxury">Luxury</option>
                    </select>
                  </div>
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label htmlFor="pickupDate">Pickup</label>
                    <div className="split-inputs">
                      <input id="pickupDate" name="pickupDate" type="date" value={booking.pickupDate} onChange={handleChange} required />
                      <input id="pickupTime" name="pickupTime" type="time" value={booking.pickupTime} onChange={handleChange} required />
                    </div>
                  </div>
                  <div className="form-group">
                    <label htmlFor="returnDate">Return</label>
                    <div className="split-inputs">
                      <input id="returnDate" name="returnDate" type="date" value={booking.returnDate} onChange={handleChange} required />
                      <input id="returnTime" name="returnTime" type="time" value={booking.returnTime} onChange={handleChange} required />
                    </div>
                  </div>
                </div>
                <div className="form-actions">
                  <button type="submit" className="btn primary xl">Search Cars</button>
                </div>
              </form>
            </div>
          </section>

          {/* Features Section moved from MainPage */}
          <section id="features" className="features-section reveal">
            <h2 className="section-title">Why choose CarRental?</h2>
            <div className="features-grid">
              <div className="feature-card fade-in">
                <div className="feature-icon">🚗</div>
                <h3>Wide Fleet</h3>
                <p>From compact city cars to luxury SUVs, pick what suits your trip.</p>
              </div>
              <div className="feature-card fade-in" style={{ animationDelay: '0.1s' }}>
                <div className="feature-icon">💳</div>
                <h3>Best Prices</h3>
                <p>Transparent pricing with no hidden fees. Pay only for what you need.</p>
              </div>
              <div className="feature-card fade-in" style={{ animationDelay: '0.2s' }}>
                <div className="feature-icon">🕒</div>
                <h3>24/7 Support</h3>
                <p>We’re here round the clock for roadside assistance and help.</p>
              </div>
              <div className="feature-card fade-in" style={{ animationDelay: '0.3s' }}>
                <div className="feature-icon">🌍</div>
                <h3>Locations Everywhere</h3>
                <p>Pickup and drop across major cities and airports hassle‑free.</p>
              </div>
            </div>
          </section>

          {/* Demo Cars Section */}
          <section id="cars" className="cars-section reveal">
            <h2 className="section-title">Explore cars</h2>
            <div className="cars-grid">
              {[
                { title: 'Economy Hatch', price: 49, img: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?q=80&w=1200&auto=format&fit=crop' },
                { title: 'City Sedan', price: 65, img: 'https://images.unsplash.com/photo-1619767886558-efdc259cde1a?q=80&w=1200&auto=format&fit=crop' },
                { title: 'Family SUV', price: 79, img: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=1200&auto=format&fit=crop' },
                { title: 'Executive Luxury', price: 129, img: 'https://images.unsplash.com/photo-1525609004556-c46c7d6cf023?q=80&w=1200&auto=format&fit=crop' },
              ].map((c, i) => (
                <div className="car-card" key={i}>
                  <div className="car-media">
                    <img src={c.img} alt={c.title} />
                  </div>
                  <div className="car-info">
                    <h3>{c.title}</h3>
                    <p className="car-price">${c.price}/day</p>
                    <button className="btn primary">Book Now</button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </main>

      <footer id="contact" className="footer reveal">
        <div className="footer-inner">
          <div className="footer-grid">
            <div className="footer-col">
              <div className="brand">CarRental</div>
              <p className="footer-text">Premium car rentals with transparent pricing and round‑the‑clock support. Travel without compromise.</p>
              <div className="socials">
                <a href="https://facebook.com" target="_blank" rel="noreferrer" aria-label="Facebook"><i className="fab fa-facebook"></i></a>
                <a href="https://twitter.com" target="_blank" rel="noreferrer" aria-label="Twitter"><i className="fab fa-twitter"></i></a>
                <a href="https://instagram.com" target="_blank" rel="noreferrer" aria-label="Instagram"><i className="fab fa-instagram"></i></a>
                <a href="https://linkedin.com" target="_blank" rel="noreferrer" aria-label="LinkedIn"><i className="fab fa-linkedin"></i></a>
              </div>
            </div>

            <div className="footer-col">
              <h4>Quick Links</h4>
              <ul className="footer-links">
                <li><a href="#book">Book a car</a></li>
                <li><a href="#cars">Our cars</a></li>
                <li><a href="#features">Features</a></li>
                <li><a href="#contact">Contact</a></li>
              </ul>
            </div>

            <div className="footer-col">
              <h4>Contact us</h4>
              <form className="contact-form" onSubmit={(e) => { e.preventDefault(); alert('Thanks! We will get back to you shortly.'); }}>
                <div className="form-row">
                  <input type="text" name="name" placeholder="Your name" required />
                  <input type="email" name="email" placeholder="Your email" required />
                </div>
                <textarea name="message" rows="3" placeholder="How can we help?" required />
                <div className="form-actions">
                  <button type="submit" className="btn primary">Send message</button>
                </div>
              </form>
            </div>
          </div>

          <div className="footer-bottom">
            <span className="copy">© {new Date().getFullYear()} CarRental. All rights reserved.</span>
            <div className="links">
              <a href="#">Privacy</a>
              <a href="#">Terms</a>
              <a href="#">Cookies</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Dashboard;
