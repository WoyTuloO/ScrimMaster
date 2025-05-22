// src/App.tsx
import React from "react";
import "./App.css";
import Nav from "./assets/Nav";
import Container from "@mui/material/Container";
import { createTheme, CssBaseline, ThemeProvider } from "@mui/material";
import { Route, Routes } from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import LandingPage from "./components/LandingPage";
import { AuthProvider } from "./assets/AuthContext";
import PlayerRanking from "./components/PlayerRanking";
import TeamRanking from "./components/TeamRanking";
import PublicUserProfile from "./components/PublicUserProfile";
import UserPanel from "./components/UserPanel";
import AdminPanel from "./components/AdminPanel";
import ProtectedRoute from "./assets/ProtectedRoute";
import PublicChat from "./components/PublicChat";
import PrivateChat from "./components/PrivateChat";
import MatchCreate from "./components/MatchCreate";


const theme = createTheme({
  palette: {
    background: { default: "#363636" },
    primary: { main: "#ff8000" },
    secondary: { main: "#242F40" },
  },
});

function App() {
  return (
      <AuthProvider>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Container disableGutters maxWidth={false}>
            <Nav />
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/players" element={<PlayerRanking />} />
              <Route path="/teams" element={<TeamRanking />} />
              <Route path="/user/:id" element={<PublicUserProfile />} />
              <Route path="/profile" element={<UserPanel />} />
              <Route
                  path="/admin"
                  element={
                    <ProtectedRoute requiredRole="ROLE_ADMIN">
                      <AdminPanel />
                    </ProtectedRoute>
                  }
              />

              <Route path="/scrims" element={<PublicChat />} />

              <Route path="/chat" element={<PublicChat />} />
              <Route path="/chat/:recipient" element={<PrivateChat />} />

              <Route path="/match/create/:proposalId" element={<MatchCreate />} />
            </Routes>
          </Container>
        </ThemeProvider>
      </AuthProvider>
  );
}

export default App;
