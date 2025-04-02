import React, {createContext, useState, ReactNode, useEffect} from 'react';

export interface User {
    id: number;
    username: string;
    email: string;
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

    useEffect(() => {
        fetch('http://localhost:8080/api/user/currentUser', {
            method: 'GET',
            credentials: 'include'
        })
            .then(res => {
                if (res.ok) {
                    return res.json();
                }
                throw new Error('Brak aktywnej sesji');
            })
            .then((data: User) => {
                setUser(data);
            })
            .catch(err => {
                setUser(null);
            });
    }, []);

    const login = (username: string, password: string): Promise<void> => {
        return fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ username, password }),
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Niepoprawne dane logowania');
                }
                return response.text();
            })
            .then(() => {
                return fetch('http://localhost:8080/api/user/currentUser', {
                    method: 'GET',
                    credentials: 'include'
                });
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Nie udało się pobrać danych użytkownika');
                }
                return res.json();
            })
            .then((data: User) => {
                setUser(data);
            });
    };

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