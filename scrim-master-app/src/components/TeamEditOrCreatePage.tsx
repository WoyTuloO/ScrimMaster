import React, { useEffect, useState } from 'react';
import {
    Box, Container, Paper, Typography, Button, TextField, Autocomplete, CircularProgress
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
    const navigate = useNavigate();
    const { user } = useAuth();
    const isEdit = !!teamId;

    useEffect(() => {
        fetch("http://localhost:8080/api/user", { credentials: "include" })
            .then(res => res.json())
            .then((data: UserDTO[]) => setAllUsers(data));
    }, []);

    useEffect(() => {
        if (isEdit) {
            fetch(`http://localhost:8080/api/team/${teamId}`, { credentials: "include" })
                .then(res => res.json())
                .then((data: TeamDTO) => setTeam(data))
                .finally(() => setLoading(false));
        } else {
            setLoading(false);
        }
    }, [isEdit, teamId]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const payload = {
            teamName: team.teamName,
            captainId: user?.id,
            playerIds: team.players.map(p => p.id),
        };

        const url = isEdit
            ? `http://localhost:8080/api/team/${teamId}`
            : `http://localhost:8080/api/team`;
        const method = isEdit ? "PUT" : "POST";
        const res = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(payload)
        });
        if (res.ok) {
            navigate("/myteams");
        } else {
            alert("Błąd przy zapisie drużyny");
        }
    };

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 10 }}><CircularProgress /></Box>;
    }

    return (
        <Box sx={{ minHeight: '100vh', background: '#f5f5f5', py: 6 }}>
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
        </Box>
    );
};

export default TeamEditOrCreatePage;
