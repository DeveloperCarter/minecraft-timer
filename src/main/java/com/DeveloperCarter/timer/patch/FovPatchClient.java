package com.DeveloperCarter.timer.patch;

import com.DeveloperCarter.timer.TimerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import top.theillusivec4.curios.api.CuriosApi;

@EventBusSubscriber(modid = TimerMod.MODID, value = Dist.CLIENT) // no deprecated 'bus='
public final class FovPatchClient {

    private static final ResourceLocation RUNNING_SHOES_ID =
            ResourceLocation.fromNamespaceAndPath("artifacts", "running_shoes");

    // Resolve the Item once (client-safe)
    private static final Item RUNNING_SHOES_ITEM =
            BuiltInRegistries.ITEM.get(RUNNING_SHOES_ID);

    @SubscribeEvent
    public static void onFov(ViewportEvent.ComputeFov event) {
        if (!(event.getCamera().getEntity() instanceof LocalPlayer player)) return;
        if (!ModList.get().isLoaded("artifacts")) return;

        boolean wearingShoes = false;

        if (ModList.get().isLoaded("curios")) {
            wearingShoes = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(stack -> stack.is(RUNNING_SHOES_ITEM)).isPresent())
                    .orElse(false);
        }

        if (!wearingShoes) {
            for (var stack : player.getInventory().armor) {
                if (stack.is(RUNNING_SHOES_ITEM)) { wearingShoes = true; break; }
            }
        }

        if (wearingShoes) {
            double baseFov = Minecraft.getInstance().options.fov().get();
            event.setFOV(baseFov);
        }
    }
}
