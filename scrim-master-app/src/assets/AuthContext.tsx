import React, {
    createContext,
    useState,
    useEffect,
    ReactNode,
    useCallback
} from 'react';

interface AuthContextType {
    isAuthenticated: boolean;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    authFetch: (input: RequestInfo, init?: RequestInit) => Promise<Response>;
}

export const AuthContext = createContext<AuthContextType>({
    isAuthenticated: false,
    login: async () => {},
    logout: async () => {},
    authFetch: fetch
});

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [isAuthenticated, setAuthenticated] = useState(false);

    useEffect(() => {
        authFetch('http://localhost:8080/api/auth/me')
            .then(res => setAuthenticated(res.ok))
            .catch(() => setAuthenticated(false));
    }, []);

    const login = async (username: string, password: string) => {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ username, password })
        });
        if (!res.ok) {
            throw new Error('Niepoprawne dane logowania');
        }
        setAuthenticated(true);
    };

    const logout = async () => {
        await fetch('http://localhost:8080/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });
        setAuthenticated(false);
    };

    const authFetch = useCallback(async (input: RequestInfo, init: RequestInit = {}) => {
        init.credentials = 'include';
        let res = await fetch(input, init);
        if (res.status === 401) {
            const r = await fetch('http://localhost:8080/api/auth/refresh', {
                method: 'POST',
                credentials: 'include'
            });
            if (r.ok) {
                res = await fetch(input, init);
            } else {
                setAuthenticated(false);
                throw new Error('Sesja wygasła');
            }
        }
        return res;
    }, []);

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout, authFetch }}>
            {children}
        </AuthContext.Provider>
    );
};
