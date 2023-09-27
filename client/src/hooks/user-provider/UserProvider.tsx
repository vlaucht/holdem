import React, {createContext, ReactNode, useContext, useEffect, useState} from 'react';
import {UserService} from "../../services/user-service/UserService";
import {UserExtra} from "../../models/UserExtra";
import {useServices} from "../service-provider/ServiceProvider";
import {useNavigate} from "react-router-dom";

interface UserContextType {
    user: UserExtra;
    updateUser: (userData: UserExtra) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{children: ReactNode}> = ({  children }) => {
    const [user, setUser] = useState<UserExtra>({} as UserExtra);
    const services = useServices();
    const userService: UserService = services.userService;
    const webSocketService = services.webSocketService;
    const navigate = useNavigate();
    const updateUser = (userData: UserExtra) => {
        setUser(userData);
    };

    /**
     * If the user has an active game when he is connecting, navigate to it.
     *
     * @param userExtra
     */
    const navigateToActiveGame = (userExtra: UserExtra)=> {
        if (userExtra.activeGameId) {
            navigate(`/poker-game/${userExtra.activeGameId}`);
        }
    }

    const fetchData = async () => {
        try {
            const response: UserExtra = await userService.getUserExtra();
            updateUser(response);
            navigateToActiveGame(response);
        } catch (error) {
            // TODO toast
            console.error('Error fetching user data:', error);
        }
    };

    const updateUserBankroll = (userUpdate: UserExtra) => {
        setUser((prevUser) => {
            return { ...prevUser, ...userUpdate };
        });
    }


    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        webSocketService.subscribe('/user/queue/user-extra', (message) => {
            updateUserBankroll(message);
        });


        return () => {
            webSocketService.unsubscribe('/user/queue/user-extra');
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