module com.marmoush.javakit.xjackson {
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.module.paramnames;
  requires com.marmoush.javakit.core;

  exports com.marmoush.javakit.jackson;
  exports com.marmoush.javakit.jackson.transformer.id to com.fasterxml.jackson.databind;

  opens com.marmoush.javakit.jackson to com.fasterxml.jackson.databind;
  opens com.marmoush.javakit.jackson.transformer.generic to com.fasterxml.jackson.databind;
  opens com.marmoush.javakit.jackson.transformer.value to com.fasterxml.jackson.databind;
}