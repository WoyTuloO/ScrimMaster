import React, { useState } from 'react';
import {
    Box, Paper, Typography, Button, Dialog, DialogTitle,
    DialogContent, DialogActions, TextField, Snackbar, Alert
} from '@mui/material';
import { useAuth } from '../assets/AuthContext';

const AccountPage: React.FC = () => {
    const { user, logout, authFetch } = useAuth();

    const [openNickname, setOpenNickname] = useState(false);
    const [openPassword, setOpenPassword] = useState(false);
    const [openDelete, setOpenDelete] = useState(false);

    const [newUsername, setNewUsername] = useState('');
    const [nicknameLoading, setNicknameLoading] = useState(false);
    const [nicknameError, setNicknameError] = useState<string | null>(null);

    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [passwordLoading, setPasswordLoading] = useState(false);
    const [passwordError, setPasswordError] = useState<string | null>(null);

    const [snackbar, setSnackbar] = useState<{ open: boolean, message: string, severity: "success" | "error" }>({
        open: false, message: '', severity: "success"
    });

    const handleNicknameChange = async () => {
        setNicknameLoading(true);
        setNicknameError(null);
        try {
            const res = await authFetch('http://localhost:8080/api/user/nickname', {
                method: 'PUT',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ newUsername: newUsername })
            });
            if (res.ok) {
                setSnackbar({ open: true, message: "Zmieniono nazwę użytkownika!", severity: "success" });
                setOpenNickname(false);
            } else {
                const data = await res.json();
                setNicknameError(data.message || "Błąd przy zmianie nicku");
            }
        } catch (e) {
            setNicknameError("Błąd sieci");
        }
        setNicknameLoading(false);
    };

    const handlePasswordChange = async () => {
        setPasswordLoading(true);
        setPasswordError(null);
        try {
            const res = await authFetch('http://localhost:8080/api/user/password', {
                method: 'PUT',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ currentPassword, newPassword })
            });
            if (res.ok) {
                setSnackbar({ open: true, message: "Hasło zmienione!", severity: "success" });
                setOpenPassword(false);
            } else {
                const data = await res.json();
                setPasswordError(data.message || "Błąd przy zmianie hasła");
            }
        } catch (e) {
            setPasswordError("Błąd sieci");
        }
        setPasswordLoading(false);
    };

    const handleDeleteAccount = async () => {
        try {
            const res = await authFetch('http://localhost:8080/api/user', {
                credentials: 'include',
                method: 'DELETE'
            });
            if (res.ok) {
                setSnackbar({ open: true, message: "Usunięto konto. Żegnaj!", severity: "success" });
                setOpenDelete(false);
                setTimeout(() => logout(), 1500);
            } else {
                const data = await res.json();
                setSnackbar({ open: true, message: data.message || "Błąd przy usuwaniu konta", severity: "error" });
            }
        } catch {
            setSnackbar({ open: true, message: "Błąd sieci przy usuwaniu konta", severity: "error" });
        }
    };

    return (
        <Box
            sx={{
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 },
                },
                height: '100vh',
                background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(/../../public/img1.jpg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                display: 'flex',
                alignItems: 'center',
            }}
        >            <Paper elevation={4} sx={{ maxWidth: 400, mx: "auto", p: 4 }}>
                <Typography variant="h5" sx={{ mb: 2, fontWeight: 700 }}>
                    Account Management
                </Typography>
                <Typography sx={{ mb: 2 }}>
                    Nickname: <strong>{user?.username}</strong><br />
                    Email: <strong>{user?.email}</strong>
                </Typography>

                <Button variant="contained" sx={{ mb: 2, width: '100%' }}
                        onClick={() => setOpenNickname(true)}>
                    Change Username
                </Button>
                <Button variant="contained" color="secondary" sx={{ mb: 2, width: '100%' }}
                        onClick={() => setOpenPassword(true)}>
                    Change Password
                </Button>
                <Button variant="outlined" color="error" sx={{ width: '100%' }}
                        onClick={() => setOpenDelete(true)}>
                    Delete Account
                </Button>
            </Paper>

            <Dialog open={openNickname} onClose={() => setOpenNickname(false)}>
                <DialogTitle>Change Username</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Nowa nazwa użytkownika"
                        fullWidth
                        value={newUsername}
                        onChange={e => setNewUsername(e.target.value)}
                    />
                    {nicknameError && <Alert severity="error" sx={{ mt: 2 }}>{nicknameError}</Alert>}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenNickname(false)}>Cancel</Button>
                    <Button onClick={handleNicknameChange} disabled={nicknameLoading}>
                        Change
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={openPassword} onClose={() => setOpenPassword(false)}>
                <DialogTitle>Change Password</DialogTitle>
                <DialogContent>
                    <TextField
                        margin="dense"
                        label="Aktualne hasło"
                        type="password"
                        fullWidth
                        value={currentPassword}
                        onChange={e => setCurrentPassword(e.target.value)}
                    />
                    <TextField
                        margin="dense"
                        label="Nowe hasło"
                        type="password"
                        fullWidth
                        value={newPassword}
                        onChange={e => setNewPassword(e.target.value)}
                    />
                    {passwordError && <Alert severity="error" sx={{ mt: 2 }}>{passwordError}</Alert>}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenPassword(false)}>Cancel</Button>
                    <Button onClick={handlePasswordChange} disabled={passwordLoading}>
                        Change
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={openDelete} onClose={() => setOpenDelete(false)}>
                <DialogTitle>Confirm account deletion</DialogTitle>
                <DialogContent>
                    <Typography color="error">Are You sure? This cannot be undone.</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDelete(false)}>Cancel</Button>
                    <Button onClick={handleDeleteAccount} color="error">
                        Change
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={() => setSnackbar(s => ({ ...s, open: false }))}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert onClose={() => setSnackbar(s => ({ ...s, open: false }))}
                       severity={snackbar.severity} variant="filled" sx={{ width: '100%' }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default AccountPage;
