package org.remipassmoilesel.safranlices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class UtilsTest {


}
