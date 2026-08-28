package br.com.customer.dto.request;

import java.time.LocalDate;

/**
 * Optional body for POST /workout/{id}/performed.
 * {@code performedOn} missing or today starts (or resumes) a live session.
 * A past date creates a diary entry for that day instead of using now().
 */
public record StartSessionRequest(
        LocalDate performedOn
) {}
