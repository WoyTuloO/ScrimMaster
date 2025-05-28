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


interface UserDTO {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
}


interface MatchProposalDTO {
    chatRoomId: string;
    yourScore: number;
    opponentScore: number;
    status: string;
    enemyCaptain: string;
}

interface MatchDTO {
    id: number;
    team1Name: string;
    team2Name: string;
    team1Score: number;
    team2Score: number;
    date: string;
}

interface ChatRoomDTO {
    userA : string;
    userB : string;
    id : string;
    status : string;
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    fontFamily: 'Montserrat',
    fontSize: 14,
}));

const Dashboard: React.FC = () => {
    const [user, setUser] = useState<UserDTO | null>(null);
    const [proposals, setProposals] = useState<MatchProposalDTO[]>([]);
    const [matches, setMatches] = useState<MatchDTO[]>([]);
    const [chatrooms, setChatrooms] = useState<ChatRoomDTO[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        let mounted = true;
        const fetchData = async () => {
            try {
                setLoading(true);
                const userRes = await fetch('http://localhost:8080/api/user/currentUser', {
                    credentials: 'include',
                });
                if (!userRes.ok) throw new Error('Brak autoryzacji');
                const userData: UserDTO = await userRes.json();
                if (!mounted) return;
                setUser(userData);

                const openChatsRes = await fetch(
                    `http://localhost:8080/api/chat/user/${userData.id}`,
                    { credentials: 'include' }
                );
                let openChats: ChatRoomDTO[] = [];
                if (openChatsRes.ok) {
                    openChats = await openChatsRes.json();
                }
                if (mounted) setChatrooms(openChats);


                const propRes = await fetch(
                    `http://localhost:8080/api/match/proposal/user/${userData.id}`,
                    { credentials: 'include' }
                );
                let propData: MatchProposalDTO[] = [];
                if (propRes.ok) {
                    propData = await propRes.json();
                }
                if (mounted) setProposals(propData);

                const matchRes = await fetch('http://localhost:8080/api/match/me', {
                    credentials: 'include',
                });
                let matchData: MatchDTO[] = [];
                if (matchRes.ok) {
                    matchData = await matchRes.json();
                }
                if (mounted) setMatches(matchData);
            } catch (e: any) {
                console.error(e);
                if (mounted) {
                    setError(e.message || 'Coś poszło nie tak');
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

    const pendingProposalIds = new Set(
        proposals
            .filter((p) => p.status === "Pending")
            .map((p) => p.chatRoomId)
    );

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

                <Paper elevation={2} sx={{ p: 2, mb: 4 }}>
                    <Typography variant="h6" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        Aktywne Mecze
                    </Typography>
                    {chatrooms.length > 0 ? (
                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell>Kapitan</StyledTableCell>
                                    <StyledTableCell>Status</StyledTableCell>
                                    <StyledTableCell align="right">Akcja</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>

                                {
                                    chatrooms.map((c) => {
                                    const captain = c.userA === user.username ? c.userB : c.userA;
                                    return (
                                        <TableRow key={c.id}>
                                            <StyledTableCell>{captain}</StyledTableCell>

                                            {c.status === "Open" ? (
                                                <StyledTableCell >{c.status}</StyledTableCell>) : (
                                                <StyledTableCell sx={{color: "#ff0000"}}>{c.status}</StyledTableCell>
                                            )}
                                            <StyledTableCell align="right">
                                                <Button
                                                    size="small"
                                                    onClick={() => navigate(`/chat/${c.id}`, { state: { captain }})}
                                                >
                                                    Przejdź
                                                </Button>
                                            </StyledTableCell>
                                        </TableRow>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    ) : (
                        <Typography sx={{ fontFamily: 'Montserrat' }}>
                            Jeszcze nie rozpocząłeś żadnego meczu.
                        </Typography>
                    )}
                </Paper>

                <Paper elevation={2} sx={{ p: 2, mb: 4 }}>
                    <Typography variant="h6" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        Oczekujące na zatwierdzenie
                    </Typography>
                    {proposals.length > 0 || pendingProposalIds.size > 0 ? (
                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell>Przeciwnik</StyledTableCell>
                                    <StyledTableCell>Wynik</StyledTableCell>
                                    <StyledTableCell align="right">Status</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {chatrooms.map((c) => {
                                    if (pendingProposalIds.has(c.id)) return null;

                                    const captain = c.userA === user.username ? c.userB : c.userA;
                                    if (c.status !== "Rejected") return null;

                                    return (
                                        <TableRow key={c.id}>
                                            <StyledTableCell>{captain}</StyledTableCell>
                                            <StyledTableCell>- : -</StyledTableCell>
                                            <StyledTableCell align="right">
                                                <Button
                                                    sx={{ color: "#ff0000", fontWeight: 500 }}
                                                    size="small"
                                                    onClick={() => navigate(`/match/create/${c.id}`)}
                                                >
                                                    Uzupełnij
                                                </Button>
                                            </StyledTableCell>
                                        </TableRow>
                                    );
                                })}
                                {proposals.map((p) => (
                                    <TableRow key={p.chatRoomId}>
                                        <StyledTableCell>
                                            {p.enemyCaptain}
                                        </StyledTableCell>
                                        <StyledTableCell>
                                            {p.yourScore} : {p.opponentScore}
                                        </StyledTableCell>
                                        {p.status === "Pending" ? (
                                        <StyledTableCell align="right" sx={{color:"#d800ff", fontWeight: 500}}>
                                            {p.status}
                                        </StyledTableCell>
                                            ) : (
                                            <StyledTableCell align="right">
                                                <Button
                                                    sx={{color:"#cf0101", fontWeight: 500}}
                                                    size="small"
                                                    onClick={() => navigate(`/match/create/${p.chatRoomId}`)}
                                                >
                                                    Uzupełnij
                                                </Button>
                                            </StyledTableCell>
                                        )}
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    ) : (
                        <Typography sx={{ fontFamily: 'Montserrat' }}>
                            Brak oczekujących.
                        </Typography>
                    )}
                </Paper>

                <Paper elevation={2} sx={{ p: 2, mb: 4 }}>
                    <Typography variant="h6" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        Twoje mecze
                    </Typography>
                    {matches.length > 0 ? (
                        <Table size="small">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell>Data</StyledTableCell>
                                    <StyledTableCell>Przeciwnik</StyledTableCell>
                                    <StyledTableCell>Wynik</StyledTableCell>
                                    <StyledTableCell align="right">Szczegóły</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {matches.map((m) => {
                                    const vs =
                                        m.team1Name === user.username
                                            ? m.team2Name
                                            : m.team1Name;
                                    const yourScore =
                                        m.team1Name === user.username
                                            ? m.team1Score
                                            : m.team2Score;
                                    const oppScore =
                                        m.team2Name === user.username
                                            ? m.team2Score
                                            : m.team1Score;
                                    return (
                                        <TableRow key={m.id}>
                                            <StyledTableCell>{m.date}</StyledTableCell>
                                            <StyledTableCell>{vs}</StyledTableCell>
                                            <StyledTableCell>
                                                {yourScore} : {oppScore}
                                            </StyledTableCell>
                                            <StyledTableCell align="right">
                                                <Button
                                                    size="small"
                                                    onClick={() => navigate(`/match/${m.id}`)}
                                                >
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

export default Dashboard;
