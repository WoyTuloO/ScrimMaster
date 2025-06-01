import React, { useEffect, useState } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../assets/AuthContext";
import {
    Box,
    Button,
    Container,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    SelectChangeEvent,
    TextField,
    Typography,
} from "@mui/material";

interface UserDTO {
    id: number;
    username: string;
}

interface TeamDTO {
    teamId: number;
    teamName: string;
    captain: UserDTO;
    players: UserDTO[];
    teamRanking: number;
}

export default function MatchCreate() {
    const { proposalId } = useParams<{ proposalId: string }>();
    const location = useLocation();
    const other = (location.state as any)?.other ?? "nieznany";
    const navigate = useNavigate();

    const { user, authFetch } = useAuth();
    const [teams, setTeams] = useState<TeamDTO[]>([]);
    const [selectedTeamId, setSelectedTeamId] = useState<number | "">("");
    const [yourScore, setYourScore] = useState<number>(0);
    const [opponentScore, setOpponentScore] = useState<number>(0);
    const [playerStats, setPlayerStats] = useState<Record<string, { kd: number; adr: number }>>({});
    const [selectedTeamName, setSelectedTeamName] = useState<string>("");

    useEffect(() => {
        if (!user) return;
        console.log(user.id);
        authFetch(`http://localhost:8080/api/team/${user.id}`)
            .then(res => {
                console.log(res);

                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                return res.json();
            })
            .then((data: TeamDTO[]) => setTeams(data))
            .catch(err => {
                console.error("Nie udało się pobrać drużyn kapitana:", err);
            });
    }, [user, authFetch]);

    useEffect(() => {
        if (!selectedTeamId) return;
        const team = teams.find(t => t.teamId === selectedTeamId)!;
        const statsInit: typeof playerStats = {};
        team.players.forEach(p => {
            statsInit[p.username] = { kd: 0, adr: 0 };
        });
        setPlayerStats(statsInit);
    }, [selectedTeamId, teams]);

    const handleTeamChange = (e: SelectChangeEvent<number>) => {
        setSelectedTeamId(e.target.value as number);
    };

    const handleStatChange = (username: string, field: "kd" | "adr", value: string) => {
        setPlayerStats(prev => ({
            ...prev,
            [username]: {
                ...prev[username],
                [field]: Number(value)
            }
        }));
    };

    const handleSubmit = async () => {
        if (!selectedTeamId) {
            alert("Wybierz najpierw swoją drużynę");
            return;
        }
        const team = teams.find((t) => t.teamId === selectedTeamId)!
        const payload = {
            chatRoomId: proposalId,
            teamName: team.teamName,
            createdBy: user.id,
            yourScore,
            opponentScore,
            stats: Object.entries(playerStats).map(([usern, s]) => ({
                username: usern,
                kd: s.kd,
                adr: s.adr
            }))
        };
        try {
            console.log(JSON.stringify(payload));
            const res = await authFetch(

                `http://localhost:8080/api/match/proposal/submit`,
                {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                }
            );
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            navigate("/");
        } catch (e: any) {
            console.error("Błąd przy wysyłaniu propozycji meczu:", e);
            alert("Nie udało się wysłać danych meczowych.");
        }
    };

    return (
        <Box
            sx={{
                height: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                background: `linear-gradient(rgba(0,0,0,0.5),rgba(0,0,0,0.5)),url(/../../public/img1.jpg)`,
                backgroundSize: "cover",
                backgroundPosition: "center",
            }}
        >
            <Container maxWidth="sm">
                <Paper elevation={4} sx={{ p: 3, background: "rgba(255,255,255,0.85)" }}>
                    <Typography variant="h5" align="center" sx={{ mb: 2 }}>
                        Propozycja meczu vs {other}
                    </Typography>

                    <FormControl fullWidth sx={{ mb: 2 }}>
                        <InputLabel>Twoja drużyna</InputLabel>
                        <Select<number>
                            value={selectedTeamId}
                            label="Twoja drużyna"
                            onChange={handleTeamChange}
                        >
                            {teams.map(t => (
                                <MenuItem key={t.teamId} value={t.teamId}>
                                    {t.teamName}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
                        <TextField
                            type="number"
                            label="Twój wynik"
                            value={yourScore}
                            onChange={e => setYourScore(Number(e.target.value))}
                            fullWidth
                        />
                        <TextField
                            type="number"
                            label="Wynik przeciwnika"
                            value={opponentScore}
                            onChange={e => setOpponentScore(Number(e.target.value))}
                            fullWidth
                        />
                    </Box>

                    {selectedTeamId && (
                        <Box sx={{ mb: 2 }}>
                            <Typography variant="h6">Statystyki zawodników</Typography>
                            {teams
                                .find(t => t.teamId === selectedTeamId)!
                                .players.map(p => (
                                    <Box key={p.username} sx={{ display: "flex", gap: 1, mb: 1 }}>
                                        <Typography sx={{ width: 120 }}>{p.username}</Typography>
                                        <TextField
                                            type="number"
                                            label="KD"
                                            value={playerStats[p.username]?.kd || 0}
                                            onChange={e => handleStatChange(p.username, "kd", e.target.value)}
                                            sx={{ flex: 1 }}
                                        />
                                        <TextField
                                            type="number"
                                            label="ADR"
                                            value={playerStats[p.username]?.adr || 0}
                                            onChange={e => handleStatChange(p.username, "adr", e.target.value)}
                                            sx={{ flex: 1 }}
                                        />
                                    </Box>
                                ))}
                        </Box>
                    )}

                    <Button
                        variant="contained"
                        fullWidth
                        onClick={handleSubmit}
                        disabled={!selectedTeamId}
                    >
                        Wyślij dane meczowe
                    </Button>
                </Paper>
            </Container>
        </Box>
    );
}
