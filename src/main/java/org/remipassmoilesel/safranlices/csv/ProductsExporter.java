package org.remipassmoilesel.safranlices.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.remipassmoilesel.safranlices.entities.Product;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductsExporter {

    private CSVFormat csvFileFormat = CSVFormat.EXCEL;
    private static final Object[] FILE_HEADER = {
            "ID",
            "Nom",
            "Prix",
            "Quantitié disponible",
            "Poids brut (grammes)",
            "Poids net (grammes)",
            "Description",
    };

    public void export(List<Product> products, BufferedWriter writer) throws IOException {

        try (CSVPrinter csvFilePrinter = new CSVPrinter(writer, csvFileFormat)) {

            csvFilePrinter.printRecord(FILE_HEADER);


            for (Product p : products) {
                System.out.println(p);
                List productRecord = new ArrayList();
                productRecord.add(p.getId());
                productRecord.add(p.getName());
                productRecord.add(p.getPrice());
                productRecord.add(p.getQuantityAvailable());
                productRecord.add(p.getGrossWeight());
                productRecord.add(p.getNetWeight());
                productRecord.add(p.getDescription());
                csvFilePrinter.printRecord(productRecord);
            }

        } finally {
            writer.flush();
        }

    }

}
