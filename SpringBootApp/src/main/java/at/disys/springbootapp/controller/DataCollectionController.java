package at.disys.springbootapp.controller;

import at.disys.springbootapp.service.DataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/***
 * This class is responsible for the REST API. <br>
 * There are two API routes: <br>
 *
 *  <li>Description:</li> <br>
 *  <b>/invoices/<customer-id> [POST] </b><br>
 *  <li>Starts data gathering job</li><br>
 *  <b>/invoices/<customer-id> [GET]</b> <br>
 *  <li>Returns invoice PDF with download link and creation time</li>
 *  <li>Returns 404 Not Found, if it’s not available</li>
 *
 */
@RequestMapping("/api/invoices")
@RestController
public class DataCollectionController {
    DataCollectionService dataCollectionService;

    /**
     * Returns invoice PDF with download link and creation time
     * Returns 404 Not Found, if it’s not available
     * @param customerId customer id
     * @return invoice PDF with download link and creation time
     */
    @GetMapping("/{customer-id}")
    public String getInvoice(@PathVariable("customer-id") String customerId) {
        return customerId;
    }

    /**
     * Starts data gathering job
     * @param customerId customer id
     * @return 202 Accepted
     */
    @PostMapping("/{customer-id}")
    public ResponseEntity<Void> startDataGathering(@PathVariable("customer-id") String customerId) {
        dataCollectionService.publishDataGatheringJob(customerId);
        return ResponseEntity.accepted().build();
    }

    @Autowired
    public void setDataCollectionService(DataCollectionService dataCollectionService) {
        this.dataCollectionService = dataCollectionService;
    }
}
