import React from "react";
import {Button, Container, Flex, Space, Table} from "@mantine/core";
import {IconPlus} from "@tabler/icons-react";
import {useDisclosure} from "@mantine/hooks";
import {CreateGameModal} from "../create-modal/CreateGameModal";

export const GameList: React.FunctionComponent = () => {
    const [opened, { open, close }] = useDisclosure(false);
    return (
        <>
        <Container mt="md" size="xl">
            <Flex
                mih={90}
                gap="md"
                justify="flex-end"
                align="center"
                direction="row"
                wrap="wrap"
            >
                <Button leftSection={<IconPlus size={20} />} variant="filled" color="cyan" size="md"
                        onClick={open}>CRATE GAME</Button>
            </Flex>

            <Table highlightOnHover horizontalSpacing="xl">
                <Table.Thead>
                    <Table.Tr>
                    <Table.Th>NAME</Table.Th>
                    <Table.Th>PLAYERS</Table.Th>
                    <Table.Th>BUI-IN</Table.Th>
                    <Table.Th>STATUS</Table.Th>
                    <Table.Th>ACTIONS</Table.Th>
                    </Table.Tr>
                </Table.Thead>
                <Table.Tbody>
                </Table.Tbody>
            </Table>
        </Container>
        <CreateGameModal opened={opened} close={close}/>
        </>
    )
}