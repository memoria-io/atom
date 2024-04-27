module io.memoria.atom.jackson {
  requires io.memoria.atom.core;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.module.paramnames;
  exports io.memoria.atom.jackson;
  opens io.memoria.atom.jackson;
  exports io.memoria.atom.jackson.transformer.generic;
  opens io.memoria.atom.jackson.transformer.generic;
  exports io.memoria.atom.jackson.transformer.id;
  opens io.memoria.atom.jackson.transformer.id;
  exports io.memoria.atom.jackson.transformer.value;
  opens io.memoria.atom.jackson.transformer.value;
}