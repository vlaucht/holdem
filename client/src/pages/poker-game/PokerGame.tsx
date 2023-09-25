import React, {useEffect, useState} from "react";

import {Title} from "@mantine/core";
import {useNavigate, useParams} from "react-router-dom";
import {useServices} from "../../hooks/service-provider/ServiceProvider";
import {PokerGameState} from "../../models/PokerGameState";
import {ContentLoader} from "../../components/loader/ContentLoader";

export const PokerGame: React.FunctionComponent = () => {
    const { id } = useParams();
    const services = useServices();
    const navigate = useNavigate();
    const [gameState, setGameState] = useState<PokerGameState | null>(null);

    const fetchGameState = async () => {
        try {
            const response: PokerGameState = await services.pokerService.getPokerGameState(id!);
            setGameState(response);
        } catch (error) {
            // TODO error toast
            navigate('/lobby');
            console.error('Game not found:', error);
        }
    };

    useEffect(() => {
        fetchGameState();
    }, []);

    return (
        gameState ?
        <Title order={1}>{gameState.name}</Title>
            : <ContentLoader text={"Loading Game..."}></ContentLoader>
    )
}