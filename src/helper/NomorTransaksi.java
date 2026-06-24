package helper;

import config.Koneksi;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class 
                NomorTransaksi {

    public static String generate(
            String prefix,
            String tableName
    ) {

        try {

            Connection conn =
                    Koneksi.getConnection();

            String tanggal =
                    LocalDate.now()
                    .format(
                        DateTimeFormatter
                        .ofPattern("yyyyMMdd")
                    );

            String pattern =
                    prefix + "-" + tanggal + "-%";

            String sql =
                    "SELECT nomor_transaksi "
                  + "FROM " + tableName + " "
                  + "WHERE nomor_transaksi LIKE ? "
                  + "ORDER BY nomor_transaksi DESC "
                  + "LIMIT 1";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(1, pattern);

            ResultSet rs =
                    ps.executeQuery();

            int nomorUrut = 1;

            if (rs.next()) {

                String nomorTerakhir =
                        rs.getString(
                                "nomor_transaksi"
                        );

                String[] bagian =
                        nomorTerakhir.split("-");

                nomorUrut =
                        Integer.parseInt(
                                bagian[2]
                        ) + 1;
            }

            return String.format(
                    "%s-%s-%03d",
                    prefix,
                    tanggal,
                    nomorUrut
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}