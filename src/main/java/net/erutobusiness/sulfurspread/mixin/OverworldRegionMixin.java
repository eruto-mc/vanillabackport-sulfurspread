package net.erutobusiness.sulfurspread.mixin;

import net.erutobusiness.sulfurspread.SulfurSpread;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Widens the one weirdness span Vanilla Backport asks for when it registers Sulfur Caves.
 *
 * <p>The original body, read out of {@code VanillaBackport-forge-1.20.1-1.1.7.10.jar}:
 *
 * <pre>
 * addSulfurCaves(VanillaParameterOverlayBuilder):
 *   temperature      FULL_RANGE
 *   humidity         FULL_RANGE
 *   continentalness  Continentalness.span(COAST, INLAND)
 *   erosion          Erosion.span(EROSION_5, EROSION_6)
 *   depth            UNDERGROUND
 *   weirdness        Climate.Parameter.span(-1.1F, -0.85F)   &lt;- the only Climate$Parameter.span here
 *   -&gt; builder.add(point, ModBiomes.SULFUR_CAVES)
 * </pre>
 *
 * <p><b>Why it needed changing.</b> Weirdness runs from -1 to 1, so the part of
 * {@code -1.1 .. -0.85} that a world can actually produce is {@code -1.0 .. -0.85} — about 7.5% of
 * the range — and erosion 5-6 plus underground depth have to line up with it at the same time. On
 * this pack's terrain that never happened: counting biomes straight out of the region files of the
 * club world (238,078 chunks) found <b>zero</b> {@code minecraft:sulfur_caves} chunks. Pale Garden,
 * which Vanilla Backport adds through the same builder but with ordinary parameters, had 2,270.
 * The mod's own switch was not the cause — {@code has_sulfur_caves} is {@code true}.
 *
 * <p><b>What this does.</b> Returns {@code -1.1 .. -0.45} instead. That is the band the biome uses
 * in the version being backported, so this widens toward upstream rather than to a number picked to
 * make the biome common. Every other parameter keeps the value Vanilla Backport chose.
 *
 * <p><b>Scope.</b> {@code method = "addSulfurCaves"} keeps the redirect inside that one method, and
 * it is the only {@code Climate$Parameter.span(FF)} call in it — the continentalness and erosion
 * spans go through {@code ParameterUtils$Continentalness} and {@code ParameterUtils$Erosion}, which
 * are different classes. {@code defaultRequire = 1} means that if a later Vanilla Backport rewrites
 * this method, the game fails to start instead of quietly leaving the biome ungeneratable again.
 */
@Mixin(targets = "com.blackgear.vanillabackport.common.worldgen.biomes.terrablender.OverworldRegion",
       remap = false)
public class OverworldRegionMixin {

    @Redirect(
            method = "addSulfurCaves",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Climate$Parameter;"
                            + "span(FF)Lnet/minecraft/world/level/biome/Climate$Parameter;",
                    remap = true
            ),
            remap = false
    )
    private static Climate.Parameter sulfurspread$widenWeirdness(float low, float high) {
        SulfurSpread.LOGGER.info(
                "Sulfur Caves weirdness {} .. {} replaced with {} .. {}",
                low, high, SulfurSpread.WEIRDNESS_MIN, SulfurSpread.WEIRDNESS_MAX);
        return Climate.Parameter.span(SulfurSpread.WEIRDNESS_MIN, SulfurSpread.WEIRDNESS_MAX);
    }
}
