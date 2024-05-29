package org.linkSphere.http;

import org.linkSphere.annotations.http.*;

public enum RequestMethodTypes {
    GET(Get.class),
    POST(Post.class),
    DELETE(Delete.class),
    UPDATE(Update.class),
    PUT(Put.class),
    HEAD(Head.class),;

    private Class clazz;
    RequestMethodTypes(Class clazz) {
    }
}
