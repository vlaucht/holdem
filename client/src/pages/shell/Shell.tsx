import React from "react";
import {AppShell} from "@mantine/core";
import {Header} from "./header/Header";
import {GameList} from "../../components/lobby/game-list/GameList";


export const Shell: React.FunctionComponent = () => {

    return (
            <AppShell
                header={{height: 60}}
                padding="md"
            >
                <Header></Header>
                <AppShell.Main>
                    <GameList/>
                </AppShell.Main>
            </AppShell>
    )
}