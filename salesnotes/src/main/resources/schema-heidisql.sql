-- ==============================================================================
-- SalesNotes Database Script for HeidiSQL (MySQL / MariaDB / Compatible SQL)
-- Project: SalesNotes (Angular + Ionic + Spring Boot)
-- Compatible with: MySQL 5.7+, MySQL 8.0+, MariaDB 10.3+
-- Instructions: Open HeidiSQL -> Query Tab -> Paste -> Press F9 (Execute)
-- ==============================================================================

-- 1. Create and select Database
CREATE DATABASE IF NOT EXISTS `salesnotes`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `salesnotes`;

-- 2. Drop existing tables in reverse dependency order (safe re-run)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `sales_notes`;
DROP TABLE IF EXISTS `note_categories`;
DROP TABLE IF EXISTS `customers`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- Table: users
-- Columns: id, username, fullname, email, password, role, active, createdat, updatedat
-- Roles: SYSADMIN, companyadmin, companyemployee
-- ==============================================================================
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `fullname` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `createdat` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedat` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  KEY `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- Table: customers
-- Columns: id, name, company, email, phone, industry, created_at
-- ==============================================================================
CREATE TABLE `customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `company` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NULL,
  `phone` VARCHAR(50) NULL,
  `industry` VARCHAR(100) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- Table: note_categories
-- Columns: id, name, color
-- ==============================================================================
CREATE TABLE `note_categories` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `color` VARCHAR(50) NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categories_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- Table: sales_notes
-- Columns: id, title, content, customer_id, category_id, deal_amount, priority,
--          status, follow_up_date, tags, created_at, updated_at
-- ==============================================================================
CREATE TABLE `sales_notes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `content` TEXT NULL,
  `customer_id` BIGINT NOT NULL,
  `category_id` BIGINT NULL,
  `deal_amount` DOUBLE NULL,
  `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  `status` VARCHAR(30) NOT NULL DEFAULT 'OPEN',
  `follow_up_date` DATE NULL,
  `tags` VARCHAR(255) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_notes_customer` (`customer_id`),
  KEY `fk_notes_category` (`category_id`),
  CONSTRAINT `fk_notes_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notes_category` FOREIGN KEY (`category_id`) REFERENCES `note_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- SEED DATA
-- ==============================================================================

-- 1. Seed Users (SYSADMIN, companyadmin, companyemployee)
INSERT INTO `users` (`username`, `fullname`, `email`, `password`, `role`, `active`, `createdat`) VALUES
('sysadmin', 'System Administrator', 'admin@salesnotes.io', 'Admin@123', 'SYSADMIN', 1, NOW()),
('companyadmin', 'Alice Johnson (Company Admin)', 'alice@acmecloud.com', 'Admin@123', 'companyadmin', 1, NOW()),
('companyemployee', 'Bob Miller (Sales Rep)', 'bob@acmecloud.com', 'User@123', 'companyemployee', 1, NOW());

-- 2. Seed Note Categories
INSERT INTO `note_categories` (`id`, `name`, `color`) VALUES
(1, 'Discovery Call', '#3b82f6'),
(2, 'Product Demo', '#8b5cf6'),
(3, 'Proposal Sent', '#f59e0b'),
(4, 'Negotiation', '#ec4899'),
(5, 'Closing', '#10b981');

-- 3. Seed Customers
INSERT INTO `customers` (`id`, `name`, `company`, `email`, `phone`, `industry`, `created_at`) VALUES
(1, 'Sarah Jenkins', 'Acme Cloud Corp', 'sarah.j@acmecloud.com', '+1-555-0101', 'Technology', NOW()),
(2, 'Michael Zhang', 'Apex Global Logistics', 'mzhang@apexlogistics.com', '+1-555-0102', 'Transportation', NOW()),
(3, 'Elena Rodriguez', 'Horizon Health Systems', 'erodriguez@horizonhealth.org', '+1-555-0103', 'Healthcare', NOW()),
(4, 'David Kim', 'Sterling Financial Group', 'dkim@sterlingfin.com', '+1-555-0104', 'Finance', NOW());

-- 4. Seed Sales Notes
INSERT INTO `sales_notes` (`title`, `content`, `customer_id`, `category_id`, `deal_amount`, `priority`, `status`, `follow_up_date`, `tags`, `created_at`) VALUES
('Enterprise SaaS Platform - Discovery Session',
 'Met with VP of Engineering Sarah and CTO. They need to migrate 200+ agents from legacy CRM. Key requirements: offline support, multi-device sync, and custom SQL analytics. Strong intent, budget approved for Q4.',
 1, 1, 45000.00, 'HIGH', 'IN_PROGRESS', DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), 'enterprise,q4,high-value', NOW()),

('Logistics Fleet App Contract Final Review',
 'Walked through final pricing tier with Michael. Agreed on 15% annual commitment discount. Legal team requested small tweak to data retention clause. Expecting signed MSA by Friday.',
 2, 4, 28500.00, 'URGENT', 'FOLLOW_UP_REQUIRED', DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), 'closing,discount,contract', NOW()),

('HIPAA Compliance Demo & Security Walkthrough',
 'Presented architecture security Whitepaper to Elena and CISO. Satisfied with encryption at rest and in transit. Scheduled pilot kickoff for 50 clinic staff next month.',
 3, 2, 62000.00, 'HIGH', 'OPEN', DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 'healthcare,compliance,pilot', NOW()),

('Financial Advisory Expansion - Proposal Approved',
 'Received green light on Proposal v2. PO issued for initial 100 seats. Setup meeting scheduled for next Monday with the onboarding team.',
 4, 5, 34000.00, 'MEDIUM', 'WON', DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), 'expansion,signed,won', NOW());

-- ==============================================================================
-- Verification Queries (View results in HeidiSQL)
-- ==============================================================================
SELECT 'Users Table:' AS '--- TABLE ---';
SELECT `id`, `username`, `fullname`, `email`, `role`, `active`, `createdat` FROM `users`;

SELECT 'Customers Table:' AS '--- TABLE ---';
SELECT `id`, `company`, `name`, `email`, `phone`, `industry` FROM `customers`;

SELECT 'Sales Notes Table:' AS '--- TABLE ---';
SELECT `id`, `title`, `customer_id`, `deal_amount`, `priority`, `status`, `follow_up_date` FROM `sales_notes`;
