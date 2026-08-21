# Vanilla Backport Sulfur Spread

Widens the one weirdness span [Vanilla Backport](https://modrinth.com/mod/vanillabackport)
asks for when it registers Sulfur Caves, so the biome can actually turn up.

- Minecraft 1.20.1 / Forge 47.x / Java 17
- Requires: `vanillabackport` `[1.1.7,)`, `terrablender` `[3,)`
- Licence: MIT

## What was wrong

Vanilla Backport places Sulfur Caves through TerraBlender with weirdness spanning
**-1.1 to -0.85**. Weirdness only runs from -1 to 1, so the reachable part of that span is
-1.0 to -0.85 — about **7.5% of the range** — and erosion 5-6 and underground depth have to
line up with it at the same time.

On a Terralith + Tectonic world that combination never came up. Counting biomes straight out
of the region files of a 238,084-chunk world found **zero** `minecraft:sulfur_caves` chunks.
Pale Garden, which the same mod adds through the same builder but with ordinary parameters,
had 2,270 in the same world. The mod's own switch was not the cause: `has_sulfur_caves` was
already `true`.

## What this does

One `@Redirect`, on the single `Climate.Parameter.span(float, float)` call inside
`OverworldRegion.addSulfurCaves`. It returns **-1.1 to -0.45** instead — the band the biome
uses in the version being backported, so this widens *toward* upstream rather than to a number
picked to make the biome common.

Nothing else is touched. Erosion, depth, continentalness, temperature and humidity keep the
values Vanilla Backport chose, and no other biome registration is affected.

| World | `minecraft:sulfur_caves` chunks |
| - | - |
| Before, 238,084 chunks | **0** |
| After, 25,237 chunks | **68** (0.27%) |

## Notes for anyone changing this

`method = "addSulfurCaves"` keeps the redirect inside that one method, and it is the only
`Climate$Parameter.span(FF)` call in it — the continentalness and erosion spans go through
`ParameterUtils$Continentalness` and `ParameterUtils$Erosion`, which are different classes.

`defaultRequire = 1` is deliberate: if a later Vanilla Backport rewrites that method, the game
fails to start instead of quietly leaving the biome ungeneratable again.

The Mixin annotation processor needs the target class on the compile classpath even with
`targets = "..."`, so `build.gradle` pulls Vanilla Backport from the Modrinth maven. It pins the
**version id** (`u8YPiN39`), not the version number — `1.1.7.10` is also the version number of
the Fabric and 1.21.1 builds, so the number alone is ambiguous.
