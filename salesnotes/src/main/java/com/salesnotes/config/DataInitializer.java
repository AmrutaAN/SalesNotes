package com.salesnotes.config;

import com.salesnotes.entity.*;
import com.salesnotes.repository.CustomerRepository;
import com.salesnotes.repository.NoteCategoryRepository;
import com.salesnotes.repository.SalesNoteRepository;
import com.salesnotes.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            CustomerRepository customerRepo,
            NoteCategoryRepository categoryRepo,
            SalesNoteRepository noteRepo,
            UserRepository userRepo
    ) {
        return args -> {
            // Seed Users if not present
            if (userRepo.count() == 0) {
                // 1) SYSADMIN
                User sysadmin = new User("sysadmin", "System Administrator", "admin@salesnotes.io", "Admin@123", UserRole.SYSADMIN);
                sysadmin.setActive(true);
                userRepo.save(sysadmin);

                // 2) companyadmin
                User companyAdmin = new User("companyadmin", "Alice Johnson (Company Admin)", "alice@acmecloud.com", "Admin@123", UserRole.COMPANYADMIN);
                companyAdmin.setActive(true);
                userRepo.save(companyAdmin);

                // 3) companyemployee
                User companyEmployee = new User("companyemployee", "Bob Miller (Sales Rep)", "bob@acmecloud.com", "User@123", UserRole.COMPANYEMPLOYEE);
                companyEmployee.setActive(true);
                userRepo.save(companyEmployee);

                System.out.println("SalesNotes Data Initializer: Default users seeded (SYSADMIN, companyadmin, companyemployee)!");
            }

            if (customerRepo.count() > 0) {
                return; // Customers already initialized
            }

            // Categories
            NoteCategory discovery = categoryRepo.save(new NoteCategory("Discovery Call", "#3b82f6"));
            NoteCategory demo = categoryRepo.save(new NoteCategory("Product Demo", "#8b5cf6"));
            NoteCategory proposal = categoryRepo.save(new NoteCategory("Proposal Sent", "#f59e0b"));
            NoteCategory negotiation = categoryRepo.save(new NoteCategory("Negotiation", "#ec4899"));
            NoteCategory closing = categoryRepo.save(new NoteCategory("Closing", "#10b981"));

            // Customers
            Customer c1 = customerRepo.save(new Customer("Sarah Jenkins", "Acme Cloud Corp", "sarah.j@acmecloud.com", "+1-555-0101", "Technology"));
            Customer c2 = customerRepo.save(new Customer("Michael Zhang", "Apex Global Logistics", "mzhang@apexlogistics.com", "+1-555-0102", "Transportation"));
            Customer c3 = customerRepo.save(new Customer("Elena Rodriguez", "Horizon Health Systems", "erodriguez@horizonhealth.org", "+1-555-0103", "Healthcare"));
            Customer c4 = customerRepo.save(new Customer("David Kim", "Sterling Financial Group", "dkim@sterlingfin.com", "+1-555-0104", "Finance"));

            // Sales Notes
            SalesNote n1 = new SalesNote();
            n1.setTitle("Enterprise SaaS Platform - Discovery Session");
            n1.setContent("Met with VP of Engineering Sarah and CTO. They need to migrate 200+ agents from legacy CRM. Key requirements: offline support, multi-device sync, and custom SQL analytics. Strong intent, budget approved for Q4.");
            n1.setCustomer(c1);
            n1.setCategory(discovery);
            n1.setDealAmount(45000.0);
            n1.setPriority(Priority.HIGH);
            n1.setStatus(NoteStatus.IN_PROGRESS);
            n1.setFollowUpDate(LocalDate.now().plusDays(3));
            n1.setTags("enterprise,q4,high-value");
            noteRepo.save(n1);

            SalesNote n2 = new SalesNote();
            n2.setTitle("Logistics Fleet App Contract Final Review");
            n2.setContent("Walked through final pricing tier with Michael. Agreed on 15% annual commitment discount. Legal team requested small tweak to data retention clause. Expecting signed MSA by Friday.");
            n2.setCustomer(c2);
            n2.setCategory(negotiation);
            n2.setDealAmount(28500.0);
            n2.setPriority(Priority.URGENT);
            n2.setStatus(NoteStatus.FOLLOW_UP_REQUIRED);
            n2.setFollowUpDate(LocalDate.now().plusDays(2));
            n2.setTags("closing,discount,contract");
            noteRepo.save(n2);

            SalesNote n3 = new SalesNote();
            n3.setTitle("HIPAA Compliance Demo & Security Walkthrough");
            n3.setContent("Presented architecture security Whitepaper to Elena and CISO. Satisfied with encryption at rest and in transit. Scheduled pilot kickoff for 50 clinic staff next month.");
            n3.setCustomer(c3);
            n3.setCategory(demo);
            n3.setDealAmount(62000.0);
            n3.setPriority(Priority.HIGH);
            n3.setStatus(NoteStatus.OPEN);
            n3.setFollowUpDate(LocalDate.now().plusDays(7));
            n3.setTags("healthcare,compliance,pilot");
            noteRepo.save(n3);

            SalesNote n4 = new SalesNote();
            n4.setTitle("Financial Advisory Expansion - Proposal Approved");
            n4.setContent("Received green light on Proposal v2. PO issued for initial 100 seats. Setup meeting scheduled for next Monday with the onboarding team.");
            n4.setCustomer(c4);
            n4.setCategory(closing);
            n4.setDealAmount(34000.0);
            n4.setPriority(Priority.MEDIUM);
            n4.setStatus(NoteStatus.WON);
            n4.setFollowUpDate(LocalDate.now().plusDays(5));
            n4.setTags("expansion,signed,won");
            noteRepo.save(n4);

            System.out.println("SalesNotes Data Initializer: Sample customers, categories and notes successfully loaded!");
        };
    }
}
