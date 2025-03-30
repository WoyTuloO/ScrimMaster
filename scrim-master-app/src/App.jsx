
import './App.css'
import Nav from "./assets/Nav.tsx";
import Container from "@mui/material/Container";
import {createTheme, CssBaseline, Paper, ThemeProvider} from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {BrowserRouter, Route, Routes} from "react-router-dom";


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

  return<>
    <ThemeProvider theme={theme}>
      <CssBaseline />
        <Container disableGutters maxWidth={false}>
          <Nav></Nav>
            <BrowserRouter>
              <Routes>
                <Route path="/" component={App} />
              </Routes>
            </BrowserRouter>
        </Container>
    </ThemeProvider>
  </>



}

export default App
