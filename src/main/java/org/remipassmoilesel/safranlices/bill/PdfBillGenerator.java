package org.remipassmoilesel.safranlices.bill;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Expense;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Generate pdf bills using IText
 * <p>
 * Units are 1/72 inch, A4 is 8.27 x 11.69 inches
 */
public class PdfBillGenerator {

    private static final float MAX_HEIGHT = 750f;

    public static Path PDF_ROOT = Paths.get("./pdf-bills");
    private static Path LOGO_PATH = Paths.get("./src/main/resources/bill/safran-lices-logo.jpg").toAbsolutePath();
    private static Path ADDRESS_FILE_PATH = Paths.get("./src/main/resources/bill/address.txt").toAbsolutePath();

    private static Font TITLE_1_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font TITLE_2_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    private static List<String> addressContent;

    public Path generateBill(CommercialOrder order,
                             List<Product> products, double total) throws IOException, DocumentException {

        String pdfName = getPdfName(order);

        createPdfRoot();
        deletePreviousPdf(pdfName);

        Path absoluteDocPath = getPdfAbsolutePath(pdfName);
        Document document = new Document();

        try (OutputStream docOutput = Files.newOutputStream(absoluteDocPath)) {
            PdfWriter.getInstance(document, docOutput);

            document.open();

            addLogo(document);
            addAddressBloc(document);

            addCommandHeader(document, order);
            addCustomerInformations(document, order);
            addOrder(document, order, products);
            addExpenses(document, order);

            addTotal(document, total);

            document.close();
        }

        return absoluteDocPath;
    }

    private void addExpenses(Document document, CommercialOrder order) throws DocumentException {

        Paragraph header = new Paragraph();

        header.add(Chunk.NEWLINE);
        Chunk title = new Chunk("Frais");
        title.setFont(TITLE_2_FONT);
        header.add(title);
        header.add(Chunk.NEWLINE);
        header.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);

        table.addCell("Désignation");
        table.addCell("Montant");

        for (Expense expense : order.getExpenses()) {
            table.addCell(expense.getName());
            table.addCell(String.valueOf(expense.getValue()));
        }

        document.add(header);
        document.add(table);
    }

    private void addTotal(Document document, Double total) throws DocumentException {

        Paragraph content = new Paragraph();

        content.add(Chunk.NEWLINE);
        Chunk title = new Chunk("Total: " + total + " €");
        title.setFont(TITLE_2_FONT);
        content.add(title);
        content.add(Chunk.NEWLINE);
        content.add(Chunk.NEWLINE);

        document.add(content);
    }

    private void addOrder(Document document, CommercialOrder order, List<Product> products) throws DocumentException {
        Paragraph header = new Paragraph();

        header.add(Chunk.NEWLINE);
        Chunk title = new Chunk("Détail de la commande");
        title.setFont(TITLE_2_FONT);
        header.add(title);
        header.add(Chunk.NEWLINE);
        header.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(3);

        table.addCell("Article");
        table.addCell("Prix unitaire");
        table.addCell("Quantité");

        HashMap<Product, Integer> productMap = Utils.mapProductWithQuantities(products, order);

        Iterator<Product> iter = productMap.keySet().iterator();
        while (iter.hasNext()) {
            Product product = iter.next();
            Integer quantity = productMap.get(product);

            table.addCell(product.getName());
            table.addCell(String.valueOf(product.getPrice()));
            table.addCell(String.valueOf(quantity));
        }

        document.add(header);
        document.add(table);
    }

    private void addCustomerInformations(Document document, CommercialOrder order) throws DocumentException {
        Paragraph header = new Paragraph();

        header.add(Chunk.NEWLINE);
        Chunk title = new Chunk("Informations client");
        title.setFont(TITLE_2_FONT);
        header.add(title);
        header.add(Chunk.NEWLINE);
        header.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);

        table.addCell("Nom");
        table.addCell(order.getLastName());

        table.addCell("Prénom");
        table.addCell(order.getFirstName());

        table.addCell("Adresse");
        table.addCell(order.getAddress() + " " + order.getPostalcode() + " " + order.getCity());

        table.addCell("Adresse de livraison");
        table.addCell(order.getShipmentAddress() + " " + order.getShipmentPostalcode() + " " + order.getShipmentCity());

        table.addCell("Téléphone");
        table.addCell(order.getPhonenumber());

        document.add(header);
        document.add(table);
    }

    private void addCommandHeader(Document document, CommercialOrder order) throws DocumentException {

        Paragraph header = new Paragraph();
        header.add(Chunk.NEWLINE);
        header.add(Chunk.NEWLINE);

        String formattedDate = order.getFormattedDate("dd/MM/yyyy");
        Chunk title = new Chunk("Facture pour la commande du " + formattedDate);
        title.setFont(TITLE_1_FONT);

        header.add(title);
        header.add(Chunk.NEWLINE);

        document.add(header);

    }

    private void addAddressBloc(Document document) throws IOException, DocumentException {

        Paragraph addressParagraph = new Paragraph();

        for (String l : getAddressContent()) {
            addressParagraph.add(new Chunk(l));
            addressParagraph.add(Chunk.NEWLINE);
        }

        document.add(addressParagraph);

    }

    private void addLogo(Document document) throws IOException, DocumentException {

        Image logo = Image.getInstance(LOGO_PATH.toString());

        float scale = 0.1f;
        float scaledWidth = logo.getWidth() * scale;
        float scaledheight = logo.getHeight() * scale;
        float xPosition = 298 - scaledWidth / 2;
        float yPosition = MAX_HEIGHT;

        logo.setAbsolutePosition(xPosition, yPosition);
        logo.scaleAbsolute(scaledWidth, scaledheight);

        document.add(logo);

    }

    private void deletePreviousPdf(String name) throws IOException {
        Path pdfPath = getPdfAbsolutePath(name);
        if (Files.isRegularFile(pdfPath)) {
            Files.delete(pdfPath);
        }
    }

    private String getPdfName(CommercialOrder order) {
        return order.getFormattedDate("yyyy-MM-dd_HH-mm-ss") + "_" + order.getLastName() + "_" + order.getFirstName() + ".pdf";
    }

    private Path getPdfAbsolutePath(String name) {
        return PDF_ROOT.resolve(name).toAbsolutePath();
    }

    private void createPdfRoot() throws IOException {
        if (Files.isDirectory(PDF_ROOT) == false) {
            Files.createDirectories(PDF_ROOT);
        }
    }

    public List<String> getAddressContent() throws IOException {

        if (addressContent == null) {
            addressContent = Files.readAllLines(ADDRESS_FILE_PATH, StandardCharsets.UTF_8);
        }

        return addressContent;
    }
}
