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
    deleted?: boolean;
}

interface TeamDTO {
    teamId: number;
    teamName: string;
    captain?: UserDTO | null;
    players: UserDTO[];
    teamRanking: number;
}

const StyledTableCell = styled(TableCell)(({ theme }) => ({
    [`&.${tableCellClasses.head}`]: {
        backgroundColor: "#ff8000",
        color: theme.palette.common.black,
        fontSize: 18,
        fontWeight: 'bold',
        fontFamily: 'Montserrat',
    },
    [`&.${tableCellClasses.body}`]: {
        fontSize: 16,
        fontFamily: 'Montserrat',
    },
}));

interface RowTeamProps {
    team: TeamDTO;
}

const RowTeam: React.FC<RowTeamProps> = ({ team }) => {
    const [open, setOpen] = useState(false);

    const players =
        team.captain && team.players.every(p => p.id !== team.captain?.id)
            ? [team.captain, ...team.players]
            : team.players;

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
                <TableCell component="th" scope="row" align="center">
                    {team.teamId}
                </TableCell>
                <TableCell align="center">{team.teamName}</TableCell>
                <TableCell align="center">{team.teamRanking}</TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={4}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            {players && players.length > 0 ? (
                                <>
                                    <Typography variant="h6" gutterBottom sx={{ fontFamily: 'Montserrat' }}>
                                        Players:
                                    </Typography>
                                    {players.map((player, index) => (
                                        player.username === "User Deleted" ? (
                                            <></>
                                            ) : (
                                                <Box key={player.id || index} sx={{ mb: 1, pl: 2 }}>
                                                    <Typography variant="body1" sx={{ fontFamily: 'Montserrat' }}>
                                                        <Link
                                                            component={RouterLink}
                                                            to={`/user/${player.id}`}
                                                            sx={{
                                                                textDecoration: 'none',
                                                                color: 'inherit',
                                                                '&:hover': {
                                                                    color: "#ff8000",
                                                                    textDecoration: 'underline',
                                                                }
                                                            }}
                                                        >
                                                            {player.deleted ? "User Deleted" : player.username}
                                                        </Link>{' '}
                                                        (KD: {player.kd}, ADR: {player.adr}, Ranking: {player.ranking})
                                                    </Typography>
                                                </Box>)

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
            .then((data: TeamDTO[]) => setTeams(data))
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
                height: '100vh',
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
                        height: "60vh"
                    }}
                >
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
                    <TableContainer sx={{ overflow: 'auto' }}>
                        <Table stickyHeader aria-label="collapsible team table">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell />
                                    <StyledTableCell align="center">Team ID</StyledTableCell>
                                    <StyledTableCell align="center">Team Name</StyledTableCell>
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
