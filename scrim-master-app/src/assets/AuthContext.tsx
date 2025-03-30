import React, { createContext, useState, ReactNode } from 'react';

export interface User {
    id: number;
    username: string;
    email: string;
    // inne pola, jeśli potrzebne
}

interface AuthContextType {
    user: User | null;
    isAuthenticated: boolean;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    setUser: (user: User | null) => void;
}

export const AuthContext = createContext<AuthContextType>({
    user: null,
    isAuthenticated: false,
    login: () => Promise.resolve(),
    logout: () => Promise.resolve(),
    setUser: () => {}
});

interface Props {
    children: ReactNode;
}

export const AuthProvider: React.FC<Props> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);

    // Funkcja login – używamy promise
    const login = (username: string, password: string): Promise<void> => {
        return fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ username, password }),
            credentials: 'include' // Ważne – przesyłamy ciasteczka (JSESSIONID)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Niepoprawne dane logowania');
                }
                return response.text(); // lub response.json() w zależności od backendu
            })
            .then(() => {
                // Ustawiamy dane użytkownika – tutaj symulujemy dane
                const userData: User = {
                    id: 1,
                    username,
                    email: `${username}@example.com`
                };
                setUser(userData);
            });
    };

    // Funkcja logout – czyści stan po wylogowaniu
    const logout = (): Promise<void> => {
        return fetch('http://localhost:8080/logout', {
            method: 'POST',
            credentials: 'include'
        })
            .then(() => {
                setUser(null);
            })
            .catch(() => {
                setUser(null);
            });
    };

    return (
        <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, logout, setUser }}>
            {children}
        </AuthContext.Provider>
    );
};
