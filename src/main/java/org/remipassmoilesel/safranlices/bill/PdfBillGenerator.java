package org.remipassmoilesel.safranlices.bill;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PdfBillGenerator {

    public static Path PDF_ROOT = Paths.get("./pdf-bills");
    public static Path LOGO_PATH = Paths.get("./src/main/resources/bill/safran-lices-logo.jpg");

    public Path generateBill(String name, CommercialOrder order, List<Product> products) throws IOException {

        createPdfRoot();
        deletePreviousPdf(name);

        PDDocument document = new PDDocument();

        PDPage firstPage = new PDPage();
        document.addPage(firstPage);

        PDPageContentStream contentStream = new PDPageContentStream(document, firstPage);

        addLogo(contentStream, document);

        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        //Setting the position for the line
        contentStream.newLineAtOffset(25, 25);

        String text = "This is the sample document and we are adding content to it.";

        //Adding text in the form of string
        contentStream.showText(text);

        //Ending the content stream
        contentStream.endText();

        System.out.println("Content added");

        //Closing the content stream
        contentStream.close();

        Path absolutePath = getPdfAbsolutePath(name);
        document.save(absolutePath.toString());

        return absolutePath;
    }

    private void addLogo(PDPageContentStream contentStream, PDDocument document) throws IOException {

        PDImageXObject pdImage = PDImageXObject.createFromFile(LOGO_PATH.toAbsolutePath().toString(), document);

        float scale = 0.1f;
        contentStream.drawImage(pdImage, 100, 100, pdImage.getWidth() * scale,
                pdImage.getHeight() * scale);

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

}
