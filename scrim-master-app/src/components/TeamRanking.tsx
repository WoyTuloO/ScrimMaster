import React, { useState, useEffect } from 'react';
import {
    Box,
    Container,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TablePagination,
    TableRow,
    Typography,
    Collapse,
    IconButton,
    Button,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { tableCellClasses } from '@mui/material/TableCell';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { Link as RouterLink } from 'react-router-dom';
import { Link } from '@mui/material';

interface UserDTO {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
}

interface TeamDTO {
    teamId: number;
    teamName: string;
    captain?: UserDTO | null;
    player2?: UserDTO | null;
    player3?: UserDTO | null;
    player4?: UserDTO | null;
    player5?: UserDTO | null;
    player6?: UserDTO | null;
    player7?: UserDTO | null;
    teamRanking: number;
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: "#ff8000",
        color: theme.palette.common.black,
        fontWeight: 'bold',
        fontFamily: 'Montserrat',
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 14,
        fontFamily: 'Montserrat',
    },
}));

interface RowTeamProps {
    team: TeamDTO;
}

const RowTeam: React.FC<RowTeamProps> = ({ team }) => {
    const [open, setOpen] = useState(false);

    const players = [
        { role: 'Captain', player: team.captain },
        { role: 'Player2', player: team.player2 },
        { role: 'Player3', player: team.player3 },
        { role: 'Player4', player: team.player4 },
        { role: 'Player5', player: team.player5 },
        { role: 'Player6', player: team.player6 },
        { role: 'Player7', player: team.player7 },
    ].filter(item => item.player != null);

    return (
        <React.Fragment>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row">
                    {team.teamId}
                </TableCell>
                <TableCell>{team.teamName}</TableCell>
                <TableCell align="center">{team.teamRanking}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={4}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            {players.length > 0 ? (
                                <>
                                    <Typography variant="h6" gutterBottom sx={{ fontFamily: 'Montserrat' }}>
                                        Players:
                                    </Typography>
                                    {players.map((item, index) => (
                                        <Box key={index} sx={{ mb: 1, pl: 2 }}>
                                            <Typography variant="body1" sx={{ fontFamily: 'Montserrat' }}>
                                                <strong>{item.role}:</strong>{' '}
                                                <Link
                                                    component={RouterLink}
                                                    to={`/user/${item.player?.id}`}
                                                    sx={{ textDecoration: 'none', color: 'inherit','&:hover': {
                                                            color: "#ff8000",
                                                            textDecoration: 'underline',
                                                        } }}
                                                >
                                                    {item.player?.username}
                                                </Link>{' '}
                                                (KD: {item.player?.kd}, ADR: {item.player?.adr}, Ranking: {item.player?.ranking})
                                            </Typography>
                                        </Box>
                                    ))}
                                </>
                            ) : (
                                <Typography variant="body2" sx={{ fontFamily: 'Montserrat' }}>
                                    No players available.
                                </Typography>
                            )}
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </React.Fragment>
    );
};

const TeamRanking: React.FC = () => {
    const [teams, setTeams] = useState<TeamDTO[]>([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);

    const fetchTeams = () => {
        fetch('http://localhost:8080/api/team')
            .then(res => res.json())
            .then((data: TeamDTO[]) => {
                setTeams(data);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchTeams();
    }, []);

    const handleChangePage = (event: unknown, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
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
                height: { xs: 'calc(100vh - 56px)', md: 'calc(100vh - 68.5px)' },
                background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(/../../public/img1.jpg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                display: 'flex',
                alignItems: 'center',
            }}
        >
            <Container sx={{ height: "60vh" }}>
                <Paper
                    elevation={4}
                    sx={{
                        padding: 2,
                        background: 'linear-gradient(rgba(255,255,255,0.75), rgba(255,255,255,0.75))',
                        display: 'grid',
                        gridTemplateRows: 'auto 1fr auto',
                        height: "60vh",
                    }}
                >
                    {/* Nagłówek z tytułem i przyciskiem Refresh */}
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                        <Typography variant="h5" sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}>
                            Team Ranking
                        </Typography>
                        <Button
                            variant="contained"
                            color="secondary"
                            onClick={fetchTeams}
                            sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}
                        >
                            Refresh
                        </Button>
                    </Box>
                    {/* Obszar tabeli – przewijalny */}
                    <TableContainer sx={{ overflow: 'auto' }}>
                        <Table stickyHeader aria-label="collapsible team table">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell />
                                    <StyledTableCell>Team ID</StyledTableCell>
                                    <StyledTableCell>Team Name</StyledTableCell>
                                    <StyledTableCell align="center">Ranking</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {teams
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((team) => (
                                        <RowTeam key={team.teamId} team={team} />
                                    ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                        sx={{ fontFamily: 'Montserrat' }}
                        rowsPerPageOptions={[10, 25, 100]}
                        component="div"
                        count={teams.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    />
                </Paper>
            </Container>
        </Box>
    );
};

export default TeamRanking;
