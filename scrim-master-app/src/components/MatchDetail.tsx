import React, { useState, useEffect } from 'react';
import {
    Box,
    Container,
    Paper,
    Typography,
    IconButton,
    Collapse,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    CircularProgress,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { tableCellClasses } from '@mui/material/TableCell';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { useParams } from 'react-router-dom';

interface PlayerStatsDto {
    id: number;
    username: string;
    kd: number;
    adr: number;
}

interface MatchDTO {
    id: number;
    team1Name: string;
    team1Score: number;
    team2Name: string;
    team2Score: number;
    team1Stats: PlayerStatsDto[];
    team2Stats: PlayerStatsDto[];
    date: string;
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: '#ff8000',
        color: theme.palette.common.black,
        fontSize: 16,
        fontWeight: 'bold',
        fontFamily: 'Montserrat',
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14,
        fontFamily: 'Montserrat',
    },
}));

interface TeamStatsProps {
    teamName: string;
    stats: PlayerStatsDto[];
}
const TeamStats: React.FC<TeamStatsProps> = ({ teamName, stats }) => {
    const [open, setOpen] = useState(false);

    return (
        <React.Fragment>
            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    px: 1,
                    py: 0.5,
                    cursor: 'pointer',
                    backgroundColor: '#fafafa',
                    borderBottom: '1px solid #ddd',
                }}
                onClick={() => setOpen(o => !o)}
            >
                <IconButton size="small">
                    {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                </IconButton>
                <Typography variant="h6" sx={{ flexGrow: 1, fontFamily: 'Montserrat', fontWeight: 700 }}>
                    {teamName}
                </Typography>
            </Box>

                {stats.length > 0 ? (
                    <Table size="small" aria-label={`${teamName} stats`} sx={{ mb: 2 }}>
                        <TableHead>
                            <TableRow>
                                <StyledTableCell align="center">Player</StyledTableCell>
                                <StyledTableCell align="center">KD</StyledTableCell>
                                <StyledTableCell align="center">ADR</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {stats.map((p) => (
                                <TableRow key={p.id}>
                                    <TableCell align="center" component="th" scope="row" sx={{ fontFamily: 'Montserrat' }}>
                                        {p.username}
                                    </TableCell>
                                    <TableCell  align="center" sx={{ fontFamily: 'Montserrat' }}>
                                        {p.kd.toFixed(2)}
                                    </TableCell>
                                    <TableCell align="center" sx={{ fontFamily: 'Montserrat' }}>
                                        {p.adr.toFixed(2)}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                ) : (
                    <Typography sx={{ px: 2, py: 1, fontFamily: 'Montserrat' }}>
                        Brak statystyk.
                    </Typography>
                )}
        </React.Fragment>
    );
};

const MatchDetail: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [match, setMatch] = useState<MatchDTO | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!id) return;
        const fetchMatch = async () => {
            try {
                setLoading(true);
                const res = await fetch(`http://localhost:8080/api/match/${id}`, {
                    credentials: 'include',
                });
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                const data: MatchDTO = await res.json();
                setMatch(data);
            } catch (e: any) {
                setError(e.message || 'Nie udało się załadować meczu');
            } finally {
                setLoading(false);
            }
        };
        fetchMatch();
    }, [id]);

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
    if (!match) {
        return (
            <Typography variant="h6" align="center" sx={{ mt: 5, fontFamily: 'Montserrat' }}>
                Nie znaleziono meczu.
            </Typography>
        );
    }

    return (
        <Box
            sx={{
                minHeight: '100vh',
                opacity: 0,
                animation: 'fadeIn 1s forwards',
                '@keyframes fadeIn': {
                    from: { opacity: 0 },
                    to: { opacity: 1 },
                },
                background: `linear-gradient(rgba(0,0,0,0.5),rgba(0,0,0,0.5)),url('/img1.jpg')`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                py: 4,
            }}
        >
            <Container maxWidth="md">
                <Paper elevation={4} sx={{ mt:14, p: 2, backgroundColor: 'rgba(255,255,255,0.9)' }}>
                    <Typography variant="h4" align="center" sx={{ mb: 2, fontFamily: 'Montserrat', fontWeight: 700 }}>
                        {match.team1Name} - {match.team1Score} vs {match.team2Score} - {match.team2Name}
                    </Typography>

                    <TeamStats
                        teamName={match.team1Name}
                        stats={match.team1Stats}
                    />

                    <TeamStats
                        teamName={match.team2Name}
                        stats={match.team2Stats}
                    />
                </Paper>
            </Container>
        </Box>
    );
};

export default MatchDetail;
