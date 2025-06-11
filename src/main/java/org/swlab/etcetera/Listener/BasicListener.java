package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.velocitysocketclient.VelocityClient;
import de.kinglol12345.GUIPlus.events.GUIClickEvent;
import fr.nocsy.mcpets.api.MCPetsAPI;
import fr.nocsy.mcpets.data.Pet;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicProjectileHitEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.skill.PlayerCastSkillEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.swlab.etcetera.EtCetera;
import org.swlab.etcetera.Util.CommandUtil;
import org.swlab.etcetera.Util.DamageIndicatorUtil;
import org.swlab.etcetera.Util.NameTagUtil;
import org.swlab.etcetera.Util.PetUtil;

public class BasicListener implements Listener {
    @EventHandler
    public void cancelInstantAttack(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
            e.setCancelled(true);
            return;
        }
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
            return;
        }
//        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
//            if (!(e.getEntity() instanceof Player)) {
//                e.getEntity().remove();
//            }
//        }
        //로비채널에서 world에선 데미지 입는거 cancel
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing") || e.getEntity().getWorld().getName().equals("tuto")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (e.getWorld().getName().equals("raid")) {

        }
//        e.getChunk().addPluginChunkTicket(EtCetera.getInstance());
    }

    @EventHandler
    public void onMythicHitEvent(MythicDamageEvent e) {
        if (!e.getTarget().getWorld().getName().equals("raid")) {
            return;
        }
        SkillCaster caster = e.getCaster();
        AbstractEntity target = e.getTarget();
        if (caster.getEntity().isPlayer() && target.isPlayer()) {
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onFishingRodHitPlayer(PlayerFishEvent e) {
        if (e.getCaught() != null) {
            if (e.getCaught() instanceof Player) {
                e.getPlayer().sendMessage("§c 낚싯대로 사람을 낚지 마세요! 인생을 낚으세요!");
                return;
            }
        }
    }


    @EventHandler
    public void cancelPlayerDebuff(MythicProjectileHitEvent e) {
        if (e.getProjectile().getData().getCaster().getEntity().isPlayer()) {
            if (e.getEntity().isPlayer()) {
                MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
                if (mmoCoreAPI.isInSameParty((Player) e.getProjectile().getData().getCaster().getEntity().asPlayer().getBukkitEntity(), (Player) e.getEntity().asPlayer().getBukkitEntity())) {
                    e.setCancelled(true);
                }
            }
        }

        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                if (e.getEntity().isPlayer()) {
                    e.setCancelled(true);
                }
            }
        }
        if (EtCetera.getChannelType().equals("lobby")) {
            String worldName = "pvp";
            if (!e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getProjectile().getData().getCaster().getEntity() instanceof BukkitPlayer) {
                    if (e.getEntity().isPlayer()) {
                        e.setCancelled(true);
                    }
                }
            }
            if (e.getEntity().getWorld().getName().equals(worldName)) {
                if (e.getEntity().getLocation().getY() >= 18) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void skillOnVillage(PlayerCastSkillEvent e) {
        if (EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing") || e.getPlayer().getWorld().getName().equals("jump"))) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onGuiClick(GUIClickEvent e) {
        InventoryClickEvent inventoryClickEvent = e.getInventoryClickEvent();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        player.playSound(player, "uisounds:buttonclick2", 1.0F, 1.0F);
    }

    @EventHandler
    public void onSculkBloom(SculkBloomEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLevelUp(PlayerLevelUpEvent e) {
        Player player = e.getPlayer();
        player.playSound(player, "uisounds:congratulations", 1.0F, 1.0F);
        NameTagUtil.setPlayerNameTag(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        if ((!e.getPlayer().hasPlayedBefore() && EtCetera.getChannelType().equals("lobby"))) {
            String text = " §f摩 #8FFFAE" + e.getPlayer().getName() + " 님께서 서버에 &e첫 접속 #8FFFAE하셨습니다! 환영해주세요!";
            String format = ColorManager.format(text);
            Bukkit.broadcastMessage(format);

            VelocityClient.getInstance().getConnectClient().send(FirstJoinVelocityListener.class, format);


            if (!e.getPlayer().hasPermission("tutorial") && EtCetera.getChannelType().equals("lobby")) {
                Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
                    CommandUtil.runCommandAsOP(e.getPlayer(), "튜토리얼");
                    e.getPlayer().sendMessage("§a 튜토리얼을 진행해주세요!");
                }, 200L);
            }

        }
        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), () -> {
            PetUtil.loadPlayerPetData(e.getPlayer());
        }, 40L);


        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                e.getPlayer().clearActivePotionEffects();
            }
        }, 20L);
        if (EtCetera.getInstance().getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(e.getPlayer(), "spawn");
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        mmoCoreAPI.getPlayerData(e.getPlayer()).setClassPoints(999);

        Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "huds layout " + e.getPlayer().getName() + " remove slot_hud-layout");
                e.getPlayer().setNoDamageTicks(0);
                e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
            }
        }, 20L);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
//        e.setQuitMessage("§c[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에서 퇴장하셨습니다.");
        e.setQuitMessage("");
        Pet activePet = MCPetsAPI.getActivePet(e.getPlayer().getUniqueId());
        if (!(activePet == null)) {
            String id = activePet.getId();
            PetUtil.savePlayerPetData(e.getPlayer(), id);
        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAttack(PlayerAttackEvent e) {

        if(!e.getAttacker().getPlayer().isOp()){
            if(e.getEntity() instanceof  Player){
                e.setCancelled(true);
            }
        }
        MMOCoreAPI mmoCoreAPI = new MMOCoreAPI(EtCetera.getInstance());
        double damage = e.getDamage().getDamage();


        if (EtCetera.getChannelType().equals("dungeon")) {
            if (e.getAttacker() instanceof Player && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        if (e.getAttacker() instanceof Cow && e.getEntity() instanceof Player) {
            String className = mmoCoreAPI.getPlayerData((Player) e.getEntity()).getProfess().getId();
            if (className.equals("크루세이더")) {
                damage -= damage * 10 / 100;
            }
        }
        if(EtCetera.getChannelType().equals("dungeon") && e.getEntity() instanceof Player) {
            System.out.println("타격함. = ");
            e.setCancelled(true);
            return;
        }

        Player attacker = e.getAttacker().getPlayer();
        String profess = mmoCoreAPI.getPlayerData(attacker).getProfess().getId();
        LivingEntity victim = e.getEntity();
        PotionEffect potionEffect = victim.getPotionEffect(PotionEffectType.BAD_OMEN);
        if (potionEffect != null) {
            damage += damage * (potionEffect.getAmplifier() + 1) * 15 / 100;
        }
        if (e.getEntity() instanceof Zombie) {
            if (profess.equals("파우스트")) {
                damage += damage * 11 / 100;
            }
            if (profess.equals("제피르")) {
                damage += damage * 8 / 100;
            }
        }
        if (e.getEntity() instanceof Cow) {
            if (profess.equals("인페르노") || profess.equals("판")) {
                damage += damage * 5 / 100;
            }
        }
        if (attacker.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            damage += damage * ((attacker.getPotionEffect(PotionEffectType.INCREASE_DAMAGE).getAmplifier() + 1) * 10) / 100;
        }
        double originalDamage = e.getDamage().getDamage();
        DamageMetadata damageMetadata = e.getDamage().add(damage - originalDamage);
        double fixedDamage = Math.round(damageMetadata.getDamage());

        boolean skillCriticalStrike = e.getAttack().getDamage().isSkillCriticalStrike();

        if(victim.getType().equals(EntityType.COW)) {
//            DamageIndicatorUtil.summonTextDisplay(attacker, fixedDamage, e.getEntity(), skillCriticalStrike);
        }


        if (skillCriticalStrike) {
            e.getAttacker().getPlayer().sendTitle("", "§f                                                                   ᎌ §6" + fixedDamage, 5, 10, 5);
            return;
        }
        attacker.sendTitle("", "§f                                                                   ᎈ §c" + fixedDamage, 5, 10, 5);


    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelInstantAttack(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getDamager() instanceof Player) { // 기본공격 캔슬
            e.setCancelled(true);
            return;
        }
        if ((e.getEntity().getWorld().getName().equals("world") || e.getEntity().getWorld().getName().equals("fishing") || e.getEntity().getWorld().getName().equals("tuto")) && EtCetera.getChannelType().equals("lobby") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireTick(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onProfileLoad(ProfileSelectEvent e) {
        Player player = e.getPlayer();
        player.setHealth(player.getMaxHealth());
        NameTagUtil.setPlayerNameTag(player);

        if (!e.getPlayer().hasPermission("tutorial") && EtCetera.getChannelType().equals("lobby")) {
            CommandUtil.runCommandAsOP(e.getPlayer(), "튜토리얼");
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelThrowItem(PlayerDropItemEvent e) {
        Item itemDrop = e.getItemDrop();
        ItemStack itemStack = itemDrop.getItemStack();
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
        String id = MMOItems.getID(itemStack);
        if (id == null) {
            return;
        }
        System.out.println("id = " + id);
        if (id.startsWith("직업무기_5")) {
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void cancelInteractItem(PlayerInteractEvent e) {
        if (!e.getPlayer().isOp()) {
            if (e.getClickedBlock() == null) {
                return;
            }
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().isInteractable()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void dontStomp(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        if (block.getType() == Material.FARMLAND && e.getEntity() != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setKeepInventory(true);
        e.setDeathMessage(null);
    }

    @EventHandler
    public void cancelCraftItem(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelHungerChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void cancelBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelItemConsume(PlayerItemConsumeEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }


}
