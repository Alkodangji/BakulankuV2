package helper;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class StrukPrinter {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd/MM/yyyy");

    private StrukPrinter() {
    }

    public static class Item {
        private final String nama;
        private final double qty;
        private final double harga;
        private final double subtotal;

        public Item(String nama, double qty, double harga, double subtotal) {
            this.nama = nama;
            this.qty = qty;
            this.harga = harga;
            this.subtotal = subtotal;
        }
    }

    public static void printPenjualan(
            String nomorTransaksi,
            Date tanggal,
            List<Item> items,
            double total,
            String metodePembayaran,
            double diterima,
            double kembalian
    ) throws PrinterException {
        List<String> lines = baseHeader("STRUK PENJUALAN", nomorTransaksi, tanggal);
        for (Item item : items) {
            lines.add(item.nama);
            lines.add(formatQty(item.qty) + " x " + RupiahFormat.format(item.harga)
                    + " = " + RupiahFormat.format(item.subtotal));
        }
        lines.add("------------------------------");
        lines.add("Total    : " + RupiahFormat.format(total));
        lines.add("Metode   : " + safe(metodePembayaran));
        lines.add("Diterima : " + RupiahFormat.format(diterima));
        lines.add("Kembali  : " + RupiahFormat.format(kembalian));
        printLines(lines);
    }

    public static void printBrilink(
            String nomorTransaksi,
            Date tanggal,
            String jenis,
            String kategori,
            double nominal,
            double fee,
            String metodeFee,
            double total
    ) throws PrinterException {
        List<String> lines = baseHeader("STRUK BRILINK", nomorTransaksi, tanggal);
        lines.add("Jenis    : " + safe(jenis));
        if (kategori != null && !kategori.trim().isEmpty()) {
            lines.add("Kategori : " + kategori);
        }
        lines.add("Nominal  : " + RupiahFormat.format(nominal));
        lines.add("Fee      : " + RupiahFormat.format(fee));
        if (metodeFee != null && !metodeFee.trim().isEmpty()) {
            lines.add("Metode Fee: " + metodeFee);
        }
        lines.add("Total    : " + RupiahFormat.format(total));
        printLines(lines);
    }

 
    private static List<String> baseHeader(String title, String nomorTransaksi, Date tanggal) {
        List<String> lines = new ArrayList<>();
        lines.add("TOKO MBAK UL");
        lines.add(title);
        lines.add("------------------------------");
        lines.add("No       : " + safe(nomorTransaksi));
        lines.add("Tanggal  : " + (tanggal == null ? "-" : DATE_FORMAT.format(tanggal)));
        lines.add("------------------------------");
        return lines;
    }

    private static void printLines(List<String> lines) throws PrinterException {
        List<String> printableLines = new ArrayList<>(lines);
        printableLines.add("------------------------------");
        printableLines.add("Terima kasih");

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new ReceiptPrintable(printableLines), receiptPageFormat());

        if (job.printDialog()) {
            job.print();
        }
    }

    private static PageFormat receiptPageFormat() {
        Paper paper = new Paper();
        double width = 226;
        double height = 600;
        paper.setSize(width, height);
        paper.setImageableArea(8, 8, width - 16, height - 16);

        PageFormat format = new PageFormat();
        format.setPaper(paper);
        return format;
    }

    private static String formatQty(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private static class ReceiptPrintable implements Printable {
        private final List<String> lines;

        ReceiptPrintable(List<String> lines) {
            this.lines = lines;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 9));

            int y = 12;
            for (String line : lines) {
                g2.drawString(line, 0, y);
                y += 12;
            }
            return PAGE_EXISTS;
        }
    }
}
