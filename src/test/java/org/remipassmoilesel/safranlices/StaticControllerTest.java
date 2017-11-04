package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.StaticController;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class StaticControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private StaticController staticController;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(staticController).build();
    }

    @Test
    public void getPages() throws Exception {

        mockMvc.perform(get(Mappings.ROOT))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.OUR_SAFRAN))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.OUR_EXPLOITATION))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.LEGAL_MENTIONS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

    }

    @Test
    @Ignore
    public void testIfDependenciesInstalled() throws Exception {

        List<String> dependencies = Arrays.asList(
                "favicon.ico",
                "bower_components/imagehover.css/css/imagehover.min.css",
                "bower_components/font-awesome/css/font-awesome.min.css",

                "bower_components/lightbox2/dist/css/lightbox.min.css",
                "bower_components/lightbox2/dist/js/lightbox.js",

                "bower_components/bootstrap/dist/css/bootstrap.min.css",
                "bower_components/bootstrap/dist/js/bootstrap.min.js",

                "bower_components/jquery/dist/jquery.js",
                "bower_components/jquery/dist/jquery.min.js",
                "bower_components/jquery-easing/jquery.easing.min.js",

                "bower_components/leaflet/dist/leaflet.css",
                "bower_components/leaflet/dist/leaflet.js"
        );

        // test if ressources are available
        for(String s : dependencies){
            mockMvc.perform(get(s)).andExpect(status().isOk());
        }
    }

}
