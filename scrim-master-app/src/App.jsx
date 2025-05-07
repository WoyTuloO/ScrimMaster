import './App.css'
import Nav from "./assets/Nav.tsx";
import Container from "@mui/material/Container";
import { createTheme, CssBaseline, ThemeProvider } from "@mui/material";
import { Route, Routes } from "react-router-dom";
import Login from './components/Login.tsx';
import Register from './components/Register.tsx';
import LandingPage from './components/LandingPage.tsx';
import {AuthProvider} from "./assets/AuthContext.js";
import PlayerRanking from "./components/PlayerRanking.tsx";
import TeamRanking from "./components/TeamRanking.js";
import PublicUserProfile from "./components/PublicUserProfile.js";
import UserPanel from "./components/UserPanel.js";
import AdminPanel from "./components/AdminPanel.tsx";
import ProtectedRoute from "./assets/ProtectedRoute.js";

const theme = createTheme({
  // palette: {
  //   background: {
  //     default: '#272727',
  //   },
  //   primary: {
  //     main: "#5223c4",
  //   },
  //   secondary:{
  //     main: '#38168e',
  //   },
  //   forms:{
  //     main: '#b0b9e1',
  //   }
  // },
  palette: {
    background: {
      default: '#363636',
    },
    primary: {
      main: "#ff8000",
    },
    secondary:{
      main: '#242F40',
    },
    forms:{
      main: '#FFFFFF',
    }
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
              <Route path="/admin" element={<ProtectedRoute requiredRole='ROLE_ADMIN'><AdminPanel/></ProtectedRoute>} />
            </Routes>
          </Container>
        </ThemeProvider>
      </AuthProvider>
  );
}

export default App;
