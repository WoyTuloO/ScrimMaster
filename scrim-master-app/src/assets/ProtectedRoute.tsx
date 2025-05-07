import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from './AuthContext';

interface ProtectedRouteProps {
    children: React.ReactElement;
    requiredRole?: string;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, requiredRole }) => {
    const { isAuthenticated, user, loading } = useContext(AuthContext);

    if(loading){
        return null;
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }
    if (requiredRole && user?.role !== requiredRole) {
        return <Navigate to="/" replace />;
    }
    return children;
};

export default ProtectedRoute;