import './App.css'
import Nav from "./assets/Nav.tsx";
import Container from "@mui/material/Container";
import { createTheme, CssBaseline, ThemeProvider } from "@mui/material";
import { Route, Routes } from "react-router-dom";
import Login from './components/Login.tsx';
import Register from './components/Register.tsx';
import LandingPage from './components/LandingPage.tsx';

const theme = createTheme({
  palette: {
    background: {
      default: 'white',
    },
    primary: {
      main: "#5223c4",
    },
    secondary:{
      main: '#38168e',
    }
  },
});

function App() {
  return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Container disableGutters maxWidth={false}>
          <Nav />
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
          </Routes>
        </Container>
      </ThemeProvider>
  );
}

export default App;
