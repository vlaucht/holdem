import React from 'react';
import { render } from '@testing-library/react';
import { ContentLoader } from './ContentLoader';

describe('ContentLoader Component', () => {
    it('should render the ContentLoader component with text', () => {
        const { getByText } = render(<ContentLoader text="Loading content" />);
        const loaderText = getByText('Loading content');

        expect(loaderText).toBeInTheDocument();
    });
});
