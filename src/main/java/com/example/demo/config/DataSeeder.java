package com.example.demo.config;

import com.example.demo.model.Booking;
import com.example.demo.model.Employee;
import com.example.demo.model.Room;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.RoomStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoomRepository roomRepository;
    private final EmployeeRepository employeeRepository;
    private final BookingRepository bookingRepository;

    public DataSeeder(RoomRepository roomRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data load.");
            return;
        }

        log.info("Seeding initial corporate rooms, employees, and demo bookings...");

        // 1. Seed Rooms
        Room apex = new Room(
                "Apex Executive Boardroom",
                "RM-501",
                "Executive Floor 5",
                18,
                "4K Dual Displays, Polycom Video Bar, Digital Smart Board, Premium Acoustic Foam, Wireless Casting",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80",
                "Flagship boardroom equipped with high-fidelity acoustics and enterprise telepresence."
        );

        Room quantum = new Room(
                "Quantum Innovation Hub",
                "RM-302",
                "Engineering Floor 3",
                12,
                "Full Wall Whiteboard, Dual 4K TVs, Movable Pod Desks, High-Speed Fiber WiFi, Studio Mic",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1517502884422-41eaead166d4?auto=format&fit=crop&w=800&q=80",
                "Dynamic collaboration space designed for sprint planning and engineering brainstorms."
        );

        Room nexus = new Room(
                "Nexus Huddle Pod",
                "RM-204",
                "Product Floor 2",
                6,
                "65\" Ultra-HD Screen, Logitech MeetUp Cam, Glass Whiteboard, USB-C Fast Dock",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1577495508048-b635879837f1?auto=format&fit=crop&w=800&q=80",
                "Cozy, agile pod ideal for 1:1 syncs, product reviews, and quick standups."
        );

        Room aurora = new Room(
                "Aurora Design Studio",
                "RM-405",
                "Creative Floor 4",
                10,
                "Interactive Touch Wall, Design Moodboards, Ergonomic Seating, High-CRI Lighting",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=800&q=80",
                "Vibrant creative haven crafted for UX workshops, customer discovery, and design sprints."
        );

        Room zenith = new Room(
                "Zenith Townhall Hall",
                "RM-101",
                "Ground Floor",
                45,
                "Dual Ceiling Laser Projectors, Wireless Lapel Mics, Surround Sound, Stage Podiums, Live Stream Deck",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1505373877841-8d25f7d46678?auto=format&fit=crop&w=800&q=80",
                "Large-capacity townhall space equipped for company all-hands and keynote presentations."
        );

        Room horizon = new Room(
                "Horizon Focus Booth",
                "RM-208",
                "Product Floor 2",
                4,
                "HD Webcam, Noise-Cancelling Acoustic Panels, Power Docks, Sit-Stand Table",
                RoomStatus.AVAILABLE,
                "https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=800&q=80",
                "Quiet, sound-dampened focus pod designed for remote client calls and deep focus."
        );

        roomRepository.saveAll(List.of(apex, quantum, nexus, aurora, zenith, horizon));

        // 2. Seed Employees
        Employee alex = new Employee("EMP-101", "Alex Morgan", "alex.morgan@company.com", "Engineering", "Principal Architect", "+1-555-0101", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80");
        Employee sarah = new Employee("EMP-102", "Sarah Chen", "sarah.chen@company.com", "Product", "VP of Product", "+1-555-0102", "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&q=80");
        Employee marcus = new Employee("EMP-103", "Marcus Vance", "marcus.vance@company.com", "Design", "Head of UX", "+1-555-0103", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&q=80");
        Employee priya = new Employee("EMP-104", "Priya Sharma", "priya.sharma@company.com", "People", "HR Director", "+1-555-0104", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&q=80");
        Employee david = new Employee("EMP-105", "David Kim", "david.kim@company.com", "Operations", "Chief Operating Officer", "+1-555-0105", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80");

        employeeRepository.saveAll(List.of(alex, sarah, marcus, priya, david));

        // 3. Seed Demo Bookings for Today
        LocalDate today = LocalDate.now();

        // Slot 1: Apex Boardroom - 10:00 to 11:30 (Confirmed) -> Perfect to test conflict rejection!
        Booking b1 = new Booking(
                apex,
                sarah,
                "Q3 Executive Strategy & OKRs",
                "Quarterly alignment on business priorities and key team metrics.",
                LocalDateTime.of(today, LocalTime.of(10, 0)),
                LocalDateTime.of(today, LocalTime.of(11, 30)),
                12
        );
        b1.setStatus(BookingStatus.CONFIRMED);

        // Slot 2: Quantum Lab - 14:00 to 15:30 (Confirmed)
        Booking b2 = new Booking(
                quantum,
                alex,
                "Sprint Architecture & Microservices Refactor",
                "Technical deep dive into event streaming and API performance tuning.",
                LocalDateTime.of(today, LocalTime.of(14, 0)),
                LocalDateTime.of(today, LocalTime.of(15, 30)),
                8
        );
        b2.setStatus(BookingStatus.CONFIRMED);

        // Slot 3: Aurora Studio - 09:00 to 10:00 (Checked In)
        Booking b3 = new Booking(
                aurora,
                marcus,
                "Design Critique & Mobile Wireframes",
                "Reviewing high-fidelity prototypes with cross-functional stakeholders.",
                LocalDateTime.of(today, LocalTime.of(9, 0)),
                LocalDateTime.of(today, LocalTime.of(10, 0)),
                6
        );
        b3.setStatus(BookingStatus.CHECKED_IN);
        b3.setCheckedInAt(LocalDateTime.of(today, LocalTime.of(8, 55)));

        // Slot 4: Demo booking for testing auto-release grace period expiry (No-Show test)
        // Starts 15 minutes ago, so organizer missed 10-minute check-in grace window!
        LocalDateTime missedStart = LocalDateTime.now().minusMinutes(15);
        Booking b4 = new Booking(
                nexus,
                david,
                "Weekly Operations Sync (Unchecked-in Demo)",
                "This booking was created with start time 15 mins ago to demonstrate the 10-min no-show auto-release rule.",
                missedStart,
                missedStart.plusMinutes(45),
                4
        );
        b4.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.saveAll(List.of(b1, b2, b3, b4));

        log.info("Successfully seeded 6 modern rooms, 5 employees, and 4 demo bookings!");
    }
}
