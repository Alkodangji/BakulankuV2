-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 21 Jun 2026 pada 20.04
-- Versi server: 10.4.27-MariaDB
-- Versi PHP: 7.4.33

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
(1, 'Cash', '0.00'),
(2, 'BRI', '0.00');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_bbm`
--

CREATE TABLE `tb_bbm` (
  `id_bbm` int(11) NOT NULL,
  `kode_bbm` varchar(20) NOT NULL,
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
  `tanggal` date NOT NULL,
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
  `tanggal` date NOT NULL,
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
  `tanggal` date NOT NULL,
  `user_id` int(11) NOT NULL,
  `kategori_id` int(11) DEFAULT NULL,
  `jenis` enum('Tarik Tunai','Setor Tunai','Top Up') NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `fee` decimal(15,2) NOT NULL,
  `metode_fee` enum('Terpisah','Terpotong') DEFAULT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_keuangan`
--

CREATE TABLE `tb_keuangan` (
  `id_keuangan` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` date NOT NULL,
  `user_id` int(11) NOT NULL,
  `akun_asal_id` int(11) DEFAULT NULL,
  `akun_tujuan_id` int(11) DEFAULT NULL,
  `jenis` enum('Pemasukan','Pengeluaran','Transfer') NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `kategori` varchar(100) DEFAULT NULL,
  `catatan` text DEFAULT NULL,
  `id_kategori` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_penjualan`
--

CREATE TABLE `tb_penjualan` (
  `id_penjualan` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` date NOT NULL,
  `user_id` int(11) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `metode_pembayaran` enum('Cash','BRI') NOT NULL,
  `diterima` decimal(15,2) NOT NULL DEFAULT 0.00,
  `kembalian` decimal(15,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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

-- --------------------------------------------------------

--
-- Struktur dari tabel `tb_produk_restok`
--

CREATE TABLE `tb_produk_restok` (
  `id_restok` int(11) NOT NULL,
  `nomor_transaksi` varchar(30) NOT NULL,
  `tanggal` date NOT NULL,
  `user_id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `akun_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga_beli` decimal(15,2) NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
  ADD PRIMARY KEY (`id_bbm`),
  ADD UNIQUE KEY `kode_bbm` (`kode_bbm`);

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
  MODIFY `id_brilink` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_kategori_keuangan`
--
ALTER TABLE `tb_kategori_keuangan`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_kategori_topup`
--
ALTER TABLE `tb_kategori_topup`
  MODIFY `id_kategori` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_keuangan`
--
ALTER TABLE `tb_keuangan`
  MODIFY `id_keuangan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_penjualan`
--
ALTER TABLE `tb_penjualan`
  MODIFY `id_penjualan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_penjualan_detail`
--
ALTER TABLE `tb_penjualan_detail`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_produk`
--
ALTER TABLE `tb_produk`
  MODIFY `id_produk` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_produk_restok`
--
ALTER TABLE `tb_produk_restok`
  MODIFY `id_restok` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `tb_user`
--
ALTER TABLE `tb_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT;

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
