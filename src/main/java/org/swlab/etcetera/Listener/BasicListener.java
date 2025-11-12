package org.swlab.etcetera.Listener;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.mmotimereset.api.DailyResetEvent;
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
import io.lumine.mythic.lib.api.event.skill.SkillCastEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.DamageMetadata;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.*;
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
import org.swlab.etcetera.Util.NameTagUtil;
import org.swlab.etcetera.Util.PetUtil;

public class BasicListener implements Listener {

    int firstJoinCount = 0;

    @EventHandler
    public void cancelInstantAttack(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
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
        if (EtCetera.getInstance().getChannelType().equals("lobby") && (e.getPlayer().getWorld().getName().equals("world") || e.getPlayer().getWorld().getName().equals("fishing") || e.getPlayer().getWorld().getName().equals("jump")) || e.getPlayer().getWorld().getName().equals("pandora")) {
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
            firstJoinCount++;
            String text = " §f摩 #8FFFAE" + e.getPlayer().getName() + " 님께서 서버에 &e첫 접속 #8FFFAE하셨습니다! 환영해주세요!";
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getName().equals("dople_L")) {
                    onlinePlayer.sendMessage("§7(오늘의 " + firstJoinCount + "번째 첫 접속자입니다.)");
                }
            }
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
            {
                PetUtil.loadPlayerPetData(e.getPlayer());

            }
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
                e.getPlayer().setNoDamageTicks(0);
                e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
            }
        }, 20L);

    }

    @EventHandler
    public void onRightClickToBlock(PlayerInteractEvent e) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                Location location = e.getClickedBlock().getLocation();

                if(location.getBlockX() == 1193 && location.getBlockY() == 15 && location.getBlockZ() == 1229){
                    e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1146, 6, 1242));
                }
            }


    }

    @EventHandler
    public void onDailyReset(DailyResetEvent e) {
        firstJoinCount = 0;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
//        e.setQuitMessage("§c[!] §e"+e.getPlayer().getName()+"§f 님께서 서버에서 퇴장하셨습니다.");
        e.setQuitMessage("");
        Pet activePet = MCPetsAPI.getActivePet(e.getPlayer().getUniqueId());
        if (activePet == null) {
            PetUtil.savePlayerPetData(e.getPlayer(), "");
        } else {
            String id = activePet.getId();
            PetUtil.savePlayerPetData(e.getPlayer(), id);
        }


    }

    @EventHandler
    public void potionEffectApplyEvent(EntityPotionEffectEvent e){
        if(e.getModifiedType().equals(PotionEffectType.WITHER) && e.getEntity().getType().equals(EntityType.PLAYER)){
            e.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAttack(PlayerAttackEvent e) {

        if (!e.getAttacker().getPlayer().isOp()) {
            if (e.getEntity() instanceof Player) {
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
        if (EtCetera.getChannelType().equals("dungeon") && e.getEntity() instanceof Player) {
            e.setCancelled(true);
            return;
        }


        Player attacker = e.getAttacker().getPlayer();
        String profess = mmoCoreAPI.getPlayerData(attacker).getProfess().getId();
        LivingEntity victim = e.getEntity();
        PotionEffect potionEffect = victim.getPotionEffect(PotionEffectType.BAD_OMEN);
        if(victim instanceof Player player && e.getAttacker() instanceof Cow boss){
            if(boss.hasPotionEffect(PotionEffectType.WEAKNESS)){
                PotionEffect potionEffect1 = boss.getPotionEffect(PotionEffectType.WEAKNESS);
                damage -= damage * (potionEffect1.getAmplifier() + 1) * 10 / 100;
            }
        }
        if (potionEffect != null) {
            if(profess.equals("페이탈")){
                damage += damage * (potionEffect.getAmplifier() + 1) * 20 / 100;
            } else{
                damage += damage * (potionEffect.getAmplifier() + 1) * 15 / 100;

            }
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
            if (profess.equals("인페르노")) {
                if(e.getEntity().getFireTicks() > 0){
                    damage += damage * 15 / 100;
                    
                } else {
                    damage += damage * 5 / 100;
                }
            }
            else if (profess.equals("판")) {
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

        if (victim.getType().equals(EntityType.COW)) {
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

    @EventHandler
    public void onPlayerUseNetherPortal(PlayerPortalEvent e){
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
//        System.out.println("id = " + id);
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
                if (e.getClickedBlock().getType().equals(Material.CHEST)) {
                    if (!e.getPlayer().getWorld().getName().equals("adventures")) {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);


                }
            }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        boolean fishing = e.getPlayer().getWorld().getName().equals("fishing");
        if (!fishing) {
            e.getPlayer().sendMessage("§c 이곳에서 낚시 하실 수 없습니다.");
            e.setCancelled(true);

            return;
        }
    }

    @EventHandler
    public void onFishing(PlayerInteractEvent e) {
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType().equals(Material.ENDER_PEARL)) {
            e.getPlayer().sendMessage("§c 엔더진주 사용 불가합니다.");
            e.setCancelled(true);
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
        Player player = e.getPlayer();
        e.setKeepInventory(true);
        e.setDeathMessage(null);
        if(e.getPlayer().getWorld().getName().equals("raid")){
            return;
        }
        int delay = 0;
        Note[] notes = {
                Note.natural(0, Note.Tone.D),   // 낮은 파
                Note.natural(0, Note.Tone.C),   // 중간 라
                Note.natural(0, Note.Tone.B),   // 높은 도
                Note.natural(0, Note.Tone.A)    // 높은 파
        };

        String[] messages = {
                "#FFD4D4D   E   A   D", "#FA9C9CD  E  A  D", "#FF5A5AD E A D", "#FF1D1DDEAD"
        };
        for (int i = 0; i < 4; i++) {
            Note note = notes[i].sharped();
            int finalI = i;
            int finalDelay = delay;
            Bukkit.getScheduler().runTaskLater(EtCetera.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.sendTitle(ColorManager.format(messages[finalI]), "§f사망하여 로비에서 부활합니다.", 0, 10, 0);
                    player.playNote(player.getLocation(), Instrument.PIANO, note);
                    player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(finalDelay, 20));

                }
            }, delay);

            delay += 5;
        }

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
