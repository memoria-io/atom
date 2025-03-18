# Business free adapters

In Active and Reactive libraries:

* Should we implement a command handler at infrastructure level ?
    * If we do so this would mean EventSourcing business logic (e.g Event, Command) would be now leaked to
      infrastructure, any change there would affect all adapters, instead we should represent the logic technically (
      saving a Partitioned/Versioned Entity) on Infrastructure level, and isolating domain level ES.
    * Counterargument: is that these are kafka adapters the logic here is not domain business logic, event is part of
      the infrastructure data types, this is an Eventsourcing adapter to work with Cassandra or Kafka

* About the moving of TextTransformer to ES Layer, instead of adapter layer
    * Decoupling the adapter from the TextTransformer, since the addition of it to adapter layer, means either we'll
      have extra class param in fetch methods or pass such information on instantiation of adapter which makes adapter
      only bound to such class.
    * Counterargument, this couples also TT to ES Layer, and introduces more unnecessary code at it, while having TT
      lower layer allows JSON to be handled at Infra level.

## Reference

* https://medium.com/@tacsiazuma/pitfalls-of-the-adapter-pattern-a0234b77ab89