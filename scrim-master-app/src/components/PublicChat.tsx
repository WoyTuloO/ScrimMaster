import React, { useEffect, useRef, useState } from "react";
import { Client, IMessage } from "@stomp/stompjs";
import { connectStomp } from "./wsClient";
import {
    Box,
    Button,
    Container,
    Dialog,
    DialogActions,
    DialogTitle,
    List,
    ListItem,
    Paper,
    TextField,
    Typography,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../assets/AuthContext";
import { v4 as uuidv4 } from "uuid";

interface ChatMessage {
    type: "CHAT";
    content: string;
    sender: string;
}

interface ChatRequest {
    from: string;
    to: string;
    correlationId: string;
}

export default function PublicChat() {
    const { username } = useAuth();
    const clientRef = useRef<Client | null>(null);
    const [msgs, setMsgs] = useState<ChatMessage[]>([]);
    const [txt, setTxt] = useState("");
    const [inviteTo, setInviteTo] = useState<string | null>(null);
    const [incoming, setIncoming] = useState<ChatRequest | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetch("http://localhost:8080/api/chat/public?limit=100", {
            credentials: "include",
        })
            .then((r) => r.json())
            .then((history: ChatMessage[]) => setMsgs(history))
            .catch(console.error);
    }, []);

    useEffect(() => {
        const client = connectStomp(() => {
            client.subscribe("/topic/public", (m: IMessage) =>
                setMsgs((prev) => [...prev, JSON.parse(m.body)])
            );
            client.subscribe("/user/queue/chat-requests", (m: IMessage) =>
                setIncoming(JSON.parse(m.body))
            );
            client.subscribe("/user/queue/chat-accepted", (m: IMessage) => {
                const rq: ChatRequest = JSON.parse(m.body);
                const other = rq.from === username ? rq.to : rq.from;
                navigate(`/chat/${rq.correlationId}`, { state: { other } });
            });
            client.subscribe("/user/queue/chat-rejected", (m: IMessage) => {
                const rq: ChatRequest = JSON.parse(m.body);
                alert(`Zaproszenie do ${rq.to} odrzucone`);
            });
        });
        clientRef.current = client;
        return () => void client.deactivate();
    }, [navigate, username]);

    const send = () => {
        if (!txt.trim()) return;
        clientRef.current?.publish({
            destination: "/app/chat.public",
            body: JSON.stringify({ type: "CHAT", content: txt }),
        });
        setTxt("");
    };

    const invite = (to: string) => setInviteTo(to);
    const doInvite = () => {
        if (!inviteTo) return;
        const rq: ChatRequest = { from: username, to: inviteTo, correlationId: uuidv4() };
        clientRef.current?.publish({
            destination: "/app/chat.request",
            body: JSON.stringify(rq),
        });
        setInviteTo(null);
        alert(`Zaproszenie wysłane do ${rq.to}`);
    };

    const accept = () => {
        if (!incoming) return;
        clientRef.current?.publish({
            destination: "/app/chat.accept",
            body: JSON.stringify(incoming),
        });
        setIncoming(null);
    };
    const reject = () => {
        if (!incoming) return;
        clientRef.current?.publish({
            destination: "/app/chat.reject",
            body: JSON.stringify(incoming),
        });
        setIncoming(null);
    };

    return (
        <Box
            sx={{
                opacity: 0,
                animation: "fadeIn 1s forwards",
                "@keyframes fadeIn": { from: { opacity: 0 }, to: { opacity: 1 } },
                height: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                background: `linear-gradient(rgba(0,0,0,0.5),rgba(0,0,0,0.5)),url(/../../public/img1.jpg)`,
                backgroundSize: "cover",
                backgroundPosition: "center",
            }}
        >    <Container sx={{ height: "70vh" }}>
                <Paper elevation={4} sx={{background: "rgba(255,255,255,0.75)", p: 2, display: "grid", gridTemplateRows: "auto 1fr auto", height: "100%", gap: 2 }}>
                    <Typography variant="h5">Public Chat</Typography>

                    <List sx={{ overflowY: "auto" }}>
                        {msgs.map((m, i) => (
                            <ListItem key={i} onClick={() => invite(m.sender)} sx={{ cursor: "pointer" }}>
                                <strong>{m.sender}:</strong> {m.content}
                            </ListItem>
                        ))}
                    </List>

                    <Box sx={{ display: "flex", gap: 1 , height: 1}}>
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

            <Dialog open={!!inviteTo} onClose={() => setInviteTo(null)}>
                <DialogTitle>Zaproszenie do prywatnego czatu</DialogTitle>
                <DialogActions>
                    <Button onClick={() => setInviteTo(null)}>Anuluj</Button>
                    <Button onClick={doInvite} variant="contained">Wyślij</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={!!incoming} onClose={reject}>
                <DialogTitle>Zaproszenie od {incoming?.from}</DialogTitle>
                <DialogActions>
                    <Button onClick={reject}>Odrzuć</Button>
                    <Button onClick={accept} variant="contained">Akceptuj</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
