package org.swlab.etcetera.Convinience;

public class SkillCooldownNotice {

    public static void scheduleStart() {

//
//        Bukkit.getScheduler().runTaskTimerAsynchronously(EtCetera.getInstance(), new Runnable() {
//            @Override
//            public void run() {
//                Bukkit.getOnlinePlayers().forEach(player -> {
//
//                    if(EtCetera.getChannelType().equals("lobby")){
//                        if(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("fishing")){
//                            return;
//                        }
//                    }
//                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
//                    String id = MMOItems.getID(itemInMainHand);
//                    Type type = MMOItems.getType(itemInMainHand);
//                    if(type == null || itemInMainHand.equals( Material.AIR)){
//                        return;
//                    }
//                    if(!type.getId().equals("SWORD")){
//                        return;
//                    }
//                    StatData data = Objects.requireNonNull(MMOItems.plugin.getMMOItem(type, id)).getData(ItemStats.ABILITIES);
//                    AbilityListData data2 = (AbilityListData) data;
//
//
//                    StringBuilder stringBuilder = new StringBuilder();
//                    if(data2 == null){
//                        return;
//                    }
//                    Iterator<AbilityData> iterator = data2.getAbilities().iterator();
//
//                    while (iterator.hasNext()) {
//                        AbilityData ability = iterator.next();
//                        String originTypeName = ability.getTrigger().getName();
//                        String key = "";
//                        switch (originTypeName) {
//                            case "Left Click":
//                                key = "L";
//                                break;
//                            case "Right Click":
//                                key = "R";
//                                break;
//                            case "Shift Right Click":
//                                key = "S+R";
//                                break;
//                            case "Shift Left Click":
//                                key = "S+L";
//                                break;
//                            case "Drop Item":
//                                key = "Q";
//                                break;
//                        }
//                        String name = ability.getAbility().getName();
//                        String replacedName = name.replace(" ", "_").toLowerCase();
//                        String cooldown = PlaceholderAPI.setPlaceholders(player, "%mythiclib_cooldown_skill_" + replacedName + "%");
//                        stringBuilder.append("§6"+key + "§f: §a" + cooldown + "§f초");
//                        if(iterator.hasNext()){
//                            stringBuilder.append(" | ");
//                        }
//                        player.sendActionBar(itemInMainHand.getItemMeta().getDisplayName()+ " §f:: " + stringBuilder.toString());
//                    }
//                });
//            }
//        },4L,4L);
    }

}

