import React, { useEffect, useState } from 'react';
import {
    Box, Container, Paper, Typography, Button, Table, TableHead, TableRow, TableCell, TableBody, CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useAuth } from "../assets/AuthContext";

interface TeamDTO {
    teamId: number;
    teamName: string;
    teamRanking: number;
    players: { id: number; username: string; }[];
    captain: { id: number; username: string; } | null;
}

interface InvitationDTO {
    id: number;
    teamName: string;
    captainName: string;
}

const MyTeamsPage: React.FC = () => {
    const [teams, setTeams] = useState<TeamDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [invitations, setInvitations] = useState<InvitationDTO[]>([]);
    const [loadingInv, setLoadingInv] = useState(true);

    const navigate = useNavigate();
    const { user } = useAuth();

    useEffect(() => {
        fetch("http://localhost:8080/api/team/me", { credentials: "include" })
            .then(res => {
                if (!res.ok) throw new Error('Błąd pobierania drużyn');
                return res.json();
            })
            .then((data: TeamDTO[]) => setTeams(data))
            .catch(e => setError(e.message))
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {

        fetch("http://localhost:8080/api/team/invitations/pending", { credentials: "include" })
            .then(res => {
                if (!res.ok) return [];

                return res.json();
            })
            .then((data) => {
                setInvitations(Array.isArray(data) ? data : []);

            })
            .catch(() => {setInvitations([]);
        })
            .finally(() => setLoadingInv(false));

    }, []);

    const handleAccept = (invId: number) => {
        fetch(`http://localhost:8080/api/team/invitations/${invId}/accept`, {
            method: "POST", credentials: "include"
        }).then(() => {
            setInvitations(prev => prev.filter(inv => inv.id !== invId));

            fetch("http://localhost:8080/api/team/me", { credentials: "include" })
                .then(res => {
                    if (!res.ok) throw new Error('Błąd pobierania drużyn');
                    return res.json();
                })
                .then((data: TeamDTO[]) => setTeams(data))
                .catch(e => setError(e.message))
                .finally(() => setLoading(false));
        });
    };

    const handleDecline = (invId: number) => {
        fetch(`http://localhost:8080/api/team/invitations/${invId}/decline`, {
            method: "POST", credentials: "include"
        }).then(() => {
            setInvitations(prev => prev.filter(inv => inv.id !== invId));

        });
    };

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}><CircularProgress /></Box>;
    }

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
        >
            <Container maxWidth="md">

                <Paper elevation={3} sx={{ p: 2, mb: 3 }}>
                    <Typography variant="h6" sx={{ mb: 2 }}>Team Invitations</Typography>
                    {
                        loadingInv ? (
                        <CircularProgress />
                    ) : Array.isArray(invitations) && invitations.length === 0 ? (
                        <Typography>There are no invitations.</Typography>
                    ) : (
                        Array.isArray(invitations) && invitations.map(inv => (
                            <Box key={inv.id} sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <Typography sx={{ flex: 1 }}>
                                    Invite from <strong>{inv.teamName}</strong> (Captain: {inv.captainName})
                                </Typography>
                                <Button onClick={() => handleAccept(inv.id)} size="small" color="primary" variant="contained" sx={{ mr: 1 }}>Accept</Button>
                                <Button onClick={() => handleDecline(inv.id)} size="small" color="error" variant="outlined">Decline</Button>
                            </Box>
                        ))
                    )}
                </Paper>

                <Paper elevation={4} sx={{ p: 4, mb: 4 }}>
                    <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
                        <Typography variant="h5" sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}>
                            My Teams
                        </Typography>
                        <Button
                            variant="contained"
                            color="secondary"
                            onClick={() => navigate('/teams/create')}
                        >
                            Create new team
                        </Button>
                    </Box>
                    {error && <Typography color="error">{error}</Typography>}
                    {teams.length === 0 ? (
                        <Typography>You are not part of any team yet.</Typography>
                    ) : (
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Team name</TableCell>
                                    <TableCell>Captain</TableCell>
                                    <TableCell>Players</TableCell>
                                    <TableCell>Ranking</TableCell>
                                    <TableCell align="right">Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {teams.map(team => (
                                    <TableRow key={team.teamId}>
                                        <TableCell>{team.teamName}</TableCell>
                                        <TableCell>{team.captain?.username || "-"}</TableCell>
                                        <TableCell>
                                            {team.players.map(p => p.username).join(', ')}
                                        </TableCell>
                                        <TableCell>{team.teamRanking}</TableCell>
                                        {user?.id === team.captain?.id ? (
                                            <TableCell align="right">
                                                <Button
                                                    size="small"
                                                    variant="outlined"
                                                    onClick={() => navigate(`/teams/edit/${team.teamId}`)}
                                                >
                                                    Edytuj
                                                </Button>
                                            </TableCell>
                                        ) : (
                                            <TableCell></TableCell>
                                        )}
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    )}
                </Paper>
            </Container>
        </Box>
    );
};

export default MyTeamsPage;
