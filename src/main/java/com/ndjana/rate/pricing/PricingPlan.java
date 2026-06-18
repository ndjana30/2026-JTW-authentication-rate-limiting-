package com.ndjana.rate.pricing;

import io.github.bucket4j.Bandwidth;
import java.time.Duration;

enum PricingPlan {
    FREE{

       public Bandwidth getLimit(){
            return Bandwidth.builder().capacity(3).refillIntervally(3,Duration.ofHours(1)).build();
        }
    }
    ,
    BASIC{

       public  Bandwidth getLimit(){
            return Bandwidth.builder().capacity(40).refillIntervally(40 ,Duration.ofHours(1)).build();
        }
    }
    ,
    PREMIUM{

         public Bandwidth getLimit()
        {
            return Bandwidth.builder().capacity(60).refillIntervally(60,Duration.ofHours(1)).build();
        }
    };



    static PricingPlan resolvePlanFromApiKey(String apiKey)
    {
        if (apiKey == null || apiKey.isEmpty())
        {
            return FREE;
        } else if (apiKey.startsWith("BP001-")) {
            return BASIC;
        } else if (apiKey.startsWith("PP001")) {
            return PREMIUM;
        }
        return FREE;
    }

    public abstract Bandwidth getLimit();
}
