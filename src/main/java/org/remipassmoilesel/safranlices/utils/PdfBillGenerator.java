package org.remipassmoilesel.safranlices.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Generate pdf bills using IText
 * <p>
 * Units are 1/72 inch, A4 is 8.27 x 11.69 inches
 */
@Component
public class PdfBillGenerator {

    private static final String TOP_LOGO_LOCATION = "/bill/safran-lices-bill-logo.jpg";
    private static final String ADDRESS_FILE_PATH = "/bill/address.txt";
    private static final float MAX_DOCUMENT_HEIGHT = 750f;
    private byte[] topLogo;

    @Value("${app.bill.rootDirectory}")
    private String billRootDirectoryString;

    /**
     * Do not use direcly this value, use getBillRootDirectory() instead
     */
    private Path billRootDirectoryPath;

    private Font TITLE_1_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private Font TITLE_2_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

    private List<String> addressContent;

    public PdfBillGenerator() {
    }

    public Path generateBill(CommercialOrder order,
                             List<Product> products, double total) throws IOException, DocumentException {

        String pdfName = getPdfName(order);

        // deletePreviousPdf(pdfName);

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

            addTotal(document, total);

            document.close();
        }

        return absoluteDocPath;
    }

    public String getPdfName(Date date, String lastName, String firstName) {
        return Utils.getFormattedDate(date, "yyyy-MM-dd_HH-mm-ss")
                + "_" + lastName + "_" + firstName + ".pdf";
    }

    public String getPdfName(CommercialOrder order) {
        return getPdfName(order.getDate(), order.getLastName(), order.getFirstName());
    }


    public Path getPdfAbsolutePath(String name) {
        return getBillRootDirectory().resolve(name).toAbsolutePath();
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

        HashMap<Product, Integer> productMap = Basket.fromOrder(order).mapProductWithQuantities(products);

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

        table.addCell("Email");
        table.addCell(order.getEmail());

        table.addCell("Téléphone");
        table.addCell(order.getPhonenumber());

        document.add(header);
        document.add(table);
    }

    private void addCommandHeader(Document document, CommercialOrder order) throws DocumentException {

        Paragraph header = new Paragraph();
        header.add(Chunk.NEWLINE);

        // add title with date
        String formattedDate = order.getFormattedDate("dd/MM/yyyy");
        Chunk title = new Chunk("Facture pour la commande du " + formattedDate);
        title.setFont(TITLE_1_FONT);
        header.add(title);
        header.add(Chunk.NEWLINE);

        // add order uid
        Chunk uid = new Chunk("Numéro de facture: " + order.getOrderUid());
        header.add(uid);
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

        Image logo = Image.getInstance(getTopLogo());

        float scale = 0.1f;
        float scaledWidth = logo.getWidth() * scale;
        float scaledheight = logo.getHeight() * scale;
        float xPosition = 298 - scaledWidth / 2;
        float yPosition = MAX_DOCUMENT_HEIGHT;

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

    public List<String> getAddressContent() throws IOException {

        if (addressContent == null) {
            addressContent = IOUtils.readLines(getClass().getResourceAsStream(ADDRESS_FILE_PATH));
        }

        return addressContent;
    }

    public Path getBillRootDirectory() {
        if (billRootDirectoryPath == null) {
            this.billRootDirectoryPath = Paths.get(billRootDirectoryString);
        }
        return billRootDirectoryPath;
    }

    public byte[] getTopLogo() throws IOException {

        if (topLogo == null) {
            topLogo = IOUtils.toByteArray(getClass().getResourceAsStream(TOP_LOGO_LOCATION));
        }

        return topLogo;
    }


}
