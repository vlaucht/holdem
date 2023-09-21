import {Box, Center, Loader, Stack, Text} from "@mantine/core";
import React from "react";
import {useViewportSize} from "@mantine/hooks";

/**
 * Component to render a loading page.
 *
 * @param text text to display on the page
 * @param height height of the page, defaults to 400px
 *
 * @author Valentin Laucht
 */
export const ContentLoader: React.FunctionComponent<{
    text: string;
}> = ({ text }) => {
    const view = useViewportSize()
    return(
        <Box style={{textAlign: 'center'}}>
            <Center style={{ height: view.height}}  >
                <Stack style={{alignItems: 'center'}}>
                    <Loader size="xl" variant="bars"/>
                    <Text fz="sm">{text}</Text>
                </Stack>
            </Center>
        </Box>
    )
};