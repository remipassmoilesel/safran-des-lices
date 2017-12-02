package org.remipassmoilesel.safranlices.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ShippingCostsImporter {

    private CSVFormat csvFileFormat = CSVFormat.EXCEL;

    public void importShippingCosts(InputStream sourceStream, ShippingCostRepository repository) throws IOException {

        Iterable<CSVRecord> records = csvFileFormat.parse(new InputStreamReader(sourceStream));
        Iterator<CSVRecord> iter = records.iterator();

        // skip headers
        iter.next();
        iter.next();

        // parse and fill database
        while (iter.hasNext()) {

            CSVRecord record = iter.next();
            Long id = Long.valueOf(record.get(0));

            ShippingCost toUpdate = repository.findOne(id);
            if (toUpdate != null) {
                this.setValues(toUpdate, record);
                repository.save(toUpdate);
            } else {
                ShippingCost toInsert = new ShippingCost();
                this.setValues(toInsert, record);
                repository.save(toInsert);
            }

        }

    }

    private void setValues(ShippingCost toUpdate, CSVRecord record) {
        toUpdate.setId(Long.valueOf(record.get(0)));
        toUpdate.setPrice(Double.valueOf(record.get(1)));
        toUpdate.setMinWeight(Double.valueOf(record.get(2)));
        toUpdate.setMaxWeight(Double.valueOf(record.get(3)));
    }

}
