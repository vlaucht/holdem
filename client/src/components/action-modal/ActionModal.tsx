import {Box, Button, Group, Modal, Space, Text} from "@mantine/core";
import React from "react";

/**
 * Properties for the ActionModal component.
 */
interface ActionModalProps {
    opened: boolean;
    close: () => void;
    onConfirm?: () => void;
    title: string;
    message: string;
}

/**
 * Component to render a modal with a message and a cancel and an optional confirm button.
 *
 * @param opened indicates if the modal is opened.
 * @param close function to close the modal.
 * @param onConfirm function to execute when the confirm button is clicked.
 * @param title title of the modal.
 * @param message message to display in the modal.
 */
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