package xyz.przemyk.timestopper.mixin;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import xyz.przemyk.timestopper.TimeStopperMod;

@SuppressWarnings("unused")
@Mixin(ParticleEngine.class)
public abstract class MixinParticleEngine implements PreparableReloadListener {

    private void tickParticle(Particle particle) {
        try {
            TimeStopperMod.updateParticle(particle);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking Particle");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
            crashreportcategory.setDetail("Particle", particle::toString);
            crashreportcategory.setDetail("Particle Type", particle.getRenderType()::toString);
            throw new ReportedException(crashreport);
        }
    }
}
