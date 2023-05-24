package at.disys.service;

import at.disys.model.Invoice;
import at.disys.model.Station;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This class is responsible for generating PDF files.
 * It uses the iText library.
 * @author Altin
 */
public class PdfHelper {

    /**
     * Generates a PDF file for the given invoice.
     * @param invoice invoice to generate PDF for
     * @param pricePerKwh price per kWh
     */
    public void generatePDF(Optional<Invoice> invoice, double pricePerKwh) {
        if(invoice.isEmpty() || invoice.get().getCustomer().getId() == null){
            System.out.println("Invoice not found");
            return;
        }
        String fileName = "invoice" + invoice.get().getCustomer().getId()+ ".pdf";
        String outputPath = Paths.get("files", fileName).toString();
        File outputFile = new File(outputPath);

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            addContent(document, invoice.get(), pricePerKwh);
            document.close();
            System.out.println("PDF generated successfully.");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the content to the PDF file.
     * @param document PDF document
     * @param invoice invoice to generate PDF for
     * @param pricePerKwh price per kWh
     * @throws DocumentException if an error occurs while adding content to the document
     */
    private void addContent(Document document, Invoice invoice, double pricePerKwh) throws DocumentException {
        pdfTitle(document);

        pdfHeader(document, invoice);

        // Charges
        Font chargesFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
        Paragraph chargesTitle = new Paragraph("\nCharges:", chargesFont);
        chargesTitle.setSpacingBefore(10);
        chargesTitle.setIndentationLeft(20);
        document.add(chargesTitle);

        PdfPTable chargesTable = new PdfPTable(4);
        chargesTable.setWidthPercentage(100);
        chargesTable.setWidths(new float[] { 4, 2, 2, 2 });
        chargesTable.setSpacingBefore(5);
        chargesTable.setSpacingAfter(10);

        double totalAmount = CreateTableReturnTotal(invoice, pricePerKwh, chargesFont, chargesTable);

        chargesTable.addCell(createCell("", null));
        chargesTable.addCell(createCell("Total:", null));
        chargesTable.addCell(createCell(String.valueOf(invoice.getTotalKwh()), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        chargesTable.addCell(createCell(String.valueOf(totalAmount), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        document.add(chargesTable);

        pdfFooter(document, totalAmount);
    }

    /**
     * Footer of the PDF file. Contains the total amount and the names of the developers.
     * @param document PDF document
     * @param totalAmount total amount of the invoice
     * @throws DocumentException if an error occurs while adding content to the document
     */
    private static void pdfFooter(Document document, double totalAmount) throws DocumentException {
        // Total amount
        Font totalAmountFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph totalAmountParagraph = new Paragraph("Total Amount: " + totalAmount + " €", totalAmountFont);
        totalAmountParagraph.setIndentationRight(20);
        totalAmountParagraph.setAlignment(Element.ALIGN_RIGHT);
        totalAmountParagraph.setSpacingAfter(10);
        document.add(totalAmountParagraph);

        // Footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Paragraph footer = new Paragraph("Altin, Julian, Sandra, Sara", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(50);
        footer.setSpacingAfter(10);
        document.add(footer);

        // Set page margin
        document.setMargins(50, 50, 50, 100);
    }

    /**
     * Creates a cell with the given content and font. If the font is null, the default font is used.
     * also returns Total amount
     * @param invoice invoice to generate PDF for
     * @param pricePerKwh price per kWh
     * @param chargesFont font for the cell
     * @param chargesTable  table to add the cell to
     * @return total amount of the invoice
     */
    private double CreateTableReturnTotal(Invoice invoice, double pricePerKwh, Font chargesFont, PdfPTable chargesTable) {
        int position = 1;
        double totalAmount = 0;

        // Table header
        chargesTable.addCell(createCell("Position", chargesFont));
        chargesTable.addCell(createCell("URL", chargesFont));
        chargesTable.addCell(createCell("kWh", chargesFont));
        chargesTable.addCell(createCell("Price", chargesFont));

        for (Station station : invoice.getLocations()) {
            double kwh = station.getTotalKwh();
            double totalPrice = kwh * pricePerKwh;
            totalAmount += totalPrice;

            chargesTable.addCell(createCell(String.valueOf(position), null));
            chargesTable.addCell(createCell(station.getURLString(), null));
            chargesTable.addCell(createCell(String.valueOf(kwh), null));
            chargesTable.addCell(createCell(String.valueOf(totalPrice), null));

            position++;
        }
        return totalAmount;
    }

    /**
     * Creates a cell with the given content and font.
     * @param document PDF document
     * @param invoice invoice to generate PDF for
     * @throws DocumentException if an error occurs while adding content to the document
     */
    private static void pdfHeader(Document document, Invoice invoice) throws DocumentException {
        // Invoice number
        Font detailsFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph invoiceDetails = new Paragraph();
        invoiceDetails.setIndentationLeft(20);
        invoiceDetails.add(new Chunk("Invoice Number: ", detailsFont));
        invoiceDetails.add(new Chunk(String.valueOf(invoice.getInvoiceNumber()), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        invoiceDetails.setAlignment(Element.ALIGN_RIGHT);
        document.add(invoiceDetails);

        // Customer + Invoice Date
        Font leftDetails = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph invoicedetails = new Paragraph();
        invoicedetails.setIndentationRight(20);
        invoicedetails.add(new Chunk("Customer: ", leftDetails));
        invoicedetails.add(new Chunk(invoice.getCustomer().getFirstName() + " " + invoice.getCustomer().getLastName()));
        invoicedetails.add(Chunk.NEWLINE);
        invoicedetails.add(new Chunk("Invoice Date: ", leftDetails));
        invoicedetails.add(new Chunk(invoice.getInvoiceDate().toString()));
        invoicedetails.setAlignment(Element.ALIGN_LEFT);
        document.add(invoicedetails);
    }

    /**
     * Adds the title to the PDF file.
     * @param document PDF document
     * @throws DocumentException if an error occurs while adding content to the document
     */
    private static void pdfTitle(Document document) throws DocumentException {
        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
        Paragraph title = new Paragraph("FuelStation Data Collector", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(50);
        document.add(title);
    }

    /**
     * Creates a PDF cell with the given content and font.
     * @param content cell content
     * @param font cell font
     * @return PDF cell
     */
    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font != null ? font : FontFactory.getFont(FontFactory.HELVETICA)));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
}
