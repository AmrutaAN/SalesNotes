package com.salesnotes.config;

import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            CustomerRepository customerRepo,
            NoteCategoryRepository categoryRepo,
            SalesNoteRepository noteRepo,
            UserRepository userRepo,
            OrganisationRepository orgRepo,
            TagRepository tagRepo,
            CatalogueItemRepository catalogueRepo,
            ItemPriceTierRepository tierRepo,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // 1. Seed Default Organisation
            Organisation defaultOrg = orgRepo.findBySlug("acme-global").orElseGet(() -> {
                Organisation org = new Organisation("Acme Global Corporation", "acme-global");
                org.setTimezone("Asia/Kolkata");
                org.setCurrencyCode("INR");
                return orgRepo.save(org);
            });

            // 2. Seed Users if not present
            if (userRepo.count() == 0) {
                String adminPass = passwordEncoder.encode("Admin@123");
                String userPass = passwordEncoder.encode("User@123");

                // SYSADMIN
                User sysadmin = new User("sysadmin", "System Administrator", "admin@salesnotes.io", adminPass, UserRole.SYSADMIN);
                sysadmin.setOrganisation(defaultOrg);
                sysadmin.setActive(true);
                userRepo.save(sysadmin);

                // companyadmin
                User companyAdmin = new User("companyadmin", "Alice Johnson (Company Admin)", "alice@acmecloud.com", adminPass, UserRole.COMPANYADMIN);
                companyAdmin.setOrganisation(defaultOrg);
                companyAdmin.setActive(true);
                userRepo.save(companyAdmin);

                // companyemployee
                User companyEmployee = new User("companyemployee", "Rahul Sharma (Field Rep)", "rahul@acmecloud.com", userPass, UserRole.COMPANYEMPLOYEE);
                companyEmployee.setOrganisation(defaultOrg);
                companyEmployee.setActive(true);
                userRepo.save(companyEmployee);

                System.out.println("SalesNotes Data Initializer: Default users seeded (SYSADMIN, companyadmin, companyemployee)!");
            }

            // 3. Seed Default Pointer Tags
            if (tagRepo.count() == 0) {
                Tag tOrder = new Tag(defaultOrg, "ORDER", "Place Order", "#16A34A");
                tOrder.setDescription("Client indicated intent to purchase or reorder items");
                tOrder.setIsSystem(true);
                tOrder.setSortOrder(1);
                tagRepo.save(tOrder);

                Tag tComplaint = new Tag(defaultOrg, "COMPLAINT", "Raise Complaint", "#DC2626");
                tComplaint.setDescription("Client raised dissatisfaction, defect, or shipment delay");
                tComplaint.setIsSystem(true);
                tComplaint.setSortOrder(2);
                tagRepo.save(tComplaint);

                Tag tInfo = new Tag(defaultOrg, "INFO", "Information", "#2563EB");
                tInfo.setDescription("Client preferences, budget cycles, upcoming events, key contacts");
                tInfo.setIsSystem(true);
                tInfo.setSortOrder(3);
                tagRepo.save(tInfo);

                Tag tFollowup = new Tag(defaultOrg, "FOLLOWUP", "Follow Up", "#D97706");
                tFollowup.setDescription("Requires follow-up call, visit, or email with due date");
                tFollowup.setIsSystem(true);
                tFollowup.setSortOrder(4);
                tagRepo.save(tFollowup);

                Tag tPrice = new Tag(defaultOrg, "PRICE", "Pricing Query", "#9333EA");
                tPrice.setDescription("Inquiry regarding volume pricing, tiers, or special discount approvals");
                tPrice.setIsSystem(true);
                tPrice.setSortOrder(5);
                tagRepo.save(tPrice);

                System.out.println("SalesNotes Data Initializer: System tags seeded (ORDER, COMPLAINT, INFO, FOLLOWUP, PRICE)!");
            }

            // 4. Seed Product Catalogue
            if (catalogueRepo.count() == 0) {
                User adminUser = userRepo.findByUsernameIgnoreCase("companyadmin").orElse(null);
                if (adminUser != null) {
                    CatalogueItem item1 = new CatalogueItem(
                            defaultOrg, adminUser, "SKU-FAST-01",
                            "Heavy Duty Industrial Fasteners (Grade 8)", "Box",
                            new BigDecimal("1500.00")
                    );
                    item1.setCategory("Hardware");
                    item1.setDescription("High tensile zinc-plated fasteners for structural assemblies");
                    item1.setDefaultDiscountPct(new BigDecimal("5.00"));
                    item1.setTaxRatePct(new BigDecimal("18.00"));
                    item1.setHasTieredPricing(true);
                    item1 = catalogueRepo.save(item1);

                    tierRepo.save(new ItemPriceTier(item1, 1, 49, new BigDecimal("5.00"), "Standard (1-49 Boxes)"));
                    tierRepo.save(new ItemPriceTier(item1, 50, 99, new BigDecimal("10.00"), "Wholesale (50-99 Boxes)"));
                    tierRepo.save(new ItemPriceTier(item1, 100, null, new BigDecimal("15.00"), "Bulk Volume (100+ Boxes)"));

                    CatalogueItem item2 = new CatalogueItem(
                            defaultOrg, adminUser, "SKU-LUB-02",
                            "Synthetic Heavy Machinery Lubricant 20L", "Bucket",
                            new BigDecimal("4500.00")
                    );
                    item2.setCategory("Chemicals");
                    item2.setDescription("Extended drain interval high-viscosity synthetic gear lubricant");
                    item2.setDefaultDiscountPct(new BigDecimal("8.00"));
                    item2.setTaxRatePct(new BigDecimal("18.00"));
                    item2.setHasTieredPricing(false);
                    catalogueRepo.save(item2);

                    System.out.println("SalesNotes Data Initializer: Catalogue items and price tiers seeded!");
                }
            }

            // 5. Seed Legacy Notes & Customers (if not present)
            if (customerRepo.count() > 0) {
                return;
            }

            NoteCategory discovery = categoryRepo.save(new NoteCategory("Discovery Call", "#3b82f6"));
            NoteCategory demo = categoryRepo.save(new NoteCategory("Product Demo", "#8b5cf6"));
            NoteCategory proposal = categoryRepo.save(new NoteCategory("Proposal Sent", "#f59e0b"));
            NoteCategory negotiation = categoryRepo.save(new NoteCategory("Negotiation", "#ec4899"));
            NoteCategory closing = categoryRepo.save(new NoteCategory("Closing", "#10b981"));

            Customer c1 = customerRepo.save(new Customer("Sarah Jenkins", "Acme Cloud Corp", "sarah.j@acmecloud.com", "+1-555-0101", "Technology"));
            Customer c2 = customerRepo.save(new Customer("Michael Zhang", "Apex Global Logistics", "mzhang@apexlogistics.com", "+1-555-0102", "Transportation"));
            Customer c3 = customerRepo.save(new Customer("Elena Rodriguez", "Horizon Health Systems", "erodriguez@horizonhealth.org", "+1-555-0103", "Healthcare"));
            Customer c4 = customerRepo.save(new Customer("David Kim", "Sterling Financial Group", "dkim@sterlingfin.com", "+1-555-0104", "Finance"));

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
