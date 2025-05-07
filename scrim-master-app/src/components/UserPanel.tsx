import React, { useState, useEffect } from 'react';
import {
    Box,
    Container,
    Paper,
    Avatar,
    Typography,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    CircularProgress,
    Button,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom';

// Typy danych
interface UserDTO {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
}

interface MatchDTO {
    id: number;
    team1: { teamName: string };
    team2: { teamName: string };
    team1Score: number;
    team2Score: number;
    date: string;
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    fontFamily: 'Montserrat',
    fontSize: 14,
}));

const UserPanel: React.FC = () => {
    const [user, setUser] = useState<UserDTO | null>(null);
    const [matches, setMatches] = useState<MatchDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        let mounted = true;
        const fetchData = async () => {
            try {
                setLoading(true);
                // Fetch current user
                const userRes = await fetch('http://localhost:8080/api/user/currentUser', {
                    credentials: 'include',
                });
                if (!userRes.ok) {
                    throw new Error('Brak autoryzacji');
                }
                const userData: UserDTO = await userRes.json();
                if (!mounted) return;
                setUser(userData);

                // Fetch matches for user
                const matchRes = await fetch('http://localhost:8080/api/match/me', {
                    credentials: 'include',
                });
                if (!matchRes.ok) {
                    // jeśli serwer zwraca 401 lub inny, traktujemy jako brak meczów
                    setMatches([]);
                } else {
                    const matchData = await matchRes.json();
                    if (!mounted) return;
                    setMatches(Array.isArray(matchData) ? matchData : []);
                }
            } catch (e: any) {
                console.error(e);
                if (mounted) {
                    setError(e.message || 'Coś poszło nie tak');
                    setUser(null);
                    setMatches([]);
                }
            } finally {
                if (mounted) setLoading(false);
            }
        };
        fetchData();
        return () => { mounted = false; };
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Typography variant="h6" align="center" sx={{ mt: 5, fontFamily: 'Montserrat' }}>
                {error}
            </Typography>
        );
    }

    if (!user) {
        return (
            <Typography variant="h6" align="center" sx={{ mt: 5, fontFamily: 'Montserrat' }}>
                Nie jesteś zalogowany.
            </Typography>
        );
    }

    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'flex-start',
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 },
                },
                background: `linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url('/img1.jpg')`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
            }}
        >
            <Container maxWidth="sm" sx={{ mt: 12 }}>
                <Paper elevation={4} sx={{ p: 3, mb: 4, textAlign: 'center' }}>
                    <Avatar
                        src={`https://i.pravatar.cc/150?u=${user.username}`}
                        alt={user.username}
                        sx={{ width: 80, height: 80, mx: 'auto', mb: 2 }}
                    />
                    <Typography variant="h5" sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}>
                        {user.username}
                    </Typography>
                    <Typography variant="body2" color="textSecondary" sx={{ fontFamily: 'Montserrat' }}>
                        KD: {user.kd} | ADR: {user.adr} | Ranking: {user.ranking}
                    </Typography>
                </Paper>

                <Paper elevation={2} sx={{ p: 2 }}>
                    <Typography variant="h6" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        Twoje mecze
                    </Typography>
                    {matches.length > 0 ? (
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell>Date</StyledTableCell>
                                    <StyledTableCell>Przeciwnik</StyledTableCell>
                                    <StyledTableCell>Wynik</StyledTableCell>
                                    <StyledTableCell align="right">Szczegóły</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {matches.map((m) => {
                                    const vs = m.team1.teamName === user.username
                                        ? m.team2.teamName
                                        : m.team1.teamName;
                                    const yourScore = m.team1.teamName === user.username ? m.team1Score : m.team2Score;
                                    const oppScore  = m.team1.teamName === user.username ? m.team2Score : m.team1Score;
                                    return (
                                        <TableRow key={m.id}>
                                            <StyledTableCell>
                                                {new Date(m.date).toLocaleDateString('pl-PL')}
                                            </StyledTableCell>
                                            <StyledTableCell>{vs}</StyledTableCell>
                                            <StyledTableCell>
                                                {yourScore} : {oppScore}
                                            </StyledTableCell>
                                            <StyledTableCell align="right">
                                                <Button size="small" onClick={() => navigate(`/match/${m.id}`)}>
                                                    Zobacz
                                                </Button>
                                            </StyledTableCell>
                                        </TableRow>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    ) : (
                        <Typography sx={{ fontFamily: 'Montserrat' }}>
                            Jeszcze nie rozegrałeś żadnego meczu.
                        </Typography>
                    )}
                </Paper>
            </Container>
        </Box>
    );
};

export default UserPanel;
