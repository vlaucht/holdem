import {Box, Button, Group, Modal, Space, Text} from "@mantine/core";
import React from "react";

interface ActionModalProps {
    opened: boolean;
    close: () => void;
    onConfirm?: () => void;
    title: string;
    message: string;
}
export const ActionModal: React.FunctionComponent<ActionModalProps> = ({opened, close, onConfirm, title, message}) => {

    return (
        <Modal opened={opened} onClose={close} title={title}>
            <Box maw={340} mx="auto">
                <Space h="xl" />
                <Text>{message}</Text>
                <Space h="xl" />
                <Group mt="xl" justify="center" gap="lg">
                    {onConfirm && <Button fullWidth onClick={onConfirm} color="orange">OK</Button>}
                    <Button fullWidth onClick={close} variant="light">Cancel</Button>
                </Group>
            </Box>
        </Modal>
    );
}