package io.memoria.atom.reactive.web;

import io.netty.handler.codec.http.HttpResponseStatus;

record DefaultResponse(HttpResponseStatus status, String payload) implements Response {}
