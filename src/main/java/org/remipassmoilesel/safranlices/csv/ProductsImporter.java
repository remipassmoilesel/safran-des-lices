package org.remipassmoilesel.safranlices.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ProductsImporter {

    private CSVFormat csvFileFormat = CSVFormat.EXCEL;

    public void importProducts(InputStream products, ProductRepository repository) throws IOException {

        Iterable<CSVRecord> records = csvFileFormat.parse(new InputStreamReader(products));
        Iterator<CSVRecord> iter = records.iterator();

        // skip headers
        iter.next();
        iter.next();

        // parse and fill database
        while (iter.hasNext()) {

            CSVRecord record = iter.next();
            Long id = Long.valueOf(record.get(0));

            Product toUpdate = repository.findOne(id);
            if (toUpdate != null) {
                this.setValues(toUpdate, record);
                repository.save(toUpdate);
            } else {
                Product toInsert = new Product();
                this.setValues(toInsert, record);
                repository.save(toInsert);
            }

        }

    }

    private void setValues(Product toUpdate, CSVRecord record) {
        toUpdate.setId(Long.valueOf(record.get(0)));
        toUpdate.setName(record.get(1));
        toUpdate.setPrice(Double.valueOf(record.get(2)));
        toUpdate.setQuantityAvailable(Integer.valueOf(record.get(3)));
        toUpdate.setGrossWeight(Double.valueOf(record.get(4)));
        toUpdate.setNetWeight(Double.valueOf(record.get(5)));
        toUpdate.setDescription(record.get(6));
        toUpdate.setPictures(record.get(7));
    }

}
