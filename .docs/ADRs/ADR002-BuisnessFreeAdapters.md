# Business free adapters

In Active and Reactive libraries:

* Should we implement a command handler at infrastructure level ?
    * If we do so this would mean EventSourcing business logic (e.g Event, Command) would be now leaked to
      infrastructure, any change there would affect all adapters, instead we should represent the logic technically (
      saving a Partitioned/Versioned Entity) on Infrastructure level, and isolating domain level ES.

* About the moving of TextTransformer to ES Layer, instead of adapter layer
    * Decoupling the adapter from the TextTransformer, since the addition of it to adapter layer, means either we'll
      have extra class param in fetch methods or pass such information on instantiation of adapter which makes adapter
      only bound to such class.

## Reference

* https://medium.com/@tacsiazuma/pitfalls-of-the-adapter-pattern-a0234b77ab89