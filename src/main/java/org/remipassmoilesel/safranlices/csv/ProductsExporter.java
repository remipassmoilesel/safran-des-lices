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
    private static final Object[] WARNING = {
            "Vous ne devez pas supprimer cet en-tête, ni l'ordre des colonnes. Si vous changez une valeur " +
                    "de la case 'ID', un nouvel élément sera créé.",
    };
    private static final Object[] COLUMN_NAMES = {
            "ID",
            "Nom",
            "Prix",
            "Quantité disponible",
            "Poids brut (grammes)",
            "Poids net (grammes)",
            "Description",
            "Photographies, séparées par des virgules",
    };

    public void export(List<Product> products, BufferedWriter writer) throws IOException {

        try (CSVPrinter csvFilePrinter = new CSVPrinter(writer, csvFileFormat)) {

            csvFilePrinter.printRecord(WARNING);
            csvFilePrinter.printRecord(COLUMN_NAMES);

            for (Product p : products) {
                System.out.println(p);
                List record = new ArrayList();
                record.add(p.getId());
                record.add(p.getName());
                record.add(p.getPrice());
                record.add(p.getQuantityAvailable());
                record.add(p.getGrossWeight());
                record.add(p.getNetWeight());
                record.add(p.getDescription());
                record.add(p.getPictures());
                csvFilePrinter.printRecord(record);
            }

        } finally {
            writer.flush();
        }

    }

}
