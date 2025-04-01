import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Container, Paper, Typography, Box, CircularProgress } from '@mui/material';
import PersonIcon from '@mui/icons-material/Person';

interface User {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
    persmissionLevel: number;
}

const UserProfile: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>('');

    useEffect(() => {
        fetch(`http://localhost:8080/api/user?id=${id}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error('Błąd pobierania danych użytkownika');
                }
                return res.json();
            })
            .then((data: User[]) => {
                if (data && data.length > 0) {
                    setUser(data[0]);
                } else {
                    setError('Nie znaleziono użytkownika');
                }
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, [id]);

    if (loading) {
        return (
            <Container sx={{ textAlign: 'center', mt: 4 }}>
                <CircularProgress />
            </Container>
        );
    }

    if (error) {
        return (
            <Container sx={{ textAlign: 'center', mt: 4 }}>
                <Typography variant="h6" sx={{ fontFamily: 'Montserrat' }}>{error}</Typography>
            </Container>
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
                background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)),url(/../../public/img1.jpg)`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                display: 'flex',
                alignItems: 'center'
            }}
        >
            <Container maxWidth="xs" sx={{ mt: 4 }}>
                <Paper elevation={4} sx={{ p: 4, textAlign: 'center' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                        <PersonIcon sx={{ fontSize: 80, color: 'primary.main' }} />
                    </Box>
                    <Typography variant="h4" sx={{ fontFamily: 'Montserrat', fontWeight: 700, mb: 2 }}>
                        {user?.username}'s Profile
                    </Typography>
                    <Box sx={{ textAlign: 'left', mt: 2 }}>
                        <Typography variant="body1" sx={{ fontFamily: 'Montserrat', mb: 1 }}>
                            <strong>Email:</strong> {user?.email}
                        </Typography>
                        <Typography variant="body1" sx={{ fontFamily: 'Montserrat', mb: 1 }}>
                            <strong>KD:</strong> {user?.kd}
                        </Typography>
                        <Typography variant="body1" sx={{ fontFamily: 'Montserrat', mb: 1 }}>
                            <strong>ADR:</strong> {user?.adr}
                        </Typography>
                        <Typography variant="body1" sx={{ fontFamily: 'Montserrat', mb: 1 }}>
                            <strong>Ranking:</strong> {user?.ranking}
                        </Typography>
                    </Box>
                </Paper>
            </Container>
        </Box>
    );
};

export default UserProfile;
