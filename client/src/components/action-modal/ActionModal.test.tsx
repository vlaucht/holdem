import {ActionModal} from "./ActionModal";
import { render, fireEvent } from "@testing-library/react";

describe('ActionModal', () => {

    it('Should display modal when opened is true', () => {
        const onClose = jest.fn();
        const { getByText } = render(
            <ActionModal opened={true} close={onClose} title="Test Modal" message="This is a test message" />
        );

        const modalTitle = getByText("Test Modal");
        const modalMessage = getByText("This is a test message");

        expect(modalTitle).toBeInTheDocument();
        expect(modalMessage).toBeInTheDocument();
    });

    it('Should not display modal when opened is false', () => {
        const onClose = jest.fn();
        const { queryByText } = render(
            <ActionModal opened={false} close={onClose} title="Test Modal" message="This is a test message" />
        );

        const modalTitle = queryByText("Test Modal");
        const modalMessage = queryByText("This is a test message");

        expect(modalTitle).toBeNull();
        expect(modalMessage).toBeNull();
    });

    it('Should call close function when cancel button is clicked', () => {
        const onClose = jest.fn();
        const onConfirm = jest.fn();
        const { getByText } = render(
            <ActionModal opened={true} close={onClose} onConfirm={onConfirm} title="Test Modal" message="This is a test message" />
        );

        const cancelButton = getByText("Cancel");
        fireEvent.click(cancelButton);

        expect(onClose).toHaveBeenCalled();
    });

    it('Should call onConfirm function when confirm button is clicked', () => {
        const onClose = jest.fn();
        const onConfirm = jest.fn();
        const { getByText } = render(
            <ActionModal opened={true} close={onClose} onConfirm={onConfirm} title="Test Modal" message="This is a test message" />
        );

        const okButton = getByText("OK");
        fireEvent.click(okButton);

        expect(onConfirm).toHaveBeenCalled();
    });
});