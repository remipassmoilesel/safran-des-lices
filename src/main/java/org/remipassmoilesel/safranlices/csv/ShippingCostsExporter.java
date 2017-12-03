package org.remipassmoilesel.safranlices.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.remipassmoilesel.safranlices.entities.ShippingCost;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShippingCostsExporter {

    private CSVFormat csvFileFormat = CSVFormat.EXCEL;
    private static final Object[] WARNING = {
            "Vous ne devez pas supprimer cet en-tête, ni l'ordre des colonnes. Si vous changez une valeur " +
                    "de la case 'ID', un nouvel élément sera créé.",
    };
    private static final Object[] COLUMN_NAMES = {
            "ID",
            "Prix",
            "Poids minimum",
            "Poids maximum",
    };

    public void export(List<ShippingCost> shippingCosts, BufferedWriter writer) throws IOException {

        try (CSVPrinter csvFilePrinter = new CSVPrinter(writer, csvFileFormat)) {

            csvFilePrinter.printRecord(WARNING);
            csvFilePrinter.printRecord(COLUMN_NAMES);

            for (ShippingCost s : shippingCosts) {
                System.out.println(s);
                List record = new ArrayList();
                record.add(s.getId());
                record.add(s.getPrice());
                record.add(s.getMinWeight());
                record.add(s.getMaxWeight());
                csvFilePrinter.printRecord(record);
            }

        } finally {
            writer.flush();
        }

    }

}
