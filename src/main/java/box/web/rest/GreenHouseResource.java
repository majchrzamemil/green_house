package box.web.rest;

import com.codahale.metrics.annotation.Timed;
import box.domain.GreenHouse;

import box.repository.GreenHouseRepository;
import box.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing GreenHouse.
 */
@RestController
@RequestMapping("/api")
public class GreenHouseResource {

    private final Logger log = LoggerFactory.getLogger(GreenHouseResource.class);

    private static final String ENTITY_NAME = "greenHouse";
        
    private final GreenHouseRepository greenHouseRepository;

    public GreenHouseResource(GreenHouseRepository greenHouseRepository) {
        this.greenHouseRepository = greenHouseRepository;
    }

    /**
     * POST  /green-houses : Create a new greenHouse.
     *
     * @param greenHouse the greenHouse to create
     * @return the ResponseEntity with status 201 (Created) and with body the new greenHouse, or with status 400 (Bad Request) if the greenHouse has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/green-houses")
    @Timed
    public ResponseEntity<GreenHouse> createGreenHouse(@RequestBody GreenHouse greenHouse) throws URISyntaxException {
        log.debug("REST request to save GreenHouse : {}", greenHouse);
        if (greenHouse.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new greenHouse cannot already have an ID")).body(null);
        }
        GreenHouse result = greenHouseRepository.save(greenHouse);
        return ResponseEntity.created(new URI("/api/green-houses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /green-houses : Updates an existing greenHouse.
     *
     * @param greenHouse the greenHouse to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated greenHouse,
     * or with status 400 (Bad Request) if the greenHouse is not valid,
     * or with status 500 (Internal Server Error) if the greenHouse couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/green-houses")
    @Timed
    public ResponseEntity<GreenHouse> updateGreenHouse(@RequestBody GreenHouse greenHouse) throws URISyntaxException {
        log.debug("REST request to update GreenHouse : {}", greenHouse);
        if (greenHouse.getId() == null) {
            return createGreenHouse(greenHouse);
        }
        GreenHouse result = greenHouseRepository.save(greenHouse);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, greenHouse.getId().toString()))
            .body(result);
    }

    /**
     * GET  /green-houses : get all the greenHouses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of greenHouses in body
     */
    @GetMapping("/green-houses")
    @Timed
    public List<GreenHouse> getAllGreenHouses() {
        log.debug("REST request to get all GreenHouses");
        List<GreenHouse> greenHouses = greenHouseRepository.findAllWithEagerRelationships();
        return greenHouses;
    }

    /**
     * GET  /green-houses/:id : get the "id" greenHouse.
     *
     * @param id the id of the greenHouse to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the greenHouse, or with status 404 (Not Found)
     */
    @GetMapping("/green-houses/{id}")
    @Timed
    public ResponseEntity<GreenHouse> getGreenHouse(@PathVariable Long id) {
        log.debug("REST request to get GreenHouse : {}", id);
        GreenHouse greenHouse = greenHouseRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(greenHouse));
    }

    /**
     * DELETE  /green-houses/:id : delete the "id" greenHouse.
     *
     * @param id the id of the greenHouse to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/green-houses/{id}")
    @Timed
    public ResponseEntity<Void> deleteGreenHouse(@PathVariable Long id) {
        log.debug("REST request to delete GreenHouse : {}", id);
        greenHouseRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
