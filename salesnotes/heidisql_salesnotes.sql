-- ==============================================================================
-- SalesNotes Master Database Script for HeidiSQL (MySQL 8+ / MariaDB 10.5+ / 11+)
-- Project: SalesNotes (Angular + Ionic + Spring Boot)
-- Spec: SalesNotes_Database_Design.docx & SalesNotes_Technical_Architecture.docx
-- Features: 15 Tables, UUID PKs, Tag System, Multi-modal Attachments,
--           Catalogue & Change Log, Clipboard & Export, Voice Transcription.
-- Instructions: Open HeidiSQL -> Select Query Tab -> Paste -> Press F9 (Execute)
-- ==============================================================================

-- 1. Create and select Database
CREATE DATABASE IF NOT EXISTS `salesnotes`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `salesnotes`;

-- 2. Drop existing tables in reverse dependency order (safe re-run)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `voice_transcriptions`;
DROP TABLE IF EXISTS `exports`;
DROP TABLE IF EXISTS `clipboard_entries`;
DROP TABLE IF EXISTS `clipboard_sessions`;
DROP TABLE IF EXISTS `item_change_log`;
DROP TABLE IF EXISTS `item_attachments`;
DROP TABLE IF EXISTS `item_price_tiers`;
DROP TABLE IF EXISTS `catalogue_items`;
DROP TABLE IF EXISTS `log_entry_attachments`;
DROP TABLE IF EXISTS `log_entries`;
DROP TABLE IF EXISTS `tags`;
DROP TABLE IF EXISTS `client_notes`;
DROP TABLE IF EXISTS `clients`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `organisations`;
-- Legacy compatibility cleanup
DROP TABLE IF EXISTS `sales_notes`;
DROP TABLE IF EXISTS `note_categories`;
DROP TABLE IF EXISTS `customers`;
SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================================================
-- 1. Table: organisations (Multi-tenant Tenant Root)
-- ==============================================================================
CREATE TABLE `organisations` (
  `id` CHAR(36) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `slug` VARCHAR(80) NOT NULL,
  `timezone` VARCHAR(60) NOT NULL DEFAULT 'Asia/Kolkata',
  `currency_code` VARCHAR(3) NOT NULL DEFAULT 'INR',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_org_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 1B. Table: roles (Dynamic Roles with Add/Edit capability)
-- ==============================================================================
CREATE TABLE `roles` (
  `id` CHAR(36) NOT NULL,
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL,
  `is_system` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 2. Table: users (Application Users with Roles: SuperUser, Sysadmin, companyadmin, companyemployee)
-- Note: User 'sysadmin' assigned role 'SuperUser'. Contains all requested fields: id, username, fullname, email, password, role,
--       active, createdat, updatedat, plus organization reference.
-- ==============================================================================
CREATE TABLE `users` (
  `id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NULL,
  `username` VARCHAR(100) NOT NULL,
  `fullname` VARCHAR(200) NOT NULL,
  `email` VARCHAR(320) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(30) NULL,
  `device_token` TEXT NULL,
  `last_login_at` DATETIME(3) NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `deleted_at` DATETIME(3) NULL,
  `createdat` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedat` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  INDEX `idx_users_email` (`email`),
  INDEX `idx_users_org` (`organisation_id`),
  INDEX `idx_users_role` (`role`, `organisation_id`),
  CONSTRAINT `fk_users_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 3. Table: clients (Client / Customer Profiles assigned to Sales Reps)
-- ==============================================================================
CREATE TABLE `clients` (
  `id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `assigned_rep_id` CHAR(36) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `company` VARCHAR(200) NULL,
  `phone` VARCHAR(30) NULL,
  `email` VARCHAR(320) NULL,
  `address` TEXT NULL,
  `industry_tag` VARCHAR(80) NULL,
  `priority_flag` TINYINT(1) NOT NULL DEFAULT 0,
  `last_interaction_at` DATETIME(3) NULL,
  `notes_count` INT NOT NULL DEFAULT 0,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `deleted_at` DATETIME(3) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_clients_org` (`organisation_id`),
  INDEX `idx_clients_rep` (`assigned_rep_id`),
  INDEX `idx_clients_name` (`name`, `organisation_id`),
  CONSTRAINT `fk_clients_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_clients_rep` FOREIGN KEY (`assigned_rep_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 4. Table: client_notes (One-to-One Persistent Container per Client)
-- ==============================================================================
CREATE TABLE `client_notes` (
  `id` CHAR(36) NOT NULL,
  `client_id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `entry_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_notes_client` (`client_id`),
  INDEX `idx_client_notes_org` (`organisation_id`),
  CONSTRAINT `fk_cn_client` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cn_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 5. Table: tags (Configurable Pointer Tags: ORDER, COMPLAINT, INFO, FOLLOWUP, PRICE)
-- ==============================================================================
CREATE TABLE `tags` (
  `id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `code` VARCHAR(30) NOT NULL,
  `label` VARCHAR(80) NOT NULL,
  `color_hex` VARCHAR(7) NOT NULL DEFAULT '#1B4F8A',
  `description` TEXT NULL,
  `is_system` TINYINT(1) NOT NULL DEFAULT 0,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `sort_order` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tags_org_code` (`organisation_id`, `code`),
  CONSTRAINT `fk_tags_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 6. Table: log_entries (Atomic Unit of Client Interactions)
-- ==============================================================================
CREATE TABLE `log_entries` (
  `id` CHAR(36) NOT NULL,
  `client_note_id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `created_by_user_id` CHAR(36) NOT NULL,
  `tag_id` CHAR(36) NULL,
  `interaction_type` VARCHAR(25) NOT NULL,
  `interaction_date` DATE NOT NULL,
  `interaction_time` TIME NULL,
  `text_content` TEXT NULL,
  `is_priority` TINYINT(1) NOT NULL DEFAULT 0,
  `has_attachments` TINYINT(1) NOT NULL DEFAULT 0,
  `has_voice_note` TINYINT(1) NOT NULL DEFAULT 0,
  `has_transcription` TINYINT(1) NOT NULL DEFAULT 0,
  `client_temp_id` CHAR(36) NULL,
  `sync_version` BIGINT NOT NULL DEFAULT 1,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `deleted_at` DATETIME(3) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_le_note_date` (`client_note_id`, `interaction_date` DESC),
  INDEX `idx_le_tag` (`tag_id`),
  INDEX `idx_le_user` (`created_by_user_id`),
  INDEX `idx_le_priority` (`is_priority`, `client_note_id`),
  INDEX `idx_le_voice` (`has_voice_note`),
  FULLTEXT KEY `ft_le_text` (`text_content`),
  CONSTRAINT `fk_le_note` FOREIGN KEY (`client_note_id`) REFERENCES `client_notes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_le_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_le_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_le_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 7. Table: log_entry_attachments (Documents, Images, Web Links, and Voice Notes)
-- ==============================================================================
CREATE TABLE `log_entry_attachments` (
  `id` CHAR(36) NOT NULL,
  `log_entry_id` CHAR(36) NOT NULL,
  `uploaded_by_user_id` CHAR(36) NOT NULL,
  `attachment_type` VARCHAR(20) NOT NULL,
  `file_name` VARCHAR(300) NULL,
  `storage_key` TEXT NULL,
  `public_url` TEXT NULL,
  `link_url` TEXT NULL,
  `link_title` VARCHAR(300) NULL,
  `mime_type` VARCHAR(100) NULL,
  `file_size_bytes` BIGINT NULL,
  `duration_seconds` INT NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_lea_entry` (`log_entry_id`),
  INDEX `idx_lea_type` (`attachment_type`, `log_entry_id`),
  CONSTRAINT `fk_lea_entry` FOREIGN KEY (`log_entry_id`) REFERENCES `log_entries` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lea_user` FOREIGN KEY (`uploaded_by_user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 8. Table: catalogue_items (Product Catalogue - Admin Managed Master)
-- ==============================================================================
CREATE TABLE `catalogue_items` (
  `id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `created_by` CHAR(36) NOT NULL,
  `last_updated_by` CHAR(36) NOT NULL,
  `item_code` VARCHAR(80) NOT NULL,
  `item_name` VARCHAR(300) NOT NULL,
  `category` VARCHAR(100) NULL,
  `uom` VARCHAR(40) NOT NULL,
  `description` TEXT NULL,
  `base_price` DECIMAL(14,2) NOT NULL,
  `default_discount_pct` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `net_price` DECIMAL(14,2) NOT NULL DEFAULT 0.00,
  `tax_rate_pct` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `has_tiered_pricing` TINYINT(1) NOT NULL DEFAULT 0,
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `deleted_at` DATETIME(3) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ci_code` (`organisation_id`, `item_code`),
  INDEX `idx_ci_org_active` (`organisation_id`, `is_active`),
  INDEX `idx_ci_category` (`category`, `organisation_id`),
  CONSTRAINT `fk_ci_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_ci_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_ci_updater` FOREIGN KEY (`last_updated_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 9. Table: item_price_tiers (Tiered Discount Rules by Quantity Range)
-- ==============================================================================
CREATE TABLE `item_price_tiers` (
  `id` CHAR(36) NOT NULL,
  `catalogue_item_id` CHAR(36) NOT NULL,
  `min_qty` INT NOT NULL,
  `max_qty` INT NULL,
  `discount_pct` DECIMAL(5,2) NOT NULL,
  `label` VARCHAR(100) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_ipt_item_qty` (`catalogue_item_id`, `min_qty`),
  CONSTRAINT `fk_ipt_item` FOREIGN KEY (`catalogue_item_id`) REFERENCES `catalogue_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 10. Table: item_attachments (Catalogue Item Brochures, Specs & Admin Voice Notes)
-- ==============================================================================
CREATE TABLE `item_attachments` (
  `id` CHAR(36) NOT NULL,
  `catalogue_item_id` CHAR(36) NOT NULL,
  `uploaded_by` CHAR(36) NOT NULL,
  `attachment_type` VARCHAR(20) NOT NULL,
  `file_name` VARCHAR(300) NOT NULL,
  `storage_key` TEXT NOT NULL,
  `public_url` TEXT NULL,
  `mime_type` VARCHAR(100) NULL,
  `file_size_bytes` BIGINT NULL,
  `duration_seconds` INT NULL,
  `update_method` VARCHAR(20) NOT NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_ia_item` (`catalogue_item_id`),
  CONSTRAINT `fk_ia_item` FOREIGN KEY (`catalogue_item_id`) REFERENCES `catalogue_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ia_user` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 11. Table: item_change_log (Immutable, Append-Only Catalogue Audit Trail)
-- ==============================================================================
CREATE TABLE `item_change_log` (
  `id` CHAR(36) NOT NULL,
  `catalogue_item_id` CHAR(36) NOT NULL,
  `changed_by` CHAR(36) NOT NULL,
  `change_type` VARCHAR(30) NOT NULL,
  `field_name` VARCHAR(80) NULL,
  `old_value` TEXT NULL,
  `new_value` TEXT NULL,
  `attachment_id` CHAR(36) NULL,
  `update_method` VARCHAR(20) NOT NULL,
  `notes` TEXT NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_icl_item_date` (`catalogue_item_id`, `created_at` DESC),
  CONSTRAINT `fk_icl_item` FOREIGN KEY (`catalogue_item_id`) REFERENCES `catalogue_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_icl_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 12. Table: clipboard_sessions (Temporary Staging Session per User)
-- ==============================================================================
CREATE TABLE `clipboard_sessions` (
  `id` CHAR(36) NOT NULL,
  `user_id` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'active',
  `entry_count` INT NOT NULL DEFAULT 0,
  `expires_at` DATETIME(3) NULL,
  `cleared_at` DATETIME(3) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updated_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cs_user` (`user_id`),
  INDEX `idx_cs_org` (`organisation_id`),
  CONSTRAINT `fk_cs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cs_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 13. Table: clipboard_entries (Non-Destructive Staged Log Entries)
-- ==============================================================================
CREATE TABLE `clipboard_entries` (
  `id` CHAR(36) NOT NULL,
  `clipboard_session_id` CHAR(36) NOT NULL,
  `log_entry_id` CHAR(36) NOT NULL,
  `staged_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ce_session_entry` (`clipboard_session_id`, `log_entry_id`),
  INDEX `idx_ce_session` (`clipboard_session_id`),
  CONSTRAINT `fk_ce_session` FOREIGN KEY (`clipboard_session_id`) REFERENCES `clipboard_sessions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ce_log_entry` FOREIGN KEY (`log_entry_id`) REFERENCES `log_entries` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 14. Table: exports (Clipboard Export Records to Sales Order / Complaint Modules)
-- ==============================================================================
CREATE TABLE `exports` (
  `id` CHAR(36) NOT NULL,
  `clipboard_session_id` CHAR(36) NOT NULL,
  `exported_by` CHAR(36) NOT NULL,
  `organisation_id` CHAR(36) NOT NULL,
  `target_module` VARCHAR(30) NOT NULL,
  `external_ref_id` VARCHAR(200) NULL,
  `export_payload` JSON NOT NULL,
  `entry_count` INT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending',
  `error_message` TEXT NULL,
  `exported_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_exp_session` (`clipboard_session_id`),
  INDEX `idx_exp_user` (`exported_by`),
  INDEX `idx_exp_target` (`target_module`, `organisation_id`),
  CONSTRAINT `fk_exp_session` FOREIGN KEY (`clipboard_session_id`) REFERENCES `clipboard_sessions` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_exp_user` FOREIGN KEY (`exported_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_exp_org` FOREIGN KEY (`organisation_id`) REFERENCES `organisations` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- 15. Table: voice_transcriptions (Audio-to-Text Pipeline with Confirmation Step)
-- ==============================================================================
CREATE TABLE `voice_transcriptions` (
  `id` CHAR(36) NOT NULL,
  `requested_by` CHAR(36) NOT NULL,
  `log_entry_attachment_id` CHAR(36) NULL,
  `item_attachment_id` CHAR(36) NULL,
  `status` VARCHAR(30) NOT NULL DEFAULT 'pending',
  `raw_transcript` TEXT NULL,
  `edited_transcript` TEXT NULL,
  `final_transcript` TEXT NULL,
  `source_language_hint` VARCHAR(20) NULL,
  `ai_model_used` VARCHAR(100) NULL,
  `processing_started_at` DATETIME(3) NULL,
  `processing_completed_at` DATETIME(3) NULL,
  `confirmed_at` DATETIME(3) NULL,
  `discarded_at` DATETIME(3) NULL,
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  INDEX `idx_vt_lea` (`log_entry_attachment_id`),
  INDEX `idx_vt_ia` (`item_attachment_id`),
  INDEX `idx_vt_status_date` (`status`, `created_at`),
  CONSTRAINT `fk_vt_user` FOREIGN KEY (`requested_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_vt_lea` FOREIGN KEY (`log_entry_attachment_id`) REFERENCES `log_entry_attachments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_vt_ia` FOREIGN KEY (`item_attachment_id`) REFERENCES `item_attachments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================================
-- SEED DATA (Organizations, Users, Tags, Clients, Catalogue & Sample Notes)
-- ==============================================================================

-- 1. Default Tenant Organization
INSERT INTO `organisations` (`id`, `name`, `slug`, `timezone`, `currency_code`, `is_active`)
VALUES ('11111111-1111-1111-1111-111111111111', 'Acme Global Corporation', 'acme-global', 'Asia/Kolkata', 'INR', 1);

-- 1B. Seed Default System Roles (SuperUser, Sysadmin, companyadmin, companyemployee)
INSERT INTO `roles` (`id`, `code`, `name`, `description`, `is_system`) VALUES
('00000000-0000-0000-0000-000000000001', 'SuperUser', 'Super User', 'Platform Master Administrator with full access across all operations and organisations', 1),
('00000000-0000-0000-0000-000000000002', 'Sysadmin', 'System Administrator', 'System configuration, platform maintenance, and tenant administration', 1),
('00000000-0000-0000-0000-000000000003', 'companyadmin', 'Company Administrator', 'Company-level user management, catalogue pricing, and pointer tag configuration', 1),
('00000000-0000-0000-0000-000000000004', 'companyemployee', 'Company Employee', 'Field sales rep managing client interactions, voice notes, and clipboard exports', 1);

-- 2. Seed Users (sysadmin as SuperUser, companyadmin, companyemployee)
INSERT INTO `users` (`id`, `organisation_id`, `username`, `fullname`, `email`, `password`, `role`, `active`) VALUES
('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111111', 'sysadmin', 'System SuperUser Administrator', 'sysadmin@salesnotes.local', '$2a$10$wE9l1l.n2YfVz2Y9iN8vK.cT.1n7Z4t3n1Y5J3z7t9r1w2e3r4t5y', 'SuperUser', 1),
('22222222-2222-2222-2222-222222222202', '11111111-1111-1111-1111-111111111111', 'companyadmin', 'Acme Company Admin', 'admin@acmeglobal.com', '$2a$10$wE9l1l.n2YfVz2Y9iN8vK.cT.1n7Z4t3n1Y5J3z7t9r1w2e3r4t5y', 'companyadmin', 1),
('22222222-2222-2222-2222-222222222203', '11111111-1111-1111-1111-111111111111', 'companyemployee', 'Rahul Sharma (Field Rep)', 'rahul@acmeglobal.com', '$2a$10$wE9l1l.n2YfVz2Y9iN8vK.cT.1n7Z4t3n1Y5J3z7t9r1w2e3r4t5y', 'companyemployee', 1);

-- 3. Seed Default System Pointer Tags
INSERT INTO `tags` (`id`, `organisation_id`, `code`, `label`, `color_hex`, `description`, `is_system`, `is_active`, `sort_order`) VALUES
('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', 'ORDER', 'Place Order', '#16A34A', 'Client indicated intent to purchase or reorder items', 1, 1, 1),
('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111', 'COMPLAINT', 'Raise Complaint', '#DC2626', 'Client raised dissatisfaction, defect, or shipment delay', 1, 1, 2),
('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', 'INFO', 'Information', '#2563EB', 'Client preferences, budget cycles, upcoming events, key contacts', 1, 1, 3),
('33333333-3333-3333-3333-333333333304', '11111111-1111-1111-1111-111111111111', 'FOLLOWUP', 'Follow Up', '#D97706', 'Requires follow-up call, visit, or email with due date', 1, 1, 4),
('33333333-3333-3333-3333-333333333305', '11111111-1111-1111-1111-111111111111', 'PRICE', 'Pricing Query', '#9333EA', 'Inquiry regarding volume pricing, tiers, or special discount approvals', 1, 1, 5);

-- 4. Seed Sample Clients
INSERT INTO `clients` (`id`, `organisation_id`, `assigned_rep_id`, `name`, `company`, `phone`, `email`, `address`, `industry_tag`, `priority_flag`, `notes_count`, `last_interaction_at`) VALUES
('44444444-4444-4444-4444-444444444401', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203', 'Rajesh Patel', 'Akash Traders', '+91 98230 11223', 'rajesh@akashtraders.com', 'Plot 45, MIDC Industrial Area, Pune', 'Wholesale Hardware', 1, 2, NOW(3)),
('44444444-4444-4444-4444-444444444402', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203', 'Sneha Rao', 'Zenith Logistics Ltd', '+91 98900 44556', 'sneha.rao@zenithlog.com', 'Tower B, Tech Park, Bengaluru', 'Supply Chain', 0, 1, NOW(3));

-- 5. Seed Client Note Containers
INSERT INTO `client_notes` (`id`, `client_id`, `organisation_id`, `entry_count`) VALUES
('55555555-5555-5555-5555-555555555501', '44444444-4444-4444-4444-444444444401', '11111111-1111-1111-1111-111111111111', 2),
('55555555-5555-5555-5555-555555555502', '44444444-4444-4444-4444-444444444402', '11111111-1111-1111-1111-111111111111', 1);

-- 6. Seed Sample Log Entries
INSERT INTO `log_entries` (`id`, `client_note_id`, `organisation_id`, `created_by_user_id`, `tag_id`, `interaction_type`, `interaction_date`, `text_content`, `is_priority`, `has_attachments`) VALUES
('66666666-6666-6666-6666-666666666601', '55555555-5555-5555-5555-555555555501', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203', '33333333-3333-3333-3333-333333333301', 'field_visit', CURDATE(), 'Client confirmed intent to purchase 50 units of Heavy Duty Fasteners. Requested dispatch before Friday.', 1, 0),
('66666666-6666-6666-6666-666666666602', '55555555-5555-5555-5555-555555555501', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203', '33333333-3333-3333-3333-333333333302', 'phone_call', CURDATE(), 'Previous consignment had 2 boxes with damaged packaging. Transferred complaint to warehouse team.', 0, 0),
('66666666-6666-6666-6666-666666666603', '55555555-5555-5555-5555-555555555502', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222203', '33333333-3333-3333-3333-333333333303', 'video_call', CURDATE(), 'Quarterly review completed. Planning warehouse expansion in Q3.', 0, 0);

-- 7. Seed Product Catalogue Items
INSERT INTO `catalogue_items` (`id`, `organisation_id`, `created_by`, `last_updated_by`, `item_code`, `item_name`, `category`, `uom`, `description`, `base_price`, `default_discount_pct`, `net_price`, `tax_rate_pct`, `has_tiered_pricing`, `is_active`) VALUES
('77777777-7777-7777-7777-777777777701', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222202', '22222222-2222-2222-2222-222222222202', 'SKU-FAST-01', 'Heavy Duty Industrial Fasteners (Grade 8)', 'Hardware', 'Box', 'High tensile zinc-plated fasteners for structural assemblies', 1500.00, 5.00, 1425.00, 18.00, 1, 1),
('77777777-7777-7777-7777-777777777702', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222202', '22222222-2222-2222-2222-222222222202', 'SKU-LUB-02', 'Synthetic Heavy Machinery Lubricant 20L', 'Chemicals', 'Bucket', 'Extended drain interval high-viscosity synthetic gear lubricant', 4500.00, 8.00, 4140.00, 18.00, 0, 1);

-- 8. Seed Tiered Pricing for SKU-FAST-01
INSERT INTO `item_price_tiers` (`id`, `catalogue_item_id`, `min_qty`, `max_qty`, `discount_pct`, `label`) VALUES
('88888888-8888-8888-8888-888888888801', '77777777-7777-7777-7777-777777777701', 1, 49, 5.00, 'Standard (1-49 Boxes)'),
('88888888-8888-8888-8888-888888888802', '77777777-7777-7777-7777-777777777701', 50, 99, 10.00, 'Wholesale (50-99 Boxes)'),
('88888888-8888-8888-8888-888888888803', '77777777-7777-7777-7777-777777777701', 100, NULL, 15.00, 'Bulk Volume (100+ Boxes)');

-- 9. Initial Change Log Entry for Catalogue Creation
INSERT INTO `item_change_log` (`id`, `catalogue_item_id`, `changed_by`, `change_type`, `field_name`, `old_value`, `new_value`, `update_method`, `notes`) VALUES
('99999999-9999-9999-9999-999999999901', '77777777-7777-7777-7777-777777777701', '22222222-2222-2222-2222-222222222202', 'item_created', 'ALL', NULL, 'Initial SKU creation with tiered discounts', 'manual', 'Launched with FY26 catalog');
