package com.neighborhood.beapp.web.rest;

import com.neighborhood.beapp.domain.DeviceDetails;
import com.neighborhood.beapp.domain.NGUser;
import com.neighborhood.beapp.domain.enumeration.Status;
import com.neighborhood.beapp.repository.DeviceDetailsRepository;
import com.neighborhood.beapp.repository.NGUserRepository;
import com.neighborhood.beapp.repository.search.NGUserSearchRepository;
import com.neighborhood.beapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.neighborhood.beapp.domain.NGUser}.
 */
@RestController
@RequestMapping("/api")
public class NGUserResource {

    private final Logger log = LoggerFactory.getLogger(NGUserResource.class);

    private static final String ENTITY_NAME = "beAppNgUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NGUserRepository nGUserRepository;

    private final NGUserSearchRepository nGUserSearchRepository;

    private final DeviceDetailsRepository deviceDetailsRepository;

    public NGUserResource(NGUserRepository nGUserRepository, NGUserSearchRepository nGUserSearchRepository, DeviceDetailsRepository deviceDetailsRepository) {
        this.nGUserRepository = nGUserRepository;
        this.nGUserSearchRepository = nGUserSearchRepository;
        this.deviceDetailsRepository = deviceDetailsRepository;
    }

    /**
     * {@code POST  /ng-users} : Create a new nGUser.
     *
     * @param nGUser the nGUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nGUser, or with status {@code 400 (Bad Request)} if the nGUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ng-users")
    public ResponseEntity<NGUser> createNGUser(@Valid @RequestBody NGUser nGUser) throws URISyntaxException {
        log.debug("REST request to save NGUser : {}", nGUser);
        if (nGUser.getId() != null) {
            throw new BadRequestAlertException("A new nGUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NGUser result = nGUserRepository.save(nGUser);
        nGUserSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ng-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /ng-users} : Create a new nGUser.
     *
     * @param nGUser the nGUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new nGUser, or with status {@code 400 (Bad Request)} if the nGUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ng-users-phonenumber-capture")
    public ResponseEntity<NGUser> createNGUserWithPhoneNumber(@Valid @RequestBody NGUser nGUser) throws URISyntaxException {
        log.debug("REST request to save NGUser : {}", nGUser);
        if (nGUser.getId() != null) {
            throw new BadRequestAlertException("A new nGUser cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Random rnd = new Random();
        int oneTimeCode = 100000 + rnd.nextInt(900000);
        System.out.println("OTC --> "+oneTimeCode);
        Calendar otcExpiration = Calendar.getInstance();
        otcExpiration.add(Calendar.MINUTE, 30);

        nGUser.setOneTimeCode(String.valueOf(oneTimeCode));
        nGUser.setOneTimeExpirationTime(otcExpiration.toInstant());

        nGUser.setStatus(Status.INVITED);

        NGUser result = nGUserRepository.save(nGUser);
        nGUserSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ng-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PostMapping("/ng-users-verify-token")
    public ResponseEntity<NGUser> createNGUserVerifyToken(@Valid @RequestBody Map nGUser) throws URISyntaxException {
        log.debug("REST request to save NGUser : {}", nGUser);

        // Change this to session
        if (nGUser.get("id") == null) {
            throw new BadRequestAlertException("Request Needs to Have an ID", ENTITY_NAME, "idnotpresent");
        }

        NGUser ngUserFromRep = nGUserRepository.findUserById((String)nGUser.get("id"));
        // Check User Status
        if(!Status.INVITED.equals(ngUserFromRep.getStatus()))
        {
            throw new BadRequestAlertException("User Already Confirmed",ENTITY_NAME,"alreadyConfirmed");
            // Redirect to Dashboard
        }
        // Compare OTC Code & TimeStamp

        Instant currentTime = Instant.now();
        if(currentTime.isAfter(ngUserFromRep.getOneTimeExpirationTime()))
        {
            throw new BadRequestAlertException("Code Expired. Please request for another code",ENTITY_NAME,"codeExpired");
        }

        if(null!=nGUser.get("oneTimeCode") && ((String)nGUser.get("oneTimeCode")).equalsIgnoreCase(ngUserFromRep.getOneTimeCode()))
        {
            System.out.println("Valid Code. Customer Authenticated");

            DeviceDetails deviceDetails = new DeviceDetails();
            deviceDetails.setDeviceId("testWindows");
//            deviceDetails.setNGUser(ngUserFromRep);
            DeviceDetails deviceDetailsFromRep = deviceDetailsRepository.save(deviceDetails);

            Set<DeviceDetails> devices = new HashSet<>();
            devices.add(deviceDetailsFromRep);

            ngUserFromRep.setDevices(devices);

            ngUserFromRep.setStatus(Status.CONFIRMED);
        }
        else
        {
            throw new BadRequestAlertException("Code Mismatch. Please reenter the code",ENTITY_NAME,"codeMisMatch");
        }
        NGUser result = nGUserRepository.save(ngUserFromRep);
        nGUserSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ng-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ng-users} : Updates an existing nGUser.
     *
     * @param nGUser the nGUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated nGUser,
     * or with status {@code 400 (Bad Request)} if the nGUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the nGUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ng-users")
    public ResponseEntity<NGUser> updateNGUser(@Valid @RequestBody NGUser nGUser) throws URISyntaxException {
        log.debug("REST request to update NGUser : {}", nGUser);
        if (nGUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NGUser result = nGUserRepository.save(nGUser);
        nGUserSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, nGUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /ng-users} : get all the nGUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of nGUsers in body.
     */
    @GetMapping("/ng-users")
    public List<NGUser> getAllNGUsers() {
        log.debug("REST request to get all NGUsers");
        return nGUserRepository.findAll();
    }

    /**
     * {@code GET  /ng-users/:id} : get the "id" nGUser.
     *
     * @param id the id of the nGUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the nGUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ng-users/{id}")
    public ResponseEntity<NGUser> getNGUser(@PathVariable String id) {
        log.debug("REST request to get NGUser : {}", id);
        Optional<NGUser> nGUser = nGUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(nGUser);
    }

    /**
     * {@code DELETE  /ng-users/:id} : delete the "id" nGUser.
     *
     * @param id the id of the nGUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ng-users/{id}")
    public ResponseEntity<Void> deleteNGUser(@PathVariable String id) {
        log.debug("REST request to delete NGUser : {}", id);
        nGUserRepository.deleteById(id);
        nGUserSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/ng-users?query=:query} : search for the nGUser corresponding
     * to the query.
     *
     * @param query the query of the nGUser search.
     * @return the result of the search.
     */
    @GetMapping("/_search/ng-users")
    public List<NGUser> searchNGUsers(@RequestParam String query) {
        log.debug("REST request to search NGUsers for query {}", query);
        return StreamSupport
            .stream(nGUserSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
