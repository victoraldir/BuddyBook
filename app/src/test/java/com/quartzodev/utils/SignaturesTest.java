package com.quartzodev.utils;

import org.junit.Assert;
import org.junit.Test;


public class SignaturesTest {

    @Test
    public void testAmazonHMACSHA256() {
        String secretKey  = "1234567890";
        String requestUrl = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&AWSAccessKeyId=AKIAIOSFODNN7EXAMPLE&AssociateTag=mytag-20&Operation=ItemLookup&ItemId=0679722769&ResponseGroup=Images,ItemAttributes,Offers,Reviews&Version=2013-08-01&Timestamp=2014-08-18T12:00:00Z";
        String targetUrl  = "http://webservices.amazon.com/onca/xml?AWSAccessKeyId=AKIAIOSFODNN7EXAMPLE&AssociateTag=mytag-20&ItemId=0679722769&Operation=ItemLookup&ResponseGroup=Images%2CItemAttributes%2COffers%2CReviews&Service=AWSECommerceService&Timestamp=2014-08-18T12%3A00%3A00Z&Version=2013-08-01&Signature=j7bZM0LXZ9eXeZruTqWm2DIvDYVUU3wxPPpp%2BiXxzQc%3D";
        String signedUrl  = Signatures.sign(requestUrl, "HmacSHA256", secretKey);
        Assert.assertEquals("Signed request", targetUrl, signedUrl);
    }

}