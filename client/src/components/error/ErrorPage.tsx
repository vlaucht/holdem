import {Box, Center, Stack, Text} from "@mantine/core";
import React from "react";
import {IconFileAlert} from "@tabler/icons-react";
import {useViewportSize} from "@mantine/hooks";

/**
 * Component to render an error page.
 *
 * @param text text to display on the page
 *
 * @author Valentin Laucht
 */
export const ErrorPage: React.FunctionComponent<{
    text: string;
}> = ({ text }) => {
    const view = useViewportSize()
    return(
        <Box style={{height: view.height}}>
            <Center style={{height: "inherit"}}>
                <Stack style={{alignItems: 'center'}}>
                    <IconFileAlert size={50}/>
                    <Text fz="sm">{text}</Text>
                </Stack>
            </Center>
        </Box>
    )
};