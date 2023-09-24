import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {UserService} from "../../services/user-service/UserService";
import {UserExtra} from "../../models/UserExtra";
import {useServices} from "../service-provider/ServiceProvider";

interface UserContextType {
    user: UserExtra;
    updateUser: (userData: UserExtra) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const [user, setUser] = useState<UserExtra>({bankroll:0, username: '', avatar: ''});
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

    const updateUserBankroll = (bankroll: number) => {
        setUser((prevUser) => {
            const userCopy: UserExtra = { ...prevUser };
            userCopy.bankroll = bankroll;
            return userCopy;
        });
    }

    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        webSocketService.subscribe('/user/queue/bankroll', (message) => {
            updateUserBankroll(message);
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