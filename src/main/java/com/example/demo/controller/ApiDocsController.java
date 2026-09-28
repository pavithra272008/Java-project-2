package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "*")
public class ApiDocsController {

    @GetMapping("/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpointsDocumentation() {
        List<Map<String, Object>> endpoints = List.of(
                Map.of(
                        "group", "Rooms",
                        "method", "GET",
                        "path", "/api/rooms",
                        "summary", "List all meeting rooms with optional filters",
                        "params", List.of("minCapacity (optional)", "status (AVAILABLE/UNDER_MAINTENANCE)", "search (keyword)"),
                        "description", "Fetches all meeting rooms along with capacity, floor, amenities, and current status."
                ),
                Map.of(
                        "group", "Rooms",
                        "method", "GET",
                        "path", "/api/rooms/{id}",
                        "summary", "Get details of a single meeting room",
                        "params", List.of("id (path variable)"),
                        "description", "Returns detailed specifications and amenities of the specified room."
                ),
                Map.of(
                        "group", "Rooms",
                        "method", "GET",
                        "path", "/api/rooms/{id}/availability",
                        "summary", "Get day timeline and available slots for a room",
                        "params", List.of("id (path variable)", "date (YYYY-MM-DD, defaults to today)"),
                        "description", "Returns hourly breakdown (08:00 to 20:00) indicating busy/free slots for the specified day."
                ),
                Map.of(
                        "group", "Rooms",
                        "method", "POST",
                        "path", "/api/rooms",
                        "summary", "Create a new meeting room",
                        "params", List.of("Body: { name, floor, capacity, amenities, status, imageUrl, description }"),
                        "description", "Adds a new room to the corporate directory."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "GET",
                        "path", "/api/bookings",
                        "summary", "List bookings with filtering",
                        "params", List.of("roomId (optional)", "employeeId (optional)", "status (optional)", "startDate (optional)", "endDate (optional)"),
                        "description", "Retrieves bookings filtered by room, employee, or status."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "POST",
                        "path", "/api/bookings",
                        "summary", "Book a meeting room with conflict detection",
                        "params", List.of("Body: { roomId, employeeId, title, description, startTime, endTime, attendeesCount }"),
                        "description", "Enforces conflict detection: rejects requests overlapping confirmed bookings with HTTP 409 Conflict."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "POST",
                        "path", "/api/bookings/check-conflicts",
                        "summary", "Preview conflict check without creating booking",
                        "params", List.of("Body: { roomId, startTime, endTime, excludeBookingId }"),
                        "description", "Used by UI to check whether a slot is available before submission."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "PUT",
                        "path", "/api/bookings/{id}/check-in",
                        "summary", "Organizer check-in to confirm attendance",
                        "params", List.of("id (path variable)"),
                        "description", "Confirms check-in within the 10-minute grace period. Updates status to CHECKED_IN."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "PUT",
                        "path", "/api/bookings/{id}/cancel",
                        "summary", "Cancel a booking and immediately free the slot",
                        "params", List.of("id (path variable)", "Body: { reason } (optional)"),
                        "description", "Frees the room time slot so other teams can reserve it."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "POST",
                        "path", "/api/bookings/run-auto-release",
                        "summary", "Run no-show auto-release check",
                        "params", List.of("None"),
                        "description", "Finds all CONFIRMED bookings with start times older than 10 mins without check-in, marks as NO_SHOW, and frees rooms."
                ),
                Map.of(
                        "group", "Bookings",
                        "method", "POST",
                        "path", "/api/bookings/{id}/simulate-no-show",
                        "summary", "Simulate 10-min grace period expiry (Testing/Demo)",
                        "params", List.of("id (path variable)"),
                        "description", "Fast-forwards a booking to NO_SHOW to demonstrate automatic release behavior."
                ),
                Map.of(
                        "group", "Dashboard",
                        "method", "GET",
                        "path", "/api/dashboard/stats",
                        "summary", "Get real-time room utilization & booking stats",
                        "params", List.of("None"),
                        "description", "Returns total rooms, today bookings, active meetings, saved no-show slots, and utilization rate."
                ),
                Map.of(
                        "group", "Employees",
                        "method", "GET",
                        "path", "/api/employees",
                        "summary", "List all employees",
                        "params", List.of("None"),
                        "description", "Returns list of registered employees for booking attribution."
                )
        );

        return ResponseEntity.ok(Map.of(
                "title", "RoomBook API Documentation",
                "version", "1.0.0",
                "description", "RESTful Backend for Meeting Room Booking with Conflict Detection & 10-Min No-Show Auto-Release",
                "endpoints", endpoints
        ));
    }
}
