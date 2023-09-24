import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {UserService} from "../../services/user-service/UserService";
import {UserExtra} from "../../models/UserExtra";
import {useServices} from "../service-provider/ServiceProvider";

interface UserContextType {
    user: UserExtra | null;
    updateUser: (userData: UserExtra) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const [user, setUser] = useState<UserExtra | null>(null);
    const services = useServices();
    const userService: UserService = services.userService;
    const webSocketService = services.webSocketService;

    const updateUser = (userData: UserExtra) => {
        setUser(userData);
    };

    const fetchData = async () => {
        try {
            const response: UserExtra = await userService.getUserExtra();
            updateUser(response);
        } catch (error) {
            // TODO toast
            console.error('Error fetching user data:', error);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        webSocketService.subscribe('/user/queue/bankroll', (message) => {
            const userCopy: UserExtra = {...user!};
            userCopy.bankroll = message;
            updateUser(userCopy);
        });

        return () => {
            webSocketService.unsubscribe('/queue/bankroll');
        };
    }, [webSocketService]);

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