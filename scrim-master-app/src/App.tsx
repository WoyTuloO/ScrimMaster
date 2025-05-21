import React from "react";
import "./App.css";
import Nav from "./assets/Nav";
import Container from "@mui/material/Container";
import { createTheme, CssBaseline, ThemeProvider } from "@mui/material";
import { Routes, Route, useParams } from "react-router-dom";
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

const theme = createTheme({
  palette: {
    background: { default: "#363636" },
    primary: { main: "#ff8000" },
    secondary: { main: "#242F40" },
    // forms: { main: "#FFFFFF" },
  },
});

function PrivateChatRoute() {
  const { recipient } = useParams();
  if (!recipient) return <p>Brak odbiorcy</p>;
  return <PrivateChat recipient={recipient} />;
}

export default function App() {
  return (
      <AuthProvider>
        <ThemeProvider theme={theme}>
          <CssBaseline />
          {/* Router jest już zdefiniowany w index.tsx, więc nie owijamy tu BrowserRouter */}
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
              <Route path="/admin" element={<ProtectedRoute requiredRole="ROLE_ADMIN">
                <AdminPanel />
              </ProtectedRoute>} />
              <Route path="/scrims" element={<PublicChat />} />
              <Route path="/chat/:recipient" element={<PrivateChatRoute />} />
            </Routes>
          </Container>
        </ThemeProvider>
      </AuthProvider>
  );
}