import {Box, Button, Group, Modal, Slider, Space, TextInput, Text} from "@mantine/core";
import React from "react";
import {useForm} from "@mantine/form";
import {PokerGameCreateRequest} from "../../../models/PokerGameCreateRequest";
import {useServices} from "../../../hooks/service-provider/ServiceProvider";

interface CreateGameModalProps {
    opened: boolean;
    close: () => void;
}
export const CreateGameModal: React.FunctionComponent<CreateGameModalProps> = ({opened, close}) => {
    const lobbyService = useServices().lobbyService;
    const initialFormValues: PokerGameCreateRequest = { name: '', buyIn: 150, maxPlayerCount: 6, tableType: 'NL' };

    const  form = useForm({
        initialValues: initialFormValues,

        validate: {
            name: (value) => ((value.length >= 3) ? null : 'Minimum length is 3 characters')
        },
    });

    const onSubmit = async (values: PokerGameCreateRequest) => {
        await lobbyService.create(values);
        // TODO navigate to game
        close();
    }

    return (
            <Modal opened={opened} onClose={close} title="CREATE GAME">
                <Box maw={340} mx="auto">
                    <form onSubmit={form.onSubmit((values) => onSubmit(values))}>
                        <TextInput
                            withAsterisk
                            label="Game Name"
                            placeholder="Hold 'em"
                            {...form.getInputProps('name')}
                        />
                        <Space h="xl" />
                        <Text size="sm">Max Players</Text>
                        <Slider
                            label={null}
                            min={2}
                            max={6}
                            step={1}
                            color="cyan"
                            size="lg"
                            marks={[
                                { value: 2, label: '2' },
                                { value: 3, label: '3' },
                                { value: 4, label: '4' },
                                { value: 5, label: '5' },
                                { value: 6, label: '6' },
                            ]}
                            {...form.getInputProps('maxPlayerCount')}
                        />
                        <Space h="xl" />
                        <Group justify="flex-end" mt="md">
                            <Button color="cyan" disabled={!form.isValid()} type="submit">CREATE</Button>
                        </Group>
                    </form>
                </Box>
            </Modal>
    );
}