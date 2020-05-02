package de.marenthyu.memedit.bunny;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import de.marenthyu.twitch.pubsub.PubSubClient;
import de.marenthyu.twitch.pubsub.channelpoints.ChannelPointsRedemptionHandler;

import javax.swing.*;
import java.io.IOException;

import static de.marenthyu.memedit.bunny.BunnyConstants.*;
import static de.marenthyu.memedit.util.Shared.*;

public class BunnyMemoryManager {


    static int RABI_BASE_SIZE;
    public static Pointer bunnyProcess;
    public static int bunnyPID;


    public static void init() {
        bunnyPID = getProcessId(RABI_TITLE);
        System.out.println("[BUNNY] Bunny PID: " + bunnyPID);
        bunnyProcess = openProcess(PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, bunnyPID);
        try {
            RABI_BASE_SIZE = getBaseAddress("rabiribi.exe");
            if (RABI_BASE_SIZE == 0) {
                throw new IOException("Invalid Size Returned from Powershell");
            }
        } catch (NumberFormatException | IOException e) {
            // e.printStackTrace();
            System.out.println();
            System.out.println("[BUNNY] Error getting the Module base address automatically, asking user.");
            String userInput = JOptionPane.showInputDialog("Please Enter the base address of rabiribi.exe\n If you dare, please help me automate this. I am at the end of my knowledge. If you don't know how to do this, ask whoever linked you this software.");
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
            public void matched(String input) {
                try {
                    int newHeahlth = Integer.parseInt(input);
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
            public void matched(String input) {
                fullHeal();
                System.out.println("[BUNNY][FULLHEAL] Healed fully!");
            }
        });
    }

    public static void addHealHandler(PubSubClient pubSub) {
        pubSub.addChannelPointsRedemptionHandler(new ChannelPointsRedemptionHandler("[BUNNY][HEAL]") {
            @Override
            public void matched(String input) {
                try {
                    int amount = Integer.parseInt(input);
                    heal(amount);
                    System.out.println("[BUNNY][HEAL] Healed by " + amount);
                } catch (NumberFormatException e) {
                    System.err.println("[BUNNY][HEAL] Invalid Number.");
                }
            }

        });
    }


    public static void addBadgeHandlers(PubSubClient pubSub) {
        for (int i = 0;i < RABI_BADGES.length;i++) {
            for (int j = 0;j<=2;j++) {
                String type;
                switch (j) {
                    case 0:
                    {
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
                    public void matched(String input) {
                        switch (finalJ) {
                            case 0:
                            {
                                removeBadge(finalI);
                                break;
                            }
                            case 1: {
                                unluckAndUnequipBadge(finalI);
                                break;
                            }
                            case 2: {
                                equipBadge(finalI);
                                break;
                            }
                            default:
                                throw new IllegalStateException("Unexpected value: " + finalJ);
                        }
                        System.out.println("[BUNNY][BADGE][" + type + "][" + RABI_BADGES[finalI] + "] Badges changed!");
                    }

                });
            }
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

}
