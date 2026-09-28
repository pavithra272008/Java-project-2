package com.example.demo.dto;

public class DashboardStatsDto {

    private long totalRooms;
    private long availableRooms;
    private long maintenanceRooms;
    private long totalBookingsToday;
    private long activeMeetingsNow;
    private long totalUpcomingBookings;
    private long totalCheckedInBookings;
    private long totalCancelledBookings;
    private long autoReleasedNoShows;
    private double roomUtilizationRate;

    public DashboardStatsDto() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public long getTotalBookingsToday() {
        return totalBookingsToday;
    }

    public void setTotalBookingsToday(long totalBookingsToday) {
        this.totalBookingsToday = totalBookingsToday;
    }

    public long getActiveMeetingsNow() {
        return activeMeetingsNow;
    }

    public void setActiveMeetingsNow(long activeMeetingsNow) {
        this.activeMeetingsNow = activeMeetingsNow;
    }

    public long getTotalUpcomingBookings() {
        return totalUpcomingBookings;
    }

    public void setTotalUpcomingBookings(long totalUpcomingBookings) {
        this.totalUpcomingBookings = totalUpcomingBookings;
    }

    public long getTotalCheckedInBookings() {
        return totalCheckedInBookings;
    }

    public void setTotalCheckedInBookings(long totalCheckedInBookings) {
        this.totalCheckedInBookings = totalCheckedInBookings;
    }

    public long getTotalCancelledBookings() {
        return totalCancelledBookings;
    }

    public void setTotalCancelledBookings(long totalCancelledBookings) {
        this.totalCancelledBookings = totalCancelledBookings;
    }

    public long getAutoReleasedNoShows() {
        return autoReleasedNoShows;
    }

    public void setAutoReleasedNoShows(long autoReleasedNoShows) {
        this.autoReleasedNoShows = autoReleasedNoShows;
    }

    public double getRoomUtilizationRate() {
        return roomUtilizationRate;
    }

    public void setRoomUtilizationRate(double roomUtilizationRate) {
        this.roomUtilizationRate = roomUtilizationRate;
    }
}
