// mode=local,language=javascript
var Function = Java.type("java.util.function.Function")
var Collectors = Java.type("java.util.stream.Collectors")
cache
    .entrySet().stream()
    .map(function(e) e.getValue())
    .sorted(function(o1, o2 ) o1.getTitle().localeCompare(o2.getTitle()))
    .collect(Collectors.toList());
