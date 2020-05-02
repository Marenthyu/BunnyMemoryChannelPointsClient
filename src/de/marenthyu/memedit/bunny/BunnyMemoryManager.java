package de.marenthyu.memedit.bunny;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import de.marenthyu.twitch.pubsub.PubSubClient;
import de.marenthyu.twitch.pubsub.channelpoints.ChannelPointsRedemptionHandler;

import javax.swing.*;
import java.io.IOException;
import java.util.Random;

import static de.marenthyu.memedit.bunny.BunnyConstants.*;
import static de.marenthyu.memedit.util.Shared.*;

public class BunnyMemoryManager {


    static int RABI_BASE_SIZE;
    public static Pointer bunnyProcess;
    public static int bunnyPID;

    private static final Random random = new Random();

    public static void init() {
        bunnyPID = getProcessIdByWindowTitle(RABI_TITLE);
        System.out.println("[BUNNY] Bunny PID: " + bunnyPID);
        bunnyProcess = openProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, bunnyPID);
        try {
            RABI_BASE_SIZE = getBaseAddress("rabiribi.exe");
            if (RABI_BASE_SIZE == 0) {
                throw new IOException("Invalid Size Returned from Base Address Detection");
            }
        } catch (NumberFormatException | IOException e) {
            // e.printStackTrace();
            System.out.println();
            System.out.println("[BUNNY] Error getting the Module base address automatically, asking user.");
            String userInput = JOptionPane.showInputDialog("Please Enter the base address of rabiribi.exe\n Too bad this failed. Thanks sig for the actual implementation that works most of the time. If you don't know how to do this, ask whoever linked you this software.");
            try {
                RABI_BASE_SIZE = Integer.decode(userInput);
            } catch (Exception y) {
                try {
                    RABI_BASE_SIZE = Integer.decode("0x" + userInput);
                } catch (Exception ex) {
                    System.out.println("[BUNNY] You're stupid. I think. something went wrong.");
                    e.printStackTrace();
                    ex.printStackTrace();
                    System.exit(2);
                }

            }
        }

        if (bunnyPID == 0) {
            System.err.println("[BUNNY] COULD NOT LOCATE PID FOR " + RABI_TITLE + " - PLEASE MAKE SURE THE GAME IS RUNNING AND ON THE SPECIFIED VERSION!");
            System.exit(1);
        }
    }

    public static void addSetHPHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HP]") {
            @Override
            public void matched(String input, String prompt) {
                try {
                    int newHeahlth = Integer.parseInt(input.trim());
                    setHP(newHeahlth);
                    System.out.println("[BUNNY][HP] HP set to " + newHeahlth);
                } catch (NumberFormatException e) {
                    System.err.println("[BUNNY][HP] Invalid Number.");
                }
            }
        });
    }

    public static void addFullHealHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][FULLHEAL]") {
            @Override
            public void matched(String input, String prompt) {
                fullHeal();
                System.out.println("[BUNNY][FULLHEAL] Healed fully!");
            }
        });
    }

    public static void addHealHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HEAL]") {
            @Override
            public void matched(String input, String prompt) {
                try {
                    int amount = Integer.parseInt(input.trim());
                    if (amount < 0) {
                        throw new NumberFormatException();
                    }
                    heal(amount);
                    System.out.println("[BUNNY][HEAL] Healed by " + amount);
                } catch (NumberFormatException e) {
                    System.err.println("[BUNNY][HEAL] Invalid Number " + input);
                }
            }

        });
    }

    public static void addDamageHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][DAMAGE]") {
            @Override
            public void matched(String input, String prompt) {
                try {
                    int amount = Integer.parseInt(input.trim());
                    if (amount < 0) {
                        throw new NumberFormatException();
                    }
                    heal(-amount);
                    System.out.println("[BUNNY][DAMAGE] Damaged by " + amount);
                } catch (NumberFormatException e) {
                    System.err.println("[BUNNY][DAMAGE] Invalid Number " + input);
                }
            }

        });
    }

    public static void addKillHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][KILL]") {
            @Override
            public void matched(String input, String prompt) {
                setHP(0);
            }

        });
    }

    public static void addBadgeHandlers(PubSubClient pubSub) {
        for (int i = 0; i < RABI_BADGES.length; i++) {
            for (int j = 0; j <= 2; j++) {
                String type;
                switch (j) {
                    case 0: {
                        type = "DELETE";
                        break;
                    }
                    case 1: {
                        type = "UNLOCK";
                        break;
                    }
                    case 2: {
                        type = "EQUIP";
                        break;
                    }
                    default:
                        throw new IllegalStateException("Unexpected value: " + j);
                }
                final int finalJ = j;
                final int finalI = i;
                pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BADGE][" + type + "][" + RABI_BADGES[finalI] + "]") {
                    @Override
                    public void matched(String input, String prompt) {
                        handleBadge(finalI, finalJ);
                        System.out.println("[BUNNY][BADGE][" + type + "][" + RABI_BADGES[finalI] + "] Badges changed!");
                    }

                });
            }
        }

    }

    public static void addBadgeRandomizationHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BADGE][RANDOM]") {
            @Override
            public void matched(String input, String prompt) {
                for (int i = 0; i < RABI_BADGES.length; i++) {
                    int r = random.nextInt(3);
                    handleBadge(i, r);
                    System.out.println("[BUNNY][BADGE][RANDOM][" + RABI_BADGES[i] + "] Badge changed!");
                }
                System.out.println("[BUNNY][BADGE][RANDOM] All Badges randomized!");
            }
        });
    }

    public static void addHealthUpHandlers(PubSubClient pubSub) {
        for (int i = 0; i <= 63; i++) {
            final int finalI = i;
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HPUP][" + finalI + "][ADD]") {
                @Override
                public void matched(String input, String prompt) {
                    collectHealthUp(finalI);
                    System.out.println("[BUNNY][HPUP] Enabled HPUp #" + (finalI + 1));
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HPUP][" + finalI + "][REMOVE]") {
                @Override
                public void matched(String input, String prompt) {
                    resetHealthUp(finalI);
                    System.out.println("[BUNNY][HPUP] Removed HPUp #" + (finalI + 1));
                }
            });
        }

    }

    // For debugging. allows chat arbitrary write. Don't use for non-debugging.
    public static void addDebugHealthUpHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HPUP][DEBUG]") {
            @Override
            public void matched(String input, String prompt) {
                int hpUpID, status;
                String[] split = input.split(",");
                hpUpID = Integer.parseInt(split[0]);
                status = Integer.parseInt(split[1]);
                setHPUp(hpUpID, status);
                System.out.println(String.format("[BUNNY][HPUP][DEBUG] Set HPUP #%d to %d!", hpUpID, status));
            }
        });
    }

    public static void addUnusedHealthUpHandlers(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HPUP][ADD]") {
            @Override
            public void matched(String input, String prompt) {
                if (addUnusedHPUP()) {
                    System.out.println("[BUNNY][HPUP] Added an unused HPUP");
                } else {
                    System.out.println("[BUNNY][HPUP] All unused HPUPs already used!");
                }

            }
        });
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HPUP][REMOVE]") {
            @Override
            public void matched(String input, String prompt) {
                if (removeUnusedHPUP()) {
                    System.out.println("[BUNNY][HPUP] Removed an unused HPUP");
                } else {
                    System.out.println("[BUNNY][HPUP] All unused HPUPs already removed!");
                }

            }
        });
    }

    public static void addBuffNameHandlers(PubSubClient pubSub) {
        for (int i = 0; i < RABI_BUFFS.length; i++) {
            int finalI = i;
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][CUSTOM]") {
                @Override
                public void matched(String input, String prompt) {
                    try {
                        setBuff(finalI, Integer.parseInt(input.trim()));
                        System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + input + " seconds!");
                    } catch (NumberFormatException e) {
                        System.err.println("[BUNNY][BUFF] Invalid number passed: " + input);
                    }
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][INSTANT]") {
                @Override
                public void matched(String input, String prompt) {
                    setBuff(finalI, 1);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for 1 second!");
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][SHORT]") {
                @Override
                public void matched(String input, String prompt) {
                    int r = 5 + random.nextInt(5);
                    setBuff(finalI, r);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + r + " seconds!");
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][MEDIUM]") {
                @Override
                public void matched(String input, String prompt) {
                    int r = 10 + random.nextInt(30);
                    setBuff(finalI, r);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + r + " seconds!");
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][LONG]") {
                @Override
                public void matched(String input, String prompt) {
                    int r = 30 + random.nextInt(60);
                    setBuff(finalI, r);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + r + " seconds!");
                }
            });
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][VERYLONG]") {
                @Override
                public void matched(String input, String prompt) {
                    int r = 100 + random.nextInt(300);
                    setBuff(finalI, r);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + r + " seconds!");
                }
            });
            // sshhh... let's not leak our hard work
            pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][" + RABI_BUFFS[finalI] + "][DONG]") {
                @Override
                public void matched(String input, String prompt) {
                    int r = 600 + random.nextInt(5000);
                    setBuff(finalI, r);
                    System.out.println("[BUNNY][BUFF] Activated buff " + RABI_BUFFS[finalI] + " for " + r + " seconds!");
                }
            });
        }

    }

    public static void addBuffDebugHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][BUFF][DEBUG]") {
            @Override
            public void matched(String input, String prompt) {
                int buffID, length;
                String[] split = input.split(",");
                buffID = Integer.parseInt(split[0]);
                length = Integer.parseInt(split[1]);
                setBuff(buffID, length);
                System.out.println("[BUNNY][BUFF][DEBUG] Set " + RABI_BUFFS[buffID] + " to length " + length);
            }
        });
    }

    public static void addAllHandlers(PubSubClient pubSub) {
        addSetHPHandler(pubSub);
        addFullHealHandler(pubSub);
        addHealHandler(pubSub);
        addDamageHandler(pubSub);
        addKillHandler(pubSub);
        addBadgeHandlers(pubSub);
        addBadgeRandomizationHandler(pubSub);
        addHealthUpHandlers(pubSub);
        addUnusedHealthUpHandlers(pubSub);
        addBuffNameHandlers(pubSub);
    }

    public static void addAllDebugHandlers(PubSubClient pubSub) {
        addDebugHealthUpHandler(pubSub);
        addBuffDebugHandler(pubSub);
    }

    private static void handleBadge(int badgeID, int newStatus) {
        switch (newStatus) {
            case 0: {
                removeBadge(badgeID);
                break;
            }
            case 1: {
                unluckAndUnequipBadge(badgeID);
                break;
            }
            case 2: {
                equipBadge(badgeID);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + newStatus);
        }
    }

    public static void setHP(int newHeahlth) {
        System.out.println("[BUNNY] Got request to change HP - setting it to " + newHeahlth);
        long dynAddress = findDynAddress(bunnyProcess, RABI_HEALTH_OFFSETS_IN_SAVBLOCK, RABI_BASE_SIZE + RABI_SAVBLOCK_OFFSET);
        int curHealth = getCurHP();
        System.out.println(String.format("[BUNNY] Old health read from memory was: %d, setting it to %d", curHealth, newHeahlth));
        writeMemory(bunnyProcess, dynAddress, intToBytes(newHeahlth));
    }

    public static int getCurHP() {
        long dynAddress = findDynAddress(bunnyProcess, RABI_HEALTH_OFFSETS_IN_SAVBLOCK, RABI_BASE_SIZE + RABI_SAVBLOCK_OFFSET);
        Memory healthCurrentMem = readMemory(bunnyProcess, dynAddress, 4);
        return healthCurrentMem.getInt(0);
    }

    public static int getMaxHP() {
        long dynAddress = findDynAddress(bunnyProcess, RABI_MAX_HEALTH_OFFSETS_IN_SAVBLOCK, RABI_BASE_SIZE + RABI_SAVBLOCK_OFFSET);
        Memory healthCurrentMem = readMemory(bunnyProcess, dynAddress, 4);
        return healthCurrentMem.getInt(0);
    }

    public static void heal(int amount) {
        setHP(getCurHP() + amount);
    }

    public static void fullHeal() {
        setHP(getMaxHP());
    }

    public static void equipBadge(int badgeID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{badgeID * 4}, RABI_BASE_SIZE + RABI_BADGE_ARRAY_BASE_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{0x02});
    }

    public static void unluckAndUnequipBadge(int badgeID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{badgeID * 4}, RABI_BASE_SIZE + RABI_BADGE_ARRAY_BASE_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{0x01});
    }

    public static void removeBadge(int badgeID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{badgeID * 4}, RABI_BASE_SIZE + RABI_BADGE_ARRAY_BASE_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{0x00});
    }

    public static void collectHealthUp(int healthUpID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{healthUpID * 4}, RABI_BASE_SIZE + RABI_HEALTHUP_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{0x01});
    }

    public static void resetHealthUp(int healthUpID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{healthUpID * 4}, RABI_BASE_SIZE + RABI_HEALTHUP_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{0x00});
    }

    public static void setHPUp(int healthUpID, int status) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{healthUpID * 4}, RABI_BASE_SIZE + RABI_HEALTHUP_POINTER_OFFSET);
        writeMemory(bunnyProcess, dynAddress, new byte[]{(byte) status});
    }

    public static boolean getHPUpStatus(int healthUpID) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{healthUpID * 4}, RABI_BASE_SIZE + RABI_HEALTHUP_POINTER_OFFSET);
        Memory healthUpCurrentMem = readMemory(bunnyProcess, dynAddress, 4);
        return healthUpCurrentMem.getInt(0) == 1;
    }

    public static boolean addUnusedHPUP() {
        for (int i = RABI_UNUSED_HPUP_ID_START; i <= RABI_UNUSED_HPUP_ID_END; i++) {
            if (!getHPUpStatus(i)) {
                collectHealthUp(i);
                return true;
            }
        }
        return false;
    }

    public static boolean removeUnusedHPUP() {
        for (int i = RABI_UNUSED_HPUP_ID_START; i <= RABI_UNUSED_HPUP_ID_END; i++) {
            if (getHPUpStatus(i)) {
                resetHealthUp(i);
                return true;
            }
        }
        return false;
    }

    public static void setBuff(int buffID, int length) {
        long dynAddress = findDynAddress(bunnyProcess, new int[]{buffID * 4}, RABI_BASE_SIZE + RABI_BUFFS_ARRAY_OFFSET);
        writeMemory(bunnyProcess, dynAddress, intToBytes(length * 60));
    }

}
