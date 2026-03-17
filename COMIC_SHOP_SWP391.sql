USE [master]
GO
/****** Object:  Database [COMIC_SHOP_HSF302] Script Date: 12/22/2025 ******/
CREATE DATABASE [COMIC_SHOP_HSF302]
 CONTAINMENT = NONE
   ON PRIMARY
( NAME = N'COMIC_SHOP_HSF302', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\COMIC_SHOP_HSF302.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON
( NAME = N'COMIC_SHOP_HSF302_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.SQLEXPRESS\MSSQL\DATA\COMIC_SHOP_HSF302_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [COMIC_SHOP_HSF302].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ANSI_NULLS OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ANSI_PADDING OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ARITHABORT OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET DISABLE_BROKER
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET RECOVERY SIMPLE
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET MULTI_USER
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET DB_CHAINING OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET TARGET_RECOVERY_TIME = 60 SECONDS
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET DELAYED_DURABILITY = DISABLED
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET ACCELERATED_DATABASE_RECOVERY = OFF
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET QUERY_STORE = OFF
GO
USE [COMIC_SHOP_HSF302]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[account] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[account](
[id] [int] IDENTITY(1,1) NOT NULL,
[username] [varchar](45) UNIQUE NULL, --UNIQUE
[password_hash] [varchar](255) NOT NULL,
[email] [varchar](255) UNIQUE NOT NULL, --UNIQUE
[role] [varchar](20) NOT NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_account] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[admin] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[admin](
[id_admin] [int] IDENTITY(1,1) NOT NULL,
[account_id] [int] NULL,
[name] [varchar](100) NULL,
[address] [nvarchar](255) NULL,
[phone] [varchar](45) NULL,
 CONSTRAINT [PK_admin] PRIMARY KEY CLUSTERED
(
[id_admin] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[user] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[user](
[id_user] [int] IDENTITY(1,1) NOT NULL,
[account_id] [int] NULL,
[name] [varchar](100) NULL,
[address] [nvarchar](255) NULL,
[phone] [varchar](45) NULL,
 CONSTRAINT [PK_user] PRIMARY KEY CLUSTERED
(
[id_user] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[author] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[author](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](100) NULL,
[description] [varchar](255) NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_author] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[category] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[category](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](100) NULL,
[parent_id] [int] NULL,
[level] [int] NULL DEFAULT 0,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_category] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[book] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[book](
[id] [int] IDENTITY(1,1) NOT NULL,
[author_id] [int] NULL,
[category_id] [int] NULL,
[title] [varchar](100) NULL,
[date] [date] NULL,
[description] [varchar](255) NULL,
[image] [varchar](255) NULL,
[price] DECIMAL(10, 3) NULL,
[number_page] [int] NULL,
[number_sold] [int] NULL DEFAULT 0,
[number_stock] [int] NULL,
[size] [varchar](50) NULL,
[language_id] [int] NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_book] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[publisher] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[language](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](50) NOT NULL UNIQUE,
[code] [varchar](10) NOT NULL UNIQUE, -- ISO 639-1 code (e.g., 'en', 'ja', 'vi')
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_language] PRIMARY KEY CLUSTERED ([id] ASC),
CONSTRAINT [UQ_language_code] UNIQUE ([code]),
CONSTRAINT [UQ_language_name] UNIQUE ([name])
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[publisher] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[publisher](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](100) NOT NULL,
[country] [nvarchar](100) NULL,
[website] [varchar](255) NULL,
[description] [nvarchar](500) NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_publisher] PRIMARY KEY CLUSTERED ([id] ASC)
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[translator] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[translator](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](100) NOT NULL,
[bio] [varchar](500) NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_translator] PRIMARY KEY CLUSTERED ([id] ASC)
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[series] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[series](
[id] [int] IDENTITY(1,1) NOT NULL,
[name] [nvarchar](100) NOT NULL,
[description] [nvarchar](500) NULL,
[total_volumes] [int] NULL,
[status] [varchar](20) NULL, -- 'ONGOING', 'COMPLETED', 'DISCONTINUED'
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_series] PRIMARY KEY CLUSTERED ([id] ASC)
) ON [PRIMARY]
GO
CREATE TABLE [dbo].[book_translator](
[id] [int] IDENTITY(1,1) NOT NULL,
[book_id] [int] NOT NULL,
[translator_id] [int] NOT NULL,
[role] [varchar](50) NULL, -- 'Main Translator', 'Co-Translator', 'Editor'
[created_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_book_translator] PRIMARY KEY CLUSTERED ([id] ASC),
CONSTRAINT [UQ_book_translator] UNIQUE ([book_id], [translator_id])
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[cart_item] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[cart_item](
[id] [int] IDENTITY(1,1) NOT NULL,
[user_id] [int] NOT NULL,
[book_id] [int] NOT NULL,
[quantity] [int] NOT NULL DEFAULT 0,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
CONSTRAINT [PK_cart_item] PRIMARY KEY CLUSTERED ([id] ASC),
CONSTRAINT [FK_cart_item_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id_user])
ON DELETE CASCADE,
CONSTRAINT [FK_cart_item_book] FOREIGN KEY([book_id])
REFERENCES [dbo].[book] ([id]),
CONSTRAINT [UQ_cart_item_user_book] UNIQUE ([user_id], [book_id])
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[comment] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[comment](
[id] [int] IDENTITY(1,1) NOT NULL,
[user_id] [int] NULL,
[book_id] [int] NULL,
[order_detail_id] [int] NULL UNIQUE,
[star] [int] NULL CHECK ([star] BETWEEN 1 AND 5),
[content] [nvarchar](255) NULL,
[status] VARCHAR(20) NOT NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_comment] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[order] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[order](
[id] [int] IDENTITY(1,1) NOT NULL,
[user_id] [int] NULL,
[voucher_id] [int] NULL,
[admin_id] [int] NULL,
[total_cost] DECIMAL(10, 3) NULL,
[customer_name] [varchar](100) NULL,
[address] [nvarchar](255) NULL,
[phone] [varchar](45) NULL,
[shipping_fee] DECIMAL(10, 3) NOT NULL,
[discount_amount] DECIMAL(10, 3) DEFAULT 0,
[status] [varchar](20) NULL,
[payment_method] VARCHAR(20) DEFAULT 'COD',
[payment_status] VARCHAR(20) DEFAULT 'UNPAID',
[payment_note] NVARCHAR(255) NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_order] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_order] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/****** Object:  Table [dbo].[order_detail] Script Date: 12/22/2025 ******/
CREATE TABLE [dbo].[order_detail](
[id] [int] IDENTITY(1,1) NOT NULL,
[order_id] [int] NULL,
[book_id] [int] NULL,
[number] [int] NULL,
[total_cost] DECIMAL(10, 3) NULL,
[created_at] [datetime] NULL DEFAULT GETDATE(),
[updated_at] [datetime] NULL DEFAULT GETDATE(),
 CONSTRAINT [PK_order_detail] PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[voucher] (
[voucher_id] INT IDENTITY(1,1) PRIMARY KEY,
[code] VARCHAR(50) NOT NULL UNIQUE,
[name] VARCHAR(100) NOT NULL,
[discount_value] DECIMAL(10, 3) NOT NULL,
[start_date] DATE NOT NULL,
[end_date] DATE NOT NULL,
[max_usage] INT NOT NULL,
[used_count] INT DEFAULT 0,
[min_order] DECIMAL(10, 3) DEFAULT 0,
[status] VARCHAR(20) NOT NULL
);
GO
GO
-- Thêm ràng buộc UNIQUE cho username trong account
ALTER TABLE [dbo].[account] ADD CONSTRAINT [UQ_account_username] UNIQUE ([username]);
GO
-- Thêm khóa ngoại cho admin
ALTER TABLE [dbo].[admin] WITH CHECK ADD CONSTRAINT [FK_admin_account] 
FOREIGN KEY([account_id]) 
REFERENCES [dbo].[account] ([id])
GO
-- Thêm khóa ngoại cho user
ALTER TABLE [dbo].[user] WITH CHECK ADD CONSTRAINT [FK_user_account] 
FOREIGN KEY([account_id]) 
REFERENCES [dbo].[account] ([id])
GO
-- Thêm khóa ngoại cho comment
ALTER TABLE [dbo].[comment] WITH CHECK ADD CONSTRAINT [FK_comment_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id_user])
GO
ALTER TABLE [dbo].[comment] CHECK CONSTRAINT [FK_comment_user]
GO
ALTER TABLE [dbo].[category]
WITH CHECK ADD CONSTRAINT [FK_category_parent]
FOREIGN KEY([parent_id])
REFERENCES [dbo].[category] ([id]);
GO
ALTER TABLE [dbo].[category] CHECK CONSTRAINT [FK_category_parent];
GO
ALTER TABLE [dbo].[comment] WITH CHECK ADD CONSTRAINT [FK_comment_book] FOREIGN KEY([book_id])
REFERENCES [dbo].[book] ([id])
GO
ALTER TABLE [dbo].[comment] WITH CHECK ADD CONSTRAINT [FK_comment_order_detail] 
FOREIGN KEY([order_detail_id])
REFERENCES [dbo].[order_detail] ([id])
GO
ALTER TABLE [dbo].[comment] CHECK CONSTRAINT [FK_comment_order_detail]
GO
ALTER TABLE [dbo].[book]
ADD [publisher_id] [int] NULL,
    [series_id] [int] NULL,
    [volume_number] [int] NULL;
GO
ALTER TABLE [dbo].[order_detail]
ADD [series_id] INT NULL,
    [is_full_series] BIT DEFAULT 0;
GO
ALTER TABLE [dbo].[order_detail]
WITH CHECK ADD CONSTRAINT [FK_order_detail_series]
FOREIGN KEY([series_id])
REFERENCES [dbo].[series] ([id]);
GO

ALTER TABLE [dbo].[order_detail] CHECK CONSTRAINT [FK_order_detail_series];
GO
ALTER TABLE [dbo].[book]
WITH CHECK ADD CONSTRAINT [FK_book_publisher]
FOREIGN KEY([publisher_id])
REFERENCES [dbo].[publisher] ([id])
GO
ALTER TABLE [dbo].[book] CHECK CONSTRAINT [FK_book_publisher]
GO
ALTER TABLE [dbo].[book]
WITH CHECK ADD CONSTRAINT [FK_book_series]
FOREIGN KEY([series_id])
REFERENCES [dbo].[series] ([id])
GO
ALTER TABLE [dbo].[book]
WITH CHECK ADD CONSTRAINT [FK_book_language]
FOREIGN KEY([language_id])
REFERENCES [dbo].[language] ([id])
GO
ALTER TABLE [dbo].[book] CHECK CONSTRAINT [FK_book_language]
GO
ALTER TABLE [dbo].[book] CHECK CONSTRAINT [FK_book_series]
GO
ALTER TABLE [dbo].[book_translator]
WITH CHECK ADD CONSTRAINT [FK_book_translator_book]
FOREIGN KEY([book_id])
REFERENCES [dbo].[book] ([id])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[book_translator] CHECK CONSTRAINT [FK_book_translator_book]
GO
ALTER TABLE [dbo].[book_translator]
WITH CHECK ADD CONSTRAINT [FK_book_translator_translator]
FOREIGN KEY([translator_id])
REFERENCES [dbo].[translator] ([id])
GO
ALTER TABLE [dbo].[book_translator] CHECK CONSTRAINT [FK_book_translator_translator]
GO
ALTER TABLE [dbo].[series]
ADD CONSTRAINT [CK_series_status]
CHECK ([status] IN ('ONGOING', 'COMPLETED', 'DISCONTINUED'));
GO
ALTER TABLE [dbo].[cart_item]
ADD CONSTRAINT [CK_cart_item_quantity] CHECK ([quantity] > 0);
GO
ALTER TABLE [dbo].[comment] CHECK CONSTRAINT [FK_comment_book]
GO
-- Thêm khóa ngoại cho order
ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [FK_order_user] FOREIGN KEY([user_id])
REFERENCES [dbo].[user] ([id_user])
GO
ALTER TABLE [dbo].[book] ADD weight_kg DECIMAL(10, 2) NULL;
GO
ALTER TABLE [dbo].[order] ADD shipping_method VARCHAR(50) DEFAULT 'STANDARD' NULL;
GO
-- Add constraint to ensure shipping_fee is positive
ALTER TABLE [dbo].[order]
ADD CONSTRAINT [CK_order_shipping_fee]
CHECK ([shipping_fee] >= 0);
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_user]
GO
ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [FK_order_voucher]
FOREIGN KEY([voucher_id])
REFERENCES [dbo].[voucher]([voucher_id]);
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_voucher];
GO
-- Thêm constraint CHECK để ràng buộc giá trị
ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [CK_order_shipping_method] 
CHECK ([shipping_method] IN ('STANDARD', 'EXPRESS', 'SAME-DAY'));
GO
ALTER TABLE [dbo].[comment]
ADD CONSTRAINT CK_comment_status
CHECK (status IN ('PENDING', 'APPROVED', 'HIDDEN'));
ALTER TABLE book
ADD CONSTRAINT CK_book_weight
CHECK (weight_kg > 0 AND weight_kg <= 10);
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [CK_order_shipping_method];
GO
ALTER TABLE [dbo].[order]
WITH CHECK ADD CONSTRAINT [FK_order_admin]
FOREIGN KEY([admin_id])
REFERENCES [dbo].[admin] ([id_admin]);
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_admin];
-- Thêm khóa ngoại cho order_detail
ALTER TABLE [dbo].[order_detail] WITH CHECK ADD CONSTRAINT [FK_order_detail_order] FOREIGN KEY([order_id])
REFERENCES [dbo].[order] ([id])
GO
ALTER TABLE [dbo].[order_detail] CHECK CONSTRAINT [FK_order_detail_order]
GO
ALTER TABLE [dbo].[order_detail] WITH CHECK ADD CONSTRAINT [FK_order_detail_book] FOREIGN KEY([book_id])
REFERENCES [dbo].[book] ([id])
GO
ALTER TABLE [dbo].[order_detail] CHECK CONSTRAINT [FK_order_detail_book]
GO
ALTER TABLE [dbo].[voucher]
ADD CONSTRAINT [CK_Promotion_Status]
CHECK ([status] IN ('CREATED','ACTIVE', 'EXPIRED'));
GO
ALTER TABLE [dbo].[voucher]
ADD CONSTRAINT [CK_Promotion_Usage]
CHECK ([used_count] <= [max_usage]);
GO
ALTER DATABASE [COMIC_SHOP_HSF302] SET READ_WRITE
GO
USE [COMIC_SHOP_HSF302]
GO
-- Bật IDENTITY_INSERT cho account để chèn ID cụ thể
SET IDENTITY_INSERT [dbo].[account] ON;
GO
INSERT INTO [dbo].[account] (id, username, password_hash, email, role, created_at, updated_at)
VALUES
(1, 'admin01', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'admin01@comicshop.com', 'ADMIN', '2026-02-01', '2026-02-01'),
(2, 'john', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'john@example.com', 'USER', '2026-02-01', '2026-02-01'),
(3, 'emma_reader', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'emma@example.com', 'USER', '2026-02-01', '2026-02-01'),
(4, 'watson_newton', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'watson@example.com', 'USER', '2026-02-01', '2026-02-01'),
(5, 'emma_dasha', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'dasha@example.com', 'USER', '2026-02-01', '2026-02-01'),
(6, 'staff01', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'staff01@comicshop.com', 'STAFF', '2026-02-01', '2026-02-01'),
(7, 'staff02', '$2a$12$g/Ee8Hm1g1/5spm2V733euokwUNVkOYqLvX8YPCcYBIQohj79L5KO', 'staff02@comicshop.com', 'STAFF', '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[account] OFF;
GO
-- Chèn admin/staff sau khi account đã có dữ liệu
SET IDENTITY_INSERT [dbo].[admin] ON;
GO
INSERT INTO [dbo].[admin] (id_admin, account_id, name, address, phone)
VALUES
(1, 1, 'Admin', '123 Admin Street', '0888999999'),
(2, 6, 'Minaru', '456 Staff Street', '0888111111'),
(3, 7, 'Takashi', '789 Staff Avenue', '0888222222');
GO
SET IDENTITY_INSERT [dbo].[admin] OFF;
GO
-- Chèn user sau khi account đã có dữ liệu
SET IDENTITY_INSERT [dbo].[user] ON;
GO
INSERT INTO [dbo].[user] (id_user, account_id, name, address, phone)
VALUES
(1, 2, 'John Doe', '45 Green Avenue', '0999999999'),
(2, 3, 'Emma Watson', '77 Maple Road', '0999889999'),
(3, 4, 'Watson Newton', '37 Maple Road', '0999883399'),
(4, 5, 'Emma Dasha', '97 Maple Road', '0999881999');
GO
SET IDENTITY_INSERT [dbo].[user] OFF;
GO
SET IDENTITY_INSERT [dbo].[category] ON;
GO
-- Level 1: 3 Main parent categories (parent_id = NULL)
INSERT INTO [dbo].[category] (id, name, parent_id, level, created_at, updated_at)
VALUES
-- LEVEL 1
(1, N'Domestic Comics', NULL, 0, '2026-02-01', '2026-02-01'),
(2, N'International Comics', NULL, 0, '2026-02-01', '2026-02-01'),
(3, N'Japanese Manga', NULL, 0, '2026-02-01', '2026-02-01'),

-- LEVEL 2: Domestic Comics
(11, N'Vietnamese Comics', 1, 1, '2026-02-01', '2026-02-01'),
(12, N'Multicultural', 1, 1, '2026-02-01', '2026-02-01'),
(13, N'Life Skills', 1, 1, '2026-02-01', '2026-02-01'),
(14, N'Vietnamese Children''s', 1, 1, '2026-02-01', '2026-02-01'),
(15, N'History & Society', 1, 1, '2026-02-01', '2026-02-01'),

-- LEVEL 2: International Comics
(21, N'Detective', 2, 1, '2026-02-01', '2026-02-01'),
(22, N'Graphic Detective Stories', 2, 1, '2026-02-01', '2026-02-01'),
(23, N'Science & Technology', 2, 1, '2026-02-01', '2026-02-01'),
(24, N'Self-Help', 2, 1, '2026-02-01', '2026-02-01'),
(25, N'Graphical Light Novels', 2, 1, '2026-02-01', '2026-02-01'),

-- LEVEL 2: Japanese Manga
(31, N'Shonen', 3, 1, '2026-02-01', '2026-02-01'),
(32, N'Seinen', 3, 1, '2026-02-01', '2026-02-01'),
(33, N'Shoujo', 3, 1, '2026-02-01', '2026-02-01'),
(34, N'Kodomomuke', 3, 1, '2026-02-01', '2026-02-01'),
(35, N'Josei', 3, 1, '2026-02-01', '2026-02-01'),

-- LEVEL 3
(111, N'Graphic Short Stories', 11, 2, '2026-02-01', '2026-02-01'),
(112, N'Graphical Long Stories', 11, 2, '2026-02-01', '2026-02-01'),
(113, N'Illustrated Poetry Stories', 11, 2, '2026-02-01', '2026-02-01'),
(114, N'Essays', 11, 2, '2026-02-01', '2026-02-01'),

(121, N'Startup', 12, 2, '2026-02-01', '2026-02-01'),
(122, N'Graphical Marketing', 12, 2, '2026-02-01', '2026-02-01'),
(123, N'Illustrated Finance', 12, 2, '2026-02-01', '2026-02-01'),
(124, N'Human Resource Management', 12, 2, '2026-02-01', '2026-02-01'),

(211, N'English & American', 21, 2, '2026-02-01', '2026-02-01'),
(212, N'Japanese', 21, 2, '2026-02-01', '2026-02-01'),
(213, N'French', 21, 2, '2026-02-01', '2026-02-01'),
(214, N'Russian', 21, 2, '2026-02-01', '2026-02-01'),

(251, N'Graphical Fantasy', 25, 2, '2026-02-01', '2026-02-01'),
(252, N'Illustrated Isekai', 25, 2, '2026-02-01', '2026-02-01'),
(253, N'Graphical Romance', 25, 2, '2026-02-01', '2026-02-01'),

(311, N'Action', 31, 2, '2026-02-01', '2026-02-01'),
(312, N'Adventure', 31, 2, '2026-02-01', '2026-02-01'),
(313, N'Fantasy', 31, 2, '2026-02-01', '2026-02-01'),
(314, N'Sports', 31, 2, '2026-02-01', '2026-02-01'),
(315, N'Comedy', 31, 2, '2026-02-01', '2026-02-01'),

(321, N'Horror', 32, 2, '2026-02-01', '2026-02-01'),
(322, N'Psychological', 32, 2, '2026-02-01', '2026-02-01'),
(323, N'Thriller', 32, 2, '2026-02-01', '2026-02-01'),
(324, N'Mystery', 32, 2, '2026-02-01', '2026-02-01'),

(331, N'Romance', 33, 2, '2026-02-01', '2026-02-01'),
(332, N'School Life', 33, 2, '2026-02-01', '2026-02-01'),
(333, N'Drama', 33, 2, '2026-02-01', '2026-02-01'),

(341, N'Educational', 34, 2, '2026-02-01', '2026-02-01'),
(342, N'Adventure', 34, 2, '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[category] OFF;
GO
-- Bật IDENTITY_INSERT cho author và chèn tất cả authors
SET IDENTITY_INSERT [dbo].[author] ON;
GO
INSERT INTO [dbo].[author] (id, name, description, created_at, updated_at)
VALUES
(1, 'Eiichiro Oda', 'Author of One Piece', '2026-02-01', '2026-02-01'),
(2, 'Masashi Kishimoto', 'Author of Naruto', '2026-02-01', '2026-02-01'),
(3, 'Hajime Isayama', 'Author of Attack on Titan', '2026-02-01', '2026-02-01'),
(4, 'Akira Toriyama', 'Author of Dragon Ball', '2026-02-01', '2026-02-01'),
(5, 'Koyoharu Gotouge', 'Author of Demon Slayer', '2026-02-01', '2026-02-01'),
(6, 'Gege Akutami', 'Author of Jujutsu Kaisen', '2026-02-01', '2026-02-01'),
(7, 'Tite Kubo', 'Author of Bleach', '2026-02-01', '2026-02-01'),
(8, 'Kohei Horikoshi', 'Author of My Hero Academia', '2026-02-01', '2026-02-01'),
(9, 'Sui Ishida', 'Author of Tokyo Ghoul', '2026-02-01', '2026-02-01'),
(10, 'Tsugumi Ohba', 'Author of Death Note', '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[author] OFF;
GO
-- Bật IDENTITY_INSERT cho publisher và chèn tất cả publishers
SET IDENTITY_INSERT [dbo].[publisher] ON;
GO
INSERT INTO [dbo].[publisher] (id, name, country, website, description, created_at, updated_at)
VALUES
(1, 'Shueisha', 'Japan', 'https://www.shueisha.co.jp', 'Leading Japanese manga publisher', '2026-02-01', '2026-02-01'),
(2, 'Kodansha', 'Japan', 'https://www.kodansha.co.jp', 'Major Japanese publishing company', '2026-02-01', '2026-02-01'),
(3, 'Shogakukan', 'Japan', 'https://www.shogakukan.co.jp', 'Japanese publisher of manga and books', '2026-02-01', '2026-02-01'),
(4, 'VIZ Media', 'USA', 'https://www.viz.com', 'English publisher of manga', '2026-02-01', '2026-02-01'),
(5, 'Kim Dong Publishing', 'Vietnam', 'https://www.nxbkimdong.com.vn', 'Vietnamese publisher', '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[publisher] OFF;
GO
-- Bật IDENTITY_INSERT cho translator và chèn tất cả translators
SET IDENTITY_INSERT [dbo].[translator] ON;
GO
INSERT INTO [dbo].[translator] (id, name, bio, created_at, updated_at)
VALUES
(1, N'Jay Rubin', N'Well-known translator of Japanese literature, including works by Haruki Murakami', '2026-02-01', '2026-02-01'),
(2, N'Ed Chavez', N'Professional manga translator specializing in shonen and seinen manga', '2026-02-01', '2026-02-01'),
(3, N'William Flanagan', N'Experienced translator of Japanese novels and light novels', '2026-02-01', '2026-02-01'),
(4, N'Michael Emmerich', N'Literary translator and scholar of modern Japanese literature', '2026-02-01', '2026-02-01'),
(5, N'Stephen Paul', N'Japanese-to-English translator focusing on fantasy and action manga', '2026-02-01', '2026-02-01'),
(6, N'Linda Hoaglund', N'Translator and editor with extensive experience in Japanese media', '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[translator] OFF;
GO
-- Bật IDENTITY_INSERT cho series và chèn tất cả series
SET IDENTITY_INSERT [dbo].[series] ON;
GO
INSERT INTO [dbo].[series] (id, name, description, total_volumes, status, created_at, updated_at)
VALUES
(1, 'One Piece', 'Epic pirate adventure manga series', 107, 'ONGOING', '2026-02-01', '2026-02-01'),
(2, 'Naruto', 'Ninja adventure manga series', 72, 'COMPLETED', '2026-02-01', '2026-02-01'),
(3, 'Attack on Titan', 'Dark fantasy manga about humanity vs titans', 34, 'COMPLETED', '2026-02-01', '2026-02-01'),
(4, 'Dragon Ball', 'Martial arts and adventure manga', 42, 'COMPLETED', '2026-02-01', '2026-02-01'),
(5, 'Demon Slayer', 'Historical fantasy manga', 23, 'COMPLETED', '2026-02-01', '2026-02-01'),
(6, 'Jujutsu Kaisen', 'Dark fantasy manga about cursed spirits', 25, 'ONGOING', '2026-02-01', '2026-02-01'),
(7, 'Bleach', 'Supernatural adventure manga', 74, 'COMPLETED', '2026-02-01', '2026-02-01'),
(8, 'My Hero Academia', 'Superhero manga series', 39, 'ONGOING', '2026-02-01', '2026-02-01'),
(9, 'Tokyo Ghoul', 'Dark fantasy horror manga', 14, 'COMPLETED', '2026-02-01', '2026-02-01'),
(10, 'Death Note', 'Psychological thriller manga', 12, 'COMPLETED', '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[series] OFF;
GO
SET IDENTITY_INSERT [dbo].[language] ON;
GO

INSERT INTO [dbo].[language] (id, name, code, created_at, updated_at)
VALUES
(1, N'English', 'en', '2026-02-01', '2026-02-01'),
(2, N'Japanese', 'ja', '2026-02-01', '2026-02-01'),
(3, N'Vietnamese', 'vi', '2026-02-01', '2026-02-01'),
(4, N'Korean', 'ko', '2026-02-01', '2026-02-01'),
(5, N'Chinese', 'zh-CN', '2026-02-01', '2026-02-01'),
(6, N'French', 'fr', '2026-02-01', '2026-02-01'),
(7, N'Spanish', 'es', '2026-02-01', '2026-02-01');
GO

SET IDENTITY_INSERT [dbo].[language] OFF;
GO
-- Bật IDENTITY_INSERT cho book và chèn tất cả books
SET IDENTITY_INSERT [dbo].[book] ON;
GO
INSERT INTO [dbo].[book] (id, author_id, category_id, title, date, description, image, price, number_page, number_sold, number_stock, created_at, updated_at)
VALUES
(1, 1, 1, 'One Piece', '1997-07-22', 'The beginning of Luffy''s pirate adventure.', 'https://images.bwbcovers.com/061/One-Piece-Volume-1-9780613962988.jpg', 5000, 220, 5000, 200, '2026-02-01', '2026-02-01'),
(2, 2, 1, 'Naruto Volume 1', '1999-09-21', 'Naruto Uzumaki starts his ninja journey.', 'https://upload.wikimedia.org/wikipedia/en/9/94/NarutoCoverTankobon1.jpg', 15000, 190, 4500, 150, '2026-02-01', '2026-02-01'),
(3, 3, 3, 'Attack on Titan', '2009-03-17', 'Eren Yeager faces the Titans.', 'https://salt.tikicdn.com/cache/w1200/media/catalog/product/i/m/img038_1_1.jpg', 16000, 210, 6000, 120, '2026-02-01', '2026-02-01'),
(4, 1, 2, 'Worlds Finest Comics', '1997-10-03', 'Luffy continues his adventure.', 'https://tse2.mm.bing.net/th/id/OIP.aWoYeGtZgVdQ_fYrn-p-LQAAAA?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3', 4000, 230, 4800, 140, '2026-02-01', '2026-02-01'),
(5, 2, 1, 'Doraemon Volume 4', '1999-12-20', 'New ninja challenges begin.', 'https://lh3.googleusercontent.com/-yx6J3fo_vtQ/XaaK_5Y5w9I/AAAAAAAA7hk/Qcol5RS7MYkd5RoqLtpfwDV5QvAJhMORwCNcBGAsYHQ/w1300/001.jpg', 23000, 200, 4100, 180, '2026-02-01', '2026-02-01'),
(6, 3, 3, 'Attack on Titan', '2010-04-07', 'The mystery of Titans deepens.', 'https://cdn0.fahasa.com/media/catalog/product/t/u/tuyen_tap_tranh_to_mau_-_thanh_guom_diet_quy_-_cham_2.jpg', 36000, 215, 6200, 100, '2026-02-01', '2026-02-01'),
(7, 4, 1, 'Dragon Ball', '1984-12-03', 'The adventure of Son Goku begins.', 'https://tingenz.com/wp-content/uploads/2022/12/bia-truyen-co-be-quang-khan-do-3-min.jpg', 25000, 210, 7000, 160, '2026-02-01', '2026-02-01'),
(8, 4, 1, 'Dragon Ball Z', '1985-02-04', 'Goku meets new friends and rivals.', 'https://freshcomics.s3.amazonaws.com/cache/cc/d7/ccd775c95487c94930bc668c3aecb423.jpg', 35000, 220, 6800, 140, '2026-02-01', '2026-02-01'),
(9, 5, 2, 'Demon Slayer', '2016-06-03', 'Tanjiro starts his demon-slaying journey.', 'https://th.bing.com/th/id/OIP.REuBT-UvO7kNprL06rUYNAHaJQ?w=148&h=185&c=7&r=0&o=7&cb=ucfimg2&dpr=1.3&pid=1.7&rm=3&ucfimg=1', 18000, 230, 8000, 200, '2026-02-01', '2026-02-01'),
(10, 5, 2, 'Demon Slayer Vol 2', '2016-08-04', 'Tanjiro faces stronger demons.', 'https://spiderfan.org/images/title/comics/peter_porker/002.jpg', 4000, 240, 7700, 180, '2026-02-01', '2026-02-01'),
(11, 6, 3, 'Jujutsu Kaisen', '2018-07-04', 'Yuji Itadori enters the world of curses.', 'https://th.bing.com/th/id/OIP.Uet6MLbTKrh8Qg13sKNPnQHaKg?w=130&h=185&c=7&r=0&o=7&cb=ucfimg2&dpr=1.3&pid=1.7&rm=3&ucfimg=1', 7000, 200, 7500, 170, '2026-02-01', '2026-02-01'),
(12, 6, 3, 'Jujutsu Kaisen Vol 2', '2018-09-04', 'Cursed battles intensify.', 'https://jetpackcomics.com/wp-content/uploads/2023/07/AM_SM_35_D100_VAR1-1.jpg', 22000, 210, 7300, 150, '2026-02-01', '2026-02-01'),
(13, 7, 1, 'Bleach', '2002-08-07', 'Ichigo becomes a Soul Reaper.', 'https://tse3.mm.bing.net/th/id/OIP.zDxSX4R_kHBH1oSzVl6IRAHaKh?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3', 25000, 190, 6900, 160, '2026-02-01', '2026-02-01'),
(14, 7, 1, 'Bleach Vol 2', '2002-10-04', 'Battles against Hollows continue.', 'https://th.bing.com/th/id/R.b88cfcfd1fbb1654b62d3bde6455d694?rik=azkhjeJQUrwfsQ&pid=ImgRaw&r=0', 15000, 195, 6700, 140, '2026-02-01', '2026-02-01'),
(15, 8, 2, 'My Hero Academia', '2014-11-04', 'Izuku Midoriya dreams of being a hero.', 'https://urlvn.net/bj5mgs7', 15000, 205, 7200, 180, '2026-02-01', '2026-02-01'),
(16, 8, 2, 'My Hero Academia Vol 2', '2015-01-05', 'Hero training begins.', 'https://tse1.mm.bing.net/th/id/OIP.llmlh_K3rGy0lzGSo-bXLgHaLP?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3', 25000, 210, 7100, 160, '2026-02-01', '2026-02-01'),
(17, 9, 3, 'Tokyo Ghoul', '2012-02-17', 'Ken Kaneki becomes a half-ghoul.', 'https://www.comicsbeat.com/wp-content/uploads/2022/01/FCBDMARVOICES2022001_cover-scaled.jpg', 24000, 220, 6500, 130, '2026-02-01', '2026-02-01'),
(18, 9, 3, 'Tokyo Ghoul Vol 2', '2012-04-19', 'Kaneki struggles with his new identity.', 'https://media.mycomicshop.com/n_iv/600/768345.jpg', 20000, 225, 6300, 120, '2026-02-01', '2026-02-01'),
(19, 10, 1, 'Death Note', '2004-04-02', 'A notebook that kills anyone whose name is written.', 'https://tse1.mm.bing.net/th/id/OIP.TMbfEpYvloVgy4pLX8Ty4wAAAA?cb=ucfimg2&ucfimg=1&w=462&h=720&rs=1&pid=ImgDetMain&o=7&rm=3', 33000, 200, 7400, 150, '2026-02-01', '2026-02-01'),
(20, 10, 1, 'Death Note Vol 2', '2004-06-04', 'The battle of wits between Light and L.', 'https://tse4.mm.bing.net/th/id/OIP.1FxJ9SK8ueU3T_jCMbxpXQHaLP?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3', 15000, 205, 7200, 140, '2026-02-01', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[book] OFF;
GO
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 1, volume_number = 1 WHERE id = 1;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 2, volume_number = 1 WHERE id = 2;
UPDATE [dbo].[book] SET publisher_id = 2, series_id = 3, volume_number = 1 WHERE id = 3;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 1, volume_number = 2 WHERE id = 4;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 2, volume_number = 2 WHERE id = 5;
UPDATE [dbo].[book] SET publisher_id = 2, series_id = 3, volume_number = 2 WHERE id = 6;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 4, volume_number = 1 WHERE id = 7;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 4, volume_number = 2 WHERE id = 8;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 5, volume_number = 1 WHERE id = 9;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 5, volume_number = 2 WHERE id = 10;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 6, volume_number = 1 WHERE id = 11;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 6, volume_number = 2 WHERE id = 12;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 7, volume_number = 1 WHERE id = 13;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 7, volume_number = 2 WHERE id = 14;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 8, volume_number = 1 WHERE id = 15;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 8, volume_number = 2 WHERE id = 16;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 9, volume_number = 1 WHERE id = 17;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 9, volume_number = 2 WHERE id = 18;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 10, volume_number = 1 WHERE id = 19;
UPDATE [dbo].[book] SET publisher_id = 1, series_id = 10, volume_number = 2 WHERE id = 20;
GO
-- Cập nhật truyện tranh vào các category MANGA phù hợp
UPDATE [dbo].[book] SET category_id = 311 WHERE id IN (1, 4); -- One Piece -> Manga > Shonen > Action
UPDATE [dbo].[book] SET category_id = 312 WHERE id IN (2, 5); -- Naruto -> Manga > Shonen > Adventure
UPDATE [dbo].[book] SET category_id = 313 WHERE id IN (3, 6); -- Attack on Titan -> Manga > Shonen > Fantasy
UPDATE [dbo].[book] SET category_id = 311 WHERE id IN (7, 8); -- Dragon Ball -> Manga > Shonen > Action
UPDATE [dbo].[book] SET category_id = 312 WHERE id IN (9, 10); -- Demon Slayer -> Manga > Shonen > Adventure
UPDATE [dbo].[book] SET category_id = 313 WHERE id IN (11, 12); -- Jujutsu Kaisen -> Manga > Shonen > Fantasy
UPDATE [dbo].[book] SET category_id = 311 WHERE id IN (13, 14); -- Bleach -> Manga > Shonen > Action
UPDATE [dbo].[book] SET category_id = 312 WHERE id IN (15, 16); -- My Hero Academia -> Manga > Shonen > Adventure
UPDATE [dbo].[book] SET category_id = 321 WHERE id IN (17, 18); -- Tokyo Ghoul -> Manga > Seinen > Horror
UPDATE [dbo].[book] SET category_id = 322 WHERE id IN (19, 20); -- Death Note -> Manga > Seinen > Psychological
GO
UPDATE [dbo].[book] SET language_id = 1 WHERE id BETWEEN 1 AND 20;
GO

-- Cập nhật size cho các sách (kích thước tiêu chuẩn manga)
UPDATE [dbo].[book] SET size = '11.4 x 17.5 cm' WHERE id IN (1, 4); -- One Piece
UPDATE [dbo].[book] SET size = '11.4 x 17.5 cm' WHERE id IN (2, 5); -- Naruto
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (3, 6); -- Attack on Titan
UPDATE [dbo].[book] SET size = '11.4 x 17.5 cm' WHERE id IN (7, 8); -- Dragon Ball
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (9, 10); -- Demon Slayer
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (11, 12); -- Jujutsu Kaisen
UPDATE [dbo].[book] SET size = '11.4 x 17.5 cm' WHERE id IN (13, 14); -- Bleach
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (15, 16); -- My Hero Academia
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (17, 18); -- Tokyo Ghoul
UPDATE [dbo].[book] SET size = '12.7 x 19.1 cm' WHERE id IN (19, 20); -- Death Note
GO

UPDATE [dbo].[book] SET weight_kg = 0.22 WHERE id = 1;
GO
UPDATE [dbo].[book] SET weight_kg = 0.23 WHERE id = 2;
GO
UPDATE [dbo].[book] SET weight_kg = 0.25 WHERE id = 3;
GO
UPDATE [dbo].[book] SET weight_kg = 0.22 WHERE id = 4;
GO
UPDATE [dbo].[book] SET weight_kg = 0.23 WHERE id = 5;
GO
UPDATE [dbo].[book] SET weight_kg = 0.25 WHERE id = 6;
GO
UPDATE [dbo].[book] SET weight_kg = 0.21 WHERE id = 7;
GO
UPDATE [dbo].[book] SET weight_kg = 0.21 WHERE id = 8;
GO
UPDATE [dbo].[book] SET weight_kg = 0.24 WHERE id = 9;
GO
UPDATE [dbo].[book] SET weight_kg = 0.24 WHERE id = 10;
GO
UPDATE [dbo].[book] SET weight_kg = 0.26 WHERE id = 11;
GO
UPDATE [dbo].[book] SET weight_kg = 0.26 WHERE id = 12;
GO
UPDATE [dbo].[book] SET weight_kg = 0.22 WHERE id = 13;
GO
UPDATE [dbo].[book] SET weight_kg = 0.22 WHERE id = 14;
GO
UPDATE [dbo].[book] SET weight_kg = 0.25 WHERE id = 15;
GO
UPDATE [dbo].[book] SET weight_kg = 0.25 WHERE id = 16;
GO
UPDATE [dbo].[book] SET weight_kg = 0.27 WHERE id = 17;
GO
UPDATE [dbo].[book] SET weight_kg = 0.27 WHERE id = 18;
GO
UPDATE [dbo].[book] SET weight_kg = 0.23 WHERE id = 19;
GO
UPDATE [dbo].[book] SET weight_kg = 0.23 WHERE id = 20;
GO
-- Bật IDENTITY_INSERT cho order
SET IDENTITY_INSERT [dbo].[order] ON;
GO
INSERT INTO [dbo].[order] (id, user_id, admin_id, total_cost, customer_name, phone, address, shipping_fee, shipping_method, status, payment_method, payment_status, payment_note, voucher_id, discount_amount, created_at, updated_order)
VALUES
(10000, 1, NULL, 86000, 'Nguyen Van A', '0912345601', 'Ha Dong, Hanoi', 20800, 'STANDARD', 'Pending', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE()),
(10001, 2, NULL, 160000, 'Tran Thi B', '0912345602', 'Ba Dinh, Hanoi', 35000, 'EXPRESS', 'Approved', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE()),
(10002, 3, NULL, 70000, 'Le Van C', '0912345603', 'Cau Giay, Hanoi', 19800, 'STANDARD', 'Completed', 'COD', 'PAID', 'Shipper confirmed customer paid cash upon delivery', NULL, 0, GETDATE(), GETDATE()),
(10003, 1, NULL, 118000, 'Pham Thi D', '0912345604', 'Dong Da, Hanoi', 34520, 'EXPRESS', 'In Delivery', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE()),
(10004, 2, NULL, 20000, 'Hoang Van E', '0912345605', 'Tay Ho, Hanoi', 17350, 'STANDARD', 'Completed', 'COD', 'PAID', 'Shipper confirmed customer paid cash upon delivery', NULL, 0, GETDATE(), GETDATE()),
(10005, 3, NULL, 77000, 'Vu Thi F', '0912345606', 'Thanh Xuan, Hanoi', 48640, 'SAME-DAY', 'Completed', 'COD', 'PAID', 'Shipper confirmed customer paid cash upon delivery', NULL, 0, GETDATE(), GETDATE()),
(10006, 1, NULL, 50000, 'Ngo Van G', '0912345607', 'Hoang Mai, Hanoi', 37480, 'EXPRESS', 'Completed', 'COD', 'PAID', 'Shipper confirmed customer paid cash upon delivery', NULL, 0, GETDATE(), GETDATE()),
(10007, 2, NULL, 51000, 'Bui Van H', '0912345608', 'Long Bien, Hanoi', 18400, 'STANDARD', 'Pending', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE()),
(10008, 3, NULL, 120000, 'Dang Thi I', '0912345609', 'Hai Ba Trung, Hanoi', 35000, 'EXPRESS', 'Approved', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE()),
(10009, 1, NULL, 53000, 'Ly Van J', '0912345610', 'Hoan Kiem, Hanoi', 20650, 'STANDARD', 'Completed', 'COD', 'PAID', 'Shipper confirmed customer paid cash upon delivery', NULL, 0, GETDATE(), GETDATE()),
(10010, 2, NULL, 135000, 'Cao Thi K', '0912345611', 'Tay Ho, Hanoi', 41560, 'EXPRESS', 'In Delivery', 'COD', 'UNPAID', NULL, NULL, 0, GETDATE(), GETDATE());

SET IDENTITY_INSERT [dbo].[order] OFF;
GO
-- Bật IDENTITY_INSERT cho promotion
SET IDENTITY_INSERT [dbo].[voucher] ON;
GO
-- INSERT dữ liệu promotion
SET IDENTITY_INSERT [dbo].[voucher] ON;
GO
-- INSERT dữ liệu voucher
INSERT INTO [dbo].[voucher]
([voucher_id], [code], [name], [discount_value], [start_date], [end_date], [max_usage], [used_count], [min_order], [status])
VALUES
(1, 'SALE10', '10k VND discount on popular comics.', 5000, '2026-01-01', '2026-12-31', 100, 1, 20000, 'ACTIVE'),
(2, 'SALE20', '20 VND discount on manga.', 10000, '2026-01-01', '2026-12-31', 50, 0, 30000, 'ACTIVE'),
(3, 'SALE15', 'Old promotion price 15 VND', 10000, '2025-01-01', '2025-12-31', 80, 0, 25000, 'EXPIRED'),
(4, 'SALE30', 'The 30 VND promotion has ended.', 20000, '2026-01-01', '2026-12-31', 40, 0, 50000, 'EXPIRED'),
(5, 'SALE05', 'The 5 VND promotion has expired.', 35000, '2024-01-01', '2024-12-31', 200, 0, 100000, 'EXPIRED');
GO
SET IDENTITY_INSERT [dbo].[voucher] OFF;
GO
GO
-- Bật IDENTITY_INSERT cho order_detail
SET IDENTITY_INSERT [dbo].[order_detail] ON;
GO
SET IDENTITY_INSERT [dbo].[order_detail] ON;
GO
INSERT INTO [dbo].[order_detail] (id, order_id, book_id, number, total_cost, series_id, is_full_series, created_at, updated_at)
VALUES
(10000, 10000, 1, 2, 10000, NULL, 0, GETDATE(), GETDATE()),
(10001, 10000, 4, 1, 4000, NULL, 0, GETDATE(), GETDATE()),
(10002, 10000, 6, 2, 72000, NULL, 0, GETDATE(), GETDATE()),
(10003, 10001, 6, 4, 144000, NULL, 0, GETDATE(), GETDATE()),
(10004, 10001, 3, 1, 16000, NULL, 0, GETDATE(), GETDATE()),
(10005, 10002, 2, 1, 15000, NULL, 0, GETDATE(), GETDATE()),
(10006, 10002, 5, 1, 23000, NULL, 0, GETDATE(), GETDATE()),
(10007, 10002, 3, 2, 32000, NULL, 0, GETDATE(), GETDATE()),
(10008, 10003, 1, 2, 10000, NULL, 0, GETDATE(), GETDATE()),
(10009, 10003, 6, 3, 108000, NULL, 0, GETDATE(), GETDATE()),
(10010, 10004, 3, 1, 16000, NULL, 0, GETDATE(), GETDATE()),
(10011, 10004, 4, 1, 4000, NULL, 0, GETDATE(), GETDATE()),
(10012, 10005, 6, 2, 72000, NULL, 0, GETDATE(), GETDATE()),
(10013, 10005, 1, 1, 5000, NULL, 0, GETDATE(), GETDATE()),
(10014, 10006, 2, 2, 30000, NULL, 0, GETDATE(), GETDATE()),
(10015, 10006, 4, 5, 20000, NULL, 0, GETDATE(), GETDATE()),
(10016, 10007, 5, 2, 46000, NULL, 0, GETDATE(), GETDATE()),
(10017, 10007, 1, 1, 5000, NULL, 0, GETDATE(), GETDATE()),
(10018, 10008, 3, 3, 48000, NULL, 0, GETDATE(), GETDATE()),
(10019, 10008, 6, 2, 72000, NULL, 0, GETDATE(), GETDATE()),
(10020, 10009, 2, 3, 45000, NULL, 0, GETDATE(), GETDATE()),
(10021, 10009, 4, 2, 8000, NULL, 0, GETDATE(), GETDATE()),
(10022, 10010, 1, 3, 15000, NULL, 0, GETDATE(), GETDATE()),
(10023, 10010, 4, 3, 12000, NULL, 0, GETDATE(), GETDATE()),
(10024, 10010, 6, 3, 108000, NULL, 0, GETDATE(), GETDATE());
GO
SET IDENTITY_INSERT [dbo].[order_detail] OFF;
GO
-- Bật IDENTITY_INSERT cho comment
SET IDENTITY_INSERT [dbo].[comment] ON;
GO
INSERT INTO [dbo].[comment] 
(id, user_id, book_id, order_detail_id, star, content, status, created_at, updated_at)
VALUES
(1, 1, 1, 10000, 5, 'Amazing start to the series!', 'APPROVED', GETDATE(), GETDATE()),
(2, 2, 2, 10001, 4, 'Great story and artwork.', 'APPROVED', GETDATE(), GETDATE()),
(3, 1, 3, NULL, 5, 'Attack on Titan is incredible!', 'APPROVED', GETDATE(), GETDATE());
GO
SET IDENTITY_INSERT [dbo].[comment] OFF;
GO
SET IDENTITY_INSERT [dbo].[book_translator] ON;
-- Một số sách có 1 dịch giả, một số có nhiều dịch giả
INSERT INTO [dbo].[book_translator] (id, book_id, translator_id, role, created_at)
VALUES
-- One Piece Vol 1: có 2 dịch giả
(1, 1, 1, N'Main Translator', '2026-02-01'),
(2, 1, 6, N'Editor', '2026-02-01'),
-- Naruto Vol 1: có 1 dịch giả
(3, 2, 2, N'Main Translator', '2026-02-01'),
-- Attack on Titan Vol 1: có 2 dịch giả
(4, 3, 3, N'Main Translator', '2026-02-01'),
(5, 3, 4, N'Co-Translator', '2026-02-01'),
-- One Piece Vol 2: cùng team với Vol 1
(6, 4, 1, N'Main Translator', '2026-02-01'),
(7, 4, 6, N'Editor', '2026-02-01'),
-- Naruto Vol 2
(8, 5, 2, N'Main Translator', '2026-02-01'),
-- Attack on Titan Vol 2
(9, 6, 3, N'Main Translator', '2026-02-01'),
(10, 6, 4, N'Co-Translator', '2026-02-01'),
-- Dragon Ball Vol 1
(11, 7, 4, N'Main Translator', '2026-02-01'),
(12, 7, 5, N'Co-Translator', '2026-02-01'),
-- Dragon Ball Vol 2
(13, 8, 4, N'Main Translator', '2026-02-01'),
-- Demon Slayer Vol 1
(14, 9, 5, N'Main Translator', '2026-02-01'),
-- Demon Slayer Vol 2
(15, 10, 5, N'Main Translator', '2026-02-01'),
-- Jujutsu Kaisen Vol 1
(16, 11, 1, N'Main Translator', '2026-02-01'),
(17, 11, 3, N'Co-Translator', '2026-02-01'),
-- Jujutsu Kaisen Vol 2
(18, 12, 1, N'Main Translator', '2026-02-01'),
-- Bleach Vol 1
(19, 13, 2, N'Main Translator', '2026-02-01'),
-- Bleach Vol 2
(20, 14, 2, N'Main Translator', '2026-02-01'),
-- My Hero Academia Vol 1
(21, 15, 3, N'Main Translator', '2026-02-01'),
(22, 15, 6, N'Editor', '2026-02-01'),
-- My Hero Academia Vol 2
(23, 16, 3, N'Main Translator', '2026-02-01'),
-- Tokyo Ghoul Vol 1
(24, 17, 4, N'Main Translator', '2026-02-01'),
-- Tokyo Ghoul Vol 2
(25, 18, 4, N'Main Translator', '2026-02-01'),
-- Death Note Vol 1
(26, 19, 5, N'Main Translator', '2026-02-01'),
(27, 19, 6, N'Editor', '2026-02-01'),
-- Death Note Vol 2
(28, 20, 5, N'Main Translator', '2026-02-01');
GO
SET IDENTITY_INSERT [dbo].[book_translator] OFF;
GO
-- Thêm khóa ngoại cho book (author và category)
ALTER TABLE [dbo].[book] WITH CHECK ADD CONSTRAINT [FK_book_author] FOREIGN KEY([author_id])
REFERENCES [dbo].[author] ([id])
GO
ALTER TABLE [dbo].[book] CHECK CONSTRAINT [FK_book_author]
GO
ALTER TABLE [dbo].[book] WITH CHECK ADD CONSTRAINT [FK_book_category] FOREIGN KEY([category_id])
REFERENCES [dbo].[category] ([id])
GO
ALTER TABLE [dbo].[book] CHECK CONSTRAINT [FK_book_category]
GO