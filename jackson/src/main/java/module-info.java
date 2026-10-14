module com.marmoush.javakit.jackson {
  requires com.marmoush.javakit.core;
  requires com.fasterxml.jackson.annotation;
  requires tools.jackson.databind;
  requires tools.jackson.core;
  requires tools.jackson.dataformat.yaml;

  opens com.marmoush.javakit.jackson to tools.jackson.databind;
  opens com.marmoush.javakit.jackson.transformer.generic to tools.jackson.databind;
  opens com.marmoush.javakit.jackson.transformer.value to tools.jackson.databind;

  exports com.marmoush.javakit.jackson;
}