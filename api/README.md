# Aggregate API

Defines the public API of the aggregate, i.e. the aggregate descriptor and associated types.
* This jar is shared with other aggregates
* To avoid dependency hell in production it should be pretty much dependency free.
* The types in this jar can be used by downstream services, but should not be part of their api,  
  i.e. a downstream `api` jar should *not* depend on this `api` jar.

## Backwards compatability

Because downstream systems should not expose types in this API in their own API, there is no need for binary 
compatability when making changes. However, any changes should be backwards compatible as far as the 
serialization format being used.