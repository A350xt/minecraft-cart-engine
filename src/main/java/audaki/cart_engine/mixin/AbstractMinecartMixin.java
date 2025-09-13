package audaki.cart_engine.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends VehicleEntity {

    @Shadow
    public abstract boolean isRideable();

    @Shadow
    public abstract boolean isFurnace();

    @Shadow
    private boolean onRails;

    @Mutable
    @Final
    @Shadow
    private MinecartBehavior behavior;

    public AbstractMinecartMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void _init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        // 在构造函数末尾根据类型设置正确的行为
        AbstractMinecart instance = (AbstractMinecart) (Object) this;
        if (entityType == EntityType.MINECART) {
            // 普通可乘矿车：强制使用新行为
            this.behavior = new NewMinecartBehavior(instance);
        } else {
            // 其他矿车：即使实验启用导致被设为新行为，也强制回退到旧行为
            if (this.behavior instanceof NewMinecartBehavior) {
                this.behavior = new OldMinecartBehavior(instance);
            }
        }
    }
}
