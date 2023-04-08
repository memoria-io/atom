package io.memoria.atom.web;

import io.netty.handler.codec.http.HttpResponseStatus;

record DefaultResponse(HttpResponseStatus status, String payload) implements Response {}
