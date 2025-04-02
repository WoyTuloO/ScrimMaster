import * as React from 'react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import { Link, Paper, TextField, Alert, Snackbar, Slide } from '@mui/material';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import { Link as RouterLink } from 'react-router-dom';
import { SlideProps } from '@mui/material/Slide';

function SlideTransition(props: SlideProps) {
    return <Slide {...props} direction="down" />;
}

function Register() {
    const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: "success" | "error"; }>(
        { open: false, message: '', severity: "success" }
    );
    const navigate = useNavigate();

    const handleCloseSnackbar = (
        _event: React.SyntheticEvent | Event,
        reason: string
    ) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    const handleRegisterSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        const username = formData.get('username') as string;
        const password = formData.get('password') as string;
        const repeatPassword = formData.get('repeatPassword') as string;
        const email = formData.get('email') as string;

        if (password !== repeatPassword) {
            setSnackbar({
                open: true,
                message: "Passwords do not match",
                severity: "error"
            });
            return;
        }

        const payload = {
            username,
            password,
            email,
            kd: 0,
            adr: 0,
            ranking: 0,
            teamId: 0,
            persmissionLevel: 0
        };

        fetch("http://localhost:8080/api/user", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (!response.ok) {
                    return response.clone().json().then(errorData => {
                        throw new Error(errorData.message || "Błąd podczas rejestracji");
                    });
                }
                return response.json();
            })
            .then(data => {
                console.log("Rejestracja przebiegła pomyślnie:", data);
                setSnackbar({
                    open: true,
                    message: "Rejestracja przebiegła pomyślnie. Przekierowywanie...",
                    severity: "success"
                });
                setTimeout(() => {
                    navigate('/login');
                }, 1500);
            })
            .catch(error => {
                console.error("Wystąpił błąd:", error.message);
                setSnackbar({
                    open: true,
                    message: error.message,
                    severity: "error"
                });
            });

    };

    // @ts-ignore
    return (
        <Box
            sx={{
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 }
                },
                height: '100vh',
                background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),url(/../../public/img1.jpg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                display: 'flex',
                alignItems: 'center'
            }}
        >
            <Container maxWidth="xs">
                <Paper elevation={4} sx={{ padding: 2, mt: { xs: 0, md: 10 } }}>
                    <Typography variant="h5" sx={{ textAlign: "center", fontFamily: 'Montserrat' }}>Register</Typography>
                    <Box component="form" onSubmit={handleRegisterSubmit} sx={{ mt: 1 }}>
                        <TextField
                            name="username"
                            variant="outlined"
                            label="Username"
                            required
                            fullWidth
                            sx={{ fontFamily: 'Montserrat' }}
                        />
                        <TextField
                            name="password"
                            variant="outlined"
                            label="Password"
                            type="password"
                            required
                            fullWidth
                            sx={{ mt: 2, fontFamily: 'Montserrat' }}
                        />
                        <TextField
                            name="repeatPassword"
                            variant="outlined"
                            label="Repeat password"
                            type="password"
                            required
                            fullWidth
                            sx={{ mt: 2, fontFamily: 'Montserrat' }}
                        />
                        <TextField
                            name="email"
                            variant="outlined"
                            label="Email"
                            type="email"
                            required
                            fullWidth
                            sx={{ mt: 2, fontFamily: 'Montserrat' }}
                        />
                        <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, fontFamily: 'Montserrat' }}>
                            Sign Up
                        </Button>
                    </Box>
                    <Typography sx={{ mt: 2, textAlign: "center", fontFamily: 'Montserrat' }}>
                        You already have an account?
                    </Typography>
                    <Typography sx={{ textAlign: "center", fontFamily: 'Montserrat' }}>
                        <Link component={RouterLink} to="/login">Sign In</Link>
                    </Typography>
                </Paper>
                <Snackbar
                    open={snackbar.open}
                    autoHideDuration={3000}
                    onClose={handleCloseSnackbar}
                    anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                    sx={{mt:10}}
                >
                    <Alert onClose={handleCloseSnackbar} variant={"filled"} severity={snackbar.severity} sx={{ width: '100%' }}>
                        {snackbar.message}
                    </Alert>
                </Snackbar>
            </Container>
        </Box>
    );
}

export default Register;
