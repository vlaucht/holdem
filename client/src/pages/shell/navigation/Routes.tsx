import React from "react";
import {Lobby} from "../../lobby/Lobby";
import {Navigate} from "react-router-dom";
import {PokerGame} from "../../poker-game/PokerGame";

export type NavRoute = {
    element: React.FunctionComponent
    path: string
}

export const Routes: NavRoute[] = [
    {
        path: 'lobby',
        element: () => (
            <React.Suspense fallback="loading">
                <Lobby />
            </React.Suspense>
        ),
    },
    {
        path: 'poker-game/:id',
        element: () => (
            <React.Suspense fallback="loading">
                <PokerGame />
            </React.Suspense>
        ),
    },
    {
        path: '*',
        element: () => <Navigate to="/lobby" replace />,
    },
]