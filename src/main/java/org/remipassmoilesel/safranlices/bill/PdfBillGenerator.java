package org.remipassmoilesel.safranlices.bill;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Generate pdf bills using IText
 * <p>
 * Units are 1/72 inch, A4 is 8.27 x 11.69 inches
 */
public class PdfBillGenerator {

    private static final float MAX_HEIGHT = 750f;

    public static Path PDF_ROOT = Paths.get("./pdf-bills");
    public static Path LOGO_PATH = Paths.get("./src/main/resources/bill/safran-lices-logo.jpg").toAbsolutePath();
    public static Path ADDRESS_FILE_PATH = Paths.get("./src/main/resources/bill/address.txt").toAbsolutePath();

    private static List<String> addressContent;

    public Path generateBill(String name, CommercialOrder order, List<Product> products) throws IOException, DocumentException {

        createPdfRoot();
        deletePreviousPdf(name);

        Path absoluteDocPath = getPdfAbsolutePath(name);
        Document document = new Document();

        try (OutputStream docOutput = Files.newOutputStream(absoluteDocPath)) {
            PdfWriter.getInstance(document, docOutput);

            document.open();

            addLogo(document);
            addAddressBloc(document);


            document.close();
        }

        return absoluteDocPath;
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
