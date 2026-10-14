module io.memoria.atom.xjackson {
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.module.paramnames;
  requires io.memoria.atom.core;

  exports io.memoria.atom.jackson;
  exports io.memoria.atom.jackson.transformer.id to com.fasterxml.jackson.databind;

  opens io.memoria.atom.jackson to com.fasterxml.jackson.databind;
  opens io.memoria.atom.jackson.transformer.generic to com.fasterxml.jackson.databind;
  opens io.memoria.atom.jackson.transformer.value to com.fasterxml.jackson.databind;
}