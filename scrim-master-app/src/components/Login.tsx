import * as React from 'react';
import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import Alert from '@mui/material/Alert';
import Snackbar from '@mui/material/Snackbar';
import { FormControlLabel, Link } from '@mui/material';
// @ts-ignore
import { CheckBox } from '@mui/icons-material';
import { Link as RouterLink } from 'react-router-dom';
import Slide, { SlideProps } from '@mui/material/Slide';
import {AuthContext} from '../assets/AuthContext';

function SlideTransition(props: SlideProps) {
    return <Slide {...props} direction="down" />;
}

const Login: React.FC = () => {
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();
    const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: "success" | "error"; }>({
        open: false,
        message: '',
        severity: 'success'
    });

    const handleCloseSnackbar = (_event?: React.SyntheticEvent, reason?: string) => {
        if (reason === 'clickaway') return;
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    const handleLoginSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        const username = formData.get('username') as string;
        const password = formData.get('password') as string;

        login(username, password)
            .then(() => {
                setSnackbar({
                    open: true,
                    message: "Logowanie przebiegło pomyślnie. Przekierowywanie...",
                    severity: "success"
                });
                setTimeout(() => {
                    navigate('/');
                }, 1500);
            })
            .catch(error => {
                setSnackbar({
                    open: true,
                    message: error.message,
                    severity: "error"
                });
            });
    };

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
            <Paper elevation={4} sx={{ padding: 2, mt: { xs : 0 ,md :10} , bgcolor: 'forms.main' }} >
                <Typography variant="h5" align="center" sx={{fontFamily: 'Montserrat'}}>Log in</Typography>
                <Box component="form" onSubmit={handleLoginSubmit} sx={{ mt: 1 }}>
                    <TextField name="username" label="Login" variant="outlined" sx={{fontFamily: 'Montserrat'}} required fullWidth />
                    <TextField name="password" label="Password" variant="outlined" type="password" sx={{mt: 2, fontFamily: 'Montserrat'}} required fullWidth/>
                    <FormControlLabel
                        sx={{ mt: 1, ml: 0 }}
                        control={<CheckBox value="remember" color="secondary" />}
                        label="Remember Me"
                    />
                    <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, fontFamily: 'Montserrat' }}>Sign in</Button>
                </Box>
                <Typography sx={{ mt: 2, textAlign: "center", fontFamily: 'Montserrat' }}>
                    You don't have an account?
                </Typography>
                <Typography sx={{ textAlign: "center", fontFamily: 'Montserrat' }}>
                    <Link component={RouterLink} to="/register">Sign Up</Link>
                </Typography>
            </Paper>
            <Snackbar
                open={snackbar.open}
                autoHideDuration={2000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                TransitionComponent={SlideTransition}
            >
                <Alert onClose={handleCloseSnackbar} variant="filled" severity={snackbar.severity} sx={{ width: '100%', mt: 8 }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
        </Box>
    );
};

export default Login;
