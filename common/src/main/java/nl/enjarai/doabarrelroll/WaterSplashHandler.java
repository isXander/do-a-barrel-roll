package nl.enjarai.doabarrelroll;

import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;

public class WaterSplashHandler {
    public static void spawnSplashParticles(LivingEntity entity) {
        // if inside water, we don't want to splash!
        if (entity.isSubmergedInWater()) return;
        // if we are not elytra flying, we don't want to splash!
        //if (!entity.isFallFlying()) return;

        // if we are not close to water, we don't want to splash!
        float maxDistanceFromWater = 7f;
        Vec3d playerPos = entity.getPos();
        Vec3d waterMinPos = playerPos.subtract(0, maxDistanceFromWater, 0);

        // checks for water up to 7 blocks below the entity
        BlockHitResult raycast = entity.getWorld().raycast(
                new RaycastContext(
                        playerPos,
                        waterMinPos,
                        RaycastContext.ShapeType.VISUAL,
                        RaycastContext.FluidHandling.WATER,
                        entity
                )
        );
        // no water found, we don't want to splash!
        if (raycast.getType() == BlockHitResult.Type.MISS)
            return;
        if (entity.getWorld().getBlockState(raycast.getBlockPos()).getFluidState().isEmpty())
            return;

        Vec3d waterPos = raycast.getPos();

        // calculate intensity of splash based on distance from water
        float distanceIntensity = (float)(playerPos.y - waterPos.y) / maxDistanceFromWater;
        distanceIntensity *= distanceIntensity; // https://easings.net/#easeInQuad
        System.out.println("distance intensity: " + distanceIntensity);

        // calculate intensity of splash based on horizontal velocity
        float maxHorizontalVelocity = 1f;
        float horizontalVelocityIntensity = Math.min((float) entity.getVelocity().horizontalLength(), maxHorizontalVelocity) * (1f / maxHorizontalVelocity);
        System.out.println("horizontal velocity intensity: " + horizontalVelocityIntensity);

        // multiply both intensities together to get the final intensity
        // if distance is half and max velocity, we get half splash, etc
        float intensity = distanceIntensity * horizontalVelocityIntensity;

        Vec3d leftVector = entity.getRotationVector().crossProduct(new Vec3d(-1, 0, 0)).normalize();
        Vec3d rightVector = entity.getRotationVector().crossProduct(new Vec3d(1, 0, 0)).normalize();

        for (int i = 0; i < 50*intensity; i++) {
            Vec3d particlePos = waterPos.add(leftVector.multiply(0.5f).multiply(Math.random() - 0.2));
            System.out.println("particlePos: " + particlePos);

            Vec3d direction = leftVector.multiply(Math.random() - 0.5);
            direction = direction.multiply(0.5 + Math.random() * 0.5);
            direction = direction.multiply(intensity);
            System.out.println("direction: " + direction);

            entity.getWorld().addParticle(ParticleTypes.SPLASH, particlePos.x, particlePos.y, particlePos.z, direction.x, direction.y, direction.z);
        }
        for (int i = 0; i < 50*intensity; i++) {
            Vec3d particlePos = waterPos.add(rightVector.multiply(0.5f).multiply(Math.random() - 0.2));

            Vec3d direction = rightVector.multiply(Math.random() - 0.5);
            direction = direction.multiply(0.5 + Math.random() * 0.5);
            direction = direction.multiply(intensity);

            entity.getWorld().addParticle(ParticleTypes.SPLASH, particlePos.x, particlePos.y, particlePos.z, direction.x, direction.y, direction.z);
        }
    }

    private static float getIntensityForEntity(LivingEntity entity) {
        return 0f;
    }
}
