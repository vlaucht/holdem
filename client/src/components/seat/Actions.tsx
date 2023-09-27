import React, {useEffect, useState} from "react";
import './Seat.css';
import { PokerPlayerDto } from "../../models/PokerPlayerDto";
import {Button, Flex, Group, Text, Popover, Slider, ActionIcon} from "@mantine/core";
import {PokerGameState} from "../../models/PokerGameState";
import {useServices} from "../../hooks/service-provider/ServiceProvider";

const actionButtonText = {
    fold: "FOLD",
    check: "CHECK",
    call: "CALL",
    raise: "RAISE",
    allIn: "ALL-IN"
};

interface ActionProps {
    player: PokerPlayerDto;
    game: PokerGameState;
}

export const Actions: React.FunctionComponent<ActionProps> = ({ player, game }) => {
    const actionButtons = [];
    const [raiseValue, setRaiseValue] = useState(game.bigBlind);
    const [opened, setOpened] = useState(false);
    const services = useServices();

    const performAction = (action: string) => {
        console.log("performing action: " + action)
        if (action == 'raise' && opened) {
            services.webSocketService.sendMessage('/app/poker-action', {gameId: game.id, action: action, amount: raiseValue});
        } else if(action != 'raise') {
            services.webSocketService.sendMessage('/app/poker-action', {gameId: game.id, action: action});
        }
    }

    // to maintain order
    if (player.isActor && player.allowedActions && player.allowedActions.length > 0) {
        if (player.allowedActions.includes("fold")) {
            actionButtons.push(<Button onClick={() => performAction('fold')} key={"fold"} size="sm" color="red">{actionButtonText["fold"]}</Button>)
        }
        if (player.allowedActions.includes("check")) {
            actionButtons.push(<Button onClick={() => performAction('check')} key={"check"} size="sm" color="cyan">{actionButtonText["check"]}</Button>)
        }
        if (player.allowedActions.includes("call")) {
            actionButtons.push(<Button onClick={() => performAction('call')} key={"call"} size="sm" color="cyan">{actionButtonText["call"]}</Button>)
        }
        if (player.allowedActions.includes("raise")) {
            // Calculate the maximum value for the slider
            const maxSliderValue = player.chips % game.bigBlind === 0 ? player.chips : Math.floor(player.chips / game.bigBlind) * game.bigBlind;
            const el = (
                <Popover opened={opened} onChange={() => {setOpened((o) => !o);}} key={"raise-popup"} width={250} position="top" withArrow shadow="md" >
                    <Popover.Target>
                        <Button onClick={() => {setOpened((o) => !o); performAction('raise')}} data-action-button="raise" size="sm" color="cyan">{actionButtonText["raise"]}</Button>
                    </Popover.Target>
                    <Popover.Dropdown>
                        <Slider
                            min={game.bigBlind}
                            step={game.bigBlind}
                            max={maxSliderValue}
                            value={raiseValue}
                            onChange={(newValue) => setRaiseValue(newValue)}
                        />
                    </Popover.Dropdown>
                </Popover>
            )
            actionButtons.push(el)
        }
        if (player.allowedActions.includes("allIn")) {
            actionButtons.push(<Button onClick={() => performAction('allIn')} key={"allIn"} size="sm" color="cyan">{actionButtonText["allIn"]}</Button>)
        }
    }
    return (
        <div className={"action-bar"}>
            <Flex
                gap="xs"
                justify="center"
                align="center"
                direction="row"
                wrap="wrap"
                style={{ width: '100%' , height: 80}}
            >
                <Group gap="xs">
                    {actionButtons}
                </Group>
            </Flex>
        </div>
    );
}
