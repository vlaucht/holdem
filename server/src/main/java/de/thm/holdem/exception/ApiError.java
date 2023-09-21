package de.thm.holdem.exception;

import java.sql.Timestamp;

/**
 * Formats an Api Exception in a readable way.
 *
 * @author Valentin Laucht
 * @version 1.0
 */
public record ApiError(Timestamp timestamp, int status, String error, String message) {}