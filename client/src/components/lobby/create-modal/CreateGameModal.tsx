import {Box, Button, Group, Modal, Slider, Space, TextInput, Text, Select} from "@mantine/core";
import React from "react";
import {useForm} from "@mantine/form";
import {PokerGameCreateRequest} from "../../../models/PokerGameCreateRequest";
import {useServices} from "../../../hooks/service-provider/ServiceProvider";
import {useUser} from "../../../hooks/user-provider/UserProvider";
import {UserExtra} from "../../../models/UserExtra";
import {PokerGameState} from "../../../models/PokerGameState";
import {useNavigate} from "react-router-dom";

interface CreateGameModalProps {
    opened: boolean;
    close: () => void;
}
export const CreateGameModal: React.FunctionComponent<CreateGameModalProps> = ({opened, close}) => {
    const pokerService = useServices().pokerService;
    const initialFormValues: PokerGameCreateRequest = { name: '', buyIn: 150, maxPlayerCount: 5, tableType: 'NL' };
    const userExtra = useUser().user;
    const navigate = useNavigate();

    const  form = useForm({
        initialValues: initialFormValues,

        validate: {
            name: (value) => ((value.length >= 3) ? null : 'Minimum length is 3 characters')
        },
    });

    // max buy in is 1 million or user's bankroll, whichever is smaller
    const maxBuyIn = Math.min(1000000, userExtra!.bankroll);

    const onSubmit = async (values: PokerGameCreateRequest) => {
        const gameState: PokerGameState = await pokerService.create(values);
        navigate(`/poker-game/${gameState.id}`);
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
                            max={5}
                            step={1}
                            color="cyan"
                            size="lg"
                            marks={[
                                { value: 2, label: '2' },
                                { value: 3, label: '3' },
                                { value: 4, label: '4' },
                                { value: 5, label: '5' },
                            ]}
                            {...form.getInputProps('maxPlayerCount')}
                        />
                        <Space h="xl" />
                        <Text size="sm">Buy-In</Text>
                        <Slider
                            label={(value) => `${value} $`}
                            min={100}
                            max={maxBuyIn}
                            step={100}
                            color="cyan"
                            size="lg"
                            {...form.getInputProps("buyIn")}
                        />
                        <Space h="xl" />
                        <Select
                            label="Table Type"
                            checkIconPosition="right"
                            data={[
                                { value: "NL", label: "No-Limit" },
                                { value: "FL", label: "Fixed-Limit" },
                            ]}
                            defaultValue="React"
                            allowDeselect={false}
                            {...form.getInputProps("tableType")}
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