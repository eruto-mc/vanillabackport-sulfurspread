package net.erutobusiness.sulfurspread;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Vanilla Backport Sulfur Spread — a one-fix mod.
 *
 * <p>All of the work happens in {@code BiomeGenerationMixin}; see that class for the measurement
 * and the reasoning.
 */
@Mod(SulfurSpread.MOD_ID)
public class SulfurSpread {

    public static final String MOD_ID = "sulfurspread";

    /**
     * The weirdness band Sulfur Caves is registered with instead of Vanilla Backport's
     * {@code -1.1 .. -0.85}.
     *
     * <p>The lower bound is kept as upstream wrote it. Only the upper bound moves, to the value the
     * biome uses in the version being backported.
     */
    public static final float WEIRDNESS_MIN = -1.1F;
    public static final float WEIRDNESS_MAX = -0.45F;

    public static final Logger LOGGER = LogUtils.getLogger();

    public SulfurSpread() {
        LOGGER.info("Sulfur Spread active: Sulfur Caves weirdness will be widened to {} .. {}.",
                WEIRDNESS_MIN, WEIRDNESS_MAX);
    }
}
