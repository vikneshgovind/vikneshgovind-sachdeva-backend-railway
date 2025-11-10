package com.sachdeva.roadlines.Enum;

public enum HubStatus {
	INITIATED, // Entry created, not yet billed
	PENDING, // Bill generated but not yet paid
	PAID // Full payment received
}
