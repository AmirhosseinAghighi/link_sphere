package org.linkSphere.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.linkSphere.core.Sphere;

public class RequestHandlerTest {
    @Test
    public void testAddPath() throws Exception{
        try {
            Sphere.start(3000, RequestHandlerTest.class);
        } catch (Exception e) {

        }
    }
}
