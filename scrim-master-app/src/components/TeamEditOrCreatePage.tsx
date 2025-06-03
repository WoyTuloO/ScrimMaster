import React, { useEffect, useState } from 'react';
import {
    Box,
    Container,
    Paper,
    Typography,
    Button,
    TextField,
    Autocomplete,
    CircularProgress,
    Snackbar,
    Alert
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from "../assets/AuthContext";

interface UserDTO {
    id: number;
    username: string;
}

interface TeamDTO {
    teamId?: number;
    teamName: string;
    players: UserDTO[];
    captain?: UserDTO;
    teamRanking?: number;
}

const TeamEditOrCreatePage: React.FC = () => {
    const { teamId } = useParams<{ teamId?: string }>();
    const [allUsers, setAllUsers] = useState<UserDTO[]>([]);
    const [team, setTeam] = useState<TeamDTO>({ teamName: '', players: [] });
    const [loading, setLoading] = useState(true);
    const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: "error" | "success" }>({
        open: false,
        message: "",
        severity: "error"
    });

    const navigate = useNavigate();
    const { user } = useAuth();
    const isEdit = !!teamId;

    useEffect(() => {
        setLoading(true);
        fetch("http://localhost:8080/api/user", { credentials: "include" })
            .then(res => res.json())
            .then((users: UserDTO[]) => {
                setAllUsers(users);
                if (isEdit) {
                    fetch(`http://localhost:8080/api/team?teamId=${teamId}`, { credentials: "include" })
                        .then(res => res.json())
                        .then((data: TeamDTO) => {
                            const playerList = data.players.map(
                                p => users.find(u => u.id === p.id) || p
                            );
                            setTeam({
                                teamName: data.teamName || '',
                                players: playerList,
                                teamId: data.teamId,
                                captain: data.captain,
                                teamRanking: data.teamRanking
                            });
                            setLoading(false);
                        });
                } else {
                    setTeam({ teamName: '', players: [] });
                    setLoading(false);
                }
            });
    }, [isEdit, teamId]);

    const handleSnackbarClose = (_event?: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === "clickaway") return;
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const payload = {
            teamName: team.teamName,
            captainId: user?.id,
            playerIds: team.players.map(p => p.id),
        };

        const url = isEdit
            ? `http://localhost:8080/api/team`
            : `http://localhost:8080/api/team`;
        const method = isEdit ? "PUT" : "POST";
        const res = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(payload)
        });

        if (res.status === 409) {
            setSnackbar({
                open: true,
                message: "Nazwa drużyny jest już zajęta.",
                severity: "error"
            });
        } else if (res.ok) {
            navigate("/myteams");
        } else {
            setSnackbar({
                open: true,
                message: "Wystąpił błąd przy zapisie drużyny.",
                severity: "error"
            });
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
                <CircularProgress />
            </Box>
        );
    }

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
                background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(/../../public/img1.jpg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                display: 'flex',
                alignItems: 'center'
            }}
        >
            <Container maxWidth="sm">
                <Paper elevation={4} sx={{ p: 4 }}>
                    <Typography variant="h5" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        {isEdit ? "Edytuj drużynę" : "Stwórz nową drużynę"}
                    </Typography>
                    <form onSubmit={handleSubmit}>
                        <TextField
                            label="Nazwa drużyny"
                            value={team.teamName}
                            required
                            fullWidth
                            sx={{ mb: 3 }}
                            onChange={e => setTeam(t => ({ ...t, teamName: e.target.value }))}
                        />
                        <Autocomplete
                            multiple
                            options={allUsers}
                            getOptionLabel={option => option.username}
                            value={team.players}
                            onChange={(_e, value) =>
                                setTeam(t => ({
                                    ...t,
                                    players: value.slice(0, 7)
                                }))
                            }
                            limitTags={7}
                            renderInput={(params) => (
                                <TextField {...params} label="Zawodnicy" placeholder="Wybierz zawodników" />
                            )}
                            sx={{ mb: 3 }}
                        />
                        <Button type="submit" variant="contained" color="primary" fullWidth>
                            {isEdit ? "Zapisz zmiany" : "Stwórz drużynę"}
                        </Button>
                    </form>
                </Paper>
            </Container>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert
                    onClose={handleSnackbarClose}
                    severity={snackbar.severity}
                    variant="filled"
                    sx={{ width: '100%', mt:8 }}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default TeamEditOrCreatePage;
