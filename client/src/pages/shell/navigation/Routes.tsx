import React from "react";
import {Lobby} from "../../lobby/Lobby";
import {Navigate} from "react-router-dom";

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
        path: '*',
        element: () => <Navigate to="/lobby" replace />,
    },
]