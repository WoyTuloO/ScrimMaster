import * as React from 'react';
import Container from "@mui/material/Container";
import {FormControlLabel, Link, Paper, TextField} from "@mui/material";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
// @ts-ignore
import {CheckBox} from "@mui/icons-material";
import {Link as RouterLink} from "react-router-dom";



function Login() {

    const handleRegisterSubmit = (e: React.FormEvent<HTMLFormElement>) => { console.log("nigga"); };


    return (
        <Container maxWidth="xs">
            <Paper elevation={4} sx={{ padding: 2, mt: 10 }}>
                <Typography variant={'h5'} sx={{ textAlign: "center"}}>Register</Typography>
                <Box component="form" onSubmit={handleRegisterSubmit} sx={{ mt: 1}} >
                    <TextField variant={"outlined"} label="Username" required autoFocus fullWidth></TextField>
                    <TextField variant={"outlined"} label="Password" type="password" required fullWidth sx={{mt: 2}}></TextField>
                    <TextField variant={"outlined"} label="Repeat password" type="password" required fullWidth sx={{mt: 2}}></TextField>
                    <TextField variant={"outlined"} label="Email" type="email" required fullWidth sx={{mt: 2}}></TextField>
                    <Button type="submit" fullWidth variant="contained" sx={{mt: 3, alignItems: 'center'}}>Sign in</Button>
                </Box>
                <Typography sx={{ mt: 2, textAlign: "center" }}>You already have an account?</Typography>
                <Typography sx={{ textAlign: "center" }}><Link component={RouterLink} to={"/login"}>Sign In</Link></Typography>

            </Paper>
        </Container>
    );
}

export default Login;


