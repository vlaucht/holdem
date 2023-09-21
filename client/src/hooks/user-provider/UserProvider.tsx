import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {UserService} from "../../services/user-service/UserService";
import {UserExtra} from "../../models/UserExtra";

interface UserContextType {
    user: UserExtra | null;
    updateUser: (userData: UserExtra) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const [user, setUser] = useState<UserExtra | null>(null);
    const userService: UserService = new UserService();

    const updateUser = (userData: UserExtra) => {
        setUser(userData);
    };
    const fetchData = async () => {
        try {
            const response: UserExtra = await userService.getUserExtra();
            updateUser(response);
        } catch (error) {
            console.error('Error fetching user data:', error);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <UserContext.Provider value={{ user, updateUser }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (context === undefined) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};