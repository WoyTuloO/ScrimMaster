import React, { useContext, useState, useEffect } from 'react';
import {
    Box, Container, Paper, Typography,
    Table, TableHead, TableRow, TableCell, TableBody,
    CircularProgress, Button
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { AuthContext } from '../assets/AuthContext'; // adjust path as needed

// Dopasowane do backendowego UserDTO
interface UserDTO {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
    role: string;
}

const StyledTableCell = styled(TableCell)({
    fontFamily: 'Montserrat',
    fontWeight: 600,
});

const AdminPanel: React.FC = () => {
    const { authFetch } = useContext(AuthContext);
    const [users, setUsers] = useState<UserDTO[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                const res = await authFetch('http://localhost:8080/api/admin/users');
                if (!res.ok) throw new Error('Brak dostępu');
                const data: UserDTO[] = await res.json();
                setUsers(data);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        })();
    }, [authFetch]);

    if (loading) {
        return <Box sx={{ mt: 10, textAlign: 'center' }}><CircularProgress/></Box>;
    }

    return (
        <Box sx={{ py: 4, background: '#f5f5f5', minHeight: '100vh' }}>
            <Container>
                <Typography variant="h4" sx={{ mb: 2, fontFamily: 'Montserrat' }}>
                    Panel Administratora
                </Typography>
                <Paper elevation={2}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <StyledTableCell>ID</StyledTableCell>
                                <StyledTableCell>Username</StyledTableCell>
                                <StyledTableCell>Email</StyledTableCell>
                                <StyledTableCell>KD</StyledTableCell>
                                <StyledTableCell>ADR</StyledTableCell>
                                <StyledTableCell>Ranking</StyledTableCell>
                                <StyledTableCell>Role</StyledTableCell>
                                <StyledTableCell align="right">Akcje</StyledTableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.map(u => (
                                <TableRow key={u.id}>
                                    <TableCell>{u.id}</TableCell>
                                    <TableCell>{u.username}</TableCell>
                                    <TableCell>{u.email}</TableCell>
                                    <TableCell>{u.kd}</TableCell>
                                    <TableCell>{u.adr}</TableCell>
                                    <TableCell>{u.ranking}</TableCell>
                                    <TableCell>{u.role}</TableCell>
                                    <TableCell align="right">
                                        <Button
                                            size="small"
                                            color="error"
                                            onClick={async () => {
                                                const resp = await authFetch(`http://localhost:8080/api/admin/users/${u.id}`, { method: 'DELETE' });
                                                if (resp.ok) {
                                                    setUsers(prev => prev.filter(x => x.id !== u.id));
                                                }
                                            }}
                                        >
                                            Usuń
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Paper>
            </Container>
        </Box>
    );
};

export default AdminPanel;
