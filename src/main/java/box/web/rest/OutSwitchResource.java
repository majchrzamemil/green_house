package box.web.rest;

import com.codahale.metrics.annotation.Timed;
import box.domain.OutSwitch;

import box.repository.OutSwitchRepository;
import box.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OutSwitch.
 */
@RestController
@RequestMapping("/api")
public class OutSwitchResource {

    private final Logger log = LoggerFactory.getLogger(OutSwitchResource.class);

    private static final String ENTITY_NAME = "outSwitch";
        
    private final OutSwitchRepository outSwitchRepository;

    public OutSwitchResource(OutSwitchRepository outSwitchRepository) {
        this.outSwitchRepository = outSwitchRepository;
    }

    /**
     * POST  /out-switches : Create a new outSwitch.
     *
     * @param outSwitch the outSwitch to create
     * @return the ResponseEntity with status 201 (Created) and with body the new outSwitch, or with status 400 (Bad Request) if the outSwitch has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/out-switches")
    @Timed
    public ResponseEntity<OutSwitch> createOutSwitch(@Valid @RequestBody OutSwitch outSwitch) throws URISyntaxException {
        log.debug("REST request to save OutSwitch : {}", outSwitch);
        if (outSwitch.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new outSwitch cannot already have an ID")).body(null);
        }
        OutSwitch result = outSwitchRepository.save(outSwitch);
        return ResponseEntity.created(new URI("/api/out-switches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /out-switches : Updates an existing outSwitch.
     *
     * @param outSwitch the outSwitch to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated outSwitch,
     * or with status 400 (Bad Request) if the outSwitch is not valid,
     * or with status 500 (Internal Server Error) if the outSwitch couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/out-switches")
    @Timed
    public ResponseEntity<OutSwitch> updateOutSwitch(@Valid @RequestBody OutSwitch outSwitch) throws URISyntaxException {
        log.debug("REST request to update OutSwitch : {}", outSwitch);
        if (outSwitch.getId() == null) {
            return createOutSwitch(outSwitch);
        }
        OutSwitch result = outSwitchRepository.save(outSwitch);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, outSwitch.getId().toString()))
            .body(result);
    }

    /**
     * GET  /out-switches : get all the outSwitches.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of outSwitches in body
     */
    @GetMapping("/out-switches")
    @Timed
    public List<OutSwitch> getAllOutSwitches() {
        log.debug("REST request to get all OutSwitches");
        List<OutSwitch> outSwitches = outSwitchRepository.findAll();
        return outSwitches;
    }

    /**
     * GET  /out-switches/:id : get the "id" outSwitch.
     *
     * @param id the id of the outSwitch to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the outSwitch, or with status 404 (Not Found)
     */
    @GetMapping("/out-switches/{id}")
    @Timed
    public ResponseEntity<OutSwitch> getOutSwitch(@PathVariable Long id) {
        log.debug("REST request to get OutSwitch : {}", id);
        OutSwitch outSwitch = outSwitchRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(outSwitch));
    }

    /**
     * DELETE  /out-switches/:id : delete the "id" outSwitch.
     *
     * @param id the id of the outSwitch to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/out-switches/{id}")
    @Timed
    public ResponseEntity<Void> deleteOutSwitch(@PathVariable Long id) {
        log.debug("REST request to delete OutSwitch : {}", id);
        outSwitchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
