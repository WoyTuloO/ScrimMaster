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
    Button,
    Typography
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { tableCellClasses } from '@mui/material/TableCell';
import { Link as RouterLink } from 'react-router-dom';
import { Link } from '@mui/material';


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
        fontFamily: 'Montserrat'
    },
}));

interface Player {
    id: number;
    username: string;
    kd: number;
    adr: number;
    ranking: number;
}

const PlayerRanking: React.FC = () => {
    const [players, setPlayers] = useState<Player[]>([]);
    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);

    const fetchPlayers = () => {
        fetch('http://localhost:8080/api/user')
            .then(res => res.json())
            .then((data: Player[]) => {
                setPlayers(data);
            })
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchPlayers();
    }, []);

    const handleRefresh = () => {
        fetchPlayers();
    };

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
            <Container>
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
                            Player Ranking
                        </Typography>
                        <Button
                            variant="contained"
                            color="secondary"
                            onClick={handleRefresh}
                            sx={{ fontFamily: 'Montserrat', fontWeight: 700 }}
                        >
                            Refresh
                        </Button>
                    </Box>
                    <TableContainer sx={{ overflow: 'auto' }}>
                        <Table stickyHeader aria-label="player ranking table">
                            <TableHead>
                                <TableRow>
                                    <StyledTableCell align="center">Username</StyledTableCell>
                                    <StyledTableCell align="center">KD</StyledTableCell>
                                    <StyledTableCell align="center">ADR</StyledTableCell>
                                    <StyledTableCell align="center">Ranking</StyledTableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {players
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((player) => (
                                        <TableRow hover role="checkbox" tabIndex={-1} key={player.id}>
                                            <TableCell align="center">
                                                <Link
                                                component={RouterLink}
                                                to={`/user/${player.id}`}
                                                sx={{ textDecoration: 'none', color: 'inherit', '&:hover': {
                                                        color: "#ff8000",
                                                        textDecoration: 'underline',
                                                    },
                                                    fontFamily: 'Montserrat' }}
                                                >
                                                {player.username}</Link>
                                            </TableCell>
                                            <TableCell sx={{ textDecoration: 'none', color: 'inherit', fontFamily: 'Montserrat' }} align="center">{player.kd}</TableCell>
                                            <TableCell sx={{ textDecoration: 'none', color: 'inherit', fontFamily: 'Montserrat' }} align="center">{player.adr}</TableCell>
                                            <TableCell sx={{ textDecoration: 'none', color: 'inherit', fontFamily: 'Montserrat' }} align="center">{player.ranking}</TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                        sx={{fontFamily: 'Montserrat' }}
                        rowsPerPageOptions={[10, 25, 100]}
                        component="div"
                        count={players.length}
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

export default PlayerRanking;
