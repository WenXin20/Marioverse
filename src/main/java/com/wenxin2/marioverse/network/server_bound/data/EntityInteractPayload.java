//package com.wenxin2.marioverse.network.server_bound.data;
//
//import com.wenxin2.marioverse.Marioverse;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//import org.jetbrains.annotations.NotNull;
//
//public record EntityInteractPayload(Entity entity, Boolean isSneaking, Vec3 hitVec) implements CustomPacketPayload {
//    public static final Type<EntityInteractPayload> ENTITY_INTERACT_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity_interact_payload"));
//
//    @NotNull
//    @Override
//    public Type<EntityInteractPayload> type() {
//        return ENTITY_INTERACT_PAYLOAD;
//    }
//
//    public void getLevel() {
//         entity.level();
//    }
//
//    public static StreamCodec<FriendlyByteBuf, EntityInteractPayload> createStreamCodec(Level level) {
//        return StreamCodec.composite(
//                // Codec for Entity (using level)
//                StreamCodec.of(
//                        (buf, entity) -> buf.writeInt(entity.getId()), // Serialize entity ID
//                        buf -> {
//                            int entityId = buf.readInt();
//                            return level.getEntity(entityId); // Deserialize entity by ID
//                        }
//                ), EntityInteractPayload::entity,
//
//                // Codec for Boolean (isSneaking)
//                ByteBufCodecs.BOOL, EntityInteractPayload::isSneaking,
//
//                // Codec for Vec3 (hitVec)
//                StreamCodec.of(
//                        (buf, vec3) -> {
//                            buf.writeDouble(vec3.x);
//                            buf.writeDouble(vec3.y);
//                            buf.writeDouble(vec3.z);
//                        },
//                        buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
//                ), EntityInteractPayload::hitVec,
//
//                EntityInteractPayload::new
//        );
//    }
//}
