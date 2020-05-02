package de.marenthyu.memedit.bunny;

public class BunnyConstants {
    final static String RABI_TITLE = "Rabi-Ribi ver 1.99t";
    final static int RABI_SAVBLOCK_OFFSET = 0x01689290;
    final static int[] RABI_HEALTH_OFFSETS_IN_SAVBLOCK = {0x4DC};
    final static int[] RABI_MAX_HEALTH_OFFSETS_IN_SAVBLOCK = {0x4EC};
    final static int[] RABI_MANA_OFFSETS_IN_SAVBLOCK = {0x6BC}; // It's a float!
    final static int RABI_BADGE_ARRAY_BASE_POINTER_OFFSET = 0x002ED130; // This still is a pointerpath, with offset 0 as the only one.
    final static String[] RABI_BADGES = {"HEALTH_UP", "HEALTH_SURGE", "MANA_UP", "MANA_SURGE", "CRISIS_BOOST",
            "ATTACK_GROW", "DEFENSE_GROW", "ATTACK_TRADE", "DEFENSE_TRADE", "ARM_STRENGTH", "CARROT_BOOST", "WEAKEN",
            "SELF_DEFENSE", "ARMORED", "LUCKY_SEVEN", "HEX_CANCEL", "PURE_LOVE", "TOXIC_STRIKE",
            "FRAME_CANCEL", "HEALTH_WAGER", "MANA_WAGER", "STAMINA_PLUS", "BLESSED", "HITBOX_DOWN", "CASHBACK",
            "SURVIVAL", "TOP_FORM", "TOUGH_SKIN", "ERINA_BADGE", "RIBBON_BADGE", "AUTO_TRIGGER", "LILITHS_GIFT"};

}
