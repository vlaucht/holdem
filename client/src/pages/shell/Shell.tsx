import React from "react";
import {AppShell} from "@mantine/core";
import {Header} from "./header/Header";
import { Routes as NavRoutes } from './navigation/Routes'
import {Outlet, Route, Routes} from "react-router-dom";


export const Shell: React.FunctionComponent = () => {

    return (
            <AppShell
                header={{height: 60}}
                padding="md"
            >
                <Header></Header>
                <AppShell.Main>
                    <Routes>
                        {NavRoutes.map((route) => (
                            <Route
                                key={route.path}
                                path={route.path}
                                element={
                                    <route.element />
                                }
                            />
                        ))}
                    </Routes>
                    <Outlet />
                </AppShell.Main>
            </AppShell>
    )
}