// src/components/PrivateChat.tsx
import React, { useEffect, useRef, useState } from "react";
import { Client, IMessage } from "@stomp/stompjs";
import { connectStomp } from "./wsClient";
import {
    Box,
    Button,
    Container,
    List,
    ListItem,
    Paper,
    TextField,
    Typography,
    CircularProgress,
} from "@mui/material";
import { useLocation, useParams } from "react-router-dom";
import { useAuth } from "../assets/AuthContext";

interface ChatMessage {
    type: "CHAT";
    content: string;
    sender: string;
}

export default function PrivateChat() {
    const { username } = useAuth();
    const { recipient } = useParams<{ recipient: string }>();
    const location = useLocation();
    const other = (location.state as any)?.other ?? "nieznany";

    if (!recipient) {
        return (
            <Box sx={{ p: 4, textAlign: "center" }}>
                <Typography variant="h6">Brak ID prywatnego czatu.</Typography>
            </Box>
        );
    }

    const clientRef = useRef<Client | null>(null);
    const [msgs, setMsgs] = useState<ChatMessage[]>([]);
    const [txt, setTxt] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;
        async function loadHistory() {
            try {
                const res = await fetch(
                    `http://localhost:8080/api/chat/private/${recipient}?limit=100`,
                    { credentials: "include" }
                );
                if (!res.ok) throw new Error(`HTTP ${res.status}`);
                const data: ChatMessage[] = await res.json();
                if (!cancelled) {
                    setMsgs(data);
                    setError(null);
                }
            } catch (e) {
                if (!cancelled) {
                    setError("Nie udało się załadować historii");
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        }
        loadHistory();
        return () => {
            cancelled = true;
        };
    }, [recipient]);

    useEffect(() => {
        const client = connectStomp(() => {
            client.subscribe(
                `/user/queue/private.${recipient}`,
                (m: IMessage) => {
                    const msg = JSON.parse(m.body) as ChatMessage;
                    setMsgs((prev) => [...prev, msg]);
                }
            );
        });
        client.onStompError = () => {
            setError("Błąd połączenia STOMP");
        };
        clientRef.current = client;
        return () => void client.deactivate();
    }, [recipient]);

    const send = () => {
        if (!txt.trim()) return;
        clientRef.current?.publish({
            destination: `/app/chat.private.${recipient}`,
            body: JSON.stringify({ type: "CHAT", content: txt, sender: username }),
        });
        setTxt("");
    };

    return (
        <Box sx={{ height: "100vh", display: "flex", alignItems: "center", justifyContent: "center" , background: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(/../../public/img1.jpg)`}}>

        <Container sx={{ height: "70vh" }}>
                <Paper
                    elevation={4}
                    sx={{
                        p: 2,
                        background: "rgba(255,255,255,0.75)",
                        display: "grid",
                        gridTemplateRows: "auto 1fr auto",
                        height: "100%",
                        gap: 2,
                    }}
                >
                    <Typography variant="h5" align="center" sx={{ fontWeight: 700 }}>
                        Prywatny czat z {other}
                    </Typography>

                    {loading ? (
                        <Box
                            sx={{
                                flex: 1,
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                            }}
                        >
                            <CircularProgress />
                        </Box>
                    ) : error ? (
                        <Typography color="error" align="center">
                            {error}
                        </Typography>
                    ) : (
                        <List sx={{ overflowY: "auto" }}>
                            {msgs.map((m, i) => (
                                <ListItem key={i} sx={{ p: 0.5 }}>
                                    <strong>{m.sender}:</strong> {m.content}
                                </ListItem>
                            ))}
                        </List>
                    )}

                    <Box sx={{ display: "flex", gap: 1}}>
                        <TextField
                            fullWidth
                            value={txt}
                            onChange={(e) => setTxt(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && send()}
                            placeholder="Napisz wiadomość…"
                        />
                        <Button variant="contained" onClick={send}>Wyślij</Button>
                    </Box>

                </Paper>
            </Container>
        </Box>
    );
}
