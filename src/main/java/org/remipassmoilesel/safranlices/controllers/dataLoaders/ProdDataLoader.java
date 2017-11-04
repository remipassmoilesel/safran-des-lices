package org.remipassmoilesel.safranlices.controllers.dataLoaders;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.remipassmoilesel.safranlices.SafranLicesApplication;
import org.remipassmoilesel.safranlices.entities.Expense;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * Created by remipassmoilesel on 12/06/17.
 */

@Component
public class ProdDataLoader implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(ProdDataLoader.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private Environment env;

    @Value("${app.product-source}")
    private String productSourcePath;

    @Value("${app.expense-source}")
    private String expenseSourcePath;

    @Value("${app.db-name}")
    private String dbName;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.warn("Database name: " + dbName);

        // TODO: set prod profile
        if (Arrays.asList(env.getActiveProfiles()).contains(SafranLicesApplication.PROD_PROFILE) == false) {
            return;
        }

        Path productSource = Paths.get(productSourcePath);
        if (Files.isRegularFile(productSource)) {

            // delete old products
            markAllProductsAsDeleted();

            // populate products
            try {
                processJsonProductSource(productSource);
                logger.warn("-- Products added from source: " + productSourcePath);
            } catch (Exception e) {
                logger.error("-- Unable to read source: " + productSourcePath, e);
            }

        }

        Path expenseSource = Paths.get(expenseSourcePath);
        if (Files.isRegularFile(expenseSource)) {

            expenseRepository.deleteAll();

            try {
                processJsonExpenseSource(expenseSource);
                logger.warn("-- Exepenses added from source: " + expenseSourcePath);
            } catch (Exception e) {
                logger.error("-- Unable to read source: " + expenseSourcePath, e);
            }

        }

    }

    private void markAllProductsAsDeleted() {
        List<Product> prods = productRepository.findAll(false);
        for (Product p : prods) {
            p.setDeleted(true);
            productRepository.save(p);
        }
    }

    private void processJsonProductSource(Path filePath) throws IOException, ParseException {

        InputStream inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);

        String sourceJson = IOUtils.toString(inputStream);
        JSONArray sourceJsonArray = (JSONArray) JSONValue.parseWithException(sourceJson);

        for (int i = 0; i < sourceJsonArray.size(); i++) {

            JSONObject el = (JSONObject) sourceJsonArray.get(i);
            String name = (String) el.get("name");
            Double price = Double.valueOf(el.get("price").toString());
            String description = (String) el.get("description");
            String pictures = (String) el.get("pictures");
            Integer quantity = Integer.valueOf(el.get("quantity").toString());

            Product p = new Product();
            p.setName(name);
            p.setPrice(price);
            p.setDescription(description);
            p.setPictures(pictures);
            p.setQuantityAvailable(quantity);

            productRepository.save(p);
        }

    }

    private void processJsonExpenseSource(Path filePath) throws IOException, ParseException {

        InputStream inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);

        String sourceJson = IOUtils.toString(inputStream);
        JSONArray sourceJsonArray = (JSONArray) JSONValue.parseWithException(sourceJson);

        for (int i = 0; i < sourceJsonArray.size(); i++) {

            JSONObject el = (JSONObject) sourceJsonArray.get(i);
            String name = (String) el.get("name");
            Double value = Double.valueOf(el.get("value").toString());

            Expense e = new Expense();
            e.setName(name);
            e.setValue(value);

            expenseRepository.save(e);
        }

    }

}
