import React from "react";
import {AppShellHeader, Avatar, Button, Grid, Group, Title} from '@mantine/core'
import {IconLogout, IconMoneybag} from "@tabler/icons-react";
import {useKeycloak} from "@react-keycloak/web";
import {useUser} from "../../../hooks/user-provider/UserProvider";
import {useServices} from "../../../hooks/service-provider/ServiceProvider";

export const Header: React.FunctionComponent = () => {
    const {keycloak} = useKeycloak();
    const services = useServices();
    const {user} = useUser();
    const onLogout = () => {
        keycloak.logout();
        services.webSocketService.disconnect();
    }

    return (
        <AppShellHeader
            p="md"
            style={{height: 80}}
        >
            <Grid>
                <Grid.Col span={1}/>
                <Grid.Col span={2}>
                    <Title order={2}>Game Lobby</Title>
                </Grid.Col>
                <Grid.Col span={5}/>
                <Grid.Col span={2}>
                    <Group>
                        <Avatar radius="xl" src={user.avatar}/>
                        <Title order={5} >{user.username}</Title>
                        <Title order={5}><IconMoneybag/>{user.bankroll}$</Title>
                    </Group>
                </Grid.Col>
                <Grid.Col span={2}>
                    <Button color="red" leftSection={<IconLogout/>} onClick={onLogout}>Log out</Button>
                </Grid.Col>
            </Grid>
        </AppShellHeader>
    )
}