import React, {
    createContext,
    useContext,
    useState,
    useEffect,
    ReactNode,
    useCallback
} from 'react';
import { useNavigate, useLocation } from 'react-router-dom';




let refreshingPromise: Promise<Response> | null = null;

interface User {
    id: number;
    username: string;
    email: string;
    kd: number;
    adr: number;
    ranking: number;
    role: string;
}

interface AuthContextType {
    username: string;
    user: User | null;
    isAuthenticated: boolean;
    loading: boolean;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    authFetch: (input: RequestInfo, init?: RequestInit) => Promise<Response>;
}

export const AuthContext = createContext<AuthContextType>({
    username: '',
    user: null,
    isAuthenticated: false,
    loading: true,
    login: async () => {},
    logout: async () => {},
    authFetch: fetch
});

export const useAuth = (): AuthContextType => {
    return useContext(AuthContext);
};



export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    const location = useLocation();
    const publicRoutes = ['/login', '/register', '/players', '/scrims','/teams', '/'];

    const navigateOnUnauth = useCallback(() => {
        if (!publicRoutes.includes(location.pathname)) {
            navigate('/login');
        }
    }, [navigate, location.pathname]);


    const authFetch = useCallback(async (input: RequestInfo, init: RequestInit = {}) => {
        init.credentials = 'include';
        let res = await fetch(input, init);

        if (res.status === 401) {
            if (!refreshingPromise) {
                refreshingPromise = fetch('http://localhost:8080/api/auth/refresh', {
                    method: 'POST',
                    credentials: 'include'
                });
            }
            const refresh = await refreshingPromise;
            refreshingPromise = null;

            if (refresh.ok) {
                res = await fetch(input, init);
            } else {
                setUser(null);
                setLoading(false);
                navigateOnUnauth();
                throw new Error('Sesja wygasła');
            }
        }
        return res;
    }, [navigateOnUnauth]);

    const fetchCurrentUser = useCallback(async () => {
        try {
            setLoading(true);
            const res = await authFetch('http://localhost:8080/api/user/currentUser');
            if (res.ok) {
                const data: User = await res.json();
                setUser(data);
            } else {
                setUser(null);
            }
        } catch {
            setUser(null);
        } finally {
            setLoading(false);
        }
    }, [authFetch]);

    useEffect(() => {
        fetchCurrentUser();
    }, [fetchCurrentUser]);

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
        await fetchCurrentUser();
    };

    const logout = async () => {
        await fetch('http://localhost:8080/api/auth/logout', { method: 'POST', credentials: 'include' });
        setUser(null);
        navigate('/login');
    };

    const isAuthenticated = !!user;

    return (
        <AuthContext.Provider value={{
            username: user?.username ?? '',
            user,
            isAuthenticated,
            loading,
            login,
            logout,
            authFetch
        }}>
            {children}
        </AuthContext.Provider>
    );
};
