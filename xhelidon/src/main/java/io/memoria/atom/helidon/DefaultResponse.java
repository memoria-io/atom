package io.memoria.atom.helidon;

import io.helidon.http.Status;

record DefaultResponse(Status status, String payload) implements Response {}
