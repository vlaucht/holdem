package de.thm.holdem.service;

public interface WebsocketService {

    <T> void broadcast(String room, T payload);
}
