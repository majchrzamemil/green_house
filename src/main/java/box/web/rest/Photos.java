/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package box.web.rest;

import box.domain.ProfileSettings;
import com.codahale.metrics.annotation.Timed;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author emil
 */
@RestController
@RequestMapping("/api")
public class Photos {
     @GetMapping("/photos")
    @Timed
    public List<String> ls() throws IOException {
                  Runtime runtime = Runtime.getRuntime();
    String command = "ls /home/macek/Documents/projects/green_house/target/green-house-0.0.1-SNAPSHOT/content/images/plant_pictures";
    Process proc = null;
         try {
             proc = runtime.exec(command);
         } catch (IOException ex) {
             Logger.getLogger(Photos.class.getName()).log(Level.SEVERE, null, ex);
         }
    BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
    String output = null;
    List<String> photos = new ArrayList<>();
    while((output = input.readLine()) != null){
      photos.add(output);
    }
        return photos;
    }

}
