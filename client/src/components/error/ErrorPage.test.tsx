import React from 'react';
import { render } from '@testing-library/react';
import { ErrorPage } from './ErrorPage';

describe('ErrorPage Component', () => {
    it('should display the text on the error page', () => {
        const { getByText } = render(<ErrorPage text="An error occurred" />);
        const errorText = getByText('An error occurred');

        expect(errorText).toBeInTheDocument();
    });

});
