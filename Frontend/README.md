# CarRental Frontend

A beautiful, responsive React frontend for the CarRental application with authentication, light/dark mode, and clean code organization.

## Features

- 🚗 **Modern UI Design** - Beautiful, responsive interface with smooth animations
- 🌓 **Light/Dark Mode** - Toggle between light and dark themes with persistent storage
- 🔐 **Authentication** - Login and signup functionality connected to Spring Boot backend
- 📱 **Responsive Design** - Works perfectly on desktop, tablet, and mobile devices
- ⚡ **Clean Code Structure** - Organized components with separate CSS files
- 🎨 **Custom CSS** - No Tailwind dependency, pure CSS with CSS variables

## Project Structure

```
Frontend/
├── public/
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/
│   │   ├── LoginSignup/
│   │   │   ├── LoginSignup.js
│   │   │   └── LoginSignup.css
│   │   ├── Dashboard/
│   │   │   ├── Dashboard.js
│   │   │   └── Dashboard.css
│   │   └── ThemeToggle/
│   │       ├── ThemeToggle.js
│   │       └── ThemeToggle.css
│   ├── contexts/
│   │   └── ThemeContext.js
│   ├── services/
│   │   └── authService.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── package.json
└── README.md
```

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Spring Boot backend running on port 8080

## Installation

1. **Navigate to the Frontend directory:**
   ```bash
   cd C:\Users\hp\Desktop\car_rental\Frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start the development server:**
   ```bash
   npm start
   ```

4. **Open your browser and navigate to:**
   ```
   http://localhost:3000
   ```

## Backend Connection

The frontend is configured to connect to your Spring Boot backend running on `http://localhost:8080`. Make sure your backend is running before using the authentication features.

### API Endpoints Used:
- `POST /auth/login` - User login
- `POST /auth/signup` - User registration

## Components Overview

### LoginSignup Component
- **Location:** `src/components/LoginSignup/LoginSignup.js`
- **Features:**
  - Toggle between login and signup forms
  - Form validation with error handling
  - Password visibility toggle
  - Responsive design with animations
  - Integration with backend authentication

### ThemeToggle Component
- **Location:** `src/components/ThemeToggle/ThemeToggle.js`
- **Features:**
  - Switch between light and dark modes
  - Persistent theme storage in localStorage
  - Smooth transitions and animations

### Dashboard Component
- **Location:** `src/components/Dashboard/Dashboard.js`
- **Features:**
  - Welcome screen after successful login
  - User information display
  - Logout functionality
  - Placeholder for future car rental features

## Styling

The application uses CSS variables for theming, making it easy to customize colors and maintain consistency across light and dark modes.

### CSS Variables (Light Mode):
```css
--primary-color: #3b82f6;
--bg-primary: #ffffff;
--text-primary: #1e293b;
```

### CSS Variables (Dark Mode):
```css
--bg-primary: #0f172a;
--text-primary: #f8fafc;
```

## Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm eject` - Eject from Create React App

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Follow the existing code structure
2. Use CSS modules or separate CSS files for components
3. Maintain responsive design principles
4. Test on multiple devices and browsers

## Troubleshooting

### Common Issues:

1. **Backend Connection Error:**
   - Ensure Spring Boot backend is running on port 8080
   - Check CORS configuration in backend

2. **Theme Not Persisting:**
   - Check localStorage permissions in browser
   - Clear browser cache if needed

3. **Responsive Issues:**
   - Test on different screen sizes
   - Check CSS media queries

## Future Enhancements

- Car browsing and booking functionality
- User profile management
- Payment integration
- Booking history
- Real-time notifications
