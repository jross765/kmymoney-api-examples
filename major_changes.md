# Major Changes

## V. 0.9 &rarr; 0.10
* Followed the deprecation of `FixedPointNumber` in the modules
  * "API (Core)", V. 0.10,
  * "Specialized Entitites", V. 0.4 and
  * "API Extended", V. 0.10:

  Changed various implementations so that it's not used any more
  and removed the FP-variant introduced in previous release.

* New package `overall.write` with new tool `ThinOutKMMFile`.

## V. 0.8.1 &rarr; 0.9
* Changed package structure so that module dependencies are clearer.

* Adapted to underlying modules' versions.

* `GenDepotTrx`: Now we have two variants: [BF|FP]
