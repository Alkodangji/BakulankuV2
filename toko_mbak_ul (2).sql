-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 25 Jun 2026 pada 00.26
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `toko_mbak_ul`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_akun`
--

CREATE TABLE `tb_akun` (
  `id_akun` int(11) NOT NULL,
  `nama_akun` varchar(50) NOT NULL,
  `saldo` decimal(15,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_akun`
--

INSERT INTO `tb_akun` (`id_akun`, `nama_akun`, `saldo`) VALUES
(1, 'Cash', 90505000.00),
(2, 'BRI', 91700000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_bbm`
--

CREATE TABLE `tb_bbm` (
  `id_bbm` int(11) NOT NULL,
  `nama_bbm` varchar(100) NOT NULL,
  `harga_beli` decimal(15,2) NOT NULL DEFAULT 0.00,
  `harga_jual` decimal(15,2) NOT NULL DEFAULT 0.00,
  `stok` decimal(12,2) NOT NULL DEFAULT 0.00,
  `stok_minimum` decimal(12,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_bbm_penjualan`
--

CREATE TABLE `tb_bbm_penjualan` (
  `id_penjualan_bbm` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `bbm_id` int(11) NOT NULL,
  `liter` decimal(12,2) NOT NULL,
  `harga_jual` decimal(15,2) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `metode_pembayaran` enum('Cash','BRI') NOT NULL,
  `diterima` decimal(15,2) NOT NULL DEFAULT 0.00,
  `kembalian` decimal(15,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_bbm_restok`
--

CREATE TABLE `tb_bbm_restok` (
  `id_restok_bbm` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `bbm_id` int(11) NOT NULL,
  `akun_id` int(11) NOT NULL,
  `liter` decimal(12,2) NOT NULL,
  `harga_beli` decimal(15,2) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_brilink`
--

CREATE TABLE `tb_brilink` (
  `id_brilink` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `kategori_id` int(11) DEFAULT NULL,
  `jenis` enum('Tarik Tunai','Setor Tunai','Top Up') NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `fee` decimal(15,2) NOT NULL,
  `metode_fee` enum('Terpisah','Terpotong') DEFAULT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_brilink`
--

INSERT INTO `tb_brilink` (`id_brilink`, `nomor_transaksi`, `tanggal`, `user_id`, `kategori_id`, `jenis`, `nominal`, `fee`, `metode_fee`, `catatan`) VALUES
(1, 'BTT-20260625-001', '2026-06-25 00:00:00', 1, NULL, 'Tarik Tunai', 1000000.00, 5000.00, 'Terpisah', ''),
(2, 'BTT-20260625-002', '2026-06-25 00:00:00', 1, NULL, 'Tarik Tunai', 200000.00, 5000.00, 'Terpotong', '');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_kategori_keuangan`
--

CREATE TABLE `tb_kategori_keuangan` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(100) NOT NULL,
  `jenis` enum('Pemasukan','Pengeluaran') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_kategori_topup`
--

CREATE TABLE `tb_kategori_topup` (
  `id_kategori` int(11) NOT NULL,
  `nama_kategori` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_kategori_topup`
--

INSERT INTO `tb_kategori_topup` (`id_kategori`, `nama_kategori`) VALUES
(1, 'Dana'),
(2, 'Shopee Pay');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_keuangan`
--

CREATE TABLE `tb_keuangan` (
  `id_keuangan` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `akun_asal_id` int(11) DEFAULT NULL,
  `akun_tujuan_id` int(11) DEFAULT NULL,
  `jenis` enum('Pemasukan','Pengeluaran','Transfer') NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `kategori` varchar(100) DEFAULT NULL,
  `catatan` text DEFAULT NULL,
  `id_kategori` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_keuangan`
--

INSERT INTO `tb_keuangan` (`id_keuangan`, `nomor_transaksi`, `tanggal`, `user_id`, `akun_asal_id`, `akun_tujuan_id`, `jenis`, `nominal`, `kategori`, `catatan`, `id_kategori`) VALUES
(1, 'KEU202606250001', '2026-06-25 00:00:00', 1, NULL, 1, 'Pemasukan', 500000.00, 'Saldo Awal', '', NULL),
(2, 'KEU202606250002', '2026-06-25 00:00:00', 1, NULL, 1, 'Pemasukan', 200000.00, 'Saldo Awal', '', NULL),
(3, 'KEU202606250003', '2026-06-25 00:00:00', 1, NULL, 1, 'Pemasukan', 1000000.00, 'Saldo Awal', '', NULL),
(4, 'KEU202606250004', '2026-06-25 00:00:00', 1, NULL, 2, 'Pemasukan', 1000000.00, 'Saldo Awal', '', NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_penjualan`
--

CREATE TABLE `tb_penjualan` (
  `id_penjualan` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `metode_pembayaran` enum('Cash','BRI') NOT NULL,
  `diterima` decimal(15,2) NOT NULL DEFAULT 0.00,
  `kembalian` decimal(15,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_penjualan`
--

INSERT INTO `tb_penjualan` (`id_penjualan`, `nomor_transaksi`, `tanggal`, `user_id`, `total`, `metode_pembayaran`, `diterima`, `kembalian`) VALUES
(1, 'PJL-20260624-001', '2026-06-24 09:38:03', 1, 74000.00, 'BRI', 100000.00, 26000.00),
(2, 'PJL-20260624-002', '2026-06-24 09:45:52', 1, 78000.00, 'Cash', 100000.00, 22000.00),
(3, 'PJL-20260624-003', '2026-06-24 12:06:55', 1, 32500.00, 'Cash', 40000.00, 7500.00),
(4, 'PJL-20260624-004', '2026-06-24 12:20:37', 1, 8500.00, 'Cash', 9000.00, 500.00),
(5, 'PJL-20260624-005', '2026-06-24 12:31:15', 1, 24000.00, 'Cash', 120000.00, 96000.00),
(10, 'PJL-20260625-001', '2026-06-25 00:25:09', 1, 54000.00, 'Cash', 60000.00, 6000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_penjualan_detail`
--

CREATE TABLE `tb_penjualan_detail` (
  `id_detail` int(11) NOT NULL,
  `penjualan_id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga` decimal(15,2) NOT NULL,
  `subtotal` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_penjualan_detail`
--

INSERT INTO `tb_penjualan_detail` (`id_detail`, `penjualan_id`, `produk_id`, `qty`, `harga`, `subtotal`) VALUES
(1, 3, 2, 2, 6000.00, 12000.00),
(2, 3, 50, 2, 9000.00, 18000.00),
(3, 3, 44, 1, 2500.00, 2500.00),
(4, 4, 2, 1, 6000.00, 6000.00),
(5, 4, 44, 1, 2500.00, 2500.00),
(6, 5, 1, 1, 3000.00, 3000.00),
(7, 5, 50, 1, 9000.00, 9000.00),
(8, 5, 45, 4, 3000.00, 12000.00),
(12, 10, 2, 9, 6000.00, 54000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_produk`
--

CREATE TABLE `tb_produk` (
  `id_produk` int(11) NOT NULL,
  `kode_produk` varchar(20) NOT NULL,
  `nama_produk` varchar(100) NOT NULL,
  `harga_beli` decimal(15,2) NOT NULL DEFAULT 0.00,
  `harga_jual` decimal(15,2) NOT NULL DEFAULT 0.00,
  `stok` int(11) NOT NULL DEFAULT 0,
  `stok_minimum` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_produk`
--

INSERT INTO `tb_produk` (`id_produk`, `kode_produk`, `nama_produk`, `harga_beli`, `harga_jual`, `stok`, `stok_minimum`) VALUES
(1, 'PRD001', 'Aqua 600ml', 2500.00, 3000.00, 49, 5),
(2, 'PRD002', 'Aqua 1500ml', 5000.00, 6000.00, 30, 5),
(3, 'PRD003', 'Le Minerale 600ml', 2500.00, 3000.00, 45, 5),
(4, 'PRD004', 'Le Minerale 1500ml', 5500.00, 6500.00, 35, 5),
(5, 'PRD005', 'Teh Pucuk 350ml', 4000.00, 5000.00, 60, 5),
(6, 'PRD006', 'Teh Botol Sosro', 4000.00, 5000.00, 55, 5),
(7, 'PRD007', 'Pocari Sweat 500ml', 7500.00, 9000.00, 30, 5),
(8, 'PRD008', 'Mizone Lychee Lemon', 5500.00, 7000.00, 25, 5),
(9, 'PRD009', 'Floridina Orange', 4000.00, 5000.00, 40, 5),
(10, 'PRD010', 'Pulpy Orange', 5500.00, 7000.00, 35, 5),
(11, 'PRD011', 'Indomie Goreng', 3000.00, 3500.00, 100, 10),
(12, 'PRD012', 'Indomie Soto', 3000.00, 3500.00, 90, 10),
(13, 'PRD013', 'Mie Sedaap Goreng', 3000.00, 3500.00, 80, 10),
(14, 'PRD014', 'Sarimi Isi 2', 3500.00, 4000.00, 70, 10),
(15, 'PRD015', 'Pop Mie Ayam', 6000.00, 7000.00, 40, 5),
(16, 'PRD016', 'Pop Mie Soto', 6000.00, 7000.00, 35, 5),
(17, 'PRD017', 'Supermi Ayam Bawang', 2500.00, 3000.00, 75, 10),
(18, 'PRD018', 'Supermi Soto', 2500.00, 3000.00, 75, 10),
(19, 'PRD019', 'Lemonilo Goreng', 5000.00, 6000.00, 20, 5),
(20, 'PRD020', 'Lemonilo Kuah', 5000.00, 6000.00, 20, 5),
(21, 'PRD021', 'Sampoerna Mild 16', 30000.00, 32000.00, 25, 5),
(22, 'PRD022', 'Sampoerna Mild 12', 23000.00, 25000.00, 20, 5),
(23, 'PRD023', 'Gudang Garam Surya 12', 26000.00, 28000.00, 20, 5),
(24, 'PRD024', 'Gudang Garam Merah', 16000.00, 18000.00, 30, 5),
(25, 'PRD025', 'Djarum Super 12', 25000.00, 27000.00, 25, 5),
(26, 'PRD026', 'Djarum Coklat', 20000.00, 22000.00, 20, 5),
(27, 'PRD027', 'LA Bold 20', 31000.00, 33000.00, 15, 5),
(28, 'PRD028', 'Esse Berry Pop', 33000.00, 35000.00, 15, 5),
(29, 'PRD029', 'Camel Activate', 36000.00, 38000.00, 10, 3),
(30, 'PRD030', 'Marlboro Merah', 40000.00, 42000.00, 10, 3),
(31, 'PRD031', 'Chitato Sapi Panggang', 9000.00, 11000.00, 25, 5),
(32, 'PRD032', 'Qtela Singkong', 7500.00, 9000.00, 20, 5),
(33, 'PRD033', 'Taro Net', 6500.00, 8000.00, 20, 5),
(34, 'PRD034', 'Lays Rumput Laut', 8500.00, 10000.00, 15, 5),
(35, 'PRD035', 'Cheetos Jagung Bakar', 7500.00, 9000.00, 20, 5),
(36, 'PRD036', 'Oreo Original', 7000.00, 8500.00, 30, 5),
(37, 'PRD037', 'Roma Kelapa', 5500.00, 7000.00, 25, 5),
(38, 'PRD038', 'Beng Beng', 2500.00, 3000.00, 40, 10),
(39, 'PRD039', 'SilverQueen Mini', 4000.00, 5000.00, 35, 5),
(40, 'PRD040', 'Tango Wafer', 6500.00, 8000.00, 30, 5),
(41, 'PRD041', 'Kopi Kapal Api', 1500.00, 2000.00, 100, 10),
(42, 'PRD042', 'Good Day Cappuccino', 2000.00, 2500.00, 80, 10),
(44, 'PRD044', 'ABC Susu', 2371.75, 2500.00, 269, 10),
(45, 'PRD045', 'Energen Coklat', 2500.00, 3000.00, 56, 10),
(46, 'PRD046', 'Nutrisari Jeruk', 1000.00, 1500.00, 90, 10),
(47, 'PRD047', 'Ultra Milk Coklat', 6000.00, 7000.00, 40, 5),
(48, 'PRD048', 'Frisian Flag Coklat', 6000.00, 7000.00, 35, 5),
(49, 'PRD049', 'Yakult', 2000.00, 2500.00, 50, 5),
(50, 'PRD050', 'Cimory Squeeze', 8000.00, 9000.00, 24, 5);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_produk_restok`
--

CREATE TABLE `tb_produk_restok` (
  `id_restok` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `akun_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga_beli` decimal(15,2) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_produk_restok`
--

INSERT INTO `tb_produk_restok` (`id_restok`, `nomor_transaksi`, `tanggal`, `user_id`, `produk_id`, `akun_id`, `qty`, `harga_beli`, `total`, `catatan`) VALUES
(1, 'RSP-20260625-001', '2026-06-25 00:00:00', 1, 44, 2, 200, 2500.00, 500000.00, '');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_user`
--

CREATE TABLE `tb_user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tb_user`
--

INSERT INTO `tb_user` (`id_user`, `username`, `password`, `nama`) VALUES
(1, 'admin', '1234', 'Tian');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tb_akun`
--
ALTER TABLE `tb_akun`
  ADD PRIMARY KEY (`id_akun`),
  ADD UNIQUE KEY `nama_akun` (`nama_akun`);

--
-- Indeks untuk tabel `tb_bbm`
--
ALTER TABLE `tb_bbm`
  ADD PRIMARY KEY (`id_bbm`);

--
-- Indeks untuk tabel `tb_bbm_penjualan`
--
ALTER TABLE `tb_bbm_penjualan`
  ADD PRIMARY KEY (`id_penjualan_bbm`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `bbm_id` (`bbm_id`);

--
-- Indeks untuk tabel `tb_bbm_restok`
--
ALTER TABLE `tb_bbm_restok`
  ADD PRIMARY KEY (`id_restok_bbm`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `bbm_id` (`bbm_id`),
  ADD KEY `akun_id` (`akun_id`);

--
-- Indeks untuk tabel `tb_brilink`
--
ALTER TABLE `tb_brilink`
  ADD PRIMARY KEY (`id_brilink`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indeks untuk tabel `tb_kategori_keuangan`
--
ALTER TABLE `tb_kategori_keuangan`
  ADD PRIMARY KEY (`id_kategori`);

--
-- Indeks untuk tabel `tb_kategori_topup`
--
ALTER TABLE `tb_kategori_topup`
  ADD PRIMARY KEY (`id_kategori`),
  ADD UNIQUE KEY `nama_kategori` (`nama_kategori`);

--
-- Indeks untuk tabel `tb_keuangan`
--
ALTER TABLE `tb_keuangan`
  ADD PRIMARY KEY (`id_keuangan`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `akun_asal_id` (`akun_asal_id`),
  ADD KEY `akun_tujuan_id` (`akun_tujuan_id`),
  ADD KEY `fk_keuangan_kategori` (`id_kategori`);

--
-- Indeks untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  ADD PRIMARY KEY (`id_penjualan`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeks untuk tabel `tb_penjualan_detail`
--
ALTER TABLE `tb_penjualan_detail`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `penjualan_id` (`penjualan_id`),
  ADD KEY `produk_id` (`produk_id`);

--
-- Indeks untuk tabel `tb_produk`
--
ALTER TABLE `tb_produk`
  ADD PRIMARY KEY (`id_produk`),
  ADD UNIQUE KEY `kode_produk` (`kode_produk`);

--
-- Indeks untuk tabel `tb_produk_restok`
--
ALTER TABLE `tb_produk_restok`
  ADD PRIMARY KEY (`id_restok`),
  ADD UNIQUE KEY `nomor_transaksi` (`nomor_transaksi`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `produk_id` (`produk_id`),
  ADD KEY `akun_id` (`akun_id`);

--
-- Indeks untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `tb_akun`
--
ALTER TABLE `tb_akun`
  MODIFY `id_akun` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `tb_bbm`
--
ALTER TABLE `tb_bbm`
  MODIFY `id_bbm` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_bbm_penjualan`
--
ALTER TABLE `tb_bbm_penjualan`
  MODIFY `id_penjualan_bbm` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_bbm_restok`
--
ALTER TABLE `tb_bbm_restok`
  MODIFY `id_restok_bbm` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_brilink`
--
ALTER TABLE `tb_brilink`
  MODIFY `id_brilink` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `tb_kategori_keuangan`
--
ALTER TABLE `tb_kategori_keuangan`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_kategori_topup`
--
ALTER TABLE `tb_kategori_topup`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `tb_keuangan`
--
ALTER TABLE `tb_keuangan`
  MODIFY `id_keuangan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  MODIFY `id_penjualan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `tb_penjualan_detail`
--
ALTER TABLE `tb_penjualan_detail`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT untuk tabel `tb_produk`
--
ALTER TABLE `tb_produk`
  MODIFY `id_produk` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT untuk tabel `tb_produk_restok`
--
ALTER TABLE `tb_produk_restok`
  MODIFY `id_restok` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `tb_bbm_penjualan`
--
ALTER TABLE `tb_bbm_penjualan`
  ADD CONSTRAINT `tb_bbm_penjualan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`),
  ADD CONSTRAINT `tb_bbm_penjualan_ibfk_2` FOREIGN KEY (`bbm_id`) REFERENCES `tb_bbm` (`id_bbm`);

--
-- Ketidakleluasaan untuk tabel `tb_bbm_restok`
--
ALTER TABLE `tb_bbm_restok`
  ADD CONSTRAINT `tb_bbm_restok_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`),
  ADD CONSTRAINT `tb_bbm_restok_ibfk_2` FOREIGN KEY (`bbm_id`) REFERENCES `tb_bbm` (`id_bbm`),
  ADD CONSTRAINT `tb_bbm_restok_ibfk_3` FOREIGN KEY (`akun_id`) REFERENCES `tb_akun` (`id_akun`);

--
-- Ketidakleluasaan untuk tabel `tb_brilink`
--
ALTER TABLE `tb_brilink`
  ADD CONSTRAINT `tb_brilink_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`),
  ADD CONSTRAINT `tb_brilink_ibfk_2` FOREIGN KEY (`kategori_id`) REFERENCES `tb_kategori_topup` (`id_kategori`);

--
-- Ketidakleluasaan untuk tabel `tb_keuangan`
--
ALTER TABLE `tb_keuangan`
  ADD CONSTRAINT `fk_keuangan_kategori` FOREIGN KEY (`id_kategori`) REFERENCES `tb_kategori_keuangan` (`id_kategori`) ON UPDATE CASCADE,
  ADD CONSTRAINT `tb_keuangan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`),
  ADD CONSTRAINT `tb_keuangan_ibfk_2` FOREIGN KEY (`akun_asal_id`) REFERENCES `tb_akun` (`id_akun`),
  ADD CONSTRAINT `tb_keuangan_ibfk_3` FOREIGN KEY (`akun_tujuan_id`) REFERENCES `tb_akun` (`id_akun`);

--
-- Ketidakleluasaan untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  ADD CONSTRAINT `tb_penjualan_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`);

--
-- Ketidakleluasaan untuk tabel `tb_penjualan_detail`
--
ALTER TABLE `tb_penjualan_detail`
  ADD CONSTRAINT `tb_penjualan_detail_ibfk_1` FOREIGN KEY (`penjualan_id`) REFERENCES `tb_penjualan` (`id_penjualan`),
  ADD CONSTRAINT `tb_penjualan_detail_ibfk_2` FOREIGN KEY (`produk_id`) REFERENCES `tb_produk` (`id_produk`);

--
-- Ketidakleluasaan untuk tabel `tb_produk_restok`
--
ALTER TABLE `tb_produk_restok`
  ADD CONSTRAINT `tb_produk_restok_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id_user`),
  ADD CONSTRAINT `tb_produk_restok_ibfk_2` FOREIGN KEY (`produk_id`) REFERENCES `tb_produk` (`id_produk`),
  ADD CONSTRAINT `tb_produk_restok_ibfk_3` FOREIGN KEY (`akun_id`) REFERENCES `tb_akun` (`id_akun`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
