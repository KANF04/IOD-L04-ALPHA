package pl.put.poznan.transformer.rest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.transformer.logic.TextTransformer;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

import java.util.Arrays;


@Controller
public class TextTransformerController {

    private static final Logger logger = LoggerFactory.getLogger(TextTransformerController.class);

//    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
//    public String get(@PathVariable String text,
//                              @RequestParam(value="transforms", defaultValue="upper,escape") String[] transforms) {
//
//        // log the parameters
//        logger.debug(text);
//        logger.debug(Arrays.toString(transforms));
//
//        // perform the transformation, you should run your logic here, below is just a silly example
//        TextTransformer transformer = new TextTransformer(transforms);
//        return transformer.transform(text);
//    }
//
//    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
//    public String post(@PathVariable String text,
//                      @RequestBody String[] transforms) {
//
//        // log the parameters
//        logger.debug(text);
//        logger.debug(Arrays.toString(transforms));
//
//        // perform the transformation, you should run your logic here, below is just a silly example
//        TextTransformer transformer = new TextTransformer(transforms);
//        return transformer.transform(text);
//    }


    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }

    @PostMapping("/uploadJson")
    public String uploadJson(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("upload-", ".json");
            file.transferTo(tempFile);

            TextTransformer transformer = new TextTransformer(new String[]{});
            transformer.transform(tempFile);

            return "redirect:/calculate.html";

        } catch (Exception e) {
            e.printStackTrace();
            return "Błąd przy przetwarzaniu pliku.";
        }
    }

}


