# TextTransformer

* Why serializer doesn't throw checked exceptions ?
    * When serialising the input is supposed to be valid therefore using RuntimeException, while when deserializing
      there's a possibility of BadRequest, which needs to be handled