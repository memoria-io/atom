package io.memoria.atom.jackson;

import java.util.List;

public record AppConfig(String projectName,
                        String nodeControllerPath,
                        String stringValue,
                        Integer integerValue,
                        long longValue,
                        double doubleValue,
                        List<String> stringList,
                        List<Integer> integerList,
                        List<Long> longList,
                        List<Double> doubleList,
                        String subName,
                        List<String> subList) {}
