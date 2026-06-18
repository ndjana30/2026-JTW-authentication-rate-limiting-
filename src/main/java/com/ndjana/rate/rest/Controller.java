package com.ndjana.rate.rest;

import com.ndjana.rate.models.mock.MockFreeUser;
import com.ndjana.rate.pricing.PricingPlanService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
public class Controller {

    @Autowired
    public PricingPlanService pricingPlanService;
    @GetMapping("username")
    public ResponseEntity<String> createMockUser(@RequestHeader(value = "X-api-key") String apiKey)
    {
        MockFreeUser freeUser = new MockFreeUser();
        freeUser.setUsername("turner");
        String name = freeUser.getUsername();
        Bucket bucket  = pricingPlanService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
        {
            return ResponseEntity.ok().header("X-Rate-Limit-Remaining",Long.toString(probe.getRemainingTokens()))
                    .body(name);
        }
        long waitForRefill = probe.getNanosToWaitForRefill()/ 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("x-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                .build();

    }
}
