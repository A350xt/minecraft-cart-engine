package audaki.cart_engine.mixin;

import audaki.cart_engine.AceGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OldMinecartBehavior.class)
public abstract class OldMinecartBehaviorMixin extends MinecartBehavior {

    protected OldMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
        super(abstractMinecart);
    }

    @Inject(at = @At("HEAD"), method = "getMaxSpeed", cancellable = true)
    public void _getMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        // 只对普通矿车（可乘坐矿车）应用自定义速度规则
        if (minecart.getType() != EntityType.MINECART) {
            return;
        }
        
        if (!minecart.isRideable()) {
            return;
        }

        Entity passenger = minecart.getFirstPassenger();
        if (passenger == null) {
            int speed = level.getGameRules().getInt(AceGameRules.MINECART_MAX_SPEED_EMPTY_RIDER);
            if (speed > 0) {
                cir.setReturnValue(speed * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
                cir.cancel();
            }
            return;
        }

        if (passenger instanceof Player player) {
            // 检查名字有 "bot_"
            if (player.getName().getString().contains("bot_")) {
                int speed = level.getGameRules().getInt(AceGameRules.MINECART_MAX_SPEED_OTHER_RIDER);
                if (speed > 0) {
                    cir.setReturnValue(speed * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
                    cir.cancel();
                }
            } else {
                int speed = level.getGameRules().getInt(AceGameRules.MINECART_MAX_SPEED_PLAYER_RIDER);
                if (speed > 0) {
                    cir.setReturnValue(speed * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
                    cir.cancel();
                }
            }
            return;
        }

        int speed = level.getGameRules().getInt(AceGameRules.MINECART_MAX_SPEED_OTHER_RIDER);
        if (speed > 0) {
            cir.setReturnValue(speed * (this.minecart.isInWater() ? 0.5 : 1.0) / 20.0);
            cir.cancel();
        }
    }
}
